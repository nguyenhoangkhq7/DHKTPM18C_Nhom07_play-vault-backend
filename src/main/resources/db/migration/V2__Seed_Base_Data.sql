USE playvaultdb;

-- ========================================
-- FILE V2: DỮ LIỆU NỀN (BASE DATA)
-- (An toàn để chạy trên Production)
-- ========================================

-- 1. TÀI KHOẢN ADMIN
INSERT INTO accounts (username, password, created_at, status, role, email, phone) VALUES
    ('admin', '$2a$10$3z8x9v7c5r2t1q9w8e7r6t5y4u3i2o1p0a9s8d7f6g5h4j3k2l1m0', '2024-01-01', 'ACTIVE', 'ADMIN', 'admin@playvault.com', '0901000001');

-- 2. CATEGORIES
INSERT INTO categories (id, name, description) VALUES
(1, 'Action', 'Game hành động, bắn súng, đánh nhau.'),
(2, 'Adventure', 'Game phiêu lưu, khám phá thế giới.'),
(3, 'RPG', 'Nhập vai, phát triển nhân vật.'),
(4, 'Simulation', 'Mô phỏng cuộc sống, xây dựng.'),
(5, 'Strategy', 'Chiến thuật, quản lý tài nguyên.'),
(6, 'Puzzle', 'Game giải đố, tư duy.'),
(7, 'Horror', 'Game kinh dị, sinh tồn.'),
(8, 'Racing', 'Đua xe, tốc độ.');

INSERT INTO platforms (id, name) VALUES
(1, 'PC'),           -- Cho game máy tính (Windows, Mac, Linux)
(2, 'PlayStation'),
(3, 'Xbox'),
(4, 'Nintendo Switch'),
(5, 'Mobile');