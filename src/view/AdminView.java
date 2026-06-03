package view;


import controller.AdminController;
import model.product.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JFrame {
    private final AdminController adminController;
    private JTextField productIdInput;
    private JTextField stockCountInput;
    private JTextArea salesLogArea;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton replenishButton;
    private JButton loadSalesButton;
    private JButton mainMenuButton;

    public AdminView(AdminController adminController) {
        this.adminController = adminController;

        setTitle("스마트 자판기 시스템 - 관리자 모드");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JLabel titleLabel = new JLabel("⚙️ 자판기 관리자 컨트롤 시스템 ⚙️", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        
        String[] columnNames = {"상품 ID", "상품명", "현재 재고"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
        leftPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        centerPanel.add(leftPanel);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        salesLogArea = new JTextArea();
        salesLogArea.setEditable(false);
        rightPanel.add(new JScrollPane(salesLogArea), BorderLayout.CENTER);
        centerPanel.add(rightPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        productIdInput = new JTextField(6);
        stockCountInput = new JTextField(6);
        replenishButton = new JButton("재고 보충");

        inputRow.add(new JLabel("상품 ID:"));
        inputRow.add(productIdInput);
        inputRow.add(new JLabel("보충 수량:"));
        inputRow.add(stockCountInput);
        inputRow.add(replenishButton);
        bottomPanel.add(inputRow);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        loadSalesButton = new JButton("매출 내역 불러오기");
        mainMenuButton = new JButton("메인으로");
        buttonRow.add(loadSalesButton);
        buttonRow.add(mainMenuButton);
        bottomPanel.add(buttonRow);
        add(bottomPanel, BorderLayout.SOUTH);

        replenishButton.addActionListener(e -> handleReplenish());
        loadSalesButton.addActionListener(e -> handleLoadSales());
        mainMenuButton.addActionListener(e -> dispose());

        refreshInventoryTable();
        setVisible(true);
    }

    private void handleReplenish() {
        String productId = productIdInput.getText().trim();
        String countText = stockCountInput.getText().trim();

        if (productId.isEmpty() || countText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모두 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isSuccess = adminController.replenishInventory(productId, countText);
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "재고가 보충되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
            productIdInput.setText("");
            stockCountInput.setText("");
            refreshInventoryTable();
        } else {
            JOptionPane.showMessageDialog(this, "재고 보충 실패 수량을 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoadSales() {
        String salesLog = adminController.loadSales();
        if (salesLog == null || salesLog.isEmpty()) {
            salesLogArea.setText("기록된 매출 내역 파일이 없습니다.");
        } else {
            salesLogArea.setText(salesLog);
        }
    }

    private void refreshInventoryTable() {
        tableModel.setRowCount(0);
        List<Product> productList = adminController.getAllProducts();
        if (productList != null) {
            for (Product p : productList) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getStock() + "개"});
            }
        }
    }
}