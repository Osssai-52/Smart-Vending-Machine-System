package view;

/**
 * 제조 상태 변화를 구독하는 View 가 구현하는 콜백 인터페이스.
 *
 * <p>MachineController 가 MakerThread 로부터 이벤트를 받아 큐를 가공한 뒤
 * 등록된 모든 리스너에게 이 메서드로 broadcast 한다. View 는 이미 가공된 문자열만
 * 받으면 되므로 큐/상태 객체를 직접 만질 필요가 없다.
 */
public interface StatusViewListener {

    /**
     * 제조 상태가 바뀌었을 때 호출된다.
     *
     * @param currentStatus 현재 제조 중인 상품 이름 (없으면 "")
     * @param queueDetails  대기열 표시용 멀티라인 문자열 (없으면 "")
     */
    void onOrderStatusChanged(String currentStatus, String queueDetails);
}
