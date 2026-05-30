package controller;

import model.Inventory;
import model.order.Order;
import model.order.OrderQueue;
import model.order.OrderStatus;
import model.payment.Payment;
import model.product.Product;

/**
 * 주문 생성/취소 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>주문 요청을 받아 [상품 조회 → 결제 → 재고 차감 → 대기열 등록] 순서로 처리한다.</li>
 *   <li>WAITING 상태인 주문에 한해 취소를 처리하고 재고를 복구한다.</li>
 * </ul>
 *
 * <p>의존성 (생성자 주입):
 * <ul>
 *   <li>{@link Inventory}            : 상품 조회</li>
 *   <li>{@link OrderQueue}           : 결제된 주문을 제조 대기열에 등록</li>
 *   <li>{@link PaymentController}    : 결제 위임</li>
 *   <li>{@link InventoryController}  : 재고 차감/복구 위임</li>
 * </ul>
 *
 * <p>주요 예외 흐름:
 * <ul>
 *   <li>{@code Inventory.getProduct} 는 없는 상품이면 {@link IllegalArgumentException} 을 던진다.</li>
 *   <li>{@code Inventory.reduceStock} 는 재고 부족이면 {@link IllegalStateException} 을 던진다.</li>
 *   <li>본 컨트롤러는 두 예외를 모두 잡아 사용자에게는 단순 실패(null)로만 보고한다.</li>
 * </ul>
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

    /**
     * 주문 생성 요청을 처리한다.
     *
     * <p>흐름:
     * <ol>
     *   <li>입력값 검증</li>
     *   <li>상품 조회 (없으면 실패)</li>
     *   <li>결제 시도 (실패 시 종료)</li>
     *   <li>재고 차감 (실패 시 종료 — 본 프로젝트 범위에선 결제 환불은 생략)</li>
     *   <li>{@link Order} 생성 → {@link OrderQueue} 에 enqueue</li>
     * </ol>
     *
     * @return 생성된 Order. 실패 시 null.
     */
    public Order createOrder(String productId, Payment payment) {
        // 1) 입력 검증
        if (productId == null || productId.isEmpty()) {
            System.out.println("[OrderController] 상품 ID 가 비어 있습니다.");
            return null;
        }
        if (payment == null) {
            System.out.println("[OrderController] 결제 수단이 선택되지 않았습니다.");
            return null;
        }

        // 2) 상품 조회 — 존재하지 않으면 Inventory 가 IllegalArgumentException 을 던진다.
        Product product;
        try {
            product = inventory.getProduct(productId);
        } catch (IllegalArgumentException e) {
            System.out.println("[OrderController] " + e.getMessage());
            return null;
        }

        // 3) 결제
        boolean paid = paymentController.pay(payment, product.getPrice());
        if (!paid) {
            System.out.println("[OrderController] 결제 실패: " + product.getName());
            return null;
        }

        // 4) 재고 차감
        try {
            inventoryController.reduceStock(productId, 1);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 결제는 이미 성공했지만 재고가 없어 진행 불가.
            // 실 운영이라면 결제 환불(보상 트랜잭션)이 필요하지만 본 과제 범위에서는 로그만 남긴다.
            System.out.println("[OrderController] 재고 차감 실패로 주문 취소: " + e.getMessage());
            return null;
        }

        // 5) 주문 생성 후 대기열 등록
        Order order = new Order(product, payment);
        orderQueue.enqueue(order);
        System.out.println("[OrderController] 주문 생성 완료: " + product.getName());
        return order;
    }

    /**
     * 주문 취소 요청을 처리한다.
     * 아직 제조가 시작되지 않은 (WAITING) 주문만 취소할 수 있다.
     *
     * <p>동시성: MakerThread 가 dequeue 직후 상태를 MAKING 으로 바꾸는 시점과
     * 사용자가 취소 버튼을 누르는 시점이 겹칠 수 있다. Order 객체를 락으로 잡아
     * "상태 확인 → 변경" 사이를 보호한다.
     *
     * @return 취소 성공 여부
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

        // 재고 복구 — 락 보유 시간을 최소화하기 위해 동기화 블록 밖에서 호출.
        inventoryController.addStock(order.getProduct().getId(), 1);
        System.out.println("[OrderController] 주문 취소 완료: " + order.getProduct().getName());
        return true;
    }
}
