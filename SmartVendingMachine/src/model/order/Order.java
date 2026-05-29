package model.order;

import model.payment.Payment;
import model.product.Product;
import java.time.LocalDateTime;

// 개별 주문 1건의 정보를 담는 객체.
// 데이터 + 자기 자신의 상태 전이만 책임진다.
// 모든 필드 private final (status만 변경 가능하므로 private).
// 외부에서 임의로 상태를 바꾸지 못하게 한다.
public class Order {

    private static int idCounter = 1;

    private final int orderId;
    private final Product product;   // 어떤 상품을 주문했는가
    private final Payment payment;   // 어떤 결제 수단으로 결제했는가
    private final LocalDateTime createdAt; // 주문 시각 (매출 기록에 사용)
    private OrderStatus status;      // 현재 주문 상태 (WAITING → MAKING → DONE)

    public Order(Product product, Payment payment) {
        this.orderId   = idCounter++;
        this.product   = product;
        this.payment   = payment;
        this.createdAt = LocalDateTime.now();
        this.status    = OrderStatus.WAITING;
    }

    // 유효한 전이만 허용 (예: DONE → WAITING 불가)
    public void setStatus(OrderStatus newStatus) {
        if (!isValidTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                "잘못된 상태 전이: " + this.status + " → " + newStatus
            );
        }
        this.status = newStatus;
    }

    // 허용된 상태 전이 규칙
    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        switch (from) {
            case WAITING:  return to == OrderStatus.MAKING   || to == OrderStatus.CANCELED;
            case MAKING:   return to == OrderStatus.DONE     || to == OrderStatus.CANCELED;
            case DONE:     return false;
            case CANCELED: return false;
            default:       return false;
        }
    }

    // Getter
    public int getOrderId()              { return orderId; }
    public Product getProduct()          { return product; }
    public Payment getPayment()          { return payment; }
    public OrderStatus getStatus()       { return status; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    @Override
    public String toString() {
        return "[주문 #" + orderId + "] " + product.getName()
             + " (" + product.getPrice() + "원) - " + status;
    }
}
