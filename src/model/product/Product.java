package model.product;

// model/product/Product.java
// 모든 판매 상품의 공통 추상 타입.
//
// 책임:
//  - 상품의 식별자/이름/가격/재고수량 등 공통 상태를 캡슐화한다 (필드 private + getter).
//  - 모든 상품이 공유하는 공통 동작 dispense() 를 일반 메서드로 구현한다.
//      dispense() 는 "상품을 외부로 내보내는" 동작으로, 모든 상품에 동일하게 적용된다.
//  - 상품별로 동작이 달라지는 make() 는 abstract 로 선언하여 자식 클래스에서 override 하게 한다.
//
// OOP 포인트:
//  - 추상화: 공통 인터페이스(dispense / make)를 부모가 제공하고, 구체 구현은 자식이 채운다.
//  - 다형성: Controller / Inventory 는 Product 타입으로만 다루고 실제 동작은 런타임에 결정된다.
//  - 정보 은닉: id, name, price, stock 은 private, 외부 변경은 메서드로만 허용.
//
// 예상 필드:
//  - private final String id;
//  - private final String name;
//  - private final int    price;
//  - private int          stock;   // Inventory 가 add/reduce 로 변경
//
// 예상 메서드:
//  - public final void dispense()         : 공통 동작 (재고 1 감소 + 콘솔/로그 출력)
//  - public abstract void make()          : 음료/스낵별로 다른 제조 동작
//  - getters / 재고 변경 protected 메서드
