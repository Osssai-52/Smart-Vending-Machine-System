package model.product;

// model/product/Snack.java
// 스낵. Product 를 직접 상속한다 (음료가 아니므로 Drink 를 거치지 않는다).
//
// 책임:
//  - make() override → 스낵은 별도 제조가 필요 없으므로 즉시 완료 처리하거나
//    아주 짧은 sleep 으로 dispense 직전 단계를 표현한다.
//
// OOP 포인트:
//  - 같은 추상 메서드 make() 라도 음료와 스낵의 구현이 전혀 다르다는 점에서 다형성을 명확히 보여준다.
