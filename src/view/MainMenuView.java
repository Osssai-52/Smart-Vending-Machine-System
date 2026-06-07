package view;

import controller.OrderController;
import controller.AdminController;
import controller.MachineController;
import model.product.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainMenuView extends JFrame {
    private final OrderController orderController;
    private final AdminController adminController;
    private final MachineController machineController;   // AdminView 가 리스너 등록할 때 필요
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField productNameInput;
    private JButton orderButton;
    private JButton adminButton;

    public MainMenuView(OrderController orderController,
                        AdminController adminController,
                        MachineController machineController) {
        this.orderController = orderController;
        this.adminController = adminController;
        this.machineController = machineController;

        setTitle("스마트 웰니스 자판기 시스템");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("SMART VENDING MACHINE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"상품 이름", "가격", "재고 상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // 사용자가 표 셀을 직접 편집하지 못하게
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        menuTable.setRowHeight(28);
        add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        productNameInput = new JTextField(12);
        orderButton = new JButton("주문하기");
        adminButton = new JButton("관리자 모드 진입");

        bottomPanel.add(new JLabel("상품명 입력:"));
        bottomPanel.add(productNameInput);
        bottomPanel.add(orderButton);
        bottomPanel.add(adminButton);
        add(bottomPanel, BorderLayout.SOUTH);

        orderButton.addActionListener(e -> handleOrderAction());
        adminButton.addActionListener(e -> handleAdminAction());

        refreshMenuDisplay();
        setVisible(true);
    }

    public void refreshMenuDisplay() {
        tableModel.setRowCount(0);   // 기존 행 전부 삭제 후 다시 채움
        List<Product> products = orderController.getAvailableProducts();
        if (products != null) {
            for (Product p : products) {
                String stockText = p.getStock() > 0 ? p.getStock() + "개" : "품절";
                tableModel.addRow(new Object[]{
                    p.getName(),
                    p.getPrice() + "원",
                    stockText
                });
            }
        }
    }

    private void handleOrderAction() {
        String productName = productNameInput.getText().trim();
        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "상품명을 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isOrderCreated = orderController.createOrder(productName, "GuestUser");
        if (isOrderCreated) {
            productNameInput.setText("");
            new PaymentView(this, orderController, productName);
        } else {
            JOptionPane.showMessageDialog(this, "존재하지 않거나 품절된 상품입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminAction() {
        // JPasswordField + showConfirmDialog 조합으로 비밀번호를 마스킹 입력받는다.
        // (JOptionPane.showInputDialog 는 평문 노출이라 사용 안 함)
        JPasswordField pwField = new JPasswordField(20);
        int option = JOptionPane.showConfirmDialog(
                this, pwField,
                "관리자 인증 — 비밀번호를 입력하세요",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;   // 사용자 취소
        }

        String password = new String(pwField.getPassword());
        if (!adminController.authenticate(password)) {
            JOptionPane.showMessageDialog(this,
                    "비밀번호가 일치하지 않습니다.",
                    "인증 실패", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new AdminView(adminController, machineController);
    }
}