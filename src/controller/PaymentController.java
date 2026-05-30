package controller;

import model.payment.Payment;

/**
 * 결제 처리 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>결제 요청의 입력값을 검증한다.</li>
 *   <li>실제 결제 수행은 Payment 인터페이스의 구현체에 위임한다 (다형성).</li>
 * </ul>
 *
 * <p>설계 의도(OCP):
 * 이 컨트롤러는 어떤 결제 수단(현금/카드/포인트)인지 알지 못한다.
 * 새 결제 수단이 추가되어도 본 클래스는 수정할 필요가 없다.
 *
 * <p>가정한 모델 시그니처:
 * <pre>
 *   boolean Payment.pay(int amount);   // 성공 시 true, 잔액 부족 등 실패 시 false
 * </pre>
 */
public class PaymentController {

    /**
     * 주어진 결제 수단으로 amount 만큼 결제를 시도한다.
     *
     * @param payment 결제 수단 (null 불가)
     * @param amount  결제 금액 (1 이상)
     * @return 결제 성공 여부
     */
    public boolean pay(Payment payment, int amount) {
        // 1) 입력 검증 — Controller 레벨에서 도메인 진입 전 차단
        if (payment == null) {
            System.out.println("[PaymentController] 결제 수단이 선택되지 않았습니다.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[PaymentController] 결제 금액은 양수여야 합니다: " + amount);
            return false;
        }

        // 2) 실제 결제는 다형성에 위임
        try {
            return payment.pay(amount);
        } catch (RuntimeException e) {
            // 결제 구현체에서 발생한 예외는 사용자에게 노출되지 않도록 여기서 흡수
            System.out.println("[PaymentController] 결제 처리 중 오류: " + e.getMessage());
            return false;
        }
    }
}
