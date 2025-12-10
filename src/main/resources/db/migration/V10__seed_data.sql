SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. HOÀN THIỆN HÓA ĐƠN VÀ THANH TOÁN
-- ========================================

-- Liên kết các Payments đã có (ID 1-4) với các Orders cũ (ID 1-4) và tạo Invoices
-- Giả sử đây là các giao dịch nạp tiền hoặc thanh toán cho đơn hàng
-- NOTE: Bảng orders hiện tại có 9 bản ghi.

-- Tạo Payments cho các Order đã COMPLETED (2, 4, 7, 8) và Order mới (10)
INSERT INTO `payments` (`id`, `amount`, `payment_date`, `payment_method`, `status`) VALUES
(5, 71.96, '2025-11-18', 'ZALOPAY', 'SUCCESS'),  -- Payment for Order 2
(6, 14.99, '2025-12-06', 'PAYPAL', 'SUCCESS'), -- Payment for Order 4
(7, 4.99, '2025-12-09', 'ZALOPAY', 'SUCCESS'),   -- Payment for Order 7
(8, 89.98, '2024-09-10', 'PAYPAL', 'SUCCESS'),  -- Payment for Order 8
(9, 39.99, '2025-12-10', 'ZALOPAY', 'PENDING');  -- Payment for New Order 10

-- Cập nhật orders để liên kết với payments và trạng thái
UPDATE `orders` SET `payment_id` = 5 WHERE `id` = 2;
UPDATE `orders` SET `payment_id` = 6 WHERE `id` = 4;
UPDATE `orders` SET `payment_id` = 7 WHERE `id` = 7;
UPDATE `orders` SET `payment_id` = 8 WHERE `id` = 8;
-- Order 1, 3, 5, 6 là các case đặc biệt (cũ/lỗi/pending nạp tiền)

-- Chèn Invoices cho các đơn hàng đã thanh toán (Order 2, 4, 7, 8)
-- Invoice ID hiện tại là 1. Tiếp tục từ 2.
INSERT INTO `invoices` (`id`, `issue_date`, `total_amount`, `status`, `order_id`, `customer_id`) VALUES
(2, '2025-11-18', 71.96, 'PAID', 2, 2),
(3, '2025-12-06', 14.99, 'PAID', 4, 2),
(4, '2025-12-09', 4.99, 'PAID', 7, 5),
(5, '2024-09-10', 89.98, 'PAID', 8, 5);

-- ========================================
-- 2. THÊM ĐƠN HÀNG VÀO GIỎ HÀNG MỚI
-- ========================================
-- Thêm 1 đơn hàng mới đang chờ thanh toán (ID: 10)
INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
(10,'2025-12-10',39.99,'PENDING',2,9); -- Customer 2 đang mua Witcher 3

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
(19, 39.99, 39.99, 10, 7, NULL); -- Witcher 3 không giảm giá

-- ========================================
-- 3. THÊM BÁO CÁO PHỨC TẠP
-- ========================================

-- Báo cáo 3: Hoàn tiền (Refund) cho Order 8 (Customer 5) - Đã giải quyết
INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
(3,'Yêu cầu hoàn tiền game Horizon Zero Dawn','Game chạy không ổn định trên máy của tôi dù đã đáp ứng cấu hình tối thiểu. Đã chơi dưới 2 tiếng.','Đã kiểm tra thời gian chơi (1.5h). Chấp nhận hoàn tiền vào số dư tài khoản.','2025-12-01','2025-12-02','RESOLVED',8,5,'admin');

-- Báo cáo 4: Báo cáo lỗi của game (Order 9) - Đang chờ xử lý
INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
(4,'Lỗi game Elden Ring','Tôi đã thanh toán những mãi không được xác nhận',null,'2025-12-10',NULL,'PENDING',9,8,null);

-- ========================================
-- 4. THÊM GAME MỚI (Từ Publisher 2)
-- ========================================
-- Publisher 2: username: publisher2 (studio_name: ấdsad)
-- Game ID hiện tại là 17. Tiếp tục từ 18.

-- Thêm 1 Yêu cầu Hệ thống mới (ID: 11)
INSERT INTO `system_requirements` (`id`, `os`, `cpu`, `gpu`, `storage`, `ram`) VALUES
(11,'WINDOWS','Intel i5 7th Gen','GTX 1070','50GB','16GB');

-- Chèn 1 Game Strategy mới (ID: 18) - Giả sử đây là game bị CẤM BÁN
INSERT INTO `game_basic_infos` (`id`, `name`, `short_description`, `description`, `price`, `file_path`, `thumbnail`, `trailer_url`, `required_age`, `is_support_controller`, `category_id`, `publisher_id`, `system_requirement_id`) VALUES
(18,'Conflict of Titans','Massive scale RTS game.','Xây dựng căn cứ, quản lý tài nguyên, chiến đấu trên quy mô lớn.','34.99','/files/conflict.zip','https://static0.gamerantimages.com/wordpress/wp-content/uploads/2024/02/1000043035.jpg?w=1600&h=1200&fit=crop','https://www.youtube.com/watch?v=K-m6fHj63dA',18,0,5,3,11);

-- Game platforms
INSERT INTO `game_platforms` (`game_basic_info_id`, `platform_id`) VALUES
(18,1);

-- Game Submission: REJECTED vì nội dung bị cấm (Bạo lực quá mức/Cấm phát hành tại VN)
INSERT INTO `game_submissions` (`game_basic_info_id`, `status`, `reject_reason`, `submitted_at`, `reviewer_username`, `reviewed_at`) VALUES
(18,'REJECTED','Nội dung bạo lực quá mức và không phù hợp với quy định phát hành địa phương.','2025-12-09','admin','2025-12-10');

-- Bật lại kiểm tra khóa ngoại
SET FOREIGN_KEY_CHECKS = 1;