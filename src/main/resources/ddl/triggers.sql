-- 关键修正：在FOR EACH ROW后添加BEGIN，即使单条语句
-- borrowers表插入触发器
CREATE TRIGGER IF NOT EXISTS trigger_borrowers_insert_time
AFTER INSERT ON borrowers
FOR EACH ROW
BEGIN
    UPDATE borrowers
    SET created_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime'),
        updated_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime')
    WHERE id = NEW.id;
END;;

-- borrowers表更新触发器
CREATE TRIGGER IF NOT EXISTS trigger_borrowers_update_time
AFTER UPDATE ON borrowers
FOR EACH ROW
BEGIN
    UPDATE borrowers
    SET updated_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime')
    WHERE id = NEW.id;
END;;

-- borrower_details表插入触发器
CREATE TRIGGER IF NOT EXISTS trigger_borrower_details_insert_time
AFTER INSERT ON borrower_details
FOR EACH ROW
BEGIN
    UPDATE borrower_details
    SET created_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime'),
        updated_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime')
    WHERE id = NEW.id;
END;;

-- borrower_details表更新触发器
CREATE TRIGGER IF NOT EXISTS trigger_borrower_details_update_time
AFTER UPDATE ON borrower_details
FOR EACH ROW
BEGIN
    UPDATE borrower_details
    SET updated_time = STRFTIME('%Y-%m-%d %H:%M:%f', 'now', 'localtime')
    WHERE id = NEW.id;
END;;
