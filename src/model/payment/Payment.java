package model.payment;

// model/payment/Payment.java
// 결제 수단의 공통 인터페이스.
//
// 책임:
//  - "결제한다" 라는 행위만 추상으로 선언한다.  boolean pay(int amount);
//  - 구체 결제 수단(CashPayment, CardPayment, PointPayment)이 이 인터페이스를 구현한다.
//
// OOP 포인트:
//  - 인터페이스 + 다형성: PaymentController.pay() 는 Payment 타입만 알면 되고,
//    실제로 어떤 결제 수단인지 알 필요가 없다 → 결합도 ↓.
//  - 새로운 결제 수단(예: QR Pay) 을 추가해도 PaymentController 코드는 수정할 필요가 없다 (OCP).
//
// 시그니처 제안:
//  - boolean pay(int amount);   // 성공 여부 반환 (잔액 부족, 한도 초과 등 실패 표현)
