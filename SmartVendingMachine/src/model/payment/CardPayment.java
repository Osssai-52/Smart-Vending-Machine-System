package model.payment;

// 카드 결제. Payment 구현체.
// 카드 번호/한도 등 카드 정보를 보관한다 (private).
// pay(amount) 호출 시 한도 검증 후 결제 승인/거절을 반환한다.
// 실제 PG 연동은 하지 않고, 한도 내라면 "승인"으로 시뮬레이션한다.
public class CardPayment implements Payment {

    private final String cardNumber; // 카드 번호 (외부 노출 X)
    private final int creditLimit;   // 결제 한도
    private int usedAmount;          // 현재까지 사용한 금액

    public CardPayment(String cardNumber, int creditLimit) {
        this.cardNumber  = cardNumber;
        this.creditLimit = creditLimit;
        this.usedAmount  = 0;
    }

    public int getRemainingLimit() {
        return creditLimit - usedAmount;
    }

    @Override
    public boolean pay(int amount) {
        if (amount <= 0) {
            System.out.println("유효하지 않은 결제 금액입니다.");
            return false;
        }
        if (getRemainingLimit() < amount) {
            System.out.println("카드 한도 초과. 남은 한도: "
                             + getRemainingLimit() + "원, 요청: " + amount + "원");
            return false;
        }
        usedAmount += amount;
        // 카드 번호 뒷 4자리만 표시 (보안)
        String masked = "**** **** **** "
                      + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("카드(" + masked + ") 승인 완료: "
                         + amount + "원 (남은 한도: " + getRemainingLimit() + "원)");
        return true;
    }
}
