package controller;

import model.payment.Payment;

// 결제 처리 컨트롤러
// 결제 요청의 입력값 검증. PaymentController는 어떤 결제 수단인지 알지 못함 → 새 결제 수단이 추가되어도 PaymentController는 수정 불필요

public class PaymentController { // 주어진 결제 수단으로 amount만큼 결제 시도

    public boolean pay(Payment payment, int amount) { // payment: 결제 수단, amount: 결제 금액. 성공 시 true, 잔액 부족 등의 이유로 실패 시 false
        // 입력 검증 — Controller 레벨에서 도메인 진입 전 차단
        if (payment == null) {
            System.out.println("[PaymentController] 결제 수단이 선택되지 않았습니다.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[PaymentController] 결제 금액은 양수여야 합니다: " + amount);
            return false;
        }

        // 실제 결제는 다형성에 위임
        try {
            return payment.pay(amount);
        } catch (RuntimeException e) {
            // 결제 구현체에서 발생한 예외는 사용자에게 노출되지 않도록 함
            System.out.println("[PaymentController] 결제 처리 중 오류: " + e.getMessage());
            return false;
        }
    }
}
