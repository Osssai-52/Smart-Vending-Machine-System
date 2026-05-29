package controller;

// controller/AdminController.java
// 관리자 화면 진입 / 매출 데이터 불러오기. 보고서 p.6.
//
// 주요 메서드:
//  - authenticate(String password) : 간단한 관리자 비밀번호 검증.
//  - loadSales()                   : SalesRepository.loadAll() 결과를 View 가 표시할 형태로 가공.
//  - refreshAdminView()            : 현재 재고 + 매출 내역을 다시 읽어 AdminView 갱신.
//
// 의존성:
//  - SalesRepository, Inventory, AdminView 참조.
//
// 책임:
//  - 매출 데이터의 "가공" 과 "표시 위임" 이 핵심. 파일 I/O 는 SalesRepository 에 위임.
