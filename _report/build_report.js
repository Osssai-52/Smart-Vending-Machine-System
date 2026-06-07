// 스마트 자판기 시스템 최종보고서 8슬라이드
// 팀23 — 김남주(View) · 함수아(Model) · 최서영(Controller)
//
// 6·7 슬라이드는 실행 예시 스크린샷 자리 (제목+placeholder만)

const pptxgen = require('pptxgenjs');
const pres = new pptxgen();

pres.layout = 'LAYOUT_16x9';   // 10" × 5.625"
pres.title = '스마트 자판기 시스템 최종보고서';
pres.author = '팀23';

// ───────────────────────────────────────────────────────────
// 팔레트 (Midnight Executive + warm amber accent)
// ───────────────────────────────────────────────────────────
const NAVY      = '1E2761';     // primary
const NAVY_DARK = '13193F';
const ICE       = 'CADCFC';     // secondary
const PANEL     = 'F4F6FB';     // very light blue-gray panel
const AMBER     = 'E8AA42';     // warm accent
const TEXT      = '1A1A1A';
const MUTED     = '6B7280';
const WHITE     = 'FFFFFF';
const BORDER    = 'D8DDE8';

// 한글 안전 폰트
const HEAD_FONT = 'Malgun Gothic';   // 헤더용
const BODY_FONT = 'Malgun Gothic';   // 본문용

// ───────────────────────────────────────────────────────────
// 공통 헬퍼
// ───────────────────────────────────────────────────────────
function pageHeader(slide, title, subtitle, pageNo) {
    // 좌측 페이지 번호 배지
    slide.addShape(pres.shapes.RECTANGLE, {
        x: 0.4, y: 0.32, w: 0.5, h: 0.4,
        fill: { color: NAVY }, line: { color: NAVY }
    });
    slide.addText(String(pageNo).padStart(2, '0'), {
        x: 0.4, y: 0.32, w: 0.5, h: 0.4,
        fontFace: HEAD_FONT, fontSize: 14, bold: true,
        color: WHITE, align: 'center', valign: 'middle', margin: 0
    });
    // 제목
    slide.addText(title, {
        x: 1.0, y: 0.28, w: 7.5, h: 0.45,
        fontFace: HEAD_FONT, fontSize: 22, bold: true,
        color: NAVY, valign: 'middle', margin: 0
    });
    // 부제
    if (subtitle) {
        slide.addText(subtitle, {
            x: 1.0, y: 0.72, w: 7.5, h: 0.28,
            fontFace: BODY_FONT, fontSize: 11, italic: true,
            color: MUTED, valign: 'top', margin: 0
        });
    }
    // 제목 아래 얇은 점선 구분선 대신 호리즌탈 라인 (얇게)
    slide.addShape(pres.shapes.LINE, {
        x: 0.4, y: 1.08, w: 9.2, h: 0,
        line: { color: BORDER, width: 0.5 }
    });
}

function pageFooter(slide) {
    slide.addText('팀 23 · 스마트 자판기 시스템 · 객체지향프로그래밍 최종보고서', {
        x: 0.4, y: 5.3, w: 9.2, h: 0.25,
        fontFace: BODY_FONT, fontSize: 9, color: MUTED,
        align: 'left', valign: 'middle', margin: 0
    });
}

// 박스 카드: 제목 + 줄리스트
function classCard(slide, opts) {
    const { x, y, w, h, title, items, accent } = opts;
    const ac = accent || NAVY;
    // 배경
    slide.addShape(pres.shapes.RECTANGLE, {
        x, y, w, h,
        fill: { color: WHITE }, line: { color: BORDER, width: 0.75 }
    });
    // 좌측 컬러 액센트 바
    slide.addShape(pres.shapes.RECTANGLE, {
        x, y, w: 0.07, h,
        fill: { color: ac }, line: { color: ac }
    });
    // 제목
    slide.addText(title, {
        x: x + 0.18, y: y + 0.06, w: w - 0.24, h: 0.3,
        fontFace: HEAD_FONT, fontSize: 12, bold: true,
        color: NAVY, valign: 'middle', margin: 0
    });
    // 아이템들
    const itemTexts = items.map((t, i) => ({
        text: t,
        options: { breakLine: i < items.length - 1 }
    }));
    slide.addText(itemTexts, {
        x: x + 0.18, y: y + 0.38, w: w - 0.24, h: h - 0.42,
        fontFace: BODY_FONT, fontSize: 9.5, color: TEXT,
        valign: 'top', margin: 0, paraSpaceAfter: 2
    });
}

// 화살표 라인
function arrow(slide, x1, y1, x2, y2, color) {
    slide.addShape(pres.shapes.LINE, {
        x: x1, y: y1, w: x2 - x1, h: y2 - y1,
        line: {
            color: color || NAVY, width: 1.5,
            endArrowType: 'triangle'
        }
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 1 — 표지 + 개요
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: NAVY };

    // 우측 장식 — 자판기 모티프 (격자)
    const colX = 6.4, colY = 0.7;
    for (let r = 0; r < 4; r++) {
        for (let c = 0; c < 3; c++) {
            const isAccent = (r === 1 && c === 1);
            s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
                x: colX + c * 1.05, y: colY + r * 1.05,
                w: 0.95, h: 0.95,
                fill: { color: isAccent ? AMBER : NAVY_DARK },
                line: { color: isAccent ? AMBER : ICE, width: 0.5 },
                rectRadius: 0.1
            });
        }
    }

    // 좌측 — 메인 타이틀
    s.addText('스마트 자판기 시스템', {
        x: 0.6, y: 1.3, w: 5.6, h: 0.7,
        fontFace: HEAD_FONT, fontSize: 38, bold: true,
        color: WHITE, valign: 'top', margin: 0
    });
    s.addText('Smart Vending Machine System', {
        x: 0.6, y: 2.05, w: 5.6, h: 0.4,
        fontFace: 'Calibri', fontSize: 16,
        color: ICE, italic: true, valign: 'top', margin: 0
    });

    // 부제 — 객체지향프로그래밍
    s.addText('객체지향프로그래밍 · 최종보고서', {
        x: 0.6, y: 2.55, w: 5.6, h: 0.35,
        fontFace: BODY_FONT, fontSize: 13,
        color: ICE, valign: 'top', margin: 0
    });

    // 액센트 바
    s.addShape(pres.shapes.RECTANGLE, {
        x: 0.6, y: 3.05, w: 0.5, h: 0.06,
        fill: { color: AMBER }, line: { color: AMBER }
    });

    // 팀 정보
    s.addText([
        { text: '팀 23  ',          options: { bold: true, color: AMBER } },
        { text: '인공지능공학부',     options: { color: ICE } }
    ], {
        x: 0.6, y: 3.25, w: 5.6, h: 0.35,
        fontFace: BODY_FONT, fontSize: 13, valign: 'top', margin: 0
    });
    s.addText('함수아 (Model)  ·  김남주 (View)  ·  최서영 (Controller)', {
        x: 0.6, y: 3.6, w: 5.6, h: 0.35,
        fontFace: BODY_FONT, fontSize: 12, color: WHITE,
        valign: 'top', margin: 0
    });

    // 하단 — 핵심 기능 4칸
    const features = [
        '상품 선택 · 주문',
        '결제 (현금/카드/포인트)',
        '실시간 제조 모니터링',
        '관리자 (재고/매출)'
    ];
    features.forEach((t, i) => {
        const fx = 0.6 + i * 1.55;
        s.addShape(pres.shapes.ROUNDED_RECTANGLE, {
            x: fx, y: 4.6, w: 1.4, h: 0.55,
            fill: { color: NAVY_DARK }, line: { color: ICE, width: 0.5 },
            rectRadius: 0.05
        });
        s.addText(t, {
            x: fx, y: 4.6, w: 1.4, h: 0.55,
            fontFace: BODY_FONT, fontSize: 9.5, color: WHITE,
            align: 'center', valign: 'middle', margin: 0
        });
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 2 — 전체 Diagram
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, '전체 아키텍처 — MVC 구조', 'View → Controller → Model 단방향 의존 + 멀티스레드 콜백 흐름', 2);
    pageFooter(s);

    // 3-column MVC 다이어그램 (View / Controller / Model)
    const top = 1.35, boxH = 2.45, boxW = 2.55;

    // VIEW 박스
    classCard(s, {
        x: 0.55, y: top, w: boxW, h: boxH,
        title: 'View  (김남주)',
        accent: AMBER,
        items: [
            'MainMenuView',
            '   메뉴 + 상품명 입력',
            'PaymentView (JDialog)',
            '   현금/카드/포인트',
            'OrderStatusView',
            '   제조 대기 + 취소',
            'AdminView',
            '   재고표 + 매출 + 보충',
            'StatusViewListener',
            '   콜백 인터페이스'
        ]
    });

    // CONTROLLER 박스
    classCard(s, {
        x: 3.72, y: top, w: boxW, h: boxH,
        title: 'Controller  (최서영)',
        accent: NAVY,
        items: [
            'OrderController',
            '   주문 검증 + 결제 + 큐 등록',
            'PaymentController',
            '   Payment 다형성 위임',
            'InventoryController',
            '   재고 차감/보충',
            'MachineController',
            '   MakerThread 라이프사이클',
            'AdminController',
            '   인증 + 매출 조회'
        ]
    });

    // MODEL 박스
    classCard(s, {
        x: 6.9, y: top, w: boxW, h: boxH,
        title: 'Model  (함수아)',
        accent: '6B7CB8',
        items: [
            'Product (abstract)',
            '   Drink → Coffee/Tea/Smoothie',
            '   Snack',
            'Payment (interface)',
            '   Cash/Card/Point',
            'Order, OrderQueue, OrderStatus',
            'Inventory  (Map<String,Product>)',
            'SalesRepository  (sales.txt I/O)',
            'MakerThread  (synchronized)'
        ]
    });

    // 화살표: View → Controller → Model
    arrow(s, 3.12, 2.55, 3.65, 2.55, NAVY);
    arrow(s, 6.3, 2.55, 6.83, 2.55, NAVY);

    // 하단 — 콜백 (점선)
    arrow(s, 6.9, 4.05, 4.6, 4.05, AMBER);   // Model(MakerThread) → Controller(Machine)
    arrow(s, 3.72, 4.05, 1.7, 4.05, AMBER);  // Controller(Machine) → View(Listener)
    s.addText('상태 변화 콜백 (MakerThread → MachineController → broadcast → View)', {
        x: 0.55, y: 4.3, w: 9.0, h: 0.3,
        fontFace: BODY_FONT, fontSize: 10, color: AMBER, italic: true,
        align: 'center', valign: 'middle', margin: 0
    });

    // 사용 기술
    s.addText('Java · Swing · MVC · 멀티스레드(synchronized + wait/notify) · 파일 I/O(UTF-8) · 컬렉션/제네릭', {
        x: 0.55, y: 4.75, w: 9.0, h: 0.35,
        fontFace: BODY_FONT, fontSize: 10, color: MUTED,
        align: 'center', valign: 'middle', margin: 0
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 3 — Model (함수아)
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, 'Model — 함수아', '도메인 데이터 · 핵심 로직 · 파일 영속화 · 작업 스레드', 3);
    pageFooter(s);

    // 좌측: Class Diagram (간이)
    classCard(s, {
        x: 0.55, y: 1.35, w: 5.4, h: 2.4,
        title: '클래스 다이어그램',
        accent: '6B7CB8',
        items: [
            'Product (abstract)',
            '   ├─ Drink (abstract, ml/isHot/brewTime)',
            '   │     ├─ Coffee   make() override',
            '   │     ├─ Tea      make() override',
            '   │     └─ Smoothie make() override',
            '   └─ Snack            make() override',
            '',
            'Payment (interface)   boolean pay(int amount)',
            '   ├─ CashPayment    (insertedAmount, getChange)',
            '   ├─ CardPayment    (cardNumber, creditLimit)',
            '   └─ PointPayment   (point)',
            '',
            'Order (Product, Payment, OrderStatus, createdAt)',
            'OrderStatus  enum  WAITING → MAKING → DONE / CANCELED',
            'OrderQueue   Queue<Order>  synchronized + wait/notifyAll',
            'Inventory    Map<String,Product>  unmodifiableMap',
            'SalesRepository  sales.txt  UTF-8 try-with-resources',
            'MakerThread  extends Thread  interrupt 종료'
        ]
    });

    // 우측 상단: 5범주 자기 대응
    classCard(s, {
        x: 6.1, y: 1.35, w: 3.45, h: 2.4,
        title: 'OOP 5범주 자기 대응',
        accent: AMBER,
        items: [
            '① 추상화/확장성',
            '   Product 추상 + Payment 인터페이스',
            '   새 상품/결제 추가 시 변경 없음(OCP)',
            '',
            '② 정보 은닉',
            '   모든 필드 private (대부분 final)',
            '   Inventory Map → unmodifiableMap',
            '',
            '③ 역할 분리',
            '   Model 은 View/Controller 를 모름',
            '   MakerThread 도 View 타입 모름',
            '',
            '④ 안정성/자원 관리',
            '   synchronized + wait/notifyAll',
            '   try-with-resources, InterruptedException 재설정',
            '',
            '⑤ 가독성',
            '   OrderStatus enum, camelCase, 단일 책임'
        ]
    });

    // 하단 박스 — 동시성 다이어그램
    classCard(s, {
        x: 0.55, y: 3.85, w: 9.0, h: 1.3,
        title: '동시성 핵심',
        accent: NAVY,
        items: [
            'OrderQueue.enqueue (synchronized) → notifyAll()       ⇄       MakerThread.dequeue (synchronized) wait()',
            'Inventory.reduceStock / addStock — 메서드 단위 synchronized로 동시 접근 보호',
            'SalesRepository.save / loadAll — synchronized + OutputStreamWriter/InputStreamReader(UTF-8) + try-with-resources'
        ]
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 4 — View (김남주)
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, 'View — 김남주', '4개 Swing 화면 + StatusViewListener 콜백 (EDT 안전)', 4);
    pageFooter(s);

    // 4개 화면 박스 (2x2)
    const top = 1.35, gap = 0.15;
    const cardW = 2.85, cardH = 1.7;
    const grid = [
        { x: 0.55, y: top, t: 'MainMenuView  (JFrame)',
          items: [
              'JTextArea 메뉴 표시',
              'JTextField 상품명 입력',
              'JButton 주문/관리자',
              'refreshMenuDisplay()'
          ]},
        { x: 0.55 + cardW + gap, y: top, t: 'PaymentView  (JDialog · modal)',
          items: [
              'JRadioButton CASH/CARD/POINT',
              'JTextField 투입 금액',
              'executePayment()',
              '결제 후 parentView 갱신'
          ]},
        { x: 0.55, y: top + cardH + gap, t: 'OrderStatusView  (JFrame)',
          items: [
              'JLabel 현재 제조 상품',
              'JTextArea 대기열 표시',
              '주문 ID 입력 → 취소',
              'StatusViewListener 구현'
          ]},
        { x: 0.55 + cardW + gap, y: top + cardH + gap, t: 'AdminView  (JFrame)',
          items: [
              'JPasswordField 인증',
              'JTable 재고표 (자동 갱신)',
              'JTextArea 매출 내역',
              'StatusViewListener + dispose 해제'
          ]}
    ];
    grid.forEach(g => classCard(s, {
        x: g.x, y: g.y, w: cardW, h: cardH,
        title: g.t, accent: AMBER, items: g.items
    }));

    // 우측 정보 영역 — 단방향 흐름 + 5범주
    classCard(s, {
        x: 6.55, y: top, w: 3.0, h: cardH * 2 + gap,
        title: '핵심 설계',
        accent: NAVY,
        items: [
            '◇ 단방향 흐름',
            '   View → Controller (Model 직접 X)',
            '   화면 갱신은 View 가 직접',
            '',
            '◇ EDT 안전성',
            '   UI 갱신은 SwingUtilities.invokeLater',
            '   listener 콜백도 EDT 위에서',
            '',
            '◇ 결합도 ↓',
            '   PaymentView → orderController 만',
            '   getMachineController 같은 게터 X',
            '',
            '◇ 자원 관리',
            '   dispose() 에서 unregisterStatusView',
            '   리스너 누수 방지',
            '',
            '◇ 보안',
            '   JPasswordField (showInputDialog X)'
        ]
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 5 — Controller (최서영)
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, 'Controller — 최서영', '5개 컨트롤러 · 검증→위임 패턴 · MakerThread 라이프사이클', 5);
    pageFooter(s);

    // 5개 컨트롤러 — 좌측에 그리드
    const top = 1.35;
    const cw = 3.0, ch = 1.05, gap = 0.12;
    const ctrls = [
        { t: 'OrderController',
          items: ['createOrder(name, user)  검증', 'processPaymentAndManufacture()', 'cancelOrder / cancelOrderById', 'findProductByName  ID 매핑'] },
        { t: 'PaymentController',
          items: ['pay(Payment, amount)', 'Payment 인터페이스에 위임', '입력 검증 (amount > 0)'] },
        { t: 'InventoryController',
          items: ['reduceStock(id, qty)', 'addStock(id, qty)', '수량 검증 (qty > 0)'] },
        { t: 'MachineController',
          items: ['startMaker / stopMaker', 'onMakerEvent(order) → broadcast', 'CopyOnWriteArrayList<Listener>'] },
        { t: 'AdminController',
          items: ['authenticate(pw)  equals()', 'replenishInventory(name, qty)', 'loadSales() · getAllProducts()'] }
    ];

    // 2x3 grid (마지막 한 칸 비고 5개 들어감)
    ctrls.forEach((c, i) => {
        const col = i % 2, row = Math.floor(i / 2);
        classCard(s, {
            x: 0.55 + col * (cw + gap),
            y: top + row * (ch + gap),
            w: cw, h: ch,
            title: c.t,
            accent: NAVY,
            items: c.items
        });
    });

    // 우측 — 핵심 설계 + 5범주 매핑
    classCard(s, {
        x: 6.7, y: top, w: 2.85, h: ch * 3 + gap * 2,
        title: '핵심 설계 + 5범주',
        accent: AMBER,
        items: [
            '◇ 검증 → 위임 패턴',
            '   Controller 가 입력 검증',
            '   도메인 처리는 Model에 위임',
            '',
            '◇ 단방향 의존',
            '   View → Controller → Model',
            '   OrderController → MachineController',
            '   (취소 broadcast 용, 순환 없음)',
            '',
            '◇ Stateless',
            '   processPaymentAndManufacture',
            '   는 productName 받음',
            '',
            '◇ ① 추상화: OCP (Payment, Product)',
            '◇ ③ 결합도: View 가 도메인 모름',
            '◇ ④ 동시성: CopyOnWriteArrayList',
            '   synchronized(order),',
            '   ShutdownHook(stopMaker)'
        ]
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 6 — 실행 예시 ① (사용자 흐름)  ※ 스크린샷 자리
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, '실행 예시 ① — 사용자 흐름', '메뉴 → 상품명 주문 → 결제 → 실시간 제조 모니터링', 6);
    pageFooter(s);

    // 4개 스크린샷 placeholder (2x2)
    const top = 1.35;
    const w = 4.4, h = 1.7, gap = 0.2;
    const labels = [
        '①  MainMenuView — 메뉴 + 상품명 입력',
        '②  PaymentView — 결제 수단 선택 + 금액',
        '③  OrderStatusView — 제조 중 + 대기열',
        '④  콘솔 로그 — [제조] · [판매 저장]'
    ];
    labels.forEach((label, i) => {
        const col = i % 2, row = Math.floor(i / 2);
        const x = 0.55 + col * (w + gap);
        const y = top + row * (h + gap);

        // placeholder 박스 (점선 느낌 — dashType: dash)
        s.addShape(pres.shapes.RECTANGLE, {
            x, y, w, h,
            fill: { color: PANEL },
            line: { color: BORDER, width: 1, dashType: 'dash' }
        });
        // 라벨
        s.addText(label, {
            x, y: y + 0.1, w, h: 0.3,
            fontFace: HEAD_FONT, fontSize: 11, bold: true,
            color: NAVY, align: 'center', valign: 'middle', margin: 0
        });
        s.addText('스크린샷을 여기에 삽입', {
            x, y: y + h / 2 - 0.15, w, h: 0.3,
            fontFace: BODY_FONT, fontSize: 10, italic: true,
            color: MUTED, align: 'center', valign: 'middle', margin: 0
        });
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 7 — 실행 예시 ② (관리자 + 부가 흐름)  ※ 스크린샷 자리
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, '실행 예시 ② — 관리자 + 부가 흐름', '인증 → 재고/매출 조회 · 재고 보충 · 주문 취소 · sales.txt 기록', 7);
    pageFooter(s);

    const top = 1.35;
    const w = 4.4, h = 1.7, gap = 0.2;
    const labels = [
        '①  관리자 인증 (JPasswordField 마스킹)',
        '②  AdminView — 재고표 + 매출 내역',
        '③  주문 취소 — OrderStatusView',
        '④  sales.txt 실제 기록 (UTF-8)'
    ];
    labels.forEach((label, i) => {
        const col = i % 2, row = Math.floor(i / 2);
        const x = 0.55 + col * (w + gap);
        const y = top + row * (h + gap);

        s.addShape(pres.shapes.RECTANGLE, {
            x, y, w, h,
            fill: { color: PANEL },
            line: { color: BORDER, width: 1, dashType: 'dash' }
        });
        s.addText(label, {
            x, y: y + 0.1, w, h: 0.3,
            fontFace: HEAD_FONT, fontSize: 11, bold: true,
            color: NAVY, align: 'center', valign: 'middle', margin: 0
        });
        s.addText('스크린샷을 여기에 삽입', {
            x, y: y + h / 2 - 0.15, w, h: 0.3,
            fontFace: BODY_FONT, fontSize: 10, italic: true,
            color: MUTED, align: 'center', valign: 'middle', margin: 0
        });
    });
}

// ═══════════════════════════════════════════════════════════
// SLIDE 8 — OOP 5범주 평가 + 한계 & 개선
// ═══════════════════════════════════════════════════════════
{
    const s = pres.addSlide();
    s.background = { color: WHITE };
    pageHeader(s, 'OOP 5범주 매핑 · 한계 · 향후 개선', '평가 기준별 근거 코드 위치 + 의식한 한계', 8);
    pageFooter(s);

    // 좌측 — 5범주 표
    const tableData = [
        [
            { text: '범주', options: { bold: true, color: WHITE, fill: { color: NAVY }, align: 'center', valign: 'middle', fontFace: HEAD_FONT } },
            { text: '근거 코드', options: { bold: true, color: WHITE, fill: { color: NAVY }, align: 'center', valign: 'middle', fontFace: HEAD_FONT } }
        ],
        ['① 추상화/확장성',  'Product/Drink abstract · Payment interface · OrderController.newPayment'],
        ['② 정보 은닉',       '필드 private(final) · Inventory.unmodifiableMap · Order.setStatus 전이 검증'],
        ['③ 역할 분리/결합도', 'View→Controller→Model 단방향 · MakerThread → MachineController 콜백'],
        ['④ 안정성/자원 관리', 'synchronized + wait/notify · CopyOnWriteArrayList · InterruptedException 재설정 · ShutdownHook · dispose() unregister · try-with-resources'],
        ['⑤ 가독성',         'camelCase · 단일 책임 메서드 · OrderStatus enum · 모든 클래스 상단 책임 주석']
    ];

    s.addTable(tableData, {
        x: 0.55, y: 1.35, w: 6.0, h: 2.4,
        colW: [1.55, 4.45],
        fontFace: BODY_FONT, fontSize: 9.5, color: TEXT,
        border: { type: 'solid', color: BORDER, pt: 0.5 },
        valign: 'middle'
    });

    // 우측 — 한계 + 개선
    classCard(s, {
        x: 6.7, y: 1.35, w: 2.85, h: 2.4,
        title: '의식한 한계',
        accent: AMBER,
        items: [
            '· 카드/포인트 잔액이 결제',
            '  때마다 생성되는 데모값',
            '  (사용자 계정 모델 없음)',
            '',
            '· 관리자 비밀번호가 상수',
            '  실 운영이라면 외부 설정',
            '  + 해시 저장 필요',
            '',
            '· 상품명 중복은 시드에선 없음',
            '  실 운영이라면 거부/선택 UI',
            '',
            '· processPayment 실패 시',
            '  결제 환불 보상 처리 생략'
        ]
    });

    // 하단 — 향후 개선
    classCard(s, {
        x: 0.55, y: 3.9, w: 9.0, h: 1.2,
        title: '향후 개선 방향',
        accent: NAVY,
        items: [
            '· 카드/포인트 → User 모델 도입(영속화), Payment 인터페이스에 refund() 추가하여 결제-재고 트랜잭션 보상',
            '· 관리자 인증 외부화: properties/env 파일 + BCrypt 해시. 추가로 권한 단계(view-only / admin) 분리',
            '· Product 식별을 ID 유지하되, 사용자 입력 UX 는 자동완성·드롭다운으로 정확 일치 부담 제거'
        ]
    });
}

// ═══════════════════════════════════════════════════════════
pres.writeFile({ fileName: '스마트자판기시스템_최종보고서.pptx' })
    .then(fileName => console.log('OK ' + fileName))
    .catch(err => { console.error('FAIL', err); process.exit(1); });
