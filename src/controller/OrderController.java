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

    public OrderController(Inventory inventory,
                           OrderQueue orderQueue,
                           PaymentController paymentController,
                           InventoryController inventoryController) {
        this.inventory = inventory;
        this.orderQueue = orderQueue;
        this.paymentController = paymentController;
        this.inventoryController = inventoryController;
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
     * @param productId 상품 ID
     * @param userName  주문자 이름 (현재는 로그 용도)
     * @return 상품이 존재하고 재고가 1 이상이면 true
     */
    public boolean createOrder(String productId, String userName) {
        if (productId == null || productId.isEmpty()) {
            return false;
        }
        try {
            Product product = inventory.getProduct(productId);
            if (product.getStock() <= 0) {
                System.out.println("[OrderController] 품절: " + product.getName());
                return false;
            }
            System.out.println("[OrderController] 주문 검증 통과: "
                             + product.getName() + " (요청자: " + userName + ")");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("[OrderController] " + e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------
    // 결제 + 제조 (PaymentView 의 "결제하기" 클릭 시점)
    // ------------------------------------------------------------

    /**
     * 실제 주문 처리: 결제 → 재고 차감 → 대기열 등록.
     *
     * @param productId   결제할 상품 ID
     * @param paymentType "CASH" / "CARD" / "POINT"
     * @param amount      현금일 때만 의미가 있는 투입 금액 (카드/포인트는 0)
     * @return 모든 단계가 성공하면 true
     */
    public boolean processPaymentAndManufacture(String productId, String paymentType, int amount) {
        // 1) 상품 조회 (없으면 실패)
        Product product;
        try {
            product = inventory.getProduct(productId);
        } catch (IllegalArgumentException e) {
            System.out.println("[OrderController] " + e.getMessage());
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

        // 4) 재고 차감
        try {
            inventoryController.reduceStock(productId, 1);
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
     * WAITING 상태 주문만 취소 + 재고 복구.
     * MakerThread 가 dequeue 직후 MAKING 으로 바꾸는 시점과 겹칠 수 있으므로
     * Order 객체를 락으로 잡아 상태 전이를 보호한다.
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
        inventoryController.addStock(order.getProduct().getId(), 1);
        System.out.println("[OrderController] 주문 취소 완료: " + order.getProduct().getName());
        return true;
    }
}
