package view;

import controller.MachineController;
import javax.swing.*;
import java.awt.*;

public class OrderStatusView extends JFrame implements StatusViewListener {
    private final MachineController machineController;
    private JLabel currentProductLabel;
    private JTextArea queueDisplayArea;

    public OrderStatusView(MachineController machineController) {
        this.machineController = machineController;

        setTitle("실시간 음료 제조 모니터링 시스템");
        setSize(500, 400);
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
}