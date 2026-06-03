package view;


import controller.AdminController;
import controller.MachineController;
import model.product.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JFrame implements StatusViewListener {
    private final AdminController adminController;
    private final MachineController machineController;
    private JTextField productNameInput;
    private JTextField stockCountInput;
    private JTextArea salesLogArea;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton replenishButton;
    private JButton loadSalesButton;
    private JButton mainMenuButton;

    public AdminView(AdminController adminController, MachineController machineController) {
        this.adminController = adminController;
        this.machineController = machineController;

        setTitle("스마트 자판기 시스템 - 관리자 모드");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        JLabel titleLabel = new JLabel("⚙️ 자판기 관리자 컨트롤 시스템 ⚙️", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        
        String[] columnNames = {"상품명", "가격", "현재 재고"};
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
        productNameInput = new JTextField(12);
        stockCountInput = new JTextField(6);
        replenishButton = new JButton("재고 보충");

        inputRow.add(new JLabel("상품명:"));
        inputRow.add(productNameInput);
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

        // 사용자 결제로 재고가 줄어들 때마다 표가 자동으로 갱신되도록 리스너 등록.
        // MachineController 가 MakerThread 이벤트를 받아 EDT 위에서 broadcast 한다.
        machineController.registerStatusView(this);

        refreshInventoryTable();
        setVisible(true);
    }

    /**
     * MakerThread 의 상태 변화 알림.
     * 본 View 는 표시할 currentStatus / queueDetails 가 따로 없고,
     * "재고가 줄었을 수 있다" 는 신호로만 사용 → 재고표를 다시 그린다.
     * MachineController 가 이미 EDT 에서 broadcast 하므로 invokeLater 불필요.
     */
    @Override
    public void onOrderStatusChanged(String currentStatus, String queueDetails) {
        refreshInventoryTable();
    }

    /**
     * 창이 닫힐 때(메인으로 버튼 or X 버튼) 리스너 해제.
     * 안 하면 닫힌 후에도 MachineController 가 계속 알림을 보내 메모리 누수가 된다.
     */
    @Override
    public void dispose() {
        machineController.unregisterStatusView(this);
        super.dispose();
    }

    private void handleReplenish() {
        String productName = productNameInput.getText().trim();
        String countText = stockCountInput.getText().trim();

        if (productName.isEmpty() || countText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모두 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isSuccess = adminController.replenishInventory(productName, countText);
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "재고가 보충되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
            productNameInput.setText("");
            stockCountInput.setText("");
            refreshInventoryTable();
        } else {
            JOptionPane.showMessageDialog(this, "재고 보충 실패 (상품명/수량을 확인하세요).", "오류", JOptionPane.ERROR_MESSAGE);
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
                tableModel.addRow(new Object[]{p.getName(), p.getPrice() + "원", p.getStock() + "개"});
            }
        }
    }
}