package model.product;

// 음료(Drink)의 추상 타입. Product를 상속한다.
// 음료 공통 속성(용량 ml, 온도 hot/cold, 제조 소요시간)을 모은다.
// make()는 여전히 abstract — Coffee/Tea/Smoothie가 자신만의 제조 과정을 정의한다.
// 컨트롤러는 Drink인지 Snack인지 구분하지 않고 Product로 다룬다 (다형성).
public abstract class Drink extends Product {

    private final int ml;        // 용량
    private final boolean isHot; // 온도 hot/cold
    private final int brewTime;  // 제조 소요시간 (ms)

    public Drink(String id, String name, int price, int stock,
                 int ml, boolean isHot, int brewTime) {
        super(id, name, price, stock);
        this.ml       = ml;
        this.isHot    = isHot;
        this.brewTime = brewTime;
    }

    public int getMl()       { return ml; }
    public boolean isHot()   { return isHot; }
    public int getBrewTime() { return brewTime; }

    public String getTemperature() {
        return isHot ? "뜨겁게" : "차갑게";
    }
}
