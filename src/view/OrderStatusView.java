package view;

import model.order.Order;

// view/OrderStatusView.java
// 실시간 제조 대기 현황 화면.
//
// 구성 컴포넌트 (보고서 p.5):
//  - JLabel    : 현재 제조중인 상품 표시
//  - JTextArea : OrderQueue 대기열 표시
//
// 책임:
//  - StatusViewListener 를 구현한다 (implements StatusViewListener).
//  - 생성 시 MachineController.registerStatusView(this) 로 자기 자신을 등록.
//  - onStatusChanged(Order) 콜백을 받으면 SwingUtilities.invokeLater(...) 안에서
//    OrderQueue.peekAll() 결과를 다시 그린다.
//
// 멀티스레드 안전성 (보고서 p.5):
//  - "멀티스레드 환경에서 UI 컴포넌트를 안전하게 갱신" → 모든 UI 변경은 EDT 에서만.
//  - "화면이 스스로 루프를 돌지 않고, 상태 변화가 있을 때만 컨트롤러가 화면을 리렌더링" → polling 금지.

public class OrderStatusView implements StatusViewListener {

    /**
     * 제조 상태 변화 콜백.
     * TODO(김남주): JLabel / JTextArea 갱신 로직 구현.
     */
    @Override
    public void onStatusChanged(Order order) {
        // empty — 김남주가 구현
    }
}
