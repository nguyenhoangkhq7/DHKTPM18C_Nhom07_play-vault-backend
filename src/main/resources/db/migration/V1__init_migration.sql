USE playvaultdb;

CREATE TABLE accounts (
                          username VARCHAR(255) PRIMARY KEY,
                          password VARCHAR(255) NOT NULL,
                          created_at DATE,
                          status VARCHAR(50) NOT NULL,
                          role VARCHAR(50) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          phone VARCHAR(20),

                          CONSTRAINT chk_account_status CHECK (status IN ('ACTIVE', 'BANNED', 'LOCKED')),
                          CONSTRAINT chk_role CHECK (role IN ('CUSTOMER', 'PUBLISHER', 'ADMIN'))
);

CREATE TABLE customers (
                           id BIGINT PRIMARY KEY,
                           full_name VARCHAR(255),
                           phone_number VARCHAR(20),
                           date_of_birth DATE,
                           balance DECIMAL(10, 2) DEFAULT 0.0,
                           account_username VARCHAR(255) NOT NULL UNIQUE,

                           CONSTRAINT fk_customer_account
                               FOREIGN KEY (account_username) REFERENCES accounts(username)
);

CREATE TABLE payment_infos(
                              id BIGINT PRIMARY KEY,
                              payment_method VARCHAR(50) NOT NULL,
                              account_name VARCHAR(255),
                              account_number VARCHAR(100),
                              bank_name VARCHAR(255),
                              is_verified BOOLEAN DEFAULT FALSE,
                              CONSTRAINT chk_payment_method_in_payment_info CHECK (payment_method IN ('ZALOPAY', 'MOMO', 'BANK'))
);

CREATE TABLE publishers (
                            id BIGINT PRIMARY KEY,
                            account_username VARCHAR(255) NOT NULL UNIQUE,
                            studio_name VARCHAR(255) NOT NULL,
                            description LONGTEXT,
                            website VARCHAR(255),
                            payment VARCHAR(255),
                            payment_info_id BIGINT UNIQUE,

                            CONSTRAINT fk_publisher_account
                                FOREIGN KEY (account_username) REFERENCES accounts(username),
                            CONSTRAINT fk_publisher_payment_info
                                FOREIGN KEY (payment_info_id) REFERENCES payment_infos(id)
);

CREATE TABLE categories (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description LONGTEXT
);


CREATE TABLE system_requirements (
                                     id BIGINT PRIMARY KEY,
                                     os VARCHAR(50),
                                     cpu VARCHAR(255),
                                     gpu VARCHAR(255),
                                     storage VARCHAR(100),
                                     ram VARCHAR(100),

                                     CONSTRAINT chk_os CHECK (os IN ('WINDOWS', 'MAC', 'LINUX'))
);

CREATE TABLE platforms (
                           id BIGINT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE game_basic_infos (
                                  id BIGINT PRIMARY KEY,
                                  name VARCHAR(255) NOT NULL,
                                  short_description LONGTEXT,
                                  description LONGTEXT,
                                  price DECIMAL(10, 2) DEFAULT 0.0,
                                  file_path VARCHAR(255),
                                  thumbnail VARCHAR(255),
                                  trailer_url VARCHAR(255),
                                  required_age INT,
                                  is_support_controller BOOLEAN,

                                  category_id BIGINT,
                                  publisher_id BIGINT,
                                  system_requirement_id BIGINT,

                                  CONSTRAINT fk_gbi_category
                                      FOREIGN KEY (category_id) REFERENCES categories(id),
                                  CONSTRAINT fk_gbi_publisher
                                      FOREIGN KEY (publisher_id) REFERENCES publishers(id),
                                  CONSTRAINT fk_gbi_system_req
                                      FOREIGN KEY (system_requirement_id) REFERENCES system_requirements(id)
);

CREATE TABLE game_platforms (
                                game_basic_info_id BIGINT NOT NULL,
                                platform_id BIGINT NOT NULL,

                                CONSTRAINT fk_gp_game
                                    FOREIGN KEY (game_basic_info_id) REFERENCES game_basic_infos(id),
                                CONSTRAINT fk_gp_platform
                                    FOREIGN KEY (platform_id) REFERENCES platforms(id),

                                PRIMARY KEY (game_basic_info_id, platform_id)
);

CREATE TABLE game_submissions (
                                  game_basic_info_id BIGINT PRIMARY KEY,
                                  status VARCHAR(50) NOT NULL,
                                  reject_reason LONGTEXT,
                                  submitted_at DATE,
                                  reviewer_username VARCHAR(255),
                                  reviewed_at DATE,

                                  CONSTRAINT fk_submission_basic_info
                                      FOREIGN KEY (game_basic_info_id) REFERENCES game_basic_infos(id),
                                  CONSTRAINT fk_submission_reviewer
                                      FOREIGN KEY (reviewer_username) REFERENCES accounts(username),
                                  CONSTRAINT chk_submission_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE games (
                       game_basic_info_id BIGINT PRIMARY KEY,
                       release_date DATE,

                       CONSTRAINT fk_game_basic_info
                           FOREIGN KEY (game_basic_info_id) REFERENCES game_basic_infos(id)
);

CREATE TABLE reviews (
                         id BIGINT PRIMARY KEY,
                         rating INT,
                         comment LONGTEXT,
                         created_at DATE,

                         customer_id BIGINT NOT NULL,
                         game_id BIGINT NOT NULL,

                         CONSTRAINT fk_review_customer
                             FOREIGN KEY (customer_id) REFERENCES customers(id),
                         CONSTRAINT fk_review_game
                             FOREIGN KEY (game_id) REFERENCES games(game_basic_info_id)
);

CREATE TABLE promotions (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description LONGTEXT,
                            start_date DATE,
                            end_date DATE,
                            is_active BOOLEAN DEFAULT FALSE,
                            discount_percent DECIMAL(5, 2),
                            discount_amount DECIMAL(10, 2),

                            publisher_id BIGINT NOT NULL,

                            CONSTRAINT fk_promo_publisher
                                FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);

CREATE TABLE carts (
                       id BIGINT PRIMARY KEY,
                       total_price DECIMAL(10, 2) DEFAULT 0.0
);

CREATE TABLE cart_items (
                            id BIGINT PRIMARY KEY,
                            price DECIMAL(10, 2),
                            discount DECIMAL(10, 2),

                            cart_id BIGINT NOT NULL,
                            game_id BIGINT NOT NULL,

                            CONSTRAINT fk_cartitem_cart
                                FOREIGN KEY (cart_id) REFERENCES carts(id),
                            CONSTRAINT fk_cartitem_game
                                FOREIGN KEY (game_id) REFERENCES games(game_basic_info_id)
);
ALTER TABLE customers
    ADD COLUMN cart_id BIGINT UNIQUE,
    ADD CONSTRAINT fk_customer_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id);

CREATE TABLE user_libraries (
                                customer_id BIGINT NOT NULL,
                                game_id BIGINT NOT NULL,

                                CONSTRAINT fk_library_customer
                                    FOREIGN KEY (customer_id) REFERENCES customers(id),
                                CONSTRAINT fk_library_game
                                    FOREIGN KEY (game_id) REFERENCES games(game_basic_info_id),
                                PRIMARY KEY (customer_id, game_id)
);

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY,
                        created_at DATE,
                        total DECIMAL(10, 2),
                        status VARCHAR(50) NOT NULL,
                        customer_id BIGINT NOT NULL,
                        payment_id BIGINT,

                        CONSTRAINT fk_order_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id),
                        CONSTRAINT chk_order_status
                            CHECK (status IN ('PENDING', 'PROCESSING', 'PAID', 'COMPLETED', 'FAILED', 'CANCELLED'))
);

CREATE TABLE order_items (
                             id BIGINT PRIMARY KEY,
                             price DECIMAL(10, 2),
                             total DECIMAL(10, 2),

                             order_id BIGINT NOT NULL,
                             game_id BIGINT NOT NULL,
                             promotion_id BIGINT NULL,
                             CONSTRAINT fk_orderitem_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id),

                             CONSTRAINT fk_orderitem_game
                                 FOREIGN KEY (game_id) REFERENCES games(game_basic_info_id),

                             CONSTRAINT fk_orderitem_promotion
                                 FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

CREATE TABLE invoices (
                          id BIGINT PRIMARY KEY,
                          issue_date DATE,
                          total_amount DECIMAL(10, 2),
                          status VARCHAR(50) NOT NULL,

                          order_id BIGINT NOT NULL UNIQUE,
                          customer_id BIGINT NOT NULL,

                          CONSTRAINT fk_invoice_order
                              FOREIGN KEY (order_id) REFERENCES orders(id),
                          CONSTRAINT fk_invoice_customer
                              FOREIGN KEY (customer_id) REFERENCES customers(id),
                          CONSTRAINT chk_invoice_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

CREATE TABLE payments (
                          id BIGINT PRIMARY KEY,
                          amount DECIMAL(10, 2),
                          payment_date DATE,
                          payment_method VARCHAR(50),
                          status VARCHAR(50),

                          invoice_id BIGINT,

                          CONSTRAINT fk_payment_invoice
                              FOREIGN KEY (invoice_id) REFERENCES invoices(id),
                          CONSTRAINT chk_payment_method_in_payments CHECK (payment_method IN ('PAYPAL', 'ZALOPAY')),
                          CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

ALTER TABLE orders
    ADD CONSTRAINT fk_order_payment
        FOREIGN KEY (payment_id) REFERENCES payments(id);

CREATE TABLE reports (
                         id BIGINT PRIMARY KEY,
                         title VARCHAR(255),
                         description LONGTEXT,
                         handler_note LONGTEXT,
                         created_at DATE,
                         resolved_at DATE,
                         status VARCHAR(50) NOT NULL,

                         order_id BIGINT NOT NULL,
                         customer_id BIGINT NOT NULL,
                         handler_username VARCHAR(255),

                         CONSTRAINT fk_report_order
                             FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_report_customer
                             FOREIGN KEY (customer_id) REFERENCES customers(id),
                         CONSTRAINT fk_report_handler
                             FOREIGN KEY (handler_username) REFERENCES accounts(username),
                         CONSTRAINT chk_report_status CHECK (status IN ('PENDING', 'RESOLVED', 'REJECT'))
);

CREATE TABLE block_records (
                               id BIGINT PRIMARY KEY,
                               is_block BOOLEAN,
                               created_at DATE,
                               reason LONGTEXT,

                               account_username VARCHAR(255) NOT NULL,

                               CONSTRAINT fk_block_account
                                   FOREIGN KEY (account_username) REFERENCES accounts(username)
);