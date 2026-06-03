package view;

import controller.MachineController;
import controller.OrderController;
import javax.swing.*;
import java.awt.*;

public class OrderStatusView extends JFrame implements StatusViewListener {
    private final MachineController machineController;
    private final OrderController orderController;
    private JLabel currentProductLabel;
    private JTextArea queueDisplayArea;
    private JTextField cancelOrderIdField;
    private JButton cancelButton;

    public OrderStatusView(MachineController machineController, OrderController orderController) {
        this.machineController = machineController;
        this.orderController = orderController;

        setTitle("실시간 음료 제조 모니터링 시스템");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JPanel northPanel = new JPanel(new BorderLayout());
        currentProductLabel = new JLabel("대기 중...", SwingConstants.CENTER);
        currentProductLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        northPanel.add(currentProductLabel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        queueDisplayArea = new JTextArea();
        queueDisplayArea.setEditable(false);
        queueDisplayArea.setFont(new Font("D2Coding", Font.PLAIN, 13));
        centerPanel.add(new JScrollPane(queueDisplayArea), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 주문 취소: 화면 하단에 취소할 주문 번호 입력 + 버튼.
        // 주문 번호는 queueDisplayArea 의 "[주문 #N] ..." 표시에서 사용자가 확인한다.
        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        cancelOrderIdField = new JTextField(6);
        cancelButton = new JButton("주문 취소");
        cancelPanel.add(new JLabel("취소할 주문 번호:"));
        cancelPanel.add(cancelOrderIdField);
        cancelPanel.add(cancelButton);
        add(cancelPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> handleCancel());

        machineController.registerStatusView(this);
        setVisible(true);
    }

    @Override
    public void onOrderStatusChanged(String currentStatus, String queueDetails) {
        SwingUtilities.invokeLater(() -> {
            if (currentStatus == null || currentStatus.isEmpty()) {
                currentProductLabel.setText("모든 제조 완료 / 대기 중");
            } else {
                currentProductLabel.setText("☕ " + currentStatus + " 제조 중...");
            }

            if (queueDetails == null || queueDetails.isEmpty()) {
                queueDisplayArea.setText(" 현재 대기 중인 주문이 없습니다.");
            } else {
                queueDisplayArea.setText(queueDetails);
            }
        });
    }

    /**
     * "주문 취소" 버튼 클릭 처리.
     * 사용자가 입력한 주문 번호로 Controller 에게 취소를 요청.
     * 성공/실패 시 사용자에게 메시지 박스로 결과 알림.
     */
    private void handleCancel() {
        String idText = cancelOrderIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "주문 번호를 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "숫자만 입력 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = orderController.cancelOrderById(orderId);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "주문 #" + orderId + " 이(가) 취소되었습니다.",
                    "완료", JOptionPane.INFORMATION_MESSAGE);
            cancelOrderIdField.setText("");
            // 화면 갱신은 OrderController.cancelOrder 가 machineController.onMakerEvent 로
            // broadcast 하므로 별도 호출 불필요.
        } else {
            JOptionPane.showMessageDialog(this,
                    "취소할 수 없는 주문입니다. (이미 제조 중이거나 존재하지 않음)",
                    "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
