USE playvaultdb;
SHOW CREATE TABLE system_requirements;
-- =======================================================
-- FILE V3 (CẬP NHẬT): DỮ LIỆU GAME ĐỂ TEST
-- (Khớp với các model mới: Publisher, SystemRequirement)
-- =======================================================

-- 4. SYSTEM REQUIREMENTS (Cấu hình)
-- (Khớp model: os, cpu, gpu, storage, ram)
-- Sửa 'WINDOW' thành 'WINDOWS'
-- Sửa 'MACBOOK' thành 'MAC'
INSERT INTO system_requirements (id, os, cpu, gpu, storage, ram) VALUES
                                                                     (1, 'WINDOWS', 'Intel Core i3-2100', 'NVIDIA GeForce GT 730', '10 GB', '4 GB'),
                                                                     (2, 'WINDOWS', 'Intel Core i5-8400', 'NVIDIA GeForce GTX 1060', '70 GB', '8 GB'),
                                                                     (3, 'WINDOWS', 'Intel Core i7-9700K', 'NVIDIA GeForce RTX 2080', '150 GB', '16 GB'),
                                                                     (4, 'MAC', 'Apple M1', 'Apple M1 8-core GPU', '50 GB', '8 GB');
-- 5. PUBLISHERS (Nhà phát hành)
-- (Khớp model: account_username, studio_name)
INSERT INTO publishers (id, account_username, studio_name, description, website) VALUES
                                                                                     (1, 'pub_epic', 'Epic Games', 'Creators of Fortnite.', 'https://www.epicgames.com'),
                                                                                     (2, 'pub_rockstar', 'Rockstar Games', 'Creators of GTA and RDR.', 'https://www.rockstargames.com'),
                                                                                     (3, 'pub_cdpr', 'CD PROJEKT RED', 'Creators of The Witcher and Cyberpunk.', 'https://www.cdprojektred.com'),
                                                                                     (4, 'pub_valve', 'Valve', 'Creators of Steam, Dota 2, Portal.', 'https://www.valvesoftware.com'),
                                                                                     (5, 'pub_capcom', 'Capcom', 'Creators of Resident Evil.', 'https://www.capcom.com');

-- 6. GAME BASIC INFOS (Thông tin game)
-- (Tham chiếu đến ID của Categories, Publishers, SystemRequirements mới)
INSERT INTO game_basic_infos (id, name, short_description, description, price, category_id, publisher_id, system_requirement_id, thumbnail) VALUES
-- Test Cases: High Price, Action (1), High-spec (3)
(1, 'Cyberpunk 2077', 'Action RPG in a dark future.', 'Một game nhập vai hành động...', 49.99, 1, 3, 3, 'thumb1.jpg'),
-- Test Cases: Mid Price, RPG (3), Mid-spec (2)
(2, 'The Witcher 3: Wild Hunt', 'Open-world RPG.', 'Game nhập vai thế giới mở...', 29.99, 3, 3, 2, 'thumb2.jpg'),
-- Test Cases: Low Price, Simulation (4), Low-spec (1), Mac-spec (4)
(3, 'Stardew Valley', 'Farming simulation.', 'Game mô phỏng nông trại...', 14.99, 4, 1, 1, 'thumb3.jpg'),
-- Test Cases: Mid Price, Adventure (2), High-spec (3)
(4, 'Red Dead Redemption 2', 'Wild West adventure.', 'Phiêu lưu Miền Tây hoang dã...', 39.99, 2, 2, 3, 'thumb4.jpg'),
-- Test Cases: Free (0.00), Strategy (5), Low-spec (1)
(5, 'Dota 2', 'MOBA strategy game.', 'Game chiến thuật MOBA miễn phí...', 0.00, 5, 4, 1, 'thumb5.jpg'),
-- Test Cases: Low Price, Horror (7), Mid-spec (2)
(6, 'Resident Evil Village', 'Survival horror.', 'Kinh dị sinh tồn...', 19.99, 7, 5, 2, 'thumb6.jpg'),
-- Test Cases: Mid Price, Action (1), Mid-spec (2)
(7, 'Grand Theft Auto V', 'Action-adventure.', 'Phiêu lưu hành động...', 29.99, 1, 2, 2, 'thumb7.jpg'),
-- Test Cases: Low Price, Puzzle (6), Low-spec (1)
(8, 'Portal 2', 'Sci-fi puzzle game.', 'Game giải đố khoa học...', 9.99, 6, 4, 1, 'thumb8.jpg'),
-- Test Cases: High Price, Racing (8), High-spec (3)
(9, 'Forza Horizon 5', 'Open-world racing.', 'Đua xe thế giới mở...', 59.99, 8, 2, 3, 'thumb9.jpg'),
-- Test Cases: Free (0.00), Action (1), Mid-spec (2)
(10, 'Apex Legends', 'Free battle royale.', 'Game bắn súng battle royale...', 0.00, 1, 1, 2, 'thumb10.jpg');

-- 7. GAMES (Bảng game chính)
-- (ID phải khớp với game_basic_infos.id)
INSERT INTO games (game_basic_info_id, release_date) VALUES
                                                         (1, '2020-12-10'),
                                                         (2, '2015-05-19'),
                                                         (3, '2016-02-26'),
                                                         (4, '2018-10-26'),
                                                         (5, '2013-07-09'),
                                                         (6, '2021-05-07'),
                                                         (7, '2013-09-17'),
                                                         (8, '2011-04-19'),
                                                         (9, '2021-11-09'),
                                                         (10, '2019-02-04');

-- 8. GAME PLATFORMS (Link Game với Platform)
INSERT INTO game_platforms (game_basic_info_id, platform_id) VALUES
                                                                 (1, 1), (1, 2), (1, 3), -- Cyberpunk (PC, PS, Xbox)
                                                                 (2, 1), (2, 2), (2, 4), -- Witcher 3 (PC, PS, Switch)
                                                                 (3, 1), (3, 5), -- Stardew (PC, Mobile)
                                                                 (4, 1), (4, 2), (4, 3), -- RDR 2 (PC, PS, Xbox)
                                                                 (5, 1), -- Dota 2 (PC)
                                                                 (6, 1), (6, 2), -- RE 8 (PC, PS)
                                                                 (7, 1), (7, 2), (7, 3), -- GTA V (PC, PS, Xbox)
                                                                 (8, 1), -- Portal 2 (PC)
                                                                 (9, 1), (9, 3), -- Forza 5 (PC, Xbox)
                                                                 (10, 1), (10, 2), (10, 4); -- Apex (PC, PS, Switch)