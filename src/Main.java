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

// 프로그램 진입점
// M, V, C를 조립하는 역할

public class Main {
    public static void main(String[] args) {

        // 1) Model 객체 생성
        Inventory inventory = new Inventory();
        OrderQueue orderQueue = new OrderQueue();
        SalesRepository salesRepository = new SalesRepository();

        // 2) 초기 메뉴
        seedInventory(inventory);

        // 3) Controller 생성 (Model을 생성자 주입)
        // OrderController가 취소 시 View에 broadcast하기 위해 MachineController를 받으므로 MachineController를 먼저 만듦
        PaymentController paymentController = new PaymentController();
        InventoryController inventoryController = new InventoryController(inventory);
        MachineController machineController = new MachineController(orderQueue, salesRepository);
        OrderController orderController = new OrderController(inventory, orderQueue, paymentController, inventoryController, machineController);
        AdminController adminController = new AdminController(inventory, salesRepository, inventoryController);

        // 4) UI는 EDT 위에서 최상위 View (MainMenuView, OrderStatusView) 생성
        SwingUtilities.invokeLater(() -> { // Swing 컴포넌트는 EDT에서만 안전하게 생성/조작 가능 (thread-safe 아님)
            // 메인 메뉴 (각 View 의 생성자에서 setVisible(true) 호출함)
            // MachineController도 함께 넘김. MainMenuView가 AdminView 생성 시 사용
            new MainMenuView(orderController, adminController, machineController);

            // 제조 대기 현황은 항상 띄워두고 콜백으로 갱신 → 매번 새 창 만들 필요 없음
            // 화면 안에서 '주문 취소' 기능을 노출하기 위해 OrderController도 함께 넘김
            new OrderStatusView(machineController, orderController);

            // 대기열 소비 시작
            machineController.startMaker();
        });

        // 5) 종료 시 MakerThread 정리
        Runtime.getRuntime().addShutdownHook(new Thread(machineController::stopMaker)); // MakerThread는 wait() 하는 무한 루프 → JVM 종료 시 interrupt로 깨끗이 정리 (좀비 스레드 방지)
    }

    private static void seedInventory(Inventory inventory) {
        // 음료 — 커피 (Coffee → Drink → Product 계층)
        inventory.addProduct(new Coffee("C01", "아메리카노", 2500, 10, 300, true,  2, false));
        inventory.addProduct(new Coffee("C02", "디카페인 라떼", 3500,  5, 300, true,  2, true));

        // 음료 — 차
        inventory.addProduct(new Tea("T01", "녹차", 2000, 10, 300, true, "녹차"));
        inventory.addProduct(new Tea("T02", "홍차", 2000, 10, 300, true, "홍차"));

        // 음료 — 스무디
        inventory.addProduct(new Smoothie("S01", "딸기 스무디", 4500, 5, 400, false, "딸기"));
        inventory.addProduct(new Smoothie("S02", "바나나 스무디", 4000, 5, 400, false, "바나나"));

        // 스낵 — Snack은 Drink를 거치지 않고 Product 직접 상속
        inventory.addProduct(new Snack("N01", "초콜릿 쿠키", 1500, 20));
        inventory.addProduct(new Snack("N02", "감자칩", 1800, 15));
    }
}
