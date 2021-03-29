CREATE OR REPLACE TRIGGER emphier_agent
    BEFORE INSERT OR UPDATE ON agent
BEGIN
    IF EXISTS (SELECT * FROM adjuster WHERE adjuster.emp_id = :NEW.emp_id) THEN
        DBMS_OUTPUT.PUT_LINE('This employee cannot be added as an agent; it already exists as an adjuster.');
    END IF
END;

CREATE OR REPLACE TRIGGER emphier_adjuster
    BEFORE INSERT OR UPDATE ON adjuster
BEGIN
    IF EXISTS (SELECT * FROM agent WHERE agent.emp_id = :NEW.emp_id) THEN
        DBMS_OUTPUT.PUT_LINE('This employee cannot be added as an adjuster; it already exists as an agent.');
    END IF
END;