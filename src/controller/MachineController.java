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

// 자판기 기계 측 컨트롤러
// MakerThread의 시작/종료 관리
// MakerThread가 보낸 이벤트를 받아 OrderQueue를 View에 맞는 문자열로 가공. 등록된 모든 StatusViewListener에게 EDT 위에서 broadcast함
// Listener 목록은 CopyOnWriteArrayList → 등록/해제와 broadcast가 다른 스레드에서 동시에 일어나도 안전

public class MachineController {

    private final OrderQueue orderQueue;
    private final SalesRepository salesRepository;

    private final CopyOnWriteArrayList<StatusViewListener> statusListeners = new CopyOnWriteArrayList<>();

    private MakerThread makerThread;

    public MachineController(OrderQueue orderQueue, SalesRepository salesRepository) {
        this.orderQueue = orderQueue;
        this.salesRepository = salesRepository;
    }

    // MakerThread 시작/종료
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

    // 리스너 등록/해제 
    public void registerStatusView(StatusViewListener listener) {
        if (listener == null) return;
        statusListeners.addIfAbsent(listener);
    }

    public void unregisterStatusView(StatusViewListener listener) {
        statusListeners.remove(listener);
    }

    // MakerThread 호출. 현재 제조 중인 주문을 받아 큐 상태와 함께 모든 View Listener에게 broadcast
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
                    // 한 리스너의 예외가 다른 리스너 통지를 막지 않도록 격리
                    System.out.println("[MachineController] 리스너 통지 중 오류: " + e.getMessage());
                }
            }
        });
    }

    // 대기열을 사용자가 읽기 쉬운 형태로 가공
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
