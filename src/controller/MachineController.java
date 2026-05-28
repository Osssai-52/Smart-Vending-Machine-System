package controller;

// controller/MachineController.java
// 자판기의 "기계" 측 컨트롤러. MakerThread 의 라이프사이클과 View 갱신 알림을 담당. 보고서 p.6.
//
// 주요 메서드 (보고서 p.6):
//  - startMaker()  : MakerThread 인스턴스 생성 후 start().
//  - stopMaker()   : MakerThread 에 interrupt() 보내고 join().
//  - notifyView()  : 등록된 StatusViewListener 들에게 상태 변경 알림 (SwingUtilities.invokeLater 로 EDT 위에서).
//  - registerStatusView(StatusViewListener v)   : OrderStatusView 가 자기 자신을 등록.
//  - unregisterStatusView(StatusViewListener v) : 해제.
//
// 책임:
//  - MakerThread 와 View 사이의 "중개자" 역할.  MakerThread 가 View 를 직접 알지 않도록 차단.
//  - 등록된 리스너는 List<StatusViewListener> 로 보관 (제네릭 컬렉션).
//
// 동시성:
//  - 리스너 등록/해제와 알림은 동시 발생 가능 → 리스너 목록을 CopyOnWriteArrayList 로 두면 안전.
