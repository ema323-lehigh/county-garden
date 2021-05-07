CREATE OR REPLACE PROCEDURE phonenums AS
    CURSOR c IS SELECT cust_id FROM customer;
    endigits NUMBER(4) := 0;
BEGIN
    DELETE phone_num;
    FOR cust IN c LOOP
        EXIT WHEN c%notfound;
        INSERT INTO phone_num VALUES (5555550000 + TO_CHAR(endigits, '0009'), 'home', cust.cust_id);
        endigits := endigits + (ROUND((DBMS_RANDOM.VALUE * 3), 0) + 1);
        IF (MOD(endigits, 2) = 0) THEN
            INSERT INTO phone_num VALUES (5555550000 + TO_CHAR(endigits, '0009'), 'cell', cust.cust_id);
            endigits := endigits + (ROUND((DBMS_RANDOM.VALUE * 3), 0) + 1);
        END IF;
    END LOOP;
END;
