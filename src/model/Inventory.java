package model;

import model.product.Product;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// 자판기 재고 관리. 보고서의 Map<String, Product>.
// 정보 은닉: Map 자체를 외부에 노출하지 않는다 (unmodifiableMap으로 감싸 반환).
// 동시성: 여러 사용자/스레드가 동시에 접근할 수 있으므로 메서드를 synchronized로 보호.
// 다형성: Map<String, Product>으로 다양한 상품을 단일 타입으로 관리.
public class Inventory {

    // 상품 ID(String) → Product 매핑
    private final Map<String, Product> products = new HashMap<>();

    // 상품 등록 (초기 세팅 시 사용)
    public synchronized void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    // 관리자 재고 보충 (qty 0 이하 불가)
    public synchronized void addStock(String productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("보충 수량은 1 이상이어야 합니다.");
        }
        Product p = getProduct(productId);
        p.increaseStock(qty);
        System.out.println("[재고 보충] " + productId + " +" + qty
                         + "개 → 현재: " + p.getStock() + "개");
    }

    // 주문 시 재고 차감 (0 미만 불가, 부족 시 예외)
    public synchronized void reduceStock(String productId, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
        Product p = getProduct(productId);
        if (p.getStock() < qty) {
            throw new IllegalStateException(
                productId + " 재고 부족. 현재: " + p.getStock() + "개, 요청: " + qty + "개"
            );
        }
        for (int i = 0; i < qty; i++) p.decreaseStock();
        System.out.println("[재고 차감] " + productId + " -" + qty
                         + "개 → 현재: " + p.getStock() + "개");
    }

    // Product 조회 (없으면 예외)
    public synchronized Product getProduct(String productId) {
        Product p = products.get(productId);
        if (p == null) {
            throw new IllegalArgumentException("존재하지 않는 상품: " + productId);
        }
        return p;
    }

    // 메뉴 표시용 전체 상품 (불변 뷰 반환 — 정보 은닉)
    public synchronized Map<String, Product> listAll() {
        return Collections.unmodifiableMap(products);
    }
}
