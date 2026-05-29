package model.product;

// model/product/Drink.java
// 음료(Drink)의 추상 타입. Product 를 상속한다.
//
// 책임:
//  - 음료 공통 속성(예: 용량 ml, 온도 hot/cold, 제조 소요시간 등)을 모은다.
//  - make() 는 여전히 abstract — 구체 음료(Coffee/Tea/Smoothie)가 자신만의 제조 과정을 정의한다.
//
// OOP 포인트:
//  - 상속 계층의 중간 추상 클래스. Product → Drink → Coffee/Tea/Smoothie 로 이어지는 트리에서
//    "음료라면 공통적으로 필요한 것"만 여기에 둔다. (Snack 과는 분리)
//  - 다형성: 컨트롤러는 Drink 인지 Snack 인지 구분하지 않고 Product 로 다룬다.
