package model;

import model.order.Order;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 판매 내역 영속화. 보고서의 sales.txt 파일 I/O.
// 파일 I/O 요구사항을 담당하는 유일한 클래스 (단일 책임 원칙).
// try-with-resources로 BufferedReader/BufferedWriter 안전하게 사용.
// IOException은 검사 예외 → 호출부에 적절히 전파하거나 사용자에게 메시지로 표시.
// 여러 곳에서 동시에 save() 호출될 수 있으므로 메서드 단위 synchronized.
//
// 인코딩: 한글이 포함된 상품명을 OS 기본 인코딩(Windows=CP949) 에 맡기면
// 다른 환경에서 깨질 수 있다. OutputStreamWriter/InputStreamReader 로
// 명시적으로 UTF-8 을 지정해 환경 독립성을 확보한다.
public class SalesRepository {

    private static final String FILE_PATH = "data/sales.txt";
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 제조 완료된 주문 1건을 sales.txt에 append
    // 형식: yyyy-MM-dd HH:mm:ss | orderId | productName | price | paymentType
    public synchronized void save(Order completedOrder) {
        String paymentType = completedOrder.getPayment()
                                           .getClass()
                                           .getSimpleName();
        String record = completedOrder.getCreatedAt().format(FORMATTER)
                + " | " + completedOrder.getOrderId()
                + " | " + completedOrder.getProduct().getName()
                + " | " + completedOrder.getProduct().getPrice()
                + " | " + paymentType;

        // try-with-resources: 자동으로 파일 닫아줌.
        // FileOutputStream(append=true) + OutputStreamWriter(UTF_8) 로 인코딩 명시.
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(FILE_PATH, true), StandardCharsets.UTF_8))) {
            writer.write(record);
            writer.newLine();
            System.out.println("[판매 저장] " + record);
        } catch (IOException e) {
            System.out.println("판매 기록 저장 실패: " + e.getMessage());
        }
    }

    // 파일을 읽어 List<String>으로 반환 → AdminView가 표시
    public synchronized List<String> loadAll() {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) records.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("판매 기록 파일이 없습니다. (첫 실행 시 정상)");
        } catch (IOException e) {
            System.out.println("판매 기록 읽기 실패: " + e.getMessage());
        }
        return records;
    }
}
