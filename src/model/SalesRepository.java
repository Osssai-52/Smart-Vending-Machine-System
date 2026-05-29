package model;

// model/SalesRepository.java
// 판매 내역 영속화. 보고서의 sales.txt 파일 I/O.
//
// 책임:
//  - save(Order completedOrder) : 제조 완료된 주문 1건을 sales.txt 에 append 한다.
//      형식 예) yyyy-MM-dd HH:mm:ss | productId | productName | price | paymentType
//  - loadAll() : 파일을 읽어 List<SalesRecord> (또는 List<String>) 으로 반환 → AdminView 가 표시.
//
// OOP 포인트:
//  - 파일 I/O 요구사항을 담당하는 유일한 클래스 (단일 책임 원칙).
//  - try-with-resources 로 BufferedReader / BufferedWriter 를 안전하게 사용.
//  - IOException 은 검사 예외 → 호출부에 적절히 전파하거나 사용자에게 메시지로 표시.
//
// 동시성:
//  - 여러 곳에서 동시에 save() 호출될 수 있으므로 메서드 단위 synchronized.
