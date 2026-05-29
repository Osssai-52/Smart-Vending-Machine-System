package model.order;

// model/order/OrderStatus.java
// 주문 상태를 표현하는 열거형.
//
// 값:
//  - WAITING : 결제 완료 후 제조 대기열에 들어간 상태
//  - MAKING  : MakerThread 가 꺼내서 제조 중
//  - DONE    : 제조 완료, 사용자에게 dispense 완료
//  - CANCELED: 사용자가 주문 취소
//
// OOP 포인트:
//  - 매직 문자열("waiting" 등) 대신 enum 으로 상태를 표현해 타입 안전성을 확보.
