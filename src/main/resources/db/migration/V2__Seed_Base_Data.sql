USE playvaultdb;

-- ========================================
-- FILE V2: DỮ LIỆU NỀN (BASE DATA)
-- (An toàn để chạy trên Production)
-- ========================================

-- 1. TÀI KHOẢN ADMIN
INSERT INTO accounts (username, password, created_at, status, role, email, phone) VALUES
    ('admin', '$2b$12$9bQ9fNDsMFHyh8tdQR.BmOVa1LvfzwHFWsDHKJnnDAKtXaCy8432O
', '2024-01-01', 'ACTIVE', 'ADMIN', 'admin@playvault.com', '0901000001');

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