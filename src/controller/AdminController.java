package controller;

import java.util.List;

import javax.swing.SwingUtilities;

import model.Inventory;
import model.SalesRepository;
import view.AdminView;

/**
 * 관리자 화면 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>관리자 비밀번호 인증</li>
 *   <li>매출 내역(sales.txt) 조회 후 AdminView 에 표시</li>
 * </ul>
 *
 * <p>설계 메모:
 * 재고 보충은 {@link InventoryController#addStock} 로 위임하여 책임 중복을 막는다.
 * 본 컨트롤러는 "관리자 화면 전용 흐름" 만 책임진다.
 *
 * <p>가정한 뷰 시그니처:
 * <pre>
 *   void AdminView.displaySales(List&lt;String&gt; records);
 * </pre>
 *
 * <p>참고: {@link SalesRepository#loadAll()} 은 자체적으로 IOException 을 catch 하고
 * 빈 리스트를 반환하도록 구현되어 있으므로, 본 컨트롤러는 별도 try-catch 가 필요 없다.
 */
public class AdminController {

    /**
     * 데모용 관리자 비밀번호.
     * 실제 시스템이라면 외부 설정 파일 + 해시 저장이 필요하지만 본 프로젝트 범위에서는 상수로 둔다.
     */
    private static final String ADMIN_PASSWORD = "admin1234";

    private final Inventory inventory;
    private final SalesRepository salesRepository;
    private AdminView adminView;   // setter 로 주입 — null 허용

    public AdminController(Inventory inventory, SalesRepository salesRepository) {
        this.inventory = inventory;
        this.salesRepository = salesRepository;
    }

    public void setAdminView(AdminView adminView) {
        this.adminView = adminView;
    }

    /**
     * 관리자 비밀번호를 검증한다.
     * 문자열 비교는 반드시 {@link String#equals} 사용 (강의 Week 3).
     */
    public boolean authenticate(String password) {
        if (password == null) {
            return false;
        }
        return ADMIN_PASSWORD.equals(password);
    }

    /**
     * 매출 내역을 파일에서 읽어 반환한다.
     */
    public List<String> loadSales() {
        return salesRepository.loadAll();
    }

    /**
     * 관리자 화면을 다시 그린다.
     * 파일 I/O 후 결과를 EDT 에서 화면에 반영한다.
     */
    public void refreshAdminView() {
        if (adminView == null) {
            return;
        }
        List<String> records = loadSales();
        SwingUtilities.invokeLater(() -> adminView.displaySales(records));
    }
}
