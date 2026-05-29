package view;

// view/StatusViewListener.java
// MakerThread → View 비동기 알림을 위한 콜백 인터페이스.
//
// 배경 (보고서 p.5 "MakerThread 와의 비동기 통신을 위해 자기 자신(View) 을 리스너로 등록"):
//  - MakerThread 는 별도 스레드에서 돌기 때문에, 제조 상태가 바뀌었음을 알릴 수단이 필요하다.
//  - 화면이 직접 polling 하지 않고, 상태 변화가 있을 때만 컨트롤러가 View 를 다시 그리도록 한다.
//
// 메서드 예:
//  - void onStatusChanged()  : 큐 또는 현재 제조중인 주문이 바뀌었을 때 호출됨.
//  - 또는 void onOrderStatusChanged(Order order) 처럼 변경된 주문을 인자로 넘기는 형태도 가능.
//
// 사용:
//  - OrderStatusView implements StatusViewListener.
//  - MachineController 가 register/unregister 를 관리.
//
// 멀티스레드 주의:
//  - 콜백은 작업 스레드(MakerThread) 에서 호출될 수 있으므로,
//    실제 UI 갱신은 반드시 SwingUtilities.invokeLater(...) 로 EDT 에서 수행해야 한다.
