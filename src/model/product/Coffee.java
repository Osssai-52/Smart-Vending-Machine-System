package model.product;

// model/product/Coffee.java
// 커피 음료. Drink 를 상속한다.
//
// 책임:
//  - make() 를 override 하여 커피 제조 동작을 정의한다.
//    예) 원두 추출 + 우유/시럽 추가 시간만큼 Thread.sleep(...) 하여 제조 지연을 시뮬레이션.
//  - 커피만의 추가 속성(예: 샷 수, decaf 여부)이 있다면 여기에 둔다.
//
// OOP 포인트:
//  - 메서드 오버라이딩 → 다형성. MakerThread 는 product.make() 만 호출하면 된다.
