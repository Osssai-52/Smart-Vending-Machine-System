package controller;

import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import model.SalesRepository;
import model.product.Product;

// 관리자 화면 컨트롤러
// 관리자 비밀번호 인증, 상품 전체 목록 조회 (재고표), 재고 보충 (입력 검증 후 InventoryController에 위임), 매출 내역 조회

public class AdminController {

    // 관리자 비밀번호
    private static final String ADMIN_PASSWORD = "admin1234";

    private final Inventory inventory;
    private final SalesRepository salesRepository;
    private final InventoryController inventoryController;

    public AdminController(Inventory inventory,SalesRepository salesRepository, InventoryController inventoryController) {
        this.inventory = inventory;
        this.salesRepository = salesRepository;
        this.inventoryController = inventoryController;
    }

    // 인증
    public boolean authenticate(String password) {
        if (password == null) return false;
        return ADMIN_PASSWORD.equals(password); // equals()로 문자열 비교 
    }

    // 상품 / 재고

    // 관리자 화면 재고표용
    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.listAll().values());
    }

    // 재고 보충 요청
    // 관리자가 상품명 입력하면 내부에서 상품명 → Product → ID 변환 후 InventoryController에 위임
    public boolean replenishInventory(String productName, String countText) { // countText: 수량 문자열 (숫자가 아니거나 음수면 false)
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

    // 상품명으로 Product 찾기
    private Product findProductByName(String productName) {
        for (Product p : inventory.listAll().values()) {
            if (productName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    // 매출 내역
    // sales.txt의 모든 레코드를 줄 단위로 합쳐 한 문자열로 반환
    public String loadSales() {
        List<String> records = salesRepository.loadAll();
        if (records == null || records.isEmpty()) {
            return "";
        }
        return String.join("\n", records);
    }
}
