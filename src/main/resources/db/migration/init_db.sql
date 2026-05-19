CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE stock (
                       book_id BIGINT PRIMARY KEY,
                       quantity INT NOT NULL DEFAULT 0,
                       CONSTRAINT fk_stock_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        payment_method VARCHAR(30),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE orders ADD COLUMN finished_at TIMESTAMP NULL;

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             book_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             price_at_purchase DECIMAL(10, 2) NOT NULL,
                             CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_item_book FOREIGN KEY (book_id) REFERENCES books(id)
);
