CREATE OR REPLACE PROCEDURE custdata AS
    agent_id agent.agent_id%type;
    agent_name agent.aname%type;
    cust_name VARCHAR(50);
    cust_pols NUMBER(2);
    cust_deps NUMBER(2);
    CURSOR c IS SELECT * FROM customer ORDER BY cust_id;
BEGIN
    FOR cust IN c LOOP
        EXIT WHEN c%notfound;
        SELECT agent_id, aname INTO agent_id, agent_name FROM agent WHERE agent.agent_id = cust.agent_id;
        SELECT COUNT(*) INTO cust_pols FROM polisy WHERE polisy.cust_id = cust.cust_id;
        SELECT COUNT(*) INTO cust_deps FROM dependentt WHERE dependentt.cust_id = cust.cust_id;
        cust_name := cust.fname || ' ' || cust.minitial || ' ' || cust.lname || ' ' || cust.suffix;
        DBMS_OUTPUT.PUT_LINE(TO_CHAR(cust.cust_id, '000009') || ' ' || RPAD(cust_name, 50) ||
            ' | ' || cust_pols || ' policies, ' || cust_deps || ' dependents | ' ||
            'Agent: ' || agent_name || ' (' || agent_id || ')');
    END LOOP;
END;
