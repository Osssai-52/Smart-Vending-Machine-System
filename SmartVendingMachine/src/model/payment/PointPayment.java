package model.payment;

// 포인트 결제. Payment 구현체.
// 사용자 포인트 잔액을 보관하고, pay() 호출 시 차감한다.
// 포인트 부족 시 false 반환.
public class PointPayment implements Payment {

    private int point; // 보유 포인트

    public PointPayment(int point) {
        this.point = point;
    }

    public int getPoint() { return point; }

    @Override
    public boolean pay(int amount) {
        if (amount <= 0) {
            System.out.println("유효하지 않은 결제 금액입니다.");
            return false;
        }
        if (point < amount) {
            System.out.println("포인트 부족. 보유: " + point
                             + "P, 필요: " + amount + "P");
            return false;
        }
        point -= amount;
        System.out.println("포인트 결제 완료! 사용: " + amount
                         + "P, 남은 포인트: " + point + "P");
        return true;
    }
}
