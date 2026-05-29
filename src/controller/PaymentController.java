package controller;

// controller/PaymentController.java
// 결제 방식 선택 및 처리. 보고서 p.6.
//
// 주요 메서드:
//  - pay(Payment payment, int amount) : boolean
//      → 내부적으로 payment.pay(amount) 만 호출 (다형성 위임).
//      → 결제 성공/실패 결과를 반환.
//
// OOP 포인트:
//  - Payment 인터페이스에 위임만 한다 → 새로운 결제 수단이 추가돼도 이 컨트롤러는 수정 불필요 (OCP).
//
// 검증 책임:
//  - amount 가 음수/0 인지 같은 입력 검증은 여기서.
//  - 잔액 부족, 한도 초과 같은 도메인 규칙 검증은 각 Payment 구현체가 담당.
