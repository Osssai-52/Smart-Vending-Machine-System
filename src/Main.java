import javax.swing.SwingUtilities;

import controller.AdminController;
import controller.InventoryController;
import controller.MachineController;
import controller.OrderController;
import controller.PaymentController;
import model.Inventory;
import model.SalesRepository;
import model.order.OrderQueue;
import model.product.Coffee;
import model.product.Smoothie;
import model.product.Snack;
import model.product.Tea;
import view.MainMenuView;
import view.OrderStatusView;

/**
 * 프로그램 진입점.
 *
 * <p>역할은 "조립" 뿐:
 * <ol>
 *   <li>Model 객체 생성</li>
 *   <li>초기 메뉴 시드</li>
 *   <li>Controller 생성 (Model 을 생성자 주입)</li>
 *   <li>EDT 위에서 최상위 View (MainMenuView, OrderStatusView) 생성</li>
 *   <li>MakerThread 시작</li>
 *   <li>ShutdownHook 으로 MakerThread 깨끗이 정리</li>
 * </ol>
 *
 * <p>PaymentView / AdminView 는 사용자가 버튼을 누르는 시점에 MainMenuView 가 직접 만든다
 * → Main 의 책임에 포함되지 않는다.
 */
public class Main {

    public static void main(String[] args) {

        // 1) Model
        Inventory       inventory       = new Inventory();
        OrderQueue      orderQueue      = new OrderQueue();
        SalesRepository salesRepository = new SalesRepository();

        // 2) 초기 메뉴
        seedInventory(inventory);

        // 3) Controller (Model 을 생성자 주입)
        PaymentController   paymentController   = new PaymentController();
        InventoryController inventoryController = new InventoryController(inventory);
        OrderController     orderController     = new OrderController(
                inventory, orderQueue, paymentController, inventoryController);
        MachineController   machineController   = new MachineController(orderQueue, salesRepository);
        AdminController     adminController     = new AdminController(
                inventory, salesRepository, inventoryController);

        // 4) UI 는 EDT 에서
        SwingUtilities.invokeLater(() -> {
            // 메인 메뉴 (각 View 의 생성자에서 setVisible(true) 호출함)
            new MainMenuView(orderController, adminController);

            // 제조 대기 현황은 항상 띄워두고 콜백으로 갱신 → 매번 새 창 만들 필요 X
            new OrderStatusView(machineController);

            // 대기열 소비 시작
            machineController.startMaker();
        });

        // 5) 종료 시 MakerThread 정리 — 평가 4번 "안정성 및 자원 관리"
        Runtime.getRuntime().addShutdownHook(new Thread(machineController::stopMaker));
    }

    /** 부팅 시 1회 등록. DB 가 없으니 코드로 하드코딩. */
    private static void seedInventory(Inventory inventory) {
        // 음료 — 커피 (Coffee → Drink → Product 계층)
        inventory.addProduct(new Coffee("C01", "아메리카노",     2500, 10, 300, true,  2, false));
        inventory.addProduct(new Coffee("C02", "디카페인 라떼",  3500,  5, 300, true,  2, true));

        // 음료 — 차
        inventory.addProduct(new Tea("T01", "녹차", 2000, 10, 300, true, "녹차"));
        inventory.addProduct(new Tea("T02", "홍차", 2000, 10, 300, true, "홍차"));

        // 음료 — 스무디
        inventory.addProduct(new Smoothie("S01", "딸기 스무디",   4500, 5, 400, false, "딸기"));
        inventory.addProduct(new Smoothie("S02", "바나나 스무디", 4000, 5, 400, false, "바나나"));

        // 스낵 — Snack 은 Drink 를 거치지 않고 Product 직접 상속
        inventory.addProduct(new Snack("N01", "초콜릿 쿠키", 1500, 20));
        inventory.addProduct(new Snack("N02", "감자칩",      1800, 15));
    }
}
