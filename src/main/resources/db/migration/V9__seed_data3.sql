SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. THÊM TÀI KHOẢN VÀ CẤU HÌNH MÁY MỚI
-- ========================================

-- Thêm 1 khách hàng mới (power_user) (ID: 8)
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
('power_user_vip','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2023-05-01','ACTIVE','CUSTOMER','poweruser@vip.com','0911334455');

INSERT INTO `carts` (`id`, `total_price`) VALUES
(10, 0.00);

INSERT INTO `customers` (`id`, `full_name`, `avatar_url`, `date_of_birth`, `balance`, `account_username`, `cart_id`) VALUES
(8,'VIP Power User',NULL,'1992-12-12',50000000.00,'power_user_vip',10);

-- Thêm dữ liệu cấu hình máy cho customer1 (ID: 2) và power_user_vip (ID: 8)
-- customer1 (ID: 2) đã có sẵn cart_id=4
INSERT INTO `system_infos` (`customer_id`, `os`, `cpu`, `gpu`, `ram`, `directx_version`, `dxdiag_content`) VALUES
(2, 'Windows 10 Home 64-bit (10.0, Build 19045)', 'Intel Core i5-11400H', 'NVIDIA GeForce RTX 3050 (4GB VRAM)', '16 GB', 'DirectX 12', 'System info for customer 2 - Mid Range'),
(8, 'Windows 11 Pro 64-bit (10.0, Build 22621)', 'AMD Ryzen 9 7950X', 'NVIDIA GeForce RTX 4090 (24GB VRAM)', '64 GB', 'DirectX 12 Ultimate', 'System info for power_user - High End');


-- ========================================
-- 2. THÊM KHUYẾN MÃI VÀ GIAO DỊCH PHỨC TẠP
-- ========================================

-- Thêm 1 khuyến mãi mới (ID: 6) - Giảm giá cố định theo số tiền
INSERT INTO `promotions` (`id`, `name`, `description`, `start_date`, `end_date`, `is_active`, `discount_percent`, `discount_amount`, `publisher_id`) VALUES
(6,'Summer Hit Sale','Giảm giá 5$ cố định cho mọi game trên 20$.','2025-07-01','2025-08-31',1,NULL,5.00,1);

-- Gán khuyến mãi mới cho game (CyberRun) - Cập nhật games ID=1
UPDATE `games` SET `promotion_id` = 6 WHERE `game_basic_info_id` = 1;

-- Thêm 1 đơn hàng phức tạp (ID: 9) - Mua nhiều game, áp dụng nhiều loại khuyến mãi
-- Customer ID 8 (power_user_vip)
INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(9,'2025-07-15', 70.97, 'COMPLETED', 8, NULL);

-- Order Items cho đơn hàng 9
-- Game 1 (CyberRun: 21.00$ - 5.00$ fixed discount = 16.00$)
-- Game 8 (Elden Ring: 59.99$ - 50% discount = 30.00$)
-- Game 3 (Galaxy Warzone: 14.99$ - NO discount = 14.99$)
-- Tổng: 16.00 + 30.00 + 14.99 = 60.99$ (Có vẻ total trong thực tế phải là 60.99, tôi sẽ sửa lại total cho order 9)
UPDATE `orders` SET `total` = 60.99 WHERE `id` = 9;

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(16, 21.00, 16.00, 9, 1, 6),   -- CyberRun: Giảm giá cố định 5$
(17, 59.99, 30.00, 9, 8, 3),   -- Elden Ring: Giảm 50% (Promo ID 3)
(18, 14.99, 14.99, 9, 3, NULL);-- Galaxy Warzone: Không khuyến mãi

-- Thêm các game đã mua vào thư viện của Power User
INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
(8,1), (8,8), (8,3);

-- ========================================
-- 3. THÊM BẢN GHI KHÓA (BLOCK RECORD) MỚI
-- ========================================

-- Thêm 1 bản ghi khóa cho tài khoản 'le_van_a' (ID: 6) - Khóa tạm thời
INSERT INTO `block_records` (`id`, `is_block`, `created_at`, `reason`, `account_username`) VALUES
(2, 1, '2025-12-08', 'Nghi ngờ hoạt động gian lận, cần xác minh danh tính.', 'le_van_a');

-- Cập nhật trạng thái tài khoản 'le_van_a' thành LOCKED
UPDATE `accounts` SET `status` = 'LOCKED' WHERE `username` = 'le_van_a';

-- ========================================
-- 4. THÊM ĐÁNH GIÁ (REVIEW) VÀ INVOICE
-- ========================================

-- Thêm đánh giá tiêu cực cho game Dungeon Breaker (ID: 4)
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(18, 2,'Game rất sơ sài, đồ họa lỗi thời và gameplay nhàm chán.', '2025-12-10', 8, 4);

-- Thêm đánh giá tích cực cho Witcher 3 (ID: 7)
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(19, 5,'Kiệt tác của mọi thời đại, tôi đã chơi đi chơi lại nhiều lần.', '2025-12-05', 5, 7);

-- Thêm Invoice cho Order 9 (power_user)
INSERT INTO `invoices` (`id`, `issue_date`, `total_amount`, `status`, `order_id`, `customer_id`) VALUES
(1, '2025-07-15', 60.99, 'PAID', 9, 8);


-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;