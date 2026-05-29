package model.order;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

// 주문 대기열. Queue<Order> 컬렉션.
// 결제 완료된 주문(Order)을 FIFO로 보관한다.
// 멀티스레드 동기화: synchronized 블록 + wait/notify 직접 구현.
public class OrderQueue {

    private final Queue<Order> queue = new ArrayDeque<>();

    // 주문 추가 (결제 완료 후 호출)
    // 추가 후 notifyAll() → 대기 중인 MakerThread를 깨움
    public synchronized void enqueue(Order order) {
        queue.offer(order);
        System.out.println("[대기열 추가] " + order);
        notifyAll();
    }

    // 가장 오래된 주문 꺼내기
    // 큐가 비어있으면 wait() → enqueue()의 notifyAll()까지 대기
    public synchronized Order dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

    // 현재 대기 중인 주문 스냅샷 (OrderStatusView 표시용, 방어적 복사)
    public synchronized List<Order> peekAll() {
        return new ArrayList<>(queue);
    }

    public synchronized int size()      { return queue.size(); }
    public synchronized boolean isEmpty() { return queue.isEmpty(); }
}
