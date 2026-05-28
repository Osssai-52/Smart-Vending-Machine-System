// Main.java
// 프로그램의 진입점 (Entry Point).
//
// 역할:
//  - 애플리케이션 부팅 시 Model 객체들 (Inventory, OrderQueue, SalesRepository) 을 생성한다.
//  - 생성한 Model 들을 Controller 들에 주입(생성자 주입)한다.
//  - 최상위 View (MainMenuView) 를 띄우고, 필요한 Controller 들을 연결한다.
//  - MakerThread 를 시작시켜 주문 큐 소비를 활성화한다.
//
// 주의:
//  - Swing 코드는 EDT(Event Dispatch Thread) 위에서 시작되어야 하므로
//    SwingUtilities.invokeLater(...) 안에서 UI 를 생성한다.
