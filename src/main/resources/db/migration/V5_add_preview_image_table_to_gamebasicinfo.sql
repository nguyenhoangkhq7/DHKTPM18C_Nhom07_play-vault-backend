USE playvaultdb;

-- Tạo bảng preview_images
CREATE TABLE preview_images (
    -- id : Long
                                id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,

    -- url : String (URL của hình ảnh xem trước)
                                url VARCHAR(255) NOT NULL,

    -- Khóa ngoại liên kết với GameBasicInfo
    -- GameBasicInfo có 1 (id) : PreviewImage có 0..* (game_basic_info_id)
                                game_basic_info_id BIGINT NOT NULL,

    -- Thiết lập Khóa ngoại
                                CONSTRAINT fk_pi_game_basic_info
                                    FOREIGN KEY (game_basic_info_id) REFERENCES game_basic_infos(id)
                                        -- ON DELETE CASCADE sẽ tự động xóa các ảnh preview khi GameBasicInfo bị xóa
                                        -- Bạn có thể điều chỉnh tùy theo chính sách dữ liệu của mình.
                                        ON DELETE CASCADE
);

-- Thêm chỉ mục cho khóa ngoại để cải thiện hiệu suất tìm kiếm
CREATE INDEX idx_pi_game_basic_info_id ON preview_images (game_basic_info_id);