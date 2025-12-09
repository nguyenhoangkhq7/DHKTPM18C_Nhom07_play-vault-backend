USE playvaultdb;

-- 1. Thêm cột khóa ngoại promotion_id vào bảng games
ALTER TABLE games
    ADD COLUMN promotion_id BIGINT NULL; -- BIGINT tương ứng với Long id trong Promotion

-- 2. Thiết lập Khóa ngoại (Foreign Key)
ALTER TABLE games
    ADD CONSTRAINT fk_game_promotion
        FOREIGN KEY (promotion_id) REFERENCES promotions(id)
            ON DELETE SET NULL; -- Nếu một khuyến mãi bị xóa, các game liên quan sẽ mất liên kết (promotion_id = NULL)

-- 3. (Tùy chọn) Thêm chỉ mục để tối ưu hiệu suất truy vấn JOIN
CREATE INDEX idx_game_promotion_id ON games (promotion_id);