package model.product;

// 모든 판매 상품의 공통 추상 타입.
// id, name, price, stock 은 private, 외부 변경은 메서드로만 허용.
// dispense()는 일반 메서드 (모든 상품 동일), make()는 abstract (자식이 override).
public abstract class Product {

    private final String id;
    private final String name;
    private final int price;
    private int stock;  // Inventory가 add/reduce로 변경

    public Product(String id, String name, int price, int stock) {
        this.id    = id;
        this.name  = name;
        this.price = price;
        this.stock = stock;
    }

    // 공통 동작: 상품을 외부로 내보내는 동작 (재고 1 감소 + 콘솔 출력)
    // MakerThread에서 DONE 직전에 호출
    public final void dispense() {
        decreaseStock();
        System.out.println("[배출] " + name + " 이(가) 나왔습니다.");
    }

    // 상품별로 동작이 달라지는 제조 동작 — 자식 클래스에서 override
    public abstract void make();

    // Getter
    public String getId()   { return id; }
    public String getName() { return name; }
    public int getPrice()   { return price; }
    public int getStock()   { return stock; }

    // 재고 변경은 외부 직접 호출 비권장 — 반드시 Inventory 를 통해서만 호출할 것.
    // (Inventory 가 다른 패키지(model)에 있어 protected 로 두면 cross-package 접근이 막힌다.
    //  따라서 public 으로 두되, 호출 위치는 Inventory 한 곳으로 제한한다.)
    public void decreaseStock() {
        if (stock > 0) stock--;
    }

    public void increaseStock(int amount) {
        stock += amount;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " (" + price + "원) - 재고: " + stock;
    }
}
