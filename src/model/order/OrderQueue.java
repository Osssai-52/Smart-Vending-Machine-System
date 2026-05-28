package model.order;

// model/order/OrderQueue.java
// 주문 대기열. 보고서의 Queue<Order> 컬렉션.
//
// 책임:
//  - 결제 완료된 주문(Order)을 FIFO 로 보관한다.
//  - enqueue(Order o)  : 주문 추가
//  - dequeue()         : 가장 오래된 주문 꺼내기 (없으면 wait)
//  - peekAll()         : 현재 대기 중인 주문 스냅샷 (OrderStatusView 표시용, 방어적 복사)
//
// 동시성 (멀티스레드 동기화 요구사항):
//  - 내부적으로 LinkedList<Order> 또는 ArrayDeque<Order> 를 들고 있고,
//    enqueue/dequeue 메서드는 synchronized 로 보호한다.
//  - dequeue() 는 큐가 비어있으면 wait(), enqueue() 는 추가 후 notifyAll() 호출.
//  - 또는 java.util.concurrent.BlockingQueue 를 사용해도 되지만,
//    수업 요구사항(synchronized 블록 사용)을 보여주기 위해 직접 구현 권장.
//
// OOP 포인트:
//  - 컬렉션/제네릭: Queue<Order>
//  - 멀티스레드 동기화: synchronized 블록 + wait/notify
