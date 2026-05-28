package view;

// view/AdminView.java
// 관리자 시스템 화면.
//
// 구성 컴포넌트 (보고서 p.5):
//  - JTextField : 보충 수량 입력
//  - JTextArea  : sales.txt 파일 데이터 출력 영역 (매출 내역)
//  - JButton    : "재고 보충", "매출 내역 불러오기", "메인으로"
//  - JTable 또는 JList : (선택) 상품별 현재 재고 표시
//
// 책임:
//  - 재고 보충: 사용자가 입력한 상품 ID + 수량을 InventoryController.addStock() 로 위임.
//  - 매출 조회: AdminController.loadSales() 호출 결과를 JTextArea 에 표시.
//  - 관리자 인증 UI(간단한 패스워드 입력) 는 진입 시점에 다이얼로그로 처리하거나 별도 화면으로 둔다.
//
// 캡슐화:
//  - 보충 수량 입력 검증(숫자 여부, 음수 여부)은 Controller 에서 책임지도록 위임.
