package controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import model.MakerThread;
import model.SalesRepository;
import model.order.Order;
import model.order.OrderQueue;
import model.order.OrderStatus;
import view.StatusViewListener;

/**
 * 자판기 "기계 측" 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>{@link MakerThread} 의 생명주기(시작/중지)를 관리한다.</li>
 *   <li>MakerThread 가 보낸 이벤트를 받아 OrderQueue 를 View 친화적인 문자열로 가공하고,
 *       등록된 모든 {@link StatusViewListener} 에게 EDT 위에서 broadcast 한다.</li>
 * </ul>
 *
 * <p>왜 가공(StringBuilder 등)이 Controller 책임인가:
 * View 는 표시만, 작업 스레드는 도메인 로직만. 둘 사이의 "표현용 가공" 은 Controller 가
 * 맡는 것이 결합도를 낮춘다. View 는 큐의 자료구조도, Order 클래스도 알 필요가 없다.
 *
 * <p>동시성:
 * <ul>
 *   <li>리스너 목록은 {@link CopyOnWriteArrayList} — 등록/해제와 broadcast 가 다른 스레드에서 동시에 일어나도 안전.</li>
 *   <li>UI 갱신은 반드시 EDT 에서 ({@link SwingUtilities#invokeLater}).</li>
 * </ul>
 */
public class MachineController {

    private final OrderQueue orderQueue;
    private final SalesRepository salesRepository;

    private final CopyOnWriteArrayList<StatusViewListener> statusListeners = new CopyOnWriteArrayList<>();

    private MakerThread makerThread;

    public MachineController(OrderQueue orderQueue, SalesRepository salesRepository) {
        this.orderQueue = orderQueue;
        this.salesRepository = salesRepository;
    }

    // ------------------------------------------------------------
    // MakerThread 생명주기
    // ------------------------------------------------------------

    public void startMaker() {
        if (makerThread != null && makerThread.isAlive()) {
            return;
        }
        makerThread = new MakerThread(orderQueue, salesRepository, this);
        makerThread.start();
        System.out.println("[MachineController] MakerThread 시작");
    }

    public void stopMaker() {
        if (makerThread == null) {
            return;
        }
        makerThread.interrupt();
        try {
            makerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        makerThread = null;
        System.out.println("[MachineController] MakerThread 종료");
    }

    // ------------------------------------------------------------
    // 리스너 등록/해제 (보고서 p.5: machineController.registerStatusView(this))
    // ------------------------------------------------------------

    public void registerStatusView(StatusViewListener listener) {
        if (listener == null) return;
        statusListeners.addIfAbsent(listener);
    }

    public void unregisterStatusView(StatusViewListener listener) {
        statusListeners.remove(listener);
    }

    // ------------------------------------------------------------
    // MakerThread → broadcast
    // ------------------------------------------------------------

    /**
     * MakerThread 가 호출. 현재 제조 중인 주문(null 이면 제조 완료)을 받아
     * 큐 상태와 함께 모든 View 리스너에게 broadcast 한다.
     */
    public void onMakerEvent(Order currentOrder) {
        // 1) 현재 제조 중 상품명 결정
        String currentStatus = "";
        if (currentOrder != null && currentOrder.getStatus() == OrderStatus.MAKING) {
            currentStatus = currentOrder.getProduct().getName();
        }
        // 2) 큐 가공 (View 가 그대로 표시할 멀티라인 텍스트)
        String queueDetails = formatQueueDetails();

        // 3) EDT 에서 broadcast — final 로 캡처
        final String csCaptured = currentStatus;
        final String qdCaptured = queueDetails;
        SwingUtilities.invokeLater(() -> {
            for (StatusViewListener listener : statusListeners) {
                try {
                    listener.onOrderStatusChanged(csCaptured, qdCaptured);
                } catch (RuntimeException e) {
                    // 한 리스너의 예외가 다른 리스너 통지를 막지 않도록 격리.
                    System.out.println("[MachineController] 리스너 통지 중 오류: " + e.getMessage());
                }
            }
        });
    }

    /** 대기열을 사용자가 읽기 쉬운 형태로 가공한다. */
    private String formatQueueDetails() {
        List<Order> orders = orderQueue.peekAll();
        if (orders.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" 대기 중인 주문: ").append(orders.size()).append("건\n");
        int n = 1;
        for (Order o : orders) {
            sb.append(" ").append(n++).append(". ").append(o).append("\n");
        }
        return sb.toString();
    }
}
