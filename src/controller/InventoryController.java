package controller;

// controller/InventoryController.java
// 재고 차감 / 보충 요청 처리. 보고서 p.6.
//
// 주요 메서드:
//  - reduceStock(String productId, int qty) : 주문 시 호출. 재고 부족이면 실패.
//  - addStock(String productId, int qty)    : 관리자가 호출. 음수 거부.
//
// 책임 분리:
//  - 실제 데이터 조작은 Inventory(Model) 가 수행.
//  - InventoryController 는 입력 검증 + 정책 결정 + View 갱신 트리거(예: refreshMenuDisplay) 까지만.
//
// 예외 처리:
//  - 재고 부족 시 IllegalStateException 같은 도메인 예외를 잡아
//    View 에 사용자 친화적 메시지로 변환해 전달.
