SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. THÊM TÀI KHOẢN VÀ KHÁCH HÀNG MỚI
-- ========================================

-- Thêm 3 tài khoản khách hàng mới
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
('gamer_viet','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2023-08-10','ACTIVE','CUSTOMER','gamer_viet@email.com','0811223344'),
('le_van_a','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-05-20','ACTIVE','CUSTOMER','levana@email.com','0977665544'),
('test_user_locked','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-11-01','LOCKED','CUSTOMER','locked@email.com','0333221100');

-- Thêm 3 giỏ hàng mới
INSERT INTO `carts` (`id`, `total_price`) VALUES
(7, 0.00), (8, 0.00), (9, 0.00);

-- Chèn 3 khách hàng mới
-- ID KH hiện tại: 1, 2, 4. Tiếp tục từ ID 5.
INSERT INTO `customers` (`id`, `full_name`, `avatar_url`, `date_of_birth`, `balance`, `account_username`, `cart_id`) VALUES
(5,'Nguyễn Việt Dũng','https://images2.thanhnien.vn/528068263637045248/2023/5/30/anh-dung-5-16854679130356563298.jpeg','1995-03-15',1500000.00,'gamer_viet',7),
(6,'Lê Văn A','https://cdn-i.doisongphapluat.com.vn/resize/dnIhFQdTPcrLPDyiALXKsQ2/upload/2025/07/15/nam-dien-vien-binh-an-bi-chan-thuong-phai-tien-hanh-phau-thuat-ava-16005776.jpg','1999-07-28',50000.00,'le_van_a',8),
(7,'Việt Hoàng Long','https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/T%E1%BB%B1_Long_2021.jpg/250px-T%E1%BB%B1_Long_2021.jpg','1990-10-10',0.00,'test_user_locked',9);

-- ========================================
-- 2. THÊM NHÀ PHÁT HÀNH MỚI
-- ========================================

-- Thêm 1 tài khoản nhà phát hành (ID: pub3)
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
('indie_dev','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2023-01-25','ACTIVE','PUBLISHER','support@indiecraft.com','0987654321');

-- Thêm 1 thông tin thanh toán mới (ID: 4)
INSERT INTO `payment_infos` (`id`, `payment_method`, `account_name`, `account_number`, `bank_name`, `is_verified`) VALUES
(4,'ZALOPAY','indie_dev','0987654321','ZaloPay',1);

-- Chèn 1 nhà phát hành mới (ID: 4)
INSERT INTO `publishers` (`id`, `account_username`, `studio_name`, `description`, `website`, `payment_info_id`) VALUES
(4,'indie_dev','Indie Craft Studio','Tập trung vào các game giải đố nhẹ nhàng và nghệ thuật.','https://indie-studio.com',4);

-- ========================================
-- 3. THÊM GAME MỚI (Từ Indie Craft Studio)
-- ========================================

-- Thêm 1 Yêu cầu Hệ thống mới (ID: 10)
INSERT INTO `system_requirements` (`id`, `os`, `cpu`, `gpu`, `storage`, `ram`) VALUES
(10,'WINDOWS','Dual Core','Intel HD Graphics','500MB','4GB');

-- Chèn 1 Game Puzzle nhẹ nhàng (ID: 17)
INSERT INTO `game_basic_infos` (`id`, `name`, `short_description`, `description`, `price`, `file_path`, `thumbnail`, `trailer_url`, `required_age`, `is_support_controller`, `category_id`, `publisher_id`, `system_requirement_id`) VALUES
(17,'Zen Garden Puzzle','Relaxing 2D puzzle game','Đặt các viên đá Zen Garden vào đúng vị trí để mở khóa cấp độ mới.','4.99','/files/zengarden.zip','https://m.media-amazon.com/images/I/81uPae1onOL._AC_UF894,1000_QL80_.jpg','https://www.youtube.com/watch?v=sBi4WNbzoFA',3,0,6,4,10);

-- Chèn game platforms cho game mới
INSERT INTO `game_platforms` (`game_basic_info_id`, `platform_id`) VALUES
(17,1);

-- Chèn game submissions (đã được duyệt)
INSERT INTO `game_submissions` (`game_basic_info_id`, `status`, `reject_reason`, `submitted_at`, `reviewer_username`, `reviewed_at`) VALUES
(17,'APPROVED',NULL,'2025-12-01','admin','2025-12-05');

-- Chèn game release
INSERT INTO `games` (`game_basic_info_id`, `release_date`, `promotion_id`) VALUES
(17,'2024-10-20',NULL);


-- ========================================
-- 4. THÊM ĐƠN HÀNG VÀ TƯƠNG TÁC MỚI
-- ========================================

-- Thêm 2 đơn hàng mới cho khách hàng ID 5 (gamer_viet)
-- Order 7: Mua Zen Garden Puzzle
INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(7,'2025-12-09',4.99,'COMPLETED',5,NULL);

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(13,4.99,4.99,7,17,NULL);

-- Order 8: Mua 2 game (Horizon và Witcher 3 - Giả sử đây là đơn hàng cũ)
INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(8,'2024-09-10',89.98,'COMPLETED',5,NULL);

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(14,49.99,49.99,8,6,NULL),
(15,39.99,39.99,8,7,NULL);


-- Thêm game vào thư viện của khách hàng ID 5
INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
(5,17), (5,6), (5,7);

-- Thêm game vào danh sách mong muốn của khách hàng ID 6
INSERT INTO `wishlists` (`customer_id`, `game_id`) VALUES
(6,8), (6,9);

-- Thêm game vào giỏ hàng của khách hàng ID 5
INSERT INTO `cart_items` (`id`, `price`, `discount`, `cart_id`, `game_id`) VALUES
(31,59.99,0.00,7,8); -- Elden Ring

-- ========================================
-- 5. THÊM ĐÁNH GIÁ VÀ BÁO CÁO MỚI
-- ========================================

-- Thêm đánh giá cho Zen Garden Puzzle (ID: 17)
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(16,5,'Game giải đố thư giãn, âm nhạc tuyệt vời. Rất đáng giá 5 sao!','2025-12-10',5,17);

-- Thêm đánh giá cho Horizon Zero Dawn (ID: 6)
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(17,5,'Đồ họa xuất sắc, cốt truyện cuốn hút. Không thể tin đây là game cũ.','2025-12-10',5,6);

-- Thêm 1 báo cáo mới (Order 7) - Báo cáo đã được giải quyết
INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
(2,'Không thấy hóa đơn','Tôi không nhận được email hóa đơn cho đơn hàng này.','Đã gửi lại hóa đơn qua email: gamer_viet@email.com','2025-12-10','2025-12-10','RESOLVED',7,5,'admin');


UPDATE game_basic_infos
SET file_path = '/uploads/games/game14.apk',
    thumbnail = 'https://hoanghamobile.com/tin-tuc/wp-content/uploads/2023/09/code-hanh-trinh-bat-tan-thumb.jpg',
    trailer_url='https://www.youtube.com/watch?v=yoM31fLwIDI'
WHERE id = 14;

INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(11,'2025-12-10',67.98,'PAID',4,NULL);

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(20, 9.99, 3.00, 11, 2, 1),      -- Magic Quest (70% off)
(21, 24.99, 24.99, 11, 5, NULL), -- Speed Racers X (Full price)
(22, 39.99, 39.99, 11, 7, NULL); -- The Witcher 3 (Full price)

-- Thêm game vào thư viện của Khách hàng ID 4
INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
(4, 2), (4, 5), (4, 7);

-- Tạo Payment và Invoice cho Order 11
INSERT INTO `payments` (`id`, `amount`, `payment_date`, `payment_method`, `status`) VALUES
(12, 67.98, '2025-12-10', 'ZALOPAY', 'SUCCESS');
UPDATE `orders` SET `payment_id` = 12 WHERE `id` = 11;

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;