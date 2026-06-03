package model;

import controller.MachineController;
import model.order.Order;
import model.order.OrderQueue;
import model.order.OrderStatus;

/**
 * 제조 스레드. 주문 큐에서 주문을 하나씩 꺼내 제조→배출→판매기록 사이클을 돈다.
 *
 * <p>역할:
 * <ul>
 *   <li>큐에서 dequeue (비어있으면 wait).</li>
 *   <li>상태를 WAITING → MAKING 으로 바꾸고, 제조(Product.make) 시작 알림.</li>
 *   <li>제조 시뮬레이션 (Thread.sleep), 배출(Product.dispense), DONE 전환.</li>
 *   <li>SalesRepository 에 판매 기록 저장 후 다시 알림.</li>
 * </ul>
 *
 * <p>왜 콜백 인터페이스 대신 MachineController 를 직접 받나:
 * 큐의 현재 상태를 화면에 표시하려면 누군가가 OrderQueue 를 읽어 문자열로 가공해야 한다.
 * 그 책임은 View 도 아니고 작업 스레드도 아닌 Controller 가 맡는 게 자연스럽다.
 * 따라서 MakerThread 는 "상태가 바뀜" 만 통지하고, MachineController 가 큐를 읽어
 * View 들에게 broadcast 한다.
 *
 * <p>종료:
 * interrupt() 신호를 받으면 InterruptedException 으로 빠져나가 루프를 깨끗하게 종료한다
 * (강의 Week 11 패턴).
 */
public class MakerThread extends Thread {

    private final OrderQueue orderQueue;
    private final SalesRepository salesRepository;
    private final MachineController machineController;

    public MakerThread(OrderQueue orderQueue,
                       SalesRepository salesRepository,
                       MachineController machineController) {
        this.orderQueue        = orderQueue;
        this.salesRepository   = salesRepository;
        this.machineController = machineController;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1) 대기열에서 한 건 꺼낸다 — 비어있으면 wait
                Order order = orderQueue.dequeue();

                // 2) WAITING → MAKING : 제조 시작을 View 들에게 알림
                order.setStatus(OrderStatus.MAKING);
                machineController.onMakerEvent(order);

                // 3) 다형성 — 어떤 음료/스낵인지 모르고 make() 만 호출 (Coffee/Tea/Smoothie/Snack)
                order.getProduct().make();

                // 4) 배출 + DONE
                order.getProduct().dispense();
                order.setStatus(OrderStatus.DONE);

                // 5) 판매 기록 (파일 I/O)
                salesRepository.save(order);

                // 6) 제조 완료 알림 (현재 제조 중인 것은 더 이상 없음)
                machineController.onMakerEvent(null);

            } catch (InterruptedException e) {
                // interrupt() 신호 → 인터럽트 상태 복원 후 깨끗하게 루프 종료
                Thread.currentThread().interrupt();
                System.out.println("[MakerThread] 종료 신호 수신. 스레드 종료.");
                break;
            }
        }
    }
}
