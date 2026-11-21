-- ============================================================
--  Retail Store Management System  -  MySQL schema + sample data
--  Requires MySQL 8.0.16 or newer (CHECK constraints are enforced).
--  Run in MySQL Workbench:  File > Open SQL Script > Execute (lightning bolt)
-- ============================================================

DROP DATABASE IF EXISTS inventory_db;
CREATE DATABASE inventory_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventory_db;

-- ------------------------------------------------------------
-- 1. categories
-- ------------------------------------------------------------
CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(60)  NOT NULL,
    description   VARCHAR(200) NULL,
    CONSTRAINT uq_category_name UNIQUE (category_name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. suppliers   (Supplier extends Person in the Java code)
-- ------------------------------------------------------------
CREATE TABLE suppliers (
    supplier_id  INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    name         VARCHAR(100) NOT NULL,          -- contact person
    phone        VARCHAR(30)  NULL,
    email        VARCHAR(100) NULL,
    address      VARCHAR(200) NULL,
    CONSTRAINT uq_supplier_company UNIQUE (company_name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. products
-- ------------------------------------------------------------
CREATE TABLE products (
    product_id      INT AUTO_INCREMENT PRIMARY KEY,
    product_code    VARCHAR(30)   NOT NULL,
    product_name    VARCHAR(100)  NOT NULL,
    category_id     INT           NOT NULL,
    supplier_id     INT           NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    stock_quantity  INT           NOT NULL DEFAULT 0,
    min_stock_level INT           NOT NULL DEFAULT 10,
    CONSTRAINT uq_product_code   UNIQUE (product_code),
    CONSTRAINT ck_product_price  CHECK (price >= 0),
    CONSTRAINT ck_product_stock  CHECK (stock_quantity >= 0),
    CONSTRAINT ck_product_min    CHECK (min_stock_level >= 0),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id)
        REFERENCES categories (category_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers (supplier_id)  ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. customers   (Customer extends Person in the Java code)
-- ------------------------------------------------------------
CREATE TABLE customers (
    customer_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(30)  NULL,
    email           VARCHAR(100) NULL,
    address         VARCHAR(200) NULL,
    registered_date DATE         NOT NULL DEFAULT (CURRENT_DATE)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 5. sales   (customer_id is NULL for walk-in customers)
--    The total is NOT stored: it is calculated from sale_details
--    (keeps the design in Third Normal Form).
-- ------------------------------------------------------------
CREATE TABLE sales (
    sale_id     INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT      NULL,
    sale_date   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id)
        REFERENCES customers (customer_id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 6. sale_details   (unit_price = selling price at time of sale)
-- ------------------------------------------------------------
CREATE TABLE sale_details (
    sale_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id        INT           NOT NULL,
    product_id     INT           NOT NULL,
    quantity       INT           NOT NULL,
    unit_price     DECIMAL(10,2) NOT NULL,
    CONSTRAINT uq_sale_product   UNIQUE (sale_id, product_id),
    CONSTRAINT ck_detail_qty     CHECK (quantity > 0),
    CONSTRAINT ck_detail_price   CHECK (unit_price >= 0),
    CONSTRAINT fk_detail_sale    FOREIGN KEY (sale_id)
        REFERENCES sales (sale_id)       ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detail_product FOREIGN KEY (product_id)
        REFERENCES products (product_id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Helpful view: one row per sale with its calculated total
CREATE VIEW v_sale_totals AS
SELECT s.sale_id,
       s.sale_date,
       s.customer_id,
       COALESCE(SUM(d.quantity * d.unit_price), 0) AS total_amount,
       COALESCE(SUM(d.quantity), 0)                AS total_items
FROM sales s
LEFT JOIN sale_details d ON d.sale_id = s.sale_id
GROUP BY s.sale_id, s.sale_date, s.customer_id;

-- ============================================================
--  SAMPLE DATA
-- ============================================================
INSERT INTO categories (category_name, description) VALUES
 ('Beverages',        'Soft drinks, juices, water, coffee'),
 ('Snacks',           'Chips, biscuits, candies'),
 ('Canned Goods',     'Canned meat, fish, vegetables'),
 ('Personal Care',    'Soap, shampoo, toothpaste'),
 ('Household',        'Cleaning supplies and home essentials');

INSERT INTO suppliers (company_name, name, phone, email, address) VALUES
 ('Metro Wholesale Trading', 'Maria Santos',  '0917-100-2001', 'maria@metrowholesale.example', 'Quezon City'),
 ('Sunrise Distributors',    'John Reyes',    '0917-100-2002', 'john@sunrise.example',         'Manila'),
 ('FreshCo Supplies',        'Ana Dela Cruz', '0917-100-2003', 'ana@freshco.example',          'Cebu City');

INSERT INTO products (product_code, product_name, category_id, supplier_id, price, stock_quantity, min_stock_level) VALUES
 ('BEV-001', 'Mineral Water 500ml',     1, 1,  15.00, 120, 30),
 ('BEV-002', 'Cola 1.5L',               1, 1,  75.00,  48, 20),
 ('BEV-003', 'Instant Coffee 3-in-1',   1, 2,   8.00, 200, 50),
 ('SNK-001', 'Potato Chips 60g',        2, 2,  32.00,  60, 20),
 ('SNK-002', 'Chocolate Biscuits',      2, 2,  25.00,   9, 15),
 ('CAN-001', 'Sardines in Tomato 155g', 3, 3,  24.50,  75, 25),
 ('CAN-002', 'Corned Beef 150g',        3, 3,  48.00,  40, 15),
 ('PRC-001', 'Bath Soap 90g',           4, 1,  28.00,  55, 20),
 ('PRC-002', 'Shampoo Sachet',          4, 1,   7.50, 150, 40),
 ('HSE-001', 'Dishwashing Liquid 250ml',5, 3,  38.00,   6, 10);

INSERT INTO customers (name, phone, email, address) VALUES
 ('Liza Mendoza',  '0918-200-3001', 'liza@example.com',  'Pasig City'),
 ('Carlo Ramos',   '0918-200-3002', 'carlo@example.com', 'Makati City'),
 ('Grace Tan',     '0918-200-3003', NULL,                'Mandaluyong City');

-- A few past sales (one is a walk-in: customer_id NULL).
INSERT INTO sales (sale_id, customer_id, sale_date) VALUES
 (1, 1,    NOW() - INTERVAL 2 DAY),
 (2, NULL, NOW() - INTERVAL 1 DAY),
 (3, 2,    NOW());

INSERT INTO sale_details (sale_id, product_id, quantity, unit_price) VALUES
 (1, 1, 6, 15.00), (1, 4, 2, 32.00),
 (2, 3, 10, 8.00), (2, 6, 4, 24.50),
 (3, 2, 2, 75.00), (3, 8, 3, 28.00);

-- Keep stock consistent with the sample sales above
UPDATE products SET stock_quantity = stock_quantity - 6  WHERE product_id = 1;
UPDATE products SET stock_quantity = stock_quantity - 2  WHERE product_id = 4;
UPDATE products SET stock_quantity = stock_quantity - 10 WHERE product_id = 3;
UPDATE products SET stock_quantity = stock_quantity - 4  WHERE product_id = 6;
UPDATE products SET stock_quantity = stock_quantity - 2  WHERE product_id = 2;
UPDATE products SET stock_quantity = stock_quantity - 3  WHERE product_id = 8;
