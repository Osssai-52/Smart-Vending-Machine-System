package controller;

import model.Inventory;

/**
 * 재고 차감/보충 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>주문 시점에 재고를 차감한다 ({@link #reduceStock}).</li>
 *   <li>관리자 화면에서 재고를 보충한다 ({@link #addStock}).</li>
 * </ul>
 *
 * <p>설계 메모:
 * 화면 갱신은 호출 측 View 가 직접 책임진다 (예: AdminView.refreshInventoryTable,
 * PaymentView 의 parentView.refreshMenuDisplay). 따라서 본 Controller 는
 * View 를 알 필요가 없다 — 결합도가 낮아진다.
 */
public class InventoryController {

    private final Inventory inventory;

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * 주문 시 호출. 재고를 qty 만큼 차감한다.
     * @throws IllegalArgumentException qty 가 0 이하 또는 존재하지 않는 productId 인 경우
     * @throws IllegalStateException    재고가 부족한 경우
     */
    public void reduceStock(String productId, int qty) {
        validateQty(qty);
        inventory.reduceStock(productId, qty);
    }

    /**
     * 관리자 보충 시 호출. 재고를 qty 만큼 더한다.
     * @throws IllegalArgumentException qty 가 0 이하 또는 존재하지 않는 productId 인 경우
     */
    public void addStock(String productId, int qty) {
        validateQty(qty);
        inventory.addStock(productId, qty);
    }

    private void validateQty(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("수량은 양수여야 합니다: " + qty);
        }
    }
}
