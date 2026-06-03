package controller;

import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import model.SalesRepository;
import model.product.Product;

/**
 * 관리자 화면 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>관리자 비밀번호 인증.</li>
 *   <li>상품 전체 목록 조회 (재고표).</li>
 *   <li>재고 보충 — 입력 검증 후 {@link InventoryController#addStock} 에 위임.</li>
 *   <li>매출 내역 조회 — 파일에서 읽어 멀티라인 문자열로 반환.</li>
 * </ul>
 *
 * <p>설계 메모:
 * 재고 보충 자체는 InventoryController 의 책임이지만, "입력 문자열 검증"
 * (숫자 여부, 음수 거부 등) 까지 InventoryController 가 알게 하면 책임이 흐려진다.
 * 그래서 AdminController 가 wrapper 로 받아 검증 후 위임한다.
 */
public class AdminController {

    /** 데모용 관리자 비밀번호 — 실 운영이면 외부 설정/해시 저장 필요. */
    private static final String ADMIN_PASSWORD = "admin1234";

    private final Inventory inventory;
    private final SalesRepository salesRepository;
    private final InventoryController inventoryController;

    public AdminController(Inventory inventory,
                           SalesRepository salesRepository,
                           InventoryController inventoryController) {
        this.inventory = inventory;
        this.salesRepository = salesRepository;
        this.inventoryController = inventoryController;
    }

    // ------------------------------------------------------------
    // 인증
    // ------------------------------------------------------------

    /** 문자열 비교는 반드시 equals() — 강의 Week 3. */
    public boolean authenticate(String password) {
        if (password == null) return false;
        return ADMIN_PASSWORD.equals(password);
    }

    // ------------------------------------------------------------
    // 상품 / 재고
    // ------------------------------------------------------------

    /** 관리자 화면 재고표용 — 방어적 복사. */
    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.listAll().values());
    }

    /**
     * 재고 보충 요청.
     *
     * <p>관리자도 사용자와 동일하게 상품명을 입력한다 (UI 일관성).
     * 내부에서 상품명 → Product → ID 변환 후 InventoryController 에 위임.
     *
     * @param productName 상품명 (빈 문자열/null 거부)
     * @param countText   수량 문자열 (숫자 아니거나 음수면 false)
     * @return 보충 성공 여부
     */
    public boolean replenishInventory(String productName, String countText) {
        if (productName == null || productName.isEmpty()) {
            return false;
        }
        int qty;
        try {
            qty = Integer.parseInt(countText.trim());
        } catch (NumberFormatException e) {
            System.out.println("[AdminController] 보충 수량이 숫자가 아닙니다: " + countText);
            return false;
        }
        if (qty <= 0) {
            System.out.println("[AdminController] 보충 수량은 1 이상이어야 합니다: " + qty);
            return false;
        }

        Product product = findProductByName(productName);
        if (product == null) {
            System.out.println("[AdminController] 존재하지 않는 상품: " + productName);
            return false;
        }
        try {
            inventoryController.addStock(product.getId(), qty);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("[AdminController] " + e.getMessage());
            return false;
        }
    }

    /** 상품명으로 Product 를 찾는다 (정확 일치). 없으면 null. */
    private Product findProductByName(String productName) {
        for (Product p : inventory.listAll().values()) {
            if (productName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    // ------------------------------------------------------------
    // 매출 내역
    // ------------------------------------------------------------

    /**
     * sales.txt 의 모든 레코드를 줄 단위로 합쳐 한 문자열로 반환한다.
     * 파일이 없거나 비어 있으면 빈 문자열.
     */
    public String loadSales() {
        List<String> records = salesRepository.loadAll();
        if (records == null || records.isEmpty()) {
            return "";
        }
        return String.join("\n", records);
    }
}
