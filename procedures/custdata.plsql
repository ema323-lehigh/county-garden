CREATE OR REPLACE PROCEDURE custdata AS
    agent_id agent.emp_id%type;
    agent_name agent.ename%type;
    cust_pols NUMBER(2);
    cust_deps NUMBER(2);
    CURSOR c IS SELECT * FROM customer;
BEGIN
    FOR cust IN c LOOP
        EXIT WHEN c%notfound;
        SELECT emp_id, ename INTO agent_id, agent_name FROM agent WHERE agent.emp_id = cust.agent_id;
        SELECT COUNT(*) INTO cust_pols FROM polisy WHERE polisy.cust_id = cust.cust_id;
        SELECT COUNT(*) INTO cust_deps FROM dependentt WHERE dependentt.cust_id = cust.cust_id;
        DBMS_OUTPUT.PUT_LINE(cust.cust_id || ' ' || cust.fname || ' ' || cust.minitial || ' ' || cust.lname ||
            ' | ' || cust_pols || ' policies, ' || cust_deps || ' dependents | ' ||
            'Agent: ' || agent_name || ' (' || agent_id || ')');
    END LOOP;
END;
