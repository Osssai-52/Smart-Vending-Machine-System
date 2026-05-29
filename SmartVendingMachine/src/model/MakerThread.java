package model;

import model.order.Order;
import model.order.OrderQueue;
import model.order.OrderStatus;
import view.StatusViewListener;

// 제조 스레드. 주문을 하나씩 꺼내 상품의 상태를 변경한다.
// extends Thread 사용.
// run() 안에서 while(!Thread.currentThread().isInterrupted()) 루프.
// OrderQueue 접근은 내부의 synchronized 메서드를 사용.
// 종료 신호: interrupt()를 받으면 깨끗하게 빠져나가도록 InterruptedException 처리.
// 의존성: OrderQueue, SalesRepository, StatusViewListener(View 갱신 콜백)를 생성자 주입으로 받음.
public class MakerThread extends Thread {

    private final OrderQueue orderQueue;
    private final SalesRepository salesRepository;
    private final StatusViewListener viewListener; // View 갱신 콜백

    // 의존성: 생성자 주입
    public MakerThread(OrderQueue orderQueue,
                       SalesRepository salesRepository,
                       StatusViewListener viewListener) {
        this.orderQueue      = orderQueue;
        this.salesRepository = salesRepository;
        this.viewListener    = viewListener;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1. OrderQueue에서 dequeue (큐가 비어있으면 wait)
                Order order = orderQueue.dequeue();

                // 2. 상태 WAITING → MAKING
                order.setStatus(OrderStatus.MAKING);
                viewListener.onStatusChanged(order); // View 갱신

                // 3. 제조 (다형성으로 음료별 제조 시간 다름 — product.make() 호출)
                order.getProduct().make();

                // 4. 배출 (product.dispense() — 재고 1 감소 + 콘솔 출력)
                order.getProduct().dispense();

                // 5. 상태 MAKING → DONE
                order.setStatus(OrderStatus.DONE);

                // 6. 판매 기록 저장
                salesRepository.save(order);

                // 7. MachineController.notifyView()를 호출하여 OrderStatusView를 갱신하도록 알림
                viewListener.onStatusChanged(order);

            } catch (InterruptedException e) {
                // interrupt() 신호 → 깨끗하게 루프 종료
                Thread.currentThread().interrupt();
                System.out.println("[MakerThread] 종료 신호 수신. 스레드 종료.");
                break;
            }
        }
    }

    // 외부에서 안전하게 종료 요청
    public void stopThread() {
        this.interrupt();
    }
}
