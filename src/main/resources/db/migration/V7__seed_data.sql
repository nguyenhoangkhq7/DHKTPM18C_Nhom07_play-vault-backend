-- V7__Seed_Data.sql
-- Tắt kiểm tra khóa ngoại để insert dữ liệu theo bất kỳ thứ tự nào mà không bị lỗi
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Insert Accounts
INSERT INTO `accounts` (`username`, `password`, `created_at`, `status`, `role`, `email`, `phone`) VALUES
                                                                                                      ('admin','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-01-01','ACTIVE','ADMIN','admin@playvault.com','0901000001'),
                                                                                                      ('cust_pro_gamer','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2025-11-16','BANNED','CUSTOMER','gamer@gmail.com','0909111222'),
                                                                                                      ('customer1','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-01-01','ACTIVE','CUSTOMER','c1@gmail.com','0901111111'),
                                                                                                      ('new_publisher','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2025-11-19','ACTIVE','PUBLISHER','contact@devstudio.com','0912345678'),
                                                                                                      ('nguyenhoangkhq','$2a$10$9RU7hzBQj01hK1eqJpEIA.Tx0pBoItzXLouWN3ckfAcwe/wTKNsIG','2025-12-06','ACTIVE','CUSTOMER','nguyenhoangkhq7@gmail.com','0385309242'),
                                                                                                      ('publisher1','$2a$12$.A8V0nMVEKsKKJ1REgxCAejK.khQBphBWVFded9syuCQ9yTTcpMt2','2024-01-01','ACTIVE','PUBLISHER','pub@gmail.com','0902222222'),
                                                                                                      ('publisher2','$2a$10$jZtebgbkE3Y48VvKRJ.euuWk/MSAU5563OnxJ//uicM3tCjMskiQq','2025-12-06','ACTIVE','PUBLISHER','nguyenhoangkhq8@gmail.com','0901111111');

-- 2. Insert Base Info (Categories, Platforms, System Requirements, Payment Infos)
INSERT INTO `categories` (`id`, `name`, `description`) VALUES
                                                           (1,'Action','Game hành động, bắn súng, đánh nhau.'),
                                                           (2,'Adventure','Game phiêu lưu, khám phá thế giới.'),
                                                           (3,'RPG','Nhập vai, phát triển nhân vật.'),
                                                           (4,'Simulation','Mô phỏng cuộc sống, xây dựng.'),
                                                           (5,'Strategy','Chiến thuật, quản lý tài nguyên.'),
                                                           (6,'Puzzle','Game giải đố, tư duy.'),
                                                           (7,'Horror','Game kinh dị, sinh tồn.'),
                                                           (8,'Racing','Đua xe, tốc độ.');

INSERT INTO `platforms` (`id`, `name`) VALUES
                                           (1,'PC'),
                                           (2,'PlayStation'),
                                           (3,'Xbox'),
                                           (4,'Nintendo Switch'),
                                           (5,'Mobile');

INSERT INTO `system_requirements` (`id`, `os`, `cpu`, `gpu`, `storage`, `ram`) VALUES
                                                                                   (1,'WINDOWS','Intel i5','GTX 960','20GB','8GB'),
                                                                                   (2,'WINDOWS','Intel i7','GTX 1060','40GB','16GB'),
                                                                                   (3,'WINDOWS','AMD Ryzen 5','RX 580','30GB','8GB'),
                                                                                   (4,'WINDOWS','i5','GTX 1050','10GB','8GB'),
                                                                                   (5,'WINDOWS','i7','RTX 2060','20GB','16GB'),
                                                                                   (6,'WINDOWS','i7-9700K','RTX 3070','80GB','16GB'),
                                                                                   (7,'WINDOWS','Intel i5 2400','GTX 950','10','2'),
                                                                                   (8,'WINDOWS','I5 4400','AMD 960','4','2'),(9,'WINDOWS','I5 2440','GTX 950','20','4');

INSERT INTO `payment_infos` (`id`, `payment_method`, `account_name`, `account_number`, `bank_name`, `is_verified`) VALUES
                                                                                                                       (1,'BANK','Publisher Bank','123456789','ACB',1),
                                                                                                                       (3,'MOMO','Aaa','0123456778','Momo',0);

-- 3. Insert Publishers & Publisher Requests
INSERT INTO `publishers` (`id`, `account_username`, `studio_name`, `description`, `website`, `payment_info_id`) VALUES
                                                                                                                    (1,'publisher1','Dream Studio','Indie game studio','https://dreamstudio.dev',1),
                                                                                                                    (3,'publisher2','ấdsad','ádasdádasd','https://accounts.google.com',3);

INSERT INTO `publisher_requests` (`id`, `account_username`, `status`, `created_at`, `updated_at`) VALUES
                                                                                                      (1,'publisher1','APPROVED','2025-11-16','2025-11-19'),
                                                                                                      (2,'publisher2','PENDING','2025-12-06','2025-12-06');

-- 4. Insert Carts & Customers
INSERT INTO `carts` (`id`, `total_price`) VALUES
                                              (1,0.00),(2,229.93),(3,0.00),(4,100009.99),(5,0.00),(6,0.00);

INSERT INTO `customers` (`id`, `full_name`, `avatar_url`, `date_of_birth`, `balance`, `account_username`, `cart_id`) VALUES
                                                                                                                         (1,'Nguyen Van Gamer',NULL,'2000-01-01',6000200.00,'cust_pro_gamer',2),
                                                                                                                         (2,'John Doe','https://res.cloudinary.com/dhkxrxsyc/image/upload/v1763520416/x62ysqqqcgdepb4nhmo4.jpg','2000-01-01',0.00,'customer1',4),
                                                                                                                         (4,'hoang khang',NULL,'2000-01-02',NULL,'nguyenhoangkhq',6);

-- 5. Insert Game Data (Basic Infos, Promotions, Games, Platforms, Submissions, Images)
INSERT INTO `game_basic_infos` (`id`, `name`, `short_description`, `description`, `price`, `file_path`, `thumbnail`, `trailer_url`, `required_age`, `is_support_controller`, `category_id`, `publisher_id`, `system_requirement_id`) VALUES
                                                                                                                                                                                                                                         (1,'CyberRun','Fast cyberpunk runner ','Full cyberpunk action...',21.00,'/files/cyberrun.zip','https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2526150/header.jpg?t=1752661826','https://youtu.be/test',16,1,1,1,4),
                                                                                                                                                                                                                                         (2,'Magic Quest','Fantasy RPG adventure','Explore magic world...',9.99,'/files/magicquest.zip','https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/454870/capsule_616x353.jpg?t=1462590110','https://youtu.be/test2',12,0,3,1,4),
                                                                                                                                                                                                                                         (3,'Galaxy Warzone','Sci-fi space shooting','Epic war in space...',14.99,'/files/galaxywarzone.zip','https://image.api.playstation.com/vulcan/ap/rnd/202308/3016/4a578a555113ea1b1e756aa55786cffff22d47ce9a3f2464.png?w=440','https://youtu.be/trailer3',12,1,1,1,5),
                                                                                                                                                                                                                                         (4,'Dungeon Breaker','Classic dungeon crawler','Fight monsters in dungeons...',4.99,'/files/dungeonbreaker.zip','https://i.ytimg.com/vi/Z2K7Gp89FvQ/maxresdefault.jpg','https://youtu.be/trailer4',10,0,3,1,5),
                                                                                                                                                                                                                                         (5,'Speed Racers X','High-speed futuristic racing','Race in neon futuristic cities...',24.99,'/files/speedracersx.zip','https://m.media-amazon.com/images/M/MV5BOWY5ZDk2MGQtMTI5ZC00YzhlLWFhMDQtMmQyZTE4NTU0MWQ5XkEyXkFqcGc@._V1_.jpg','https://youtu.be/trailer5',7,1,8,1,5),
                                                                                                                                                                                                                                         (6,'Horizon Zero Dawn','Open-world action RPG','Battle robot creatures in a stunning open world.',49.99,'/files/horizonzerodawn.zip','https://cdn1.epicgames.com/3328b08ac1c14540aa265a1a85c07839/offer/hzd_wide-2560x1440-bd312be05c49cf339097777c493cb899.jpg','https://youtu.be/u4-FCsiF5x4',16,1,2,1,6),
                                                                                                                                                                                                                                         (7,'The Witcher 3: Wild Hunt','Legendary open-world RPG','Play as Geralt and hunt monsters across a vast world.',39.99,'/files/witcher3.zip','https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/292030/ad9240e088f953a84aee814034c50a6a92bf4516/header.jpg?t=1761131270','https://youtu.be/ehjJ614QfeM',18,1,3,1,6),
                                                                                                                                                                                                                                         (8,'Elden Ring','Open-world soulslike','Explore the Lands Between in a brutal, beautiful adventure.',59.99,'/files/eldenring.zip','https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg?t=1748630546','https://youtu.be/E3Huy2cdih0',16,1,1,1,6),
                                                                                                                                                                                                                                         (9,'Cyberpunk 2077','Open-world sci-fi RPG','Enter Night City in a futuristic action RPG.',59.99,'/files/cyberpunk2077.zip','https://cdn1.epicgames.com/offer/77f2b98e2cef40c8a7437518bf420e47/EGS_Cyberpunk2077_CDPROJEKTRED_S1_03_2560x1440-359e77d3cd0a40aebf3bbc130d14c5c7','https://youtu.be/8X2kIfS6fb8',18,1,1,1,6),
                                                                                                                                                                                                                                         (14,'Hành trình bất tận','game hay','game rất mô tả hay',100000.00,'','','https://www.youtube.com/watch?v=8X2kIfS6fb8',16,1,1,1,7),
                                                                                                                                                                                                                                         (15,'Game hay','game hay cực','Tính năng thanh toán thực tế: Hiện tại quy trình nạp G-Coin thông qua mã QR VietQR vẫn cần sự xác nhận hoặc xử lý bán tự động ở một số bước. Chưa tích hợp hoàn toàn API thanh toán tự động (IPN) từ các cổng thanh toán lớn (như VNPAY, MoMo) để tự động cập nhật số dư tức thì trong mọi trường hợp mạng không ổn định.\nQuy trình Digital Download: Giải quyết được bài toán bản quyền số. Hệ thống tự động kiểm tra quyền sở hữu (isOwned) trước khi cho phép tải xuống, ngăn chặn việc chia sẻ liên kết trái phép thông qua cơ chế Token tải xuống có thời hạn.\n',0.00,'https://drive.google.com/uc?id=1pLQ4JJZStnp8Gsf-cT7kTeymMfxDHYLW','https://lh3.googleusercontent.com/d/1D1t8sTgYigUFJ04c0ylDyZMsGpE1Tuct=w1200','https://www.youtube.com/watch?v=8X2kIfS6fb8',10,1,1,1,8),
                                                                                                                                                                                                                                         (16,'Pokemon Go','Game hay ','Dưới đây là một bản mô tả chi tiết và sâu sắc về dòng game Pokémon (viết tắt của Pocket Monsters), bao quát từ lối chơi, cốt truyện cho đến sức hút lâu dài của thương hiệu này.\n\nPokémon: Hành Trình Của Những Nhà Huấn Luyện\nPokémon không đơn thuần là một trò chơi điện tử; nó là một hành trình nhập vai (RPG) đầy màu sắc, nơi người chơi bước vào một thế giới giả tưởng rộng lớn, chung sống cùng những sinh vật bí ẩn và đầy quyền năng được gọi là Pokémon. Được phát triển bởi Game Freak và phát hành bởi Nintendo, dòng game này đã định hình tuổi thơ của hàng triệu người và trở thành một tượng đài văn hóa toàn cầu.\n\n1. Khởi Đầu Và Sứ Mệnh: \"Gotta Catch \'Em All\"\nTrò chơi thường bắt đầu với một mô hời quen thuộc nhưng đầy cảm hứng: Bạn là một đứa trẻ (khoảng 10-11 tuổi) sống tại một thị trấn nhỏ yên bình. Bạn nhận được Pokémon khởi đầu (Starter Pokémon) từ một Giáo sư trong vùng. Đây là lựa chọn quan trọng nhất game, thường xoay quanh bộ ba nguyên tố cơ bản:\n\nHệ Cỏ (Grass): Mạnh mẽ, bền bỉ.\n\nHệ Lửa (Fire): Tấn công cao, nhiệt huyết.\n\nHệ Nước (Water): Linh hoạt, cân bằng.\n\nMục tiêu của bạn bao gồm hai nhiệm vụ lớn:\n\nTrở thành Nhà vô địch: Đánh bại 8 Thủ lĩnh Nhà thi đấu (Gym Leaders), thách thức Tứ Đại Thiên Vương (Elite Four) và Nhà vô địch vùng (Champion).\n\nHoàn thành Pokédex: Đi khắp thế giới để tìm kiếm, bắt giữ và ghi lại dữ liệu của mọi loài Pokémon tồn tại.\n\n2. Lối Chơi Chiến Thuật Sâu Sắc\nDù vẻ ngoài dễ thương, Pokémon sở hữu một hệ thống chiến đấu theo lượt (turn-based) cực kỳ phức tạp và có chiều sâu chiến thuật:\n\nTương khắc hệ (Type Matchups): Đây là cốt lõi của gameplay. Mỗi Pokémon mang một hoặc hai hệ (như Điện, Rồng, Tiên, Ma, Thép...). Quy luật tương khắc hoạt động như \"Oẳn tù tì\" nhưng phức tạp hơn: Nước dập Lửa, Lửa đốt Cỏ, Cỏ hút Nước, Điện giật Nước nhưng vô hại với Đất.\n\nChiến đấu theo lượt: Mỗi lượt, bạn chọn một trong bốn chiêu thức (Moves) hoặc sử dụng vật phẩm. Việc dự đoán nước đi của đối thủ, tính toán sát thương và quản lý trạng thái (như Bỏng, Tê liệt, Ngủ) là chìa khóa chiến thắng.\n\nTiến hóa (Evolution): Khi đạt đủ cấp độ hoặc đáp ứng điều kiện đặc biệt, Pokémon của bạn sẽ biến hình. Chúng trở nên to lớn hơn, ngầu hơn và mạnh mẽ hơn. Cảm giác nhìn thấy chú sâu nhỏ Caterpie hóa thành bướm Butterfree xinh đẹp luôn mang lại sự thỏa mãn lớn.\n\n3. Thế Giới Mở Và Sự Khám Phá\nMỗi thế hệ game (Generation) giới thiệu một vùng đất mới (Region), thường được lấy cảm hứng từ các địa danh thực tế:\n\nKanto/Johto: Dựa trên Nhật Bản.\n\nUnova: Dựa trên New York, Mỹ.\n\nKalos: Dựa trên Pháp.\n\nGalar: Dựa trên Anh Quốc.\n\nPaldea: Dựa trên Tây Ban Nha/Bồ Đào Nha.\n\nNgười chơi phải băng qua những khu rừng rậm rạp, leo lên những ngọn núi tuyết, thám hiểm hang động tối tăm và vượt biển cả mênh mông. Trên đường đi, bạn sẽ đụng độ các Pokémon hoang dã nấp trong bụi cỏ và chiến đấu với các Nhà huấn luyện (Trainer) khác để kiếm tiền và kinh nghiệm.\n\n4. Cốt Truyện Và Những Thế Lực Đối Địch\nBên cạnh việc thi đấu thể thao, người chơi luôn bị cuốn vào một âm mưu lớn hơn do các Đội quân phản diện (Evil Teams) gây ra. Từ Team Rocket (những kẻ trộm chó mèo mafia) đến Team Galactic (muốn hủy diệt vũ trụ để tái tạo thế giới mới), người chơi vô tình trở thành người hùng giải cứu thế giới, thường là nhờ sự giúp đỡ của các Pokémon Huyền thoại (Legendary Pokémon) – những thực thể thần thánh đại diện cho thời gian, không gian, sự sống hoặc cái chết.\n\n5. Kết Nối Cộng Đồng\nPokémon là một trong những game đầu tiên khuyến khích sự kết nối xã hội mạnh mẽ:\n\nTrao đổi (Trading): Mỗi phiên bản game thường thiếu một số loài Pokémon nhất định, buộc người chơi phải giao lưu, trao đổi với bạn bè để hoàn thành Pokédex.\n\nThi đấu (Competitive Battling): Ngoài cốt truyện, cộng đồng Pokémon thế giới cực kỳ sôi động với các giải đấu eSports, nơi người chơi tính toán từng chỉ số ẩn (IVs, EVs) để tạo ra đội hình tối thượng.',100000.00,'https://drive.google.com/uc?id=1vKA_ZVoycd5mu1-L_HOQJbyxIiOTBhra','https://lh3.googleusercontent.com/d/125DR-7ri16zmzaumUP8HlefEA89zg0lW=w1200','https://youtu.be/1roy4o4tqQM',12,1,1,1,9);

INSERT INTO `preview_images` (`id`, `url`, `game_basic_info_id`) VALUES
                                                                     (1,'https://static.cdprojektred.com/cms.cdprojektred.com/16x9_big/872822c5e50dc71f345416098d29fc3ae5cd26c1-1280x720.jpg',1),
                                                                     (2,'https://images.ladbible.com/resize?type=webp&quality=70&width=3840&fit=contain&gravity=auto&url=https://images.ladbiblegroup.com/v3/assets/bltbc1876152fcd9f07/bltc84db100620e345e/67c71bd2ab5168007f14aea4/cydemo.jpg',1),
                                                                     (3,'https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2023_10_21_638335013236510812_cyberpunk-la-gi-thum.jpg',1),
                                                                     (4,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0d.jpg',8),
                                                                     (5,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0e.jpg',8),
                                                                     (6,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0f.jpg',8),
                                                                     (7,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0g.jpg',9),
                                                                     (8,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0h.jpg',9),
                                                                     (9,'https://images.igdb.com/igdb/image/upload/t_1080p/ar1f0i.jpg',6),
                                                                     (10,'https://lh3.googleusercontent.com/d/1skhtLVyfnmuMcGQAzsA9WOg2MBnAv8We=w1200',15),
                                                                     (11,'https://lh3.googleusercontent.com/d/1YrnjdGLSqntSHQiFZ_p3ptT3Rq6Gu2HU=w1200',15),
                                                                     (12,'https://lh3.googleusercontent.com/d/1YXEkAX3nH85WxH_0gJv1P0yq_Tw26Niw=w1200',15),
                                                                     (13,'https://lh3.googleusercontent.com/d/1SclbL0sg7c7NbRyT8KADtgpWjUS1Rx6S=w1200',15),
                                                                     (14,'https://lh3.googleusercontent.com/d/1YAZU35vsV5MiJw-yB1xBH0c7H3x5amL-=w1200',16),
                                                                     (15,'https://lh3.googleusercontent.com/d/18a9jQae9H7iHjk2PewTRQS4lVYQtcYfz=w1200',16),
                                                                     (16,'https://lh3.googleusercontent.com/d/1kMm4vUxqGTyONAPZFNXf_czRqk6ULWVI=w1200',16),
                                                                     (17,'https://lh3.googleusercontent.com/d/1vSCZTprqs8vRBDHvMCxN9knnUBlKZG8S=w1200',16);

INSERT INTO `game_platforms` (`game_basic_info_id`, `platform_id`) VALUES
                                                                       (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(16,1),(6,2),(7,2),(8,2),(9,2),(3,3),(7,3),(8,3),(9,3),(1,5),(5,5),(6,5);

INSERT INTO `game_submissions` (`game_basic_info_id`, `status`, `reject_reason`, `submitted_at`, `reviewer_username`, `reviewed_at`) VALUES
                                                                                                                                         (14,'APPROVED',NULL,'2025-12-06',NULL,'2025-12-06'),
                                                                                                                                         (15,'APPROVED',NULL,'2025-12-07',NULL,'2025-12-07'),
                                                                                                                                         (16,'REJECTED','game này vi phạm bản quyền game khác','2025-12-08','admin','2025-12-08');

INSERT INTO `promotions` (`id`, `name`, `description`, `start_date`, `end_date`, `is_active`, `discount_percent`, `discount_amount`, `publisher_id`) VALUES
                                                                                                                                                         (1,'Black Friday Mega Sale','Giảm tới 70% toàn bộ game!','2025-11-15','2025-12-05',1,70.00,NULL,1),
                                                                                                                                                         (2,'Weekend Flash Sale','Chỉ cuối tuần này - Cyberpunk 2077 giảm 40%','2025-11-21','2025-11-24',0,40.00,NULL,1),
                                                                                                                                                         (3,'Elden Ring 50% OFF','Giảm giá kỷ niệm 3 năm phát hành','2025-11-18','2025-11-30',1,50.00,NULL,1),
                                                                                                                                                         (4,'jjjj','','2025-11-20','2025-11-21',1,100.00,NULL,1),
                                                                                                                                                         (5,'Mới','Khuyến mãi cuối tuần','2025-12-06','2025-12-08',1,30.00,NULL,1);

INSERT INTO `games` (`game_basic_info_id`, `release_date`, `promotion_id`) VALUES
                                                                               (1,'2024-02-01',2),(2,'2024-03-01',1),(3,'2024-04-01',1),(4,'2024-05-01',1),(5,'2024-06-01',1),(6,'2020-08-01',NULL),(7,'2015-05-19',NULL),(8,'2022-02-25',3),(9,'2020-12-10',2),(14,'2025-12-09',5),(15,'2025-12-12',NULL),(16,'2025-12-10',NULL);

-- 6. Insert Order, Payment & Interactions
INSERT INTO `payments` (`id`, `amount`, `payment_date`, `payment_method`, `status`, `invoice_id`) VALUES
                                                                                                      (1,50000.00,'2025-12-06','ZALOPAY','SUCCESS',NULL),
                                                                                                      (2,500000.00,'2025-12-06','ZALOPAY','SUCCESS',NULL),
                                                                                                      (3,500000.00,'2025-12-06','ZALOPAY','SUCCESS',NULL),
                                                                                                      (4,500000.00,'2025-12-06','ZALOPAY','SUCCESS',NULL);

INSERT INTO `orders` (`id`, `created_at`, `total`, `status`, `customer_id`, `payment_id`) VALUES
                                                                                              (1,'2025-11-16',29.98,'PAID',1,NULL),
                                                                                              (2,'2025-11-18',71.96,'COMPLETED',2,NULL),
                                                                                              (3,'2025-12-05',-0.03,'COMPLETED',2,NULL),
                                                                                              (4,'2025-12-06',14.99,'COMPLETED',2,NULL),
                                                                                              (5,'2025-12-06',100000.00,'PENDING',2,NULL),
                                                                                              (6,'2025-12-09',21.00,'COMPLETED',2,NULL);

INSERT INTO `order_items` (`id`, `price`, `total`, `order_id`, `game_id`, `promotion_id`) VALUES
                                                                                              (1,19.99,19.99,1,1,NULL),
                                                                                              (2,9.99,9.99,1,2,NULL),
                                                                                              (3,19.99,6.00,2,1,1),
                                                                                              (4,59.99,30.00,2,8,3),
                                                                                              (5,59.99,35.99,2,9,2),
                                                                                              (6,19.99,50.01,3,1,NULL),
                                                                                              (7,59.99,9.99,3,8,NULL),
                                                                                              (8,59.99,19.99,3,9,NULL),
                                                                                              (9,20.00,20.00,3,1,NULL),
                                                                                              (10,14.99,14.99,4,3,NULL),
                                                                                              (11,100000.00,100000.00,5,14,NULL),
                                                                                              (12,21.00,21.00,6,1,NULL);

INSERT INTO `cart_items` (`id`, `price`, `discount`, `cart_id`, `game_id`) VALUES
                                                                               (1,19.99,0.00,2,1),(2,9.99,0.00,2,2),(3,14.99,0.00,2,3),(7,39.99,0.00,2,7),(8,59.99,0.00,2,8),(9,59.99,0.00,2,9),(14,24.99,0.00,2,5),(26,9.99,0.00,4,2),(30,100000.00,0.00,4,16);

INSERT INTO `wishlists` (`customer_id`, `game_id`) VALUES
                                                       (2,2),(2,6),(2,7),(2,16);

INSERT INTO `user_libraries` (`customer_id`, `game_id`) VALUES
                                                            (2,1),(2,3),(2,8),(2,9),(2,14);

INSERT INTO `reviews` (`id`, `rating`, `comment`, `created_at`, `customer_id`, `game_id`) VALUES
                                                                                              (1,5,'Game đỉnh cao, đồ họa đẹp mê hồn, đáng tiền từng xu!','2025-11-19',2,8),
                                                                                              (2,4,'Chơi ổn, nhưng tối ưu chưa tốt trên máy yếu.','2025-11-18',2,1),
                                                                                              (3,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!','2025-11-30',2,1),
                                                                                              (4,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (5,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (6,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (7,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (8,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (9,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (10,5,'Game chơi rất hay, đồ họa đẹp tuyệt vời!1','2025-11-30',2,1),
                                                                                              (11,5,'Game rất hay, tôi rất thích, rất xinh đẹp tuyệt vời, xin cảm ơn','2025-12-05',2,8),
                                                                                              (12,5,'sâsdasdsadsadasdsadasd','2025-12-05',2,8),
                                                                                              (13,1,'Game rất hay ','2025-12-06',2,8),
                                                                                              (14,1,'Game hay aaa','2025-12-06',2,14),
                                                                                              (15,5,'aaaaaaaaaaaaa','2025-12-06',2,14);

INSERT INTO `block_records` (`id`, `is_block`, `created_at`, `reason`, `account_username`) VALUES
    (1,1,'2025-11-19','Spam, quảng cáo link lừa đảo trong phần bình luận game','cust_pro_gamer');

INSERT INTO `reports` (`id`, `title`, `description`, `handler_note`, `created_at`, `resolved_at`, `status`, `order_id`, `customer_id`, `handler_username`) VALUES
    (1,'Không nhận được key game CyberRun','Mua hôm qua nhưng chưa thấy key trong library, giúp mình với admin ơi!',NULL,'2025-11-19',NULL,'PENDING',2,2,NULL);

-- Bật lại kiểm tra khóa ngoại sau khi insert xong
SET FOREIGN_KEY_CHECKS = 1;