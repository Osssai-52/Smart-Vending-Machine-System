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
import view.AdminView;
import view.MainMenuView;
import view.OrderStatusView;
import view.PaymentView;

/**
 * 프로그램 진입점.
 *
 * <p>역할은 딱 "조립" 만 한다:
 * <ol>
 *   <li>Model 객체 생성</li>
 *   <li>초기 메뉴(상품) 시드</li>
 *   <li>Controller 생성 (Model 을 생성자 주입)</li>
 *   <li>EDT 위에서 View 생성 + Controller 와 setter 로 양방향 연결</li>
 *   <li>MakerThread 시작</li>
 *   <li>JVM 종료 시 MakerThread 깨끗이 정리 (ShutdownHook)</li>
 * </ol>
 *
 * <p>코드 리뷰 대응 메모 (왜 이렇게 짰는가):
 * <ul>
 *   <li><b>왜 SwingUtilities.invokeLater?</b> — Swing 컴포넌트는 EDT(Event Dispatch Thread)
 *       에서만 안전하게 생성/조작 가능. main 스레드에서 직접 만들면 동시성 버그 가능 (강의 Week 11).</li>
 *   <li><b>왜 시드 데이터를 별도 메서드로?</b> — main 의 책임은 "조립" 하나로 좁히고,
 *       메뉴 구성은 separateOf concern 으로 분리. 줄 수도 줄어든다.</li>
 *   <li><b>왜 Controller 는 Model 을 생성자 주입, View 는 setter 주입?</b>
 *       Model 은 main 스레드에서 먼저 만들어지므로 생성자 주입이 자연스럽다.
 *       반면 View 는 EDT 안에서 만들어지므로 Controller 가 생성된 시점에는 아직 없다.
 *       그래서 View 가 만들어진 직후 setter 로 연결한다.</li>
 *   <li><b>왜 ShutdownHook 으로 stopMaker?</b> — MakerThread 는 while 루프 안에서 wait 한다.
 *       JVM 이 일반 종료될 때 interrupt() 를 안 보내면 좀비 스레드가 남을 수 있다.
 *       평가 항목 4번 "안정성 및 자원 관리" 대응.</li>
 *   <li><b>왜 inventory.addProduct(new Coffee(...)) 처럼 Product 로만 다루는가?</b>
 *       다형성 — Inventory 는 구체 타입(Coffee/Tea/Snack)을 모르고 동작한다.
 *       새 상품 타입이 추가돼도 Inventory 와 Main 의 조립 코드는 그대로 (OCP).</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {

        // ---------- 1) Model 생성 ----------
        Inventory       inventory       = new Inventory();
        OrderQueue      orderQueue      = new OrderQueue();
        SalesRepository salesRepository = new SalesRepository();

        // ---------- 2) 초기 메뉴 시드 ----------
        seedInventory(inventory);

        // ---------- 3) Controller 생성 (Model 은 생성자 주입) ----------
        PaymentController   paymentController   = new PaymentController();
        InventoryController inventoryController = new InventoryController(inventory);
        OrderController     orderController     = new OrderController(
                inventory, orderQueue, paymentController, inventoryController);
        MachineController   machineController   = new MachineController(orderQueue, salesRepository);
        AdminController     adminController     = new AdminController(inventory, salesRepository);

        // ---------- 4) UI 는 EDT 에서 생성 ----------
        SwingUtilities.invokeLater(() -> {

            // 4-1. View 생성
            //      각 View 의 생성자에 어떤 Controller 를 받을지는 김남주가 결정.
            //      여기서는 일단 빈 생성자로 만들고, Controller → View 갱신 채널만 setter 로 연결한다.
            MainMenuView    mainMenuView    = new MainMenuView();
            PaymentView     paymentView     = new PaymentView();
            OrderStatusView orderStatusView = new OrderStatusView();
            AdminView       adminView       = new AdminView();

            // 4-2. Controller → View 갱신 연결 (setter 주입)
            inventoryController.setMainMenuView(mainMenuView);
            adminController.setAdminView(adminView);

            // 4-3. 상태 변화 알림 구독 등록 (보고서 p.5: registerStatusView(this))
            machineController.registerStatusView(orderStatusView);

            // 4-4. MakerThread 시작 — 대기열에 들어온 주문을 소비하기 시작.
            machineController.startMaker();

            // 4-5. 메인 화면 표시.
            //      MainMenuView 가 JFrame 을 상속한다고 가정 (김남주가 결정).
            //      만약 다른 형태(JPanel 등)이면 김남주가 표시 방식을 main 에 반영해야 한다.
            // mainMenuView.setVisible(true);   // TODO(김남주): View 완성 후 주석 해제
        });

        // ---------- 5) JVM 종료 시 MakerThread 깨끗이 정리 ----------
        Runtime.getRuntime().addShutdownHook(new Thread(machineController::stopMaker));
    }

    /**
     * 초기 메뉴 데이터를 등록한다. 부팅 시 1회만 호출.
     *
     * <p>DB 가 없는 본 프로젝트에서는 코드에 하드코딩한다.
     * 실제 운영이라면 외부 설정/DB 에서 로드하는 게 맞다.
     */
    private static void seedInventory(Inventory inventory) {
        // 음료 — 커피 (Coffee → Drink → Product 계층, make() 오버라이드)
        inventory.addProduct(new Coffee("C01", "아메리카노",     2500, 10, 300, true,  2, false));
        inventory.addProduct(new Coffee("C02", "디카페인 라떼",  3500,  5, 300, true,  2, true));

        // 음료 — 차
        inventory.addProduct(new Tea("T01", "녹차", 2000, 10, 300, true, "녹차"));
        inventory.addProduct(new Tea("T02", "홍차", 2000, 10, 300, true, "홍차"));

        // 음료 — 스무디
        inventory.addProduct(new Smoothie("S01", "딸기 스무디",   4500, 5, 400, false, "딸기"));
        inventory.addProduct(new Smoothie("S02", "바나나 스무디", 4000, 5, 400, false, "바나나"));

        // 스낵 (Snack → Product 직접 상속 — Drink 를 거치지 않음)
        inventory.addProduct(new Snack("N01", "초콜릿 쿠키", 1500, 20));
        inventory.addProduct(new Snack("N02", "감자칩",      1800, 15));
    }
}
