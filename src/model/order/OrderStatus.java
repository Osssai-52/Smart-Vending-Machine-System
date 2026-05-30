package model.order;

// 주문 상태를 표현하는 열거형.
// 매직 문자열("waiting" 등) 대신 enum으로 타입 안전성 확보.
public enum OrderStatus {
    WAITING,   // 결제 완료 후 제조 대기열에 들어간 상태
    MAKING,    // MakerThread가 꺼내서 제조 중
    DONE,      // 제조 완료, 사용자에게 dispense 완료
    CANCELED   // 사용자가 주문 취소
}
