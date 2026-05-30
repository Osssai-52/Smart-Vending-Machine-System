package controller;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import model.MakerThread;
import model.SalesRepository;
import model.order.Order;
import model.order.OrderQueue;
import view.StatusViewListener;

/**
 * 자판기 "기계 측" 컨트롤러.
 *
 * <p>책임:
 * <ul>
 *   <li>{@link MakerThread} 의 생명주기(시작/중지)를 관리한다.</li>
 *   <li>MakerThread 가 통지하는 상태 변화를 받아, 등록된 모든 View 리스너에게 broadcast 한다.</li>
 * </ul>
 *
 * <p>왜 자신이 {@link StatusViewListener} 를 구현하나:
 * 함수아의 {@link MakerThread} 는 단일 {@link StatusViewListener} 만 받도록 설계되었다.
 * 본 컨트롤러가 리스너 역할을 맡으면 (1) MakerThread 시그니처를 그대로 만족하면서
 * (2) 여러 OrderStatusView 가 등록/해제될 수 있는 broadcast 구조를 동시에 제공한다.
 * 결과적으로 MakerThread 는 View 타입을 전혀 알 필요가 없다 (결합도 ↓).
 *
 * <p>동시성:
 * <ul>
 *   <li>리스너 등록/해제와 알림이 다른 스레드에서 동시에 일어날 수 있으므로
 *       {@link CopyOnWriteArrayList} 사용 (락 없이 안전한 순회).</li>
 *   <li>UI 갱신은 반드시 EDT 에서 ({@link SwingUtilities#invokeLater}).</li>
 * </ul>
 */
public class MachineController implements StatusViewListener {

    private final OrderQueue orderQueue;
    private final SalesRepository salesRepository;

    /** OrderStatusView 등 상태 변화를 구독하는 리스너 목록. */
    private final CopyOnWriteArrayList<StatusViewListener> statusListeners = new CopyOnWriteArrayList<>();

    /** 현재 동작 중인 MakerThread. null 이면 미동작. */
    private MakerThread makerThread;

    public MachineController(OrderQueue orderQueue, SalesRepository salesRepository) {
        this.orderQueue = orderQueue;
        this.salesRepository = salesRepository;
    }

    // ------------------------------------------------------------
    // MakerThread 생명주기
    // ------------------------------------------------------------

    /** MakerThread 를 생성하고 시작한다. 이미 동작 중이면 아무 일도 하지 않는다. */
    public void startMaker() {
        if (makerThread != null && makerThread.isAlive()) {
            return;
        }
        // 자기 자신을 StatusViewListener 로 넘긴다 → MakerThread 가 호출 시 broadcast.
        makerThread = new MakerThread(orderQueue, salesRepository, this);
        makerThread.start();
        System.out.println("[MachineController] MakerThread 시작");
    }

    /**
     * MakerThread 에 중지 신호를 보내고 종료를 기다린다.
     * 강의 Week 11 의 interrupt() + InterruptedException 패턴.
     */
    public void stopMaker() {
        if (makerThread == null) {
            return;
        }
        makerThread.interrupt();
        try {
            makerThread.join();
        } catch (InterruptedException e) {
            // 현재 스레드(보통 EDT) 가 join 대기 중 인터럽트당하면
            // 인터럽트 상태를 보존해 상위에서 인지할 수 있도록 다시 설정한다.
            Thread.currentThread().interrupt();
        }
        makerThread = null;
        System.out.println("[MachineController] MakerThread 종료");
    }

    // ------------------------------------------------------------
    // 리스너 등록/해제 (보고서 p.5: machineController.registerStatusView(this))
    // ------------------------------------------------------------

    public void registerStatusView(StatusViewListener listener) {
        if (listener == null) {
            return;
        }
        statusListeners.addIfAbsent(listener);
    }

    public void unregisterStatusView(StatusViewListener listener) {
        statusListeners.remove(listener);
    }

    // ------------------------------------------------------------
    // StatusViewListener 구현 — MakerThread 가 호출
    // ------------------------------------------------------------

    /**
     * MakerThread 가 상태 변경 시 호출.
     * 등록된 모든 리스너에게 EDT 위에서 broadcast 한다.
     */
    @Override
    public void onStatusChanged(Order order) {
        SwingUtilities.invokeLater(() -> {
            for (StatusViewListener listener : statusListeners) {
                try {
                    listener.onStatusChanged(order);
                } catch (RuntimeException e) {
                    // 한 리스너의 예외가 다른 리스너 통지를 막지 않도록 격리한다.
                    System.out.println("[MachineController] 리스너 통지 중 오류: " + e.getMessage());
                }
            }
        });
    }
}
