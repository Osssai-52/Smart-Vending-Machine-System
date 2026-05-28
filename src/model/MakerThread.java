package model;

// model/MakerThread.java
// 제조 스레드. 주문을 하나씩 꺼내 상품의 상태를 변경한다.
//
// 책임 (주제선정보고서 p.4 그대로):
//  - OrderQueue 에서 dequeue() 로 주문 1건을 꺼낸다 (큐가 비어있으면 wait).
//  - 주문 상태를 WAITING → MAKING 으로 변경.
//  - order.getProduct().make() 호출 (다형성으로 음료별 제조 시간 다름).
//  - 완료되면 상태를 DONE 으로 바꾸고, product.dispense() 로 배출.
//  - SalesRepository.save(order) 로 판매 기록 저장.
//  - MachineController.notifyView() 를 호출하여 OrderStatusView 를 갱신하도록 알린다.
//
// 동시성 (요구사항의 핵심):
//  - extends Thread 또는 implements Runnable.
//  - run() 안에서 while(!Thread.currentThread().isInterrupted()) 루프.
//  - OrderQueue 접근은 OrderQueue 내부의 synchronized 메서드를 사용.
//  - 종료 신호: interrupt() 를 받으면 깨끗하게 빠져나가도록 InterruptedException 처리.
//
// 의존성:
//  - OrderQueue, SalesRepository, MachineController (혹은 View 갱신 콜백) 를 생성자 주입으로 받음.
