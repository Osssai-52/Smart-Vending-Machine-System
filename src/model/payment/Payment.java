package model.payment;

// 결제 수단의 공통 인터페이스.
// "결제한다"라는 행위만 추상으로 선언.
// PaymentController는 Payment 타입만 알면 되고,
// 실제로 어떤 결제 수단인지 알 필요 없음 → 결합도 ↓ (OCP).
public interface Payment {

    // 성공 여부 반환 (잔액 부족, 한도 초과 등 실패 표현)
    boolean pay(int amount);
}
