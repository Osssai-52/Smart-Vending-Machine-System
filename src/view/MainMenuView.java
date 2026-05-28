package view;

// view/MainMenuView.java
// 메인 메뉴 & 상품 선택 화면. 보고서 p.5 의 ProductSelectionView 와 동일 역할.
//
// 구성 컴포넌트 (보고서 명시):
//  - JTextArea  : 메뉴(상품 리스트) 표시
//  - JTextField : 상품 ID 입력
//  - JButton    : "주문하기", "관리자 모드 진입"
//
// 책임:
//  - 화면을 렌더링한다 (JFrame / JPanel 구성).
//  - 사용자의 입력(상품 ID, 버튼 클릭)을 잡아 컨트롤러에 위임한다 — 직접 Model 을 만지지 않는다.
//  - refreshMenuDisplay() : InventoryController 또는 MachineController 에서 호출하면
//    현재 재고를 다시 읽어 JTextArea 를 갱신한다.
//
// 단방향 흐름 (보고서 p.5):
//   View → Controller → Model.
//   View 는 Controller 의 참조만 가진다 (생성자 주입).
//
// 캡슐화 장벽 (보고서 p.5):
//   "뷰가 직접 연산하지 않고 컨트롤러에 파라미터만 안전하게 전달".
//   → 버튼 핸들러는 controller.createOrder(productId) 만 호출.
//
// 다형성 활용 (보고서 p.5):
//   "부모 타입(Product) 으로 리스트를 일괄 수집" → Inventory.listAll() 결과를 Product 타입으로 표시.
