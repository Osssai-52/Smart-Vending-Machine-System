package model.order;

// model/order/Order.java
// 개별 주문 1건의 정보를 담는 객체.
//
// 책임 (주제선정보고서 p.4 그대로):
//  - product : 어떤 상품을 주문했는가 (Product)
//  - payment : 어떤 결제 수단으로 결제했는가 (Payment)
//  - status  : 현재 주문 상태 (OrderStatus, WAITING → MAKING → DONE)
//  - createdAt : 주문 시각 (선택, 매출 기록에 사용)
//
// 책임 분리:
//  - Order 는 "데이터 + 자기 자신의 상태 전이"만 책임진다.
//    setStatus(OrderStatus s) 는 유효한 전이만 허용 (예: DONE → WAITING 불가).
//  - 제조 동작은 product.make() 가, 결제 동작은 payment.pay() 가 담당한다.
//
// 캡슐화:
//  - 모든 필드는 private final (또는 status 만 private).
//  - 외부에서 임의로 상태를 바꾸지 못하게 한다.
