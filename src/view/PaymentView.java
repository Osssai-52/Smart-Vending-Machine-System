package view;

// view/PaymentView.java
// 결제 처리 화면.
//
// 구성 컴포넌트 (보고서 p.5):
//  - JRadioButton : CASH / CARD / POINT 선택 (ButtonGroup 으로 묶기)
//  - JTextField   : 투입 금액 입력 (현금일 때 활성화)
//  - JButton      : "결제하기", "취소"
//  - JLabel       : 안내 메시지, 잔액/거스름돈 표시
//
// 책임:
//  - 결제 수단 선택 + 금액 입력 UI 제공.
//  - "결제하기" 버튼 클릭 시 executePayment() 호출 → 선택된 결제 타입과 금액을
//    PaymentController.pay(...) 로 전달.
//  - 결제 성공 시 → MainMenuView 의 재고 표시 갱신 + OrderStatusView 호출.
//  - 결제 실패 시 → JOptionPane 등으로 사용자에게 사유 표시.
//
// 캡슐화:
//  - 결제 결과 처리 로직(잔액 차감 등)은 절대 View 에 두지 않는다 → Controller / Model 이 담당.
