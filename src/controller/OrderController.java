package controller;

import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import model.order.Order;
import model.order.OrderQueue;
import model.order.OrderStatus;
import model.payment.CardPayment;
import model.payment.CashPayment;
import model.payment.Payment;
import model.payment.PointPayment;
import model.product.Product;

// 주문 컨트롤러

public class OrderController {

    private final Inventory inventory;
    private final OrderQueue orderQueue;
    private final PaymentController paymentController;
    private final InventoryController inventoryController;
    private final MachineController machineController;   // 취소 시 View broadcast 위함

    public OrderController(Inventory inventory, OrderQueue orderQueue, PaymentController paymentController, InventoryController inventoryController, MachineController machineController) {
        this.inventory = inventory;
        this.orderQueue = orderQueue;
        this.paymentController = paymentController;
        this.inventoryController = inventoryController;
        this.machineController = machineController;
    }

    // 메뉴 조회

    // 메뉴 표시용 전체 상품 목록 
    public List<Product> getAvailableProducts() { 
        return new ArrayList<>(inventory.listAll().values());
    }

    // 주문 사전 검증 (MainMenuView 의 '주문하기' 클릭 시점)
    // 주문 가능 여부만 검증 (실제 결제/재고차감은 하지 않음)
    public boolean createOrder(String productName, String userName) {
        Product product = findProductByName(productName); // findProductByName: Product 찾아 검증. productName: 사용자가 입력한 상품명
        if (product == null) {
            return false;
        }
        if (product.getStock() <= 0) {
            System.out.println("[OrderController] 품절: " + product.getName());
            return false;
        }
        System.out.println("[OrderController] 주문 검증 통과: " + product.getName() + " (요청자: " + userName + ")");
        return true; // 상품이 존재하고 재고가 1 이상이면 true
    }

    // 결제 + 제조 (PaymentView의 '결제하기' 클릭 시점)
    // 실제 주문 처리: 결제 → 재고 차감 → 대기열 등록
    public boolean processPaymentAndManufacture(String productName, String paymentType, int amount) { // productName: 결제할 상품의 이름. paymentType: CASH, CARD, POINT. amount: 현금일 때만 의미가 있는 투입 금액
        // 1) 상품 조회 — View는 이름만 알지만 Inventory는 ID 키이므로 변환
        Product product = findProductByName(productName);
        if (product == null) {
            System.out.println("[OrderController] 존재하지 않는 상품: " + productName);
            return false;
        }

        // 2) 결제 수단 객체 생성 (View가 직접 Payment 구현체를 알 필요 없도록 여기서 변환)
        Payment payment = newPayment(paymentType, amount);
        if (payment == null) {
            return false;
        }

        // 3) 결제 시도
        if (!paymentController.pay(payment, product.getPrice())) {
            return false;
        }

        // 4) 재고 차감 (내부 식별은 ID로)
        try {
            inventoryController.reduceStock(product.getId(), 1);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 결제는 이미 성공했지만 재고가 없는 경우
            System.out.println("[OrderController] 재고 차감 실패: " + e.getMessage());
            return false;
        }

        // 5) 주문 생성 후 대기열 등록 → MakerThread가 깨어남
        Order order = new Order(product, payment);
        orderQueue.enqueue(order);
        System.out.println("[OrderController] 주문 접수: " + product.getName());
        return true;
    }

    // 상품명으로 Product 찾음
    // 시드 데이터는 이름이 모두 다름 → 첫번째 매치 반환
    private Product findProductByName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return null;
        }
        for (Product p : inventory.listAll().values()) {
            if (productName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    // paymentType 문자열을 실제 Payment 구현체로 변환
    private Payment newPayment(String paymentType, int amount) {
        if (paymentType == null) {
            return null;
        }
        switch (paymentType) {
            case "CASH": {
                CashPayment cash = new CashPayment();
                cash.insert(amount);
                return cash;
            }
            case "CARD":
                // 가상 카드 
                return new CardPayment("1234567812345678", 100_000);
            case "POINT":
                // 가상 포인트 잔액
                return new PointPayment(50_000);
            default:
                System.out.println("[OrderController] 알 수 없는 결제 방식: " + paymentType);
                return null;
        }
    }

    // 주문 취소 
    // WAITING 상태 주문만 취소, 재고 복구, 큐에서 제거
    // MakerThread가 dequeue 직후 MAKING으로 바꾸는 시점과 겹칠 수 있으므로 Order 객체를 락으로 잡아 상태 전이 보호. 큐 제거는 락 보유를 짧게 하기 위해 블록 밖에서
    // 큐에서 제거하지 않으면 MakerThread가 CANCELED인 주문을 꺼내 setStatus(MAKING) 호출 시 잘못된 전이로 예외가 발생해 스레드 자체가 죽음
    public boolean cancelOrder(Order order) {
        if (order == null) {
            return false;
        }
        synchronized (order) {
            if (order.getStatus() != OrderStatus.WAITING) {
                System.out.println("[OrderController] 제조가 이미 시작되어 취소할 수 없습니다.");
                return false;
            }
            order.setStatus(OrderStatus.CANCELED);
        }
        orderQueue.remove(order);
        inventoryController.addStock(order.getProduct().getId(), 1);
        machineController.onMakerEvent(null);   // 모든 View 에 큐/재고 갱신 알림
        System.out.println("[OrderController] 주문 취소 완료: " + order.getProduct().getName());
        return true;
    }

    // 주문 번호로 큐에서 주문을 찾아 취소
    // View는 Order 객체가 아닌 ID만 알고 있으므로 별도 메서드 제공
    public boolean cancelOrderById(int orderId) {
        for (Order o : orderQueue.peekAll()) {
            if (o.getOrderId() == orderId) {
                return cancelOrder(o);
            }
        }
        return false; // 해당 ID의 WAITING 주문을 찾아 취소했으면 true
    }
}
