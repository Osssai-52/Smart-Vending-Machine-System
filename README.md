# Smart Vending Machine System

2026학년도 1학기 객체지향프로그래밍 팀 23 프로젝트.

- **주제**: 스마트 자판기 시스템 (상품 선택 → 결제 → 제조 대기 → 수령 + 관리자 기능)
- **팀원**: 김남주(View) · 함수아(Model) · 최서영(Controller)

## 폴더 구조

```
Smart-Vending-Machine-System/
├── data/
│   └── sales.txt                       # 판매 내역 (파일 I/O 대상)
├── src/
│   ├── Main.java                       # 진입점. 의존성 조립.
│   ├── model/                          # 함수아
│   │   ├── product/
│   │   │   ├── Product.java            # 추상 클래스: 모든 상품 공통
│   │   │   ├── Drink.java              # 추상 클래스: 음료 공통
│   │   │   ├── Coffee.java             # Drink 상속, make() 오버라이드
│   │   │   ├── Tea.java                # Drink 상속, make() 오버라이드
│   │   │   ├── Smoothie.java           # Drink 상속, make() 오버라이드
│   │   │   └── Snack.java              # Product 직접 상속
│   │   ├── payment/
│   │   │   ├── Payment.java            # 인터페이스: boolean pay(int)
│   │   │   ├── CashPayment.java
│   │   │   ├── CardPayment.java
│   │   │   └── PointPayment.java
│   │   ├── order/
│   │   │   ├── OrderStatus.java        # enum: WAITING / MAKING / DONE / CANCELED
│   │   │   ├── Order.java              # 주문 1건의 데이터
│   │   │   └── OrderQueue.java         # Queue<Order> + synchronized
│   │   ├── Inventory.java              # Map<String, Product>
│   │   ├── SalesRepository.java        # sales.txt 파일 I/O
│   │   └── MakerThread.java            # 제조 스레드 (synchronized)
│   ├── view/                           # 김남주
│   │   ├── MainMenuView.java           # 메뉴 + 상품 선택
│   │   ├── PaymentView.java            # 결제 화면
│   │   ├── OrderStatusView.java        # 제조 대기 현황 (StatusViewListener 구현)
│   │   ├── AdminView.java              # 관리자 화면
│   │   └── StatusViewListener.java     # MakerThread → View 콜백 인터페이스
│   └── controller/                     # 최서영
│       ├── OrderController.java        # createOrder / cancelOrder
│       ├── PaymentController.java      # pay() → Payment 위임
│       ├── InventoryController.java    # reduceStock / addStock
│       ├── MachineController.java      # startMaker / notifyView / 리스너 관리
│       └── AdminController.java        # loadSales / refreshAdminView
└── README.md
```

## OOP 5대 평가 범주 대응 (과제공지 §6)

1. **추상화 및 확장성**: `Product`/`Drink` 추상 클래스 + `Payment` 인터페이스. 새 음료·결제 수단 추가 시 컨트롤러 코드 수정 불필요 (OCP).
2. **정보 은닉**: 모든 도메인 필드는 `private`. Inventory 의 `Map` 자체는 외부 노출 금지(`unmodifiableMap`).
3. **역할 분리**: View → Controller → Model 단방향 의존. MakerThread 와 View 사이는 `MachineController` + `StatusViewListener` 가 중개.
4. **안정성 및 자원 관리**: `OrderQueue` / `Inventory` 의 `synchronized` 블록, 파일 I/O 의 `try-with-resources`, MakerThread 의 `InterruptedException` 처리.
5. **친절한 코딩**: camelCase, 단일 책임 메서드, 모든 파일 상단에 책임 주석.

## 실행

```powershell
# 컴파일
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)

# 실행
java -cp out Main
```
