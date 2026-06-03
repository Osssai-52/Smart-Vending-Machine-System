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

/**
 * 주문 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>{@link #getAvailableProducts} : 메뉴 표시용 상품 목록 제공.</li>
 *   <li>{@link #createOrder} : 주문 가능 여부 사전 검증 (상품 존재 + 재고 > 0).</li>
 *   <li>{@link #processPaymentAndManufacture} : 결제 → 재고 차감 → 대기열 등록 (실제 주문 처리).</li>
 *   <li>{@link #cancelOrder} : WAITING 상태 주문만 취소 + 재고 복구.</li>
 * </ul>
 *
 * <p>왜 createOrder 와 processPaymentAndManufacture 가 두 단계로 나뉘나:
 * View 흐름이 [메인 메뉴 → 결제 화면] 두 단계라 검증과 실제 처리를 분리해 두면
 * "결제 화면 진입 후 결제 취소" 같은 케이스에서 부작용이 남지 않는다.
 * (createOrder 는 부작용 없는 검증만 수행)
 */
public class OrderController {

    private final Inventory inventory;
    private final OrderQueue orderQueue;
    private final PaymentController paymentController;
    private final InventoryController inventoryController;
    private final MachineController machineController;   // 취소 시 View broadcast 용

    public OrderController(Inventory inventory,
                           OrderQueue orderQueue,
                           PaymentController paymentController,
                           InventoryController inventoryController,
                           MachineController machineController) {
        this.inventory = inventory;
        this.orderQueue = orderQueue;
        this.paymentController = paymentController;
        this.inventoryController = inventoryController;
        this.machineController = machineController;
    }

    // ------------------------------------------------------------
    // 메뉴 조회
    // ------------------------------------------------------------

    /** 메뉴 표시용 전체 상품 목록 (방어적 복사). */
    public List<Product> getAvailableProducts() {
        return new ArrayList<>(inventory.listAll().values());
    }

    // ------------------------------------------------------------
    // 주문 사전 검증 (MainMenuView 의 "주문하기" 클릭 시점)
    // ------------------------------------------------------------

    /**
     * 주문 가능 여부만 검증한다 — 실제 결제/재고차감은 하지 않는다.
     *
     * <p>View 는 상품명만 알고 ID 는 모른다 (UX 단순화). 내부적으로
     * {@link #findProductByName} 으로 Product 를 찾아 검증한다.
     *
     * @param productName 사용자가 입력한 상품명
     * @param userName    주문자 이름 (현재는 로그 용도)
     * @return 상품이 존재하고 재고가 1 이상이면 true
     */
    public boolean createOrder(String productName, String userName) {
        Product product = findProductByName(productName);
        if (product == null) {
            return false;
        }
        if (product.getStock() <= 0) {
            System.out.println("[OrderController] 품절: " + product.getName());
            return false;
        }
        System.out.println("[OrderController] 주문 검증 통과: "
                         + product.getName() + " (요청자: " + userName + ")");
        return true;
    }

    // ------------------------------------------------------------
    // 결제 + 제조 (PaymentView 의 "결제하기" 클릭 시점)
    // ------------------------------------------------------------

    /**
     * 실제 주문 처리: 결제 → 재고 차감 → 대기열 등록.
     *
     * @param productName 결제할 상품의 이름
     * @param paymentType "CASH" / "CARD" / "POINT"
     * @param amount      현금일 때만 의미가 있는 투입 금액 (카드/포인트는 0)
     * @return 모든 단계가 성공하면 true
     */
    public boolean processPaymentAndManufacture(String productName, String paymentType, int amount) {
        // 1) 상품 조회 — View 는 이름만 알지만 Inventory 는 ID 키이므로 변환
        Product product = findProductByName(productName);
        if (product == null) {
            System.out.println("[OrderController] 존재하지 않는 상품: " + productName);
            return false;
        }

        // 2) 결제 수단 객체 생성 (View 가 직접 Payment 구현체를 알 필요 없도록 여기서 변환)
        Payment payment = newPayment(paymentType, amount);
        if (payment == null) {
            return false;
        }

        // 3) 결제 시도
        if (!paymentController.pay(payment, product.getPrice())) {
            return false;
        }

        // 4) 재고 차감 (내부 식별은 여전히 ID)
        try {
            inventoryController.reduceStock(product.getId(), 1);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 결제는 이미 성공했지만 재고가 없는 경우. 실 운영이라면 환불 처리 필요.
            System.out.println("[OrderController] 재고 차감 실패: " + e.getMessage());
            return false;
        }

        // 5) 주문 생성 후 대기열 등록 → MakerThread 가 깨어남
        Order order = new Order(product, payment);
        orderQueue.enqueue(order);
        System.out.println("[OrderController] 주문 접수: " + product.getName());
        return true;
    }

    /**
     * 상품명으로 Product 를 찾는다 (대소문자 구분, 정확 일치).
     * 시드 데이터는 이름이 모두 다르므로 첫 번째 매치를 반환한다.
     * @return 없으면 null
     */
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

    /**
     * paymentType 문자열을 실제 Payment 구현체로 변환한다.
     *
     * <p>지금은 데모용 고정값(카드 번호/한도, 보유 포인트) 으로 만든다.
     * 실 운영에서는 사용자 계정에서 가져올 값이다.
     */
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
                // 데모용 가상 카드 (실제로는 사용자 선택 카드를 받아야 함)
                return new CardPayment("1234567812345678", 100_000);
            case "POINT":
                // 데모용 가상 포인트 잔액
                return new PointPayment(50_000);
            default:
                System.out.println("[OrderController] 알 수 없는 결제 방식: " + paymentType);
                return null;
        }
    }

    // ------------------------------------------------------------
    // 주문 취소 (현재 view 에서는 노출 안 했지만 controller API 는 유지)
    // ------------------------------------------------------------

    /**
     * WAITING 상태 주문만 취소 + 재고 복구 + 큐에서 제거.
     *
     * <p>동시성: MakerThread 가 dequeue 직후 MAKING 으로 바꾸는 시점과 겹칠 수 있으므로
     * Order 객체를 락으로 잡아 상태 전이를 보호한다. 큐 제거는 락 보유를 짧게 하기 위해
     * 블록 밖에서.
     *
     * <p>큐에서 제거하지 않으면 MakerThread 가 CANCELED 인 주문을 꺼내 setStatus(MAKING)
     * 호출 시 잘못된 전이로 예외가 발생해 스레드 자체가 죽는다.
     */
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

    /**
     * 주문 번호로 큐에서 주문을 찾아 취소한다.
     * View 는 Order 객체가 아닌 ID 만 알고 있으므로 별도 메서드를 제공한다.
     *
     * @return 해당 ID 의 WAITING 주문을 찾아 취소했으면 true
     */
    public boolean cancelOrderById(int orderId) {
        for (Order o : orderQueue.peekAll()) {
            if (o.getOrderId() == orderId) {
                return cancelOrder(o);
            }
        }
        return false;
    }
}
