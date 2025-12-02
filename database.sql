USE playvaultdb;

-- =============================================
-- 1. TẠO TÀI KHOẢN (Mật khẩu là: 12345678)
-- =============================================
-- Khách hàng test
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('customer_test', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'CUSTOMER', 'customer@test.com', '0909123456');

-- Admin test
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('admin_test', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'ADMIN', 'admin@test.com', '0909999999');

-- Tạo Profile Customer
INSERT INTO customers (full_name, balance, account_username)
VALUES ('Nguyen Van Test', 0, 'customer_test');

-- Lấy ID của customer vừa tạo (Giả sử là ID cuối cùng)
SET @cust_id = (SELECT id FROM customers WHERE account_username = 'customer_test');


-- =============================================
-- 2. TẠO GAME MẪU (Để mua hàng)
-- =============================================
-- Game 1: Elden Ring (Giả lập)
INSERT INTO game_basic_infos (name, price, description, thumbnail)
VALUES ('Elden Ring', 1000000, 'Game nhập vai hành động', 'eldenring.jpg');
SET @game1_id = LAST_INSERT_ID();
INSERT INTO games (game_basic_info_id, release_date) VALUES (@game1_id, NOW());

-- Game 2: GTA V
INSERT INTO game_basic_infos (name, price, description, thumbnail)
VALUES ('GTA V', 500000, 'Game thế giới mở', 'gtav.jpg');
SET @game2_id = LAST_INSERT_ID();
INSERT INTO games (game_basic_info_id, release_date) VALUES (@game2_id, NOW());


-- =============================================
-- 3. KỊCH BẢN 1: MẤT ĐƠN (NO PAYMENT RECORD)
-- Khách đã chuyển khoản 1.000.000đ nhưng hệ thống chưa có Payment
-- =============================================
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (NOW(), 1000000, 'PENDING', @cust_id, NULL);

SET @order_missing_id = LAST_INSERT_ID();

-- Thêm item vào đơn hàng
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (1000000, 1000000, @order_missing_id, @game1_id);

-- => TEST CASE: Login customer_test -> Báo cáo đơn này (kèm mã GD: 'TCB-MISSING-01') -> Admin check -> Tạo Payment bù.


-- =============================================
-- 4. KỊCH BẢN 2: TREO ĐƠN (PAYMENT PENDING)
-- Hệ thống đã nhận payment nhưng status bị kẹt là PENDING/FAILED
-- =============================================
-- Tạo Payment bị treo
INSERT INTO payments (amount, payment_date, payment_method, status)
VALUES (500000, NOW(), 'ZALOPAY', 'PENDING');

SET @payment_stuck_id = LAST_INSERT_ID();

-- Tạo Order gắn với Payment này
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (NOW(), 500000, 'PENDING', @cust_id, @payment_stuck_id);

SET @order_stuck_id = LAST_INSERT_ID();

-- Thêm item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (500000, 500000, @order_stuck_id, @game2_id);

-- => TEST CASE: Admin sẽ thấy đơn này đã có Payment nhưng chưa thành công.


-- =============================================
-- 5. TẠO BÁO CÁO MẪU (Sẵn sàng để Admin xử lý)
-- Giả sử khách đã báo cáo cho Kịch bản 2
-- =============================================
INSERT INTO reports (title, description, created_at, status, order_id, customer_id)
VALUES (
           'Đã thanh toán MOMO nhưng đơn vẫn PENDING',
           'Mã GD: MOMO-STUCK-02 | Tôi đã chuyển 500k lúc 10:00 sáng nay.',
           NOW(),
           'PENDING',
           @order_stuck_id,
           @cust_id
       );



-- =============================================
-- 6. KỊCH BẢN 3: BÁO CÁO ĐÃ ĐƯỢC XỬ LÝ (RESOLVED)
-- Đơn hàng này lúc đầu lỗi, khách báo cáo, Admin đã duyệt xong.
-- =============================================

-- 1. Tạo Game mới
INSERT INTO game_basic_infos (name, price, description, thumbnail)
VALUES ('Black Myth: Wukong', 1200000, 'Game hành động nhập vai', 'wukong.jpg');
SET @game3_id = LAST_INSERT_ID();
INSERT INTO games (game_basic_info_id, release_date) VALUES (@game3_id, NOW());

-- 2. Tạo Payment thành công (Do Admin đã duyệt tạo ra)
INSERT INTO payments (amount, payment_date, payment_method, status)
VALUES (1200000, NOW(), 'ZALOPAY', 'SUCCESS');
SET @payment_resolved_id = LAST_INSERT_ID();

-- 3. Tạo Order đã hoàn tất
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (DATE_SUB(NOW(), INTERVAL 1 DAY), 1200000, 'COMPLETED', @cust_id, @payment_resolved_id);
SET @order_resolved_id = LAST_INSERT_ID();

-- 4. Thêm Item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (1200000, 1200000, @order_resolved_id, @game3_id);

-- 5. Tạo Report trạng thái RESOLVED (Đã xác nhận)
INSERT INTO reports (
    title,
    description,
    created_at,
    resolved_at,
    status,
    order_id,
    customer_id,
    handler_username,
    handler_note
)
VALUES (
           'Lỗi không nhận được game',
           'Mã GD: ZALO-112233 | Tôi đã thanh toán lúc 20:00 hôm qua',
           DATE_SUB(NOW(), INTERVAL 1 DAY), -- Tạo hôm qua
           NOW(),                           -- Xử lý hôm nay
           'RESOLVED',
           @order_resolved_id,
           @cust_id,
           'admin_test',                    -- Người xử lý
           'Đã đối soát thành công, giao dịch hợp lệ.'
       );



USE playvaultdb;

SET @cust_id = (SELECT id FROM customers WHERE account_username = 'customer_test');

-- =======================================================
-- 1. TẠO TÀI KHOẢN & KHÁCH HÀNG
-- Password mặc định là: 12345678 (đã hash)
-- =======================================================

-- Admin (để đăng nhập Dashboard)
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('admin_test', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'ADMIN', 'admin@test.com', '0909999999')
ON DUPLICATE KEY UPDATE email=email; -- Tránh lỗi nếu đã chạy rồi

-- Customer (để test gửi báo cáo)
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('customer_test', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'CUSTOMER', 'customer@test.com', '0909123456')
ON DUPLICATE KEY UPDATE email=email;

-- Tạo Profile cho Customer
INSERT INTO customers (full_name, balance, account_username)
SELECT 'Nguyễn Văn Test', 0, 'customer_test'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE account_username = 'customer_test');

-- Lấy ID khách hàng để dùng cho các lệnh dưới
SET @cust_id = (SELECT id FROM customers WHERE account_username = 'customer_test');


-- =======================================================
-- 2. TẠO GAME MẪU (Sản phẩm)
-- =======================================================
INSERT INTO game_basic_infos (name, price, description, thumbnail)
VALUES ('Black Myth: Wukong', 1500000, 'Game hành động nhập vai đình đám', 'https://example.com/wukong.jpg');
SET @game_id = LAST_INSERT_ID();

INSERT INTO games (game_basic_info_id, release_date) VALUES (@game_id, NOW());


-- =======================================================
-- KỊCH BẢN 1: MẤT ĐƠN (NO PAYMENT) - ADMIN CẦN DUYỆT
-- Khách: Đã chuyển khoản, nhập mã "MOMO-MISSING-01"
-- Hệ thống: Chưa có Payment, Order đang PENDING
-- =======================================================

-- 1.1 Tạo Order PENDING (Payment ID = NULL)
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (NOW(), 1500000, 'PENDING', @cust_id, NULL);
SET @order_missing_id = LAST_INSERT_ID();

-- 1.2 Tạo Order Item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (1500000, 1500000, @order_missing_id, @game_id);

-- 1.3 Tạo Báo cáo (Mô phỏng khách đã gửi)
-- Title chứa mã giao dịch để Mapper tự tách ra: "USER-MOMO-MISSING-01"
INSERT INTO reports (
    title,
    description,
    created_at,
    status,
    order_id,
    customer_id
)
VALUES (
           '[Mã GD: MOMO-MISSING-01] Đã thanh toán nhưng chưa nhận game',
           'Tôi đã chuyển khoản lúc 10:00 sáng nay, số tiền 1.500.000đ.',
           NOW(),
           'PENDING',
           @order_missing_id,
           @cust_id
       );


-- =======================================================
-- KỊCH BẢN 2: TREO ĐƠN (PAYMENT PENDING) - ADMIN CẦN DUYỆT
-- Khách: Nhập mã "ZALO-STUCK-02"
-- Hệ thống: Có Payment nhưng bị kẹt status PENDING
-- =======================================================

-- 2.1 Tạo Payment bị treo
INSERT INTO payments (amount, payment_date, payment_method, status)
VALUES (1500000, NOW(), 'ZALOPAY', 'PENDING');
SET @payment_stuck_id = LAST_INSERT_ID();

-- 2.2 Tạo Order gắn với Payment này
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (DATE_SUB(NOW(), INTERVAL 1 HOUR), 1500000, 'PENDING', @cust_id, @payment_stuck_id);
SET @order_stuck_id = LAST_INSERT_ID();

-- 2.3 Tạo Order Item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (1500000, 1500000, @order_stuck_id, @game_id);

-- 2.4 Tạo Báo cáo
-- Title chứa mã: "USER-ZALO-STUCK-02" (Vì Payment hệ thống chưa success nên Mapper sẽ lấy mã này)
INSERT INTO reports (
    title,
    description,
    created_at,
    status,
    order_id,
    customer_id
)
VALUES (
           '[Mã GD: ZALO-STUCK-02] Lỗi giao dịch đang xử lý',
           'App báo trừ tiền rồi mà web vẫn quay vòng vòng.',
           NOW(),
           'PENDING',
           @order_stuck_id,
           @cust_id
       );


-- =======================================================
-- KỊCH BẢN 3: ĐÃ XỬ LÝ XONG (RESOLVED) - ĐỂ TEST LỊCH SỬ
-- Admin đã duyệt, mọi thứ đã xanh (SUCCESS/COMPLETED)
-- =======================================================

-- 3.1 Tạo Payment Thành công (Giả sử Admin đã tạo/duyệt)
INSERT INTO payments (amount, payment_date, payment_method, status)
VALUES (1500000, DATE_SUB(NOW(), INTERVAL 1 DAY), 'ZALOPAY', 'SUCCESS');
SET @payment_success_id = LAST_INSERT_ID();

-- 3.2 Tạo Order Hoàn tất
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (DATE_SUB(NOW(), INTERVAL 1 DAY), 1500000, 'COMPLETED', @cust_id, @payment_success_id);
SET @order_success_id = LAST_INSERT_ID();

-- 3.3 Order Item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (1500000, 1500000, @order_success_id, @game_id);

-- 3.4 Báo cáo đã đóng (Resolved)
INSERT INTO reports (
    title,
    description,
    handler_note,
    handler_username,
    created_at,
    resolved_at,
    status,
    order_id,
    customer_id
)
VALUES (
           '[Mã GD: ZALO-OK-03] Kiểm tra giúp em đơn này',
           'Em lỡ tắt trình duyệt sớm.',
           'Đã đối soát thành công. Đã cộng game.',
           'admin_test',
           DATE_SUB(NOW(), INTERVAL 1 DAY),
           NOW(),
           'RESOLVED',
           @order_success_id,
           @cust_id
       );


# --------------------------------------

USE playvaultdb;

-- =======================================================
-- 1. TẠO TÀI KHOẢN MỚI (User 2)
-- Password: 12345678
-- =======================================================

-- Admin mới
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('admin_test_2', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'ADMIN', 'admin2@test.com', '0909000002');

-- Customer mới
INSERT INTO accounts (username, password, created_at, status, role, email, phone)
VALUES ('customer_test_2', '$2a$10$wW5.uX2qGq/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1/1', NOW(), 'ACTIVE', 'CUSTOMER', 'customer2@test.com', '0909000003');

-- Profile Customer
INSERT INTO customers (full_name, balance, account_username)
VALUES ('Lê Thị Test 2', 0, 'customer_test_2');

SET @cust2_id = LAST_INSERT_ID();


-- =======================================================
-- 2. TẠO GAME MỚI
-- =======================================================
INSERT INTO game_basic_infos (name, price, description, thumbnail)
VALUES ('Cyberpunk 2077', 990000, 'Game nhập vai thế giới mở', 'cyberpunk.jpg');
SET @game_cp_id = LAST_INSERT_ID();

INSERT INTO games (game_basic_info_id, release_date) VALUES (@game_cp_id, NOW());


-- =======================================================
-- 3. TẠO ĐƠN HÀNG BỊ TREO (PENDING)
-- =======================================================

-- Tạo Payment đang xử lý (Chưa success)
INSERT INTO payments (amount, payment_date, payment_method, status)
VALUES (990000, NOW(), 'ZALOPAY', 'PENDING');
SET @payment_new_id = LAST_INSERT_ID();

-- Tạo Order PENDING
INSERT INTO orders (created_at, total, status, customer_id, payment_id)
VALUES (NOW(), 990000, 'PENDING', @cust2_id, @payment_new_id);
SET @order_new_id = LAST_INSERT_ID();

-- Tạo Order Item
INSERT INTO order_items (price, total, order_id, game_id)
VALUES (990000, 990000, @order_new_id, @game_cp_id);


-- =======================================================
-- 4. TẠO BÁO CÁO (ĐỂ ADMIN DUYỆT)
-- =======================================================
INSERT INTO reports (
    title,
    description,
    created_at,
    status,
    order_id,
    customer_id
)
VALUES (
           '[Mã GD: ZALO-NEW-999] Đã thanh toán Cyberpunk',
           'Tôi đã thanh toán lúc 15:30 chiều nay.',
           NOW(),
           'PENDING',
           @order_new_id,
           @cust2_id
       );

-- In ra ID của báo cáo vừa tạo để bạn biết mà gọi API
SELECT @new_report_id := LAST_INSERT_ID() as 'New_Report_ID_To_Test';