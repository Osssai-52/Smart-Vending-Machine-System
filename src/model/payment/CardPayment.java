package model.payment;

// model/payment/CardPayment.java
// 카드 결제. Payment 구현체.
//
// 책임:
//  - 카드 번호/한도 등 카드 정보를 보관한다 (private).
//  - pay(amount) 호출 시 한도 검증 후 결제 승인/거절을 반환.
//
// 메모:
//  - 실제 PG 연동은 하지 않고, 한도 내라면 "승인" 으로 시뮬레이션한다.
