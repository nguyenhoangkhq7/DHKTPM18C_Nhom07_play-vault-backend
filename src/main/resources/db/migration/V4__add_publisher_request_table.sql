USE playvaultdb;

CREATE TABLE publisher_requests (
    -- Khóa chính
                                    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,

    -- Khóa ngoại liên kết với Account (username)
                                    account_username VARCHAR(255) NOT NULL,

    -- Trạng thái yêu cầu
                                    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,

    -- Ngày tạo và cập nhật
                                    created_at DATE NOT NULL,
                                    updated_at DATE NOT NULL,

    -- Khóa ngoại tham chiếu đến bảng 'accounts'
                                    CONSTRAINT fk_pr_account
                                        FOREIGN KEY (account_username) REFERENCES accounts(username),

    -- Ràng buộc kiểm tra trạng thái
                                    CONSTRAINT chk_pr_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- (Tùy chọn nhưng nên có) Thêm chỉ mục cho khóa ngoại
CREATE INDEX idx_pr_account_username ON publisher_requests (account_username);