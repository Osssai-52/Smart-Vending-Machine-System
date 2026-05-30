package model.product;

// 커피 음료. Drink를 상속한다.
// make() override → 원두 추출 + 우유/시럽 추가 시간만큼 Thread.sleep()으로 제조 지연 시뮬레이션.
// 커피만의 추가 속성(샷 수, decaf 여부)이 여기에 있다.
// 메서드 오버라이딩 → 다형성. MakerThread는 product.make()만 호출하면 된다.
public class Coffee extends Drink {

    private final int shots;     // 샷 수
    private final boolean decaf; // decaf 여부

    public Coffee(String id, String name, int price, int stock,
                  int ml, boolean isHot, int shots, boolean decaf) {
        super(id, name, price, stock, ml, isHot, 3000); // 커피 기본 제조시간 3초
        this.shots = shots;
        this.decaf = decaf;
    }

    @Override
    public void make() {
        System.out.println("[제조] " + getName() + " - "
                         + shots + "샷" + (decaf ? "(디카페인)" : "")
                         + " " + getTemperature() + " 추출 중...");
        try {
            Thread.sleep(getBrewTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[제조 완료] " + getName());
    }

    public int getShots()    { return shots; }
    public boolean isDecaf() { return decaf; }
}
