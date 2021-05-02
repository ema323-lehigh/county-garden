CREATE OR REPLACE TRIGGER dependage
    BEFORE INSERT OR UPDATE ON dependentt
    REFERENCING NEW AS newdep
    FOR EACH ROW
DECLARE cust_dob DATE;
BEGIN
    SELECT birth_date INTO cust_dob FROM customer WHERE cust_id = :newdep.cust_id;
    IF ((:newdep.relationship = 'child' AND :newdep.birth_date > cust_dob - 10) OR
        (:newdep.relationship = 'parent' AND :newdep.birth_date < cust_dob + 10)) THEN
        RAISE_APPLICATION_ERROR(-20998, 'Invalid dependent age based on their relationship to the customer.');
    END IF;
END;
