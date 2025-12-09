-- V7__create_system_infos_table.sql
USE playvaultdb;

-- Tạo bảng system_infos để lưu cấu hình máy của người dùng
CREATE TABLE IF NOT EXISTS system_infos (
    -- Khóa chính
                              id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,

    -- Tham chiếu đến user (customer)
                              customer_id BIGINT NOT NULL,

    -- Thông tin hệ thống được parse từ file DXDiag
                              os TEXT,
                              cpu TEXT,
                              gpu TEXT,
                              ram TEXT,
                              directx_version VARCHAR(100),

    -- Lưu nội dung file DXDiag gốc (có thể lớn)
                              dxdiag_content LONGTEXT,

    -- Thời gian cập nhật
                              last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Khóa ngoại đến customers
                              CONSTRAINT fk_system_info_customer
                                  FOREIGN KEY (customer_id) REFERENCES customers(id)
                                      ON DELETE CASCADE,

    -- Tạo index cho customer_id để tối ưu tìm kiếm
                              INDEX idx_system_info_customer_id (customer_id),

    -- Tạo index cho last_updated để tối ưu sắp xếp
                              INDEX idx_system_info_last_updated (last_updated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm comment cho bảng
ALTER TABLE system_infos COMMENT = 'Lưu thông tin cấu hình máy của người dùng sau khi upload file DXDiag.txt';

-- ========================================
-- TẠO THỦ TỤC (PROCEDURES) TIỆN ÍCH
-- ========================================

-- Procedure: Lấy thông tin cấu hình mới nhất của customer
DELIMITER $$
CREATE PROCEDURE sp_get_latest_system_info(IN p_customer_id BIGINT)
BEGIN
SELECT
    si.id,
    si.os,
    si.cpu,
    si.gpu,
    si.ram,
    si.directx_version,
    si.last_updated
FROM system_infos si
WHERE si.customer_id = p_customer_id
ORDER BY si.last_updated DESC
    LIMIT 1;
END$$
DELIMITER ;

-- Procedure: Xóa tất cả cấu hình cũ của customer (giữ lại bản mới nhất)
DELIMITER $$
CREATE PROCEDURE sp_cleanup_old_system_infos(IN p_customer_id BIGINT)
BEGIN
    -- Tìm ID của bản ghi mới nhất
    DECLARE latest_id BIGINT;

SELECT id INTO latest_id
FROM system_infos
WHERE customer_id = p_customer_id
ORDER BY last_updated DESC
    LIMIT 1;

-- Xóa tất cả trừ bản mới nhất
DELETE FROM system_infos
WHERE customer_id = p_customer_id
  AND id != latest_id;
END$$
DELIMITER ;

-- ========================================
-- INSERT DỮ LIỆU MẪU (OPTIONAL)
-- ========================================

/*
-- Đoạn này có thể comment lại nếu không muốn insert dữ liệu mẫu
INSERT INTO system_infos (customer_id, os, cpu, gpu, ram, directx_version, dxdiag_content) VALUES
(
    1, -- customer_id (giả sử customer có id = 1)
    'Windows 11 Pro 64-bit (10.0, Build 22621)',
    'AMD Ryzen 7 5800X 8-Core Processor',
    'NVIDIA GeForce RTX 4070 Ti (12GB VRAM)',
    '32 GB',
    'DirectX 12',
    '--- System Information ---
Operating System: Windows 11 Pro 64-bit (10.0, Build 22621)
Processor: AMD Ryzen 7 5800X 8-Core Processor (16 CPUs), ~3.8GHz
Memory: 32768MB RAM
DirectX Version: DirectX 12
Card name: NVIDIA GeForce RTX 4070 Ti
Display Memory: 12138 MB'
);
*/