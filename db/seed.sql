-- Seed data for Libra BMS
USE `libra_bms`;

-- Clean existing seed rows (safe to re-run)
DELETE FROM lib_reservation WHERE id IN (5001, 5002);
DELETE FROM lib_borrow_record WHERE id IN (4001, 4002, 4003);
DELETE FROM lib_inventory WHERE id IN (3001,3002,3003,3004,3005,3006,3007,3008,3009,3010,3011);
DELETE FROM lib_book WHERE id IN (2001,2002,2003,2004);
DELETE FROM sys_user WHERE id IN (1001,1002);

-- Tenant
INSERT INTO sys_tenant (id, tenant_name, status, create_time, update_time, is_deleted)
VALUES (1, '默认租户', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE tenant_name=VALUES(tenant_name), status=1, is_deleted=0;

-- Users (password: Password123)
INSERT INTO sys_user (id, tenant_id, username, password, nickname, email, phone, status, create_time, update_time, is_deleted)
VALUES
  (1001, 1, '20250001', '$2b$10$04Lx3lpMTYhRNvdKEqre6uriP7MYE/uiA0L0MXx0ampRcxizx4nbm', '张同学', 'student@example.com', '13800000001', 1, NOW(), NOW(), 0),
  (1002, 1, 'admin01',  '$2b$10$04Lx3lpMTYhRNvdKEqre6uriP7MYE/uiA0L0MXx0ampRcxizx4nbm', '系统管理员', 'admin@example.com',  '13800000002', 1, NOW(), NOW(), 0);

-- Books (使用中图法分类：18=工业技术，13=数理科学和化学)
INSERT INTO lib_book (id, tenant_id, isbn, title, author, publisher, pub_date, price, category_id, cover_url, summary, total_count, stock_count, status, create_time, update_time, is_deleted)
VALUES
  (2001, 1, '9787300000011', '计算机网络', '谢希仁', '电子工业出版社', '2020-01-01', 39.80, 18, 'https://img2.doubanio.com/view/subject/l/public/s33952222.jpg', '经典教材，覆盖网络基础与协议。', 3, 2, 1, NOW(), NOW(), 0),
  (2002, 1, '9787300000028', '数据结构', '严蔚敏', '清华大学出版社', '2019-06-01', 49.80, 13, 'https://img2.doubanio.com/view/subject/l/public/s2366821.jpg', '常用数据结构与算法入门。', 2, 2, 1, NOW(), NOW(), 0),
  (2003, 1, '9787300000035', '操作系统', '汤小丹', '西安电子科技大学出版社', '2021-03-01', 59.00, 18, 'https://img9.doubanio.com/view/subject/l/public/s27242944.jpg', '操作系统原理与实践。', 4, 4, 1, NOW(), NOW(), 0),
  (2004, 1, '9787300000042', 'Java 编程思想', 'Bruce Eckel', '机械工业出版社', '2018-08-01', 89.00, 18, 'https://img2.doubanio.com/view/subject/l/public/s27243452.jpg', 'Java 经典权威著作。', 2, 1, 1, NOW(), NOW(), 0);

-- Inventory (status: 1=在馆, 2=借出)
INSERT INTO lib_inventory (id, tenant_id, book_id, location, status, create_time, update_time, is_deleted)
VALUES
  (3001, 1, 2001, 'A-1-01', 2, NOW(), NOW(), 0),
  (3002, 1, 2001, 'A-1-02', 1, NOW(), NOW(), 0),
  (3003, 1, 2001, 'A-1-03', 1, NOW(), NOW(), 0),
  (3004, 1, 2002, 'B-2-01', 1, NOW(), NOW(), 0),
  (3005, 1, 2002, 'B-2-02', 1, NOW(), NOW(), 0),
  (3006, 1, 2003, 'C-3-01', 1, NOW(), NOW(), 0),
  (3007, 1, 2003, 'C-3-02', 1, NOW(), NOW(), 0),
  (3008, 1, 2003, 'C-3-03', 1, NOW(), NOW(), 0),
  (3009, 1, 2003, 'C-3-04', 1, NOW(), NOW(), 0),
  (3010, 1, 2004, 'B-2-10', 2, NOW(), NOW(), 0),
  (3011, 1, 2004, 'B-2-11', 1, NOW(), NOW(), 0);

-- Borrow records
INSERT INTO lib_borrow_record (id, tenant_id, inventory_id, book_id, user_id, borrow_time, due_time, return_time, status, fine_amount, remark, create_time, update_time)
VALUES
  (4001, 1, 3001, 2001, 1001, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NULL, 1, 0.00, '', NOW(), NOW()),
  (4002, 1, 3010, 2004, 1001, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, 3, 0.00, '已逾期', NOW(), NOW()),
  (4003, 1, 3004, 2002, 1001, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 2, 0.00, '已归还', NOW(), NOW());

-- Reservations
INSERT INTO lib_reservation (id, tenant_id, user_id, book_id, status, queue_no, expected_date, pickup_deadline, notified, create_time, update_time, is_deleted)
VALUES
  (5001, 1, 1001, 2003, 0, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1, NOW(), NOW(), 0),
  (5002, 1, 1001, 2002, 1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, NOW(), NOW(), 0);
