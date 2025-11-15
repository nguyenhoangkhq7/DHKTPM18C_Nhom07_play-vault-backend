CREATE TABLE wishlists (
                           customer_id BIGINT NOT NULL,
                           game_id BIGINT NOT NULL,

                           PRIMARY KEY (customer_id, game_id),

                           CONSTRAINT fk_wishlist_customer
                               FOREIGN KEY (customer_id) REFERENCES customers(id),
                           CONSTRAINT fk_wishlist_game
                               FOREIGN KEY (game_id) REFERENCES games(game_basic_info_id)
);