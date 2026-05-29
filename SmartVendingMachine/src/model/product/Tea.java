package model.product;

// 차(Tea) 음료. Drink를 상속한다.
// make() override → 티백 우림 등 차 제조 동작 정의.
// 종류(녹차/홍차 등) 같은 속성을 두어도 좋다.
public class Tea extends Drink {

    private final String teaType; // 종류 (예: 녹차, 홍차)

    public Tea(String id, String name, int price, int stock,
               int ml, boolean isHot, String teaType) {
        super(id, name, price, stock, ml, isHot, 2500); // 차 기본 제조시간 2.5초
        this.teaType = teaType;
    }

    @Override
    public void make() {
        System.out.println("[제조] " + getName() + " - "
                         + teaType + " 티백 " + getTemperature() + " 우리는 중...");
        try {
            Thread.sleep(getBrewTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[제조 완료] " + getName());
    }

    public String getTeaType() { return teaType; }
}
