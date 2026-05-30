package view;

import model.order.Order;

/**
 * 제조 상태 변화를 구독하는 View 가 구현하는 콜백 인터페이스.
 *
 * <p>MakerThread (작업 스레드) 는 주문의 상태가 바뀔 때마다
 * 이 인터페이스의 onStatusChanged 를 호출한다.
 * View 는 polling 하지 않고, 이벤트가 올 때만 화면을 갱신한다.
 *
 * <p>주의:
 * MakerThread 에서 호출되므로 실제 UI 갱신은 구현 측에서 반드시
 * {@link javax.swing.SwingUtilities#invokeLater} 로 EDT 위에서 수행해야 한다.
 */
public interface StatusViewListener {

    /**
     * 주문 상태가 바뀌었을 때 호출된다.
     *
     * @param order 상태가 바뀐 주문 (null 가능 — 전체 큐 변경 알림 시)
     */
    void onStatusChanged(Order order);
}
