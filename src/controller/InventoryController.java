package controller;

import javax.swing.SwingUtilities;

import model.Inventory;
import view.MainMenuView;

/**
 * 재고 차감/보충 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>주문 시점에 재고를 차감한다 ({@link #reduceStock}).</li>
 *   <li>관리자 화면에서 재고를 보충한다 ({@link #addStock}).</li>
 *   <li>재고 변경 후 MainMenuView 가 등록되어 있으면 화면 갱신을 트리거한다.</li>
 * </ul>
 *
 * <p>View 참조는 setter 로 주입한다.
 * View 가 아직 만들어지지 않은 시점에도 컨트롤러를 단독으로 동작/테스트할 수 있도록 하기 위함이다.
 *
 * <p>가정한 모델 시그니처:
 * <pre>
 *   void Inventory.reduceStock(String productId, int qty);   // 재고 부족 시 IllegalStateException
 *   void Inventory.addStock(String productId, int qty);
 * </pre>
 *
 * <p>가정한 뷰 시그니처:
 * <pre>
 *   void MainMenuView.refreshMenuDisplay();   // EDT 에서 호출되어야 안전
 * </pre>
 */
public class InventoryController {

    private final Inventory inventory;
    private MainMenuView mainMenuView;   // 선택적 — null 허용

    public InventoryController(Inventory inventory) {
        this.inventory = inventory;
    }

    /** Main 또는 View 가 자기 자신을 등록할 때 호출. */
    public void setMainMenuView(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
    }

    /**
     * 주문 시 호출. 재고를 qty 만큼 차감한다.
     *
     * @throws IllegalArgumentException qty 가 0 이하인 경우
     * @throws IllegalStateException    재고가 부족한 경우 (Inventory 가 던짐)
     */
    public void reduceStock(String productId, int qty) {
        validateQty(qty);
        inventory.reduceStock(productId, qty);
        refreshMenuDisplay();
    }

    /**
     * 관리자 보충 시 호출. 재고를 qty 만큼 더한다.
     *
     * @throws IllegalArgumentException qty 가 0 이하인 경우
     */
    public void addStock(String productId, int qty) {
        validateQty(qty);
        inventory.addStock(productId, qty);
        refreshMenuDisplay();
    }

    // ------------------------------------------------------------
    // 내부 헬퍼
    // ------------------------------------------------------------

    private void validateQty(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("수량은 양수여야 합니다: " + qty);
        }
    }

    /**
     * 메뉴 화면의 재고 표시를 다시 그린다.
     * View 가 등록되지 않은 경우 아무 일도 하지 않는다.
     * GUI 갱신은 항상 EDT 에서 수행해야 한다 (강의 Week 11).
     */
    private void refreshMenuDisplay() {
        if (mainMenuView == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> mainMenuView.refreshMenuDisplay());
    }
}
