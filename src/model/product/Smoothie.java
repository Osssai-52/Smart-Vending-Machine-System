package model.product;

// 스무디 음료. Drink를 상속한다.
// make() override → 과일 블렌딩 등 스무디 제조 동작 정의.
// 베이스 과일 등 추가 속성을 둘 수 있다.
public class Smoothie extends Drink {

    private final String baseFruit; // 베이스 과일 (예: 딸기, 바나나)

    public Smoothie(String id, String name, int price, int stock,
                    int ml, boolean isHot, String baseFruit) {
        super(id, name, price, stock, ml, isHot, 20000); // 스무디 기본 제조시간 20초
        this.baseFruit = baseFruit;
    }

    @Override
    public void make() {
        System.out.println("[제조] " + getName() + " - "
                         + baseFruit + " 블렌딩 중...");
        try {
            Thread.sleep(getBrewTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[제조 완료] " + getName());
    }

    public String getBaseFruit() { return baseFruit; }
}
