package model.payment;

// 현금 결제. Payment 구현체.
// 투입된 금액(insertedAmount)을 보관하고, pay() 호출 시 차감한다.
// 잔액이 부족하면 false 반환.
public class CashPayment implements Payment {

    private int insertedAmount; // 현재까지 투입한 금액

    // 돈 투입 (여러 번 나눠서 넣을 수 있음)
    public void insert(int money) {
        if (money <= 0) {
            System.out.println("유효하지 않은 금액입니다.");
            return;
        }
        insertedAmount += money;
        System.out.println(money + "원 투입. 현재 투입액: " + insertedAmount + "원");
    }

    // 거스름돈 계산 (pay() 성공 후 호출)
    public int getChange(int amount) {
        return Math.max(0, insertedAmount - amount);
    }

    public int getInsertedAmount() { return insertedAmount; }

    @Override
    public boolean pay(int amount) {
        if (insertedAmount < amount) {
            System.out.println("금액 부족. 투입: " + insertedAmount
                             + "원, 필요: " + amount + "원");
            return false;
        }
        int change = getChange(amount);
        insertedAmount = 0; // 결제 후 초기화
        System.out.println("현금 결제 완료! 거스름돈: " + change + "원");
        return true;
    }
}
