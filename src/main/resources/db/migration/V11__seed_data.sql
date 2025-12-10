SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. THÊM TÀI KHOẢN VÀ KHÁCH HÀNG THỨ 9
-- ========================================

-- Thêm 1 tài khoản mới: reviewer_mod (Dùng để duyệt game và xử lý báo cáo)
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
('reviewer_mod','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-03-01','ACTIVE','ADMIN','mod@playvault.com','0944556677');

-- Thêm 1 khách hàng mới (ID: 9)
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
('free_to_play_guy','random_hash_ftp','2025-01-01','ACTIVE','CUSTOMER','ftp_guy@email.com','0808080808');

INSERT INTO `carts` (`id`, `total_price`) VALUES
(11, 0.00);

INSERT INTO `customers` (`id`, `full_name`, `avatar_url`, `date_of_birth`, `balance`, `account_username`, `cart_id`) VALUES
(9,'Free To Play Guy',NULL,'2003-03-03',0.00,'free_to_play_guy',11);


-- ========================================
-- 2. ĐA DẠNG HÓA THƯ VIỆN VÀ WISHLIST
-- ========================================

-- Khách hàng 9 (free_to_play_guy) sở hữu game miễn phí (Game ID 15 có price = 0.00)
INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
(9, 15);

-- Khách hàng 9 thêm game vào wishlist
INSERT INTO `wishlists` (`customer_id`, `game_id`) VALUES
(9, 6), (9, 7);

-- Khách hàng 6 (Le Van A, đang bị LOCKED) có game trong thư viện
INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
(6, 5); -- Speed Racers X

-- ========================================
-- 3. THÊM ĐÁNH GIÁ ĐA DẠNG
-- ========================================

-- Đánh giá 1 sao (Rất tệ) cho game Cyberpunk 2077 (ID: 9) - Vấn đề ra mắt
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(20, 1,'Lỗi ngập tràn lúc mới ra mắt, hoàn tiền đi!', '2021-01-10', 5, 9);

-- Đánh giá 5 sao (Tốt) cho game Cyberpunk 2077 sau khi vá lỗi
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(21, 5,'Sau bản 2.0 thì game quá đỉnh! Đồ họa và cốt truyện không chê vào đâu được.', '2025-05-15', 8, 9);

-- Đánh giá game Dungeon Breaker (ID: 4) - Review 3 sao trung bình
INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
(22, 3,'Gameplay cũ kỹ, nhưng vẫn là game giết thời gian tốt với mức giá này.', '2025-12-10', 9, 4);

-- ========================================
-- 4. THÊM BÁO CÁO MỚI VÀ PHÂN CÔNG XỬ LÝ
-- ========================================

-- Báo cáo 5: Báo cáo vi phạm bản quyền (Game ID 16 đã REJECTED) - Đã giải quyết bởi reviewer_mod
INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
(5,'Báo cáo Pokemon Go (Game ID 16)','Game 16 không thể nhận được game dù đã thanh toán.','Đã kiểm tra.','2025-12-08','2025-12-09','RESOLVED',3,2,'reviewer_mod'); -- Dùng Order 3 làm FK tạm thời

-- Báo cáo 6: Báo cáo lỗi kỹ thuật (Order 10 - Đang PENDING) - Đang chờ xử lý bởi admin
INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
(6,'Không thanh toán được Order 10','Thanh toán qua Paypal bị lỗi, tiền đã bị trừ nhưng Order vẫn PENDING.',NULL,'2025-12-10',NULL,'PENDING',10,2,NULL);

-- ========================================
-- 5. CẬP NHẬT THANH TOÁN CHO CÁC ĐƠN HÀNG CŨ
-- ========================================

-- Cập nhật Payment cho Order 1 (PAID) và Order 6 (COMPLETED)
INSERT INTO `payments` (`id`, `amount`, `payment_date`, `payment_method`, `status`) VALUES
(10, 29.98, '2025-11-16', 'PAYPAL', 'SUCCESS'),  -- Payment for Order 1
(11, 21.00, '2025-12-09', 'ZALOPAY', 'SUCCESS');  -- Payment for Order 6

UPDATE `orders` SET `payment_id` = 10 WHERE `id` = 1;
UPDATE `orders` SET `payment_id` = 11 WHERE `id` = 6;

-- Thêm Invoices còn thiếu (Order 1, Order 6)
INSERT INTO `invoices` (`id`, `issue_date`, `total_amount`, `status`, `order_id`, `customer_id`) VALUES
(6, '2025-11-16', 29.98, 'PAID', 1, 1),
(7, '2025-12-09', 21.00, 'PAID', 6, 2);

-- ========================================
-- 6. THÊM GAME MỚI (TỪ PUBLISHER 4 - Indie Craft Studio)
-- ========================================

-- Game ID: 19 - Game này không có trên PC (chỉ Mobile)
INSERT INTO `game_basic_infos` (`id`, `name`, `short_description`, `description`, `price`, `file_path`, `thumbnail`, `trailer_url`, `required_age`, `is_support_controller`, `category_id`, `publisher_id`, `system_requirement_id`) VALUES
(19,'Tiny Builders','Cute 2D City Builder for Mobile.','Xây dựng thành phố mini của bạn trên thiết bị di động.','1.99','https://m.media-amazon.com/images/I/818vULoASnL.jpg','https://m.media-amazon.com/images/I/818vULoASnL.jpg','https://www.youtube.com/watch?v=et5uFFsKXQU',3,0,4,4,10);

-- Game platforms: Chỉ Mobile (ID 5)
INSERT INTO `game_platforms` (`game_basic_info_id`, `platform_id`) VALUES
(19,5);

-- Game Submission: Approved
INSERT INTO `game_submissions` (`game_basic_info_id`, `status`, `reject_reason`, `submitted_at`, `reviewer_username`, `reviewed_at`) VALUES
(19,'APPROVED',NULL,'2025-12-05','reviewer_mod','2025-12-06');

-- Game Release
INSERT INTO `games` (`game_basic_info_id`, `release_date`, `promotion_id`) VALUES
(19,'2025-12-06',NULL);


INSERT INTO `invoices` (`id`, `issue_date`, `total_amount`, `status`, `order_id`, `customer_id`) VALUES
(8, '2025-12-10', 67.98, 'PAID', 11, 4);

INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(12,'2025-12-09',14.99,'CANCELLED',6,NULL);

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(23, 14.99, 14.99, 12, 3, NULL);

INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(13,'2025-01-01',0.00,'COMPLETED',9,NULL); -- Total = 0.00

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(24, 0.00, 0.00, 13, 15, NULL);
-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;