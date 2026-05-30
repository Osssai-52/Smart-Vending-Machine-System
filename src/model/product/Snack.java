package model.product;

// 스낵. Product를 직접 상속한다 (음료가 아니므로 Drink를 거치지 않는다).
// make() override → 스낵은 별도 제조가 필요 없으므로 즉시 완료 처리하거나
//                   아주 짧은 sleep으로 dispense 직전 단계를 표현한다.
// 같은 추상 메서드 make()라도 음료와 구현이 전혀 다른 점에서 다형성을 명확히 보여준다.
public class Snack extends Product {

    public Snack(String id, String name, int price, int stock) {
        super(id, name, price, stock);
    }

    @Override
    public void make() {
        System.out.println("[제조] " + getName() + " - 포장 확인 중...");
        try {
            Thread.sleep(500); // 스낵은 짧은 준비 시간만 (0.5초)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[제조 완료] " + getName());
    }
}
