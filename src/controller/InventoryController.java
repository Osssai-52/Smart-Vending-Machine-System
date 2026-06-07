package controller;

import model.Inventory;

// 재고 차감/보충 컨트롤러
// 주문 시점에 재고 차감(reduceStock), 관리자 화면에서 재고 보충(addStock)

public class InventoryController {

    private final Inventory inventory;

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    // 주문 시 호출. 재고를 qty만큼 차감
    public void reduceStock(String productId, int qty) {
        validateQty(qty);
        inventory.reduceStock(productId, qty);
    }

    // 관리자 보충 시 호출. 재고를 qty만큼 더함
    public void addStock(String productId, int qty) {
        validateQty(qty);
        inventory.addStock(productId, qty);
    }

    private void validateQty(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("수량은 양수여야 합니다: " + qty); // qty가 0 이하인 경우
        }
    }
}
