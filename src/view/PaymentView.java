package view;

import controller.OrderController;
import javax.swing.*;
import java.awt.*;

public class PaymentView extends JDialog {
    private final MainMenuView parentView;
    private final OrderController orderController;
    private final String productId;

    private JRadioButton cashRadio;
    private JRadioButton cardRadio;
    private JRadioButton pointRadio;
    private JTextField amountField;
    private JButton payButton;
    private JButton cancelButton;
    private JLabel infoLabel;

    public PaymentView(MainMenuView parentView, OrderController orderController, String productId) {
        super(parentView, "결제 진행 단계", true);
        this.parentView = parentView;
        this.orderController = orderController;
        this.productId = productId;

        setSize(420, 280);
        setLocationRelativeTo(parentView);
        setLayout(new BorderLayout(10, 10));

        infoLabel = new JLabel("결제 수단을 선택하고 투입 금액을 입력하세요.", SwingConstants.CENTER);
        add(infoLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        cashRadio = new JRadioButton("현금 (CASH)", true);
        cardRadio = new JRadioButton("카드 (CARD)");
        pointRadio = new JRadioButton("포인트 (POINT)");

        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(cashRadio);
        paymentGroup.add(cardRadio);
        paymentGroup.add(pointRadio);

        radioPanel.add(cashRadio);
        radioPanel.add(cardRadio);
        radioPanel.add(pointRadio);
        centerPanel.add(radioPanel);

        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        amountField = new JTextField(12);
        amountPanel.add(new JLabel("투입 금액: "));
        amountPanel.add(amountField);
        centerPanel.add(amountPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        payButton = new JButton("결제하기");
        cancelButton = new JButton("취소");
        bottomPanel.add(payButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);

        cashRadio.addActionListener(e -> amountField.setEnabled(true));
        cardRadio.addActionListener(e -> { amountField.setText(""); amountField.setEnabled(false); });
        pointRadio.addActionListener(e -> { amountField.setText(""); amountField.setEnabled(false); });

        payButton.addActionListener(e -> executePayment());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void executePayment() {
        String paymentType = "CASH";
        if (cardRadio.isSelected()) paymentType = "CARD";
        if (pointRadio.isSelected()) paymentType = "POINT";

        int amount = 0;
        if (cashRadio.isSelected()) {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "현금을 투입해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                amount = Integer.parseInt(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자만 입력 가능합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        boolean isSuccess = orderController.processPaymentAndManufacture(paymentType, amount);
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "결제가 정상 승인되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            parentView.refreshMenuDisplay();
            dispose();
            new OrderStatusView(orderController.getMachineController());
        } else {
            JOptionPane.showMessageDialog(this, "결제 실패: 잔액을 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}