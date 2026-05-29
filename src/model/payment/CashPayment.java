package model.payment;

// model/payment/CashPayment.java
// 현금 결제. Payment 구현체.
//
// 책임:
//  - 투입된 금액(insertedAmount) 을 보관하고, pay(amount) 호출 시 차감한다.
//  - 잔액이 부족하면 false 반환. 거스름돈 계산 메서드를 추가로 제공할 수 있다.
//
// 상태(필드 예시):
//  - private int insertedAmount;
//
// 메서드 예시:
//  - public void insert(int money)
//  - public int  getChange()
//  - @Override boolean pay(int amount)
