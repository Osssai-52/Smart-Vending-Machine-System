package view;

import controller.OrderController;
import controller.AdminController;
import controller.MachineController;
import model.product.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainMenuView extends JFrame {
    private final OrderController orderController;
    private final AdminController adminController;
    private final MachineController machineController;   // AdminView 가 리스너 등록할 때 필요
    private JTextArea menuDisplayArea;
    private JTextField productIdInput;
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

        JLabel titleLabel = new JLabel("🥤 SMART VENDING MACHINE 🥤", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        menuDisplayArea = new JTextArea();
        menuDisplayArea.setEditable(false);
        menuDisplayArea.setFont(new Font("D2Coding", Font.PLAIN, 14));
        add(new JScrollPane(menuDisplayArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        productIdInput = new JTextField(8);
        orderButton = new JButton("주문하기");
        adminButton = new JButton("관리자 모드 진입");

        bottomPanel.add(new JLabel("상품 ID 입력:"));
        bottomPanel.add(productIdInput);
        bottomPanel.add(orderButton);
        bottomPanel.add(adminButton);
        add(bottomPanel, BorderLayout.SOUTH);

        orderButton.addActionListener(e -> handleOrderAction());
        adminButton.addActionListener(e -> handleAdminAction());

        refreshMenuDisplay();
        setVisible(true);
    }

    public void refreshMenuDisplay() {
        menuDisplayArea.setText("");
        menuDisplayArea.append(" ====================================================\n");
        menuDisplayArea.append("   상품 ID\t상품 이름\t\t가격\t재고 상태\n");
        menuDisplayArea.append(" ====================================================\n");

        List<Product> products = orderController.getAvailableProducts();
        if (products == null || products.isEmpty()) {
            menuDisplayArea.append("   현재 자판기에 등록된 상품이 없습니다.\n");
        } else {
            for (Product p : products) {
                String stockText = p.getStock() > 0 ? p.getStock() + "개" : "품절";
                menuDisplayArea.append(String.format("   [%s]\t%-15s\t%d원\t%s\n", 
                        p.getId(), p.getName(), p.getPrice(), stockText));
            }
        }
        menuDisplayArea.append(" ====================================================\n");
    }

    private void handleOrderAction() {
        String productId = productIdInput.getText().trim();
        if (productId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "상품 ID를 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isOrderCreated = orderController.createOrder(productId, "GuestUser");
        if (isOrderCreated) {
            productIdInput.setText("");
            new PaymentView(this, orderController, productId);
        } else {
            JOptionPane.showMessageDialog(this, "존재하지 않거나 품절된 상품입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdminAction() {
        new AdminView(adminController, machineController);
    }
}