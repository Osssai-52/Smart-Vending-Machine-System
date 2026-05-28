package model;

// model/Inventory.java
// 자판기 재고 관리. 보고서의 Map<String, Product>.
//
// 책임:
//  - 상품 ID(String) → Product 매핑을 보관한다.
//  - addStock(String productId, int qty)    : 관리자 재고 보충
//  - reduceStock(String productId, int qty) : 주문 시 재고 차감 (0 미만 불가, 부족 시 예외)
//  - getProduct(String productId)            : Product 조회
//  - listAll()                                : 메뉴 표시용 전체 상품 (불변 뷰 반환)
//
// 동시성:
//  - 여러 사용자/스레드가 동시에 접근할 수 있으므로 메서드를 synchronized 로 보호하거나
//    ConcurrentHashMap 사용. (수업 요구상 synchronized 직접 사용을 권장)
//
// OOP 포인트:
//  - 컬렉션/제네릭: Map<String, Product> 으로 다양한 상품을 단일 타입으로 관리 (다형성).
//  - 정보 은닉: Map 자체를 외부에 노출하지 않는다 (Collections.unmodifiableMap 으로 감싸 반환).
