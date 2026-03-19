-- 清理旧数据
DELETE FROM lib_reservation;
DELETE FROM lib_borrow_record;
DELETE FROM lib_inventory;
DELETE FROM lib_book;
DELETE FROM sys_user;
DELETE FROM sys_tenant;

-- 租户
INSERT INTO sys_tenant (id, tenant_name, status, create_time, update_time, is_deleted)
VALUES (1, '默认租户', 1, NOW(), NOW(), 0);

-- 用户
INSERT INTO sys_user (id, tenant_id, username, password, nickname, email, phone, status, create_time, update_time, is_deleted)
VALUES (1001, 1, '20250001', '$2a$10$0uL4qKjLPZxpJ0KQCRmLYeJGG0qX5xY5KjL5s5s5s5s5s5s5s5s5', '张同学', 'student@example.com', '13800000001', 1, NOW(), NOW(), 0);
INSERT INTO sys_user (id, tenant_id, username, password, nickname, email, phone, status, create_time, update_time, is_deleted)
VALUES (1002, 1, 'admin01', '$2a$10$0uL4qKjLPZxpJ0KQCRmLYeJGG0qX5xY5KjL5s5s5s5s5s5s5s5s5', '系统管理员', 'admin@example.com', '13800000002', 1, NOW(), NOW(), 0);

-- 图书
INSERT INTO lib_book (id, tenant_id, isbn, title, author, publisher, pub_date, price, category_id, cover_url, summary, total_count, stock_count, status, create_time, update_time, is_deleted)
VALUES (2001, 1, '9787300000011', '计算机网络', '谢希仁', '电子工业出版社', '2020-01-01', 39.80, 1, 'https://img2.doubanio.com/view/subject/l/public/s33952222.jpg', '经典教材，覆盖网络基础与协议。', 3, 2, 1, NOW(), NOW(), 0);
INSERT INTO lib_book (id, tenant_id, isbn, title, author, publisher, pub_date, price, category_id, cover_url, summary, total_count, stock_count, status, create_time, update_time, is_deleted)
VALUES (2002, 1, '9787300000028', '算法导论', 'Thomas H. Cormen', 'MIT Press', '2009-07-01', 128.00, 1, 'https://img1.doubanio.com/view/subject/l/public/s3388518.jpg', '算法领域的经典教材。', 5, 4, 1, NOW(), NOW(), 0);
INSERT INTO lib_book (id, tenant_id, isbn, title, author, publisher, pub_date, price, category_id, cover_url, summary, total_count, stock_count, status, create_time, update_time, is_deleted)
VALUES (2003, 1, '9787111000002', '百年孤独', '加西亚·马尔克斯', '南海出版公司', '2011-06-01', 39.50, 2, 'https://img2.doubanio.com/view/subject/l/public/s10079019.jpg', '拉丁美洲魔幻现实主义文学代表作。', 2, 1, 1, NOW(), NOW(), 0);
INSERT INTO lib_book (id, tenant_id, isbn, title, author, publisher, pub_date, price, category_id, cover_url, summary, total_count, stock_count, status, create_time, update_time, is_deleted)
VALUES (2004, 1, '9787020000017', '活着', '余华', '作家出版社', '2012-08-01', 28.00, 2, 'https://img1.doubanio.com/view/subject/l/public/s27243581.jpg', '讲述农民福贵的人生悲欢。', 4, 3, 1, NOW(), NOW(), 0);

-- 库存
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3001, 1, 2001, 'A区-01-01', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3002, 1, 2001, 'A区-01-02', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3003, 1, 2001, 'A区-01-03', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3004, 1, 2002, 'A区-02-01', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3005, 1, 2002, 'A区-02-02', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3006, 1, 2002, 'A区-02-03', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3007, 1, 2002, 'A区-02-04', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3008, 1, 2002, 'A区-02-05', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3009, 1, 2003, 'B区-01-01', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3010, 1, 2003, 'B区-01-02', 1, NOW(), NOW(), 0);
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES (3011, 1, 2004, 'B区-02-01', 1, NOW(), NOW(), 0);

-- 借阅记录（注意：lib_borrow_record 表没有 is_deleted 字段）
INSERT INTO lib_borrow_record (id, tenant_id, user_id, inventory_id, book_id, borrow_time, due_time, return_time, status, create_time, update_time)
VALUES (4001, 1, 1001, 3001, 2001, '2025-12-01', '2025-12-15', NULL, 1, NOW(), NOW());
INSERT INTO lib_borrow_record (id, tenant_id, user_id, inventory_id, book_id, borrow_time, due_time, return_time, status, create_time, update_time)
VALUES (4002, 1, 1001, 3004, 2002, '2025-12-10', '2025-12-24', NULL, 1, NOW(), NOW());
INSERT INTO lib_borrow_record (id, tenant_id, user_id, inventory_id, book_id, borrow_time, due_time, return_time, status, create_time, update_time)
VALUES (4003, 1, 1002, 3009, 2003, '2025-12-05', '2025-12-19', '2025-12-18', 2, NOW(), NOW());

-- 预约记录（修正字段名）
INSERT INTO lib_reservation (id, tenant_id, user_id, book_id, status, create_time, update_time, is_deleted)
VALUES (5001, 1, 1001, 2003, 1, NOW(), NOW(), 0);
INSERT INTO lib_reservation (id, tenant_id, user_id, book_id, status, create_time, update_time, is_deleted)
VALUES (5002, 1, 1002, 2004, 2, NOW(), NOW(), 0);
