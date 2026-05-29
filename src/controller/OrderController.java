package controller;

// controller/OrderController.java
// 주문 생성 / 취소를 총괄한다. 보고서 p.6 의 OrderController.
//
// 주요 메서드 (보고서 p.6):
//  - createOrder(String productId, Payment payment)
//      1) Inventory 에서 productId 로 Product 를 찾는다 (없으면 실패 반환).
//      2) PaymentController.pay(payment, product.getPrice()) 로 결제 시도.
//      3) 결제 성공 시 InventoryController.reduceStock(productId, 1).
//      4) Order 를 생성하여 OrderQueue.enqueue(order).
//      5) View 에 결과(성공/실패) 알림.
//  - cancelOrder(Order order)
//      - 아직 WAITING 상태인 주문만 취소 가능.
//      - 재고 복구 + 결제 환불 처리 호출.
//
// 의존성:
//  - Inventory, OrderQueue (Model)
//  - PaymentController, InventoryController (다른 컨트롤러)
//  - 생성자 주입으로 받기 — new 로 내부에서 만들지 않는다 (테스트 용이성, 결합도 ↓).
//
// 단방향 의존 (보고서 p.7):
//   View → Controller → Model 만 허용.
