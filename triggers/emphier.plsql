CREATE OR REPLACE TRIGGER emphier_agent
    BEFORE INSERT OR UPDATE ON agent
    FOR EACH ROW
DECLARE already_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO already_exists FROM adjuster WHERE adjuster.emp_id = :NEW.emp_id;
    IF (already_exists > 0) THEN
        DBMS_OUTPUT.PUT_LINE('This employee cannot be added as an agent; it already exists as an adjuster.');
    END IF;
END;

CREATE OR REPLACE TRIGGER emphier_adjuster
    BEFORE INSERT OR UPDATE ON adjuster
    FOR EACH ROW
DECLARE already_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO already_exists FROM adjuster WHERE adjuster.emp_id = :NEW.emp_id;
    IF (already_exists > 0) THEN
        DBMS_OUTPUT.PUT_LINE('This employee cannot be added as an adjuster; it already exists as an agent.');
    END IF;
END;