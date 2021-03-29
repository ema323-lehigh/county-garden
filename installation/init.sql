DROP TABLE employee CASCADE CONSTRAINTS;
CREATE TABLE employee (
    emp_id NUMBER(6) NOT NULL,
    ename VARCHAR(100) NOT NULL,
    PRIMARY KEY (emp_id, ename)
)

DROP TABLE agent CASCADE CONSTRAINTS;
CREATE TABLE agent (
    years_experience NUMBER(2),
    emp_id NUMBER(6) NOT NULL,
    ename VARCHAR(100) NOT NULL,
    FOREIGN KEY (emp_id, ename) REFERENCES employee(emp_id, ename)
        ON DELETE CASCADE,
    PRIMARY KEY (emp_id)
)

DROP TABLE adjuster CASCADE CONSTRAINTS;
CREATE TABLE adjuster (
    specialty VARCHAR(20),
    emp_id NUMBER(6) NOT NULL,
    ename VARCHAR(100) NOT NULL,
    FOREIGN KEY (emp_id, ename) REFERENCES employee(emp_id, ename)
        ON DELETE CASCADE,
    PRIMARY KEY (emp_id)
)

DROP TABLE customer CASCADE CONSTRAINTS;
CREATE TABLE customer (
    cust_id NUMBER(6) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    lastname VARCHAR(50) NOT NULL,
    suffix VARCHAR(10),
    birth_date DATE NOT NULL,
    agent_id NUMBER(6) NOT NULL,
    FOREIGN KEY (agent_id) REFERENCES agent(emp_id)
        ON DELETE CASCADE,
    PRIMARY KEY (cust_id)
)

DROP TABLE cust_add CASCADE CONSTRAINTS;
CREATE TABLE cust_add (
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    astate VARCHAR(2) NOT NULL,
    zipcode NUMBER(5) NOT NULL,
    cust_id NUMBER(6) NOT NULL,
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
        ON DELETE CASCADE,
    PRIMARY KEY (street, city)
)

DROP TABLE phone_num CASCADE CONSTRAINTS;
CREATE TABLE phone_num (
    numb NUMBER(10) NOT NULL,
    kind VARCHAR(10) NOT NULL,
    cust_id NUMBER(6) NOT NULL,
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
        ON DELETE CASCADE,
    PRIMARY KEY (numb)
)

DROP TABLE polisy CASCADE CONSTRAINTS;
CREATE TABLE polisy (
    policy_id NUMBER(6) NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    quoted_price NUMBER(6,2),
    cancelled NUMBER(1) NOT NULL CHECK(cancelled = 0 OR cancelled = 1),
    cust_id NUMBER(6) NOT NULL,
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
        ON DELETE CASCADE,
    PRIMARY KEY (policy_id)
)

DROP TABLE item CASCADE CONSTRAINTS;
CREATE TABLE item (
    item_id NUMBER(6) NOT NULL,
    category VARCHAR(20) NOT NULL,
    approx_value NUMBER(6,2) NOT NULL,
    policy_id NUMBER(6) NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES polisy(policy_id)
        ON DELETE CASCADE,
    PRIMARY KEY (item_id)
)

DROP TABLE claim CASCADE CONSTRAINTS;
CREATE TABLE claim (
    claim_id NUMBER(6) NOT NULL,
    claim_title VARCHAR(50) NOT NULL,
    event_loc VARCHAR(100) NOT NULL,
    event_desc VARCHAR(280) NOT NULL,
    occurred_date DATE NOT NULL,
    submitted_date DATE NOT NULL,
    policy_id NUMBER(6) NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES polisy(policy_id)
        ON DELETE CASCADE,
    cust_id NUMBER(6) NOT NULL,
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
        ON DELETE CASCADE,
    PRIMARY KEY (claim_id)
)

DROP TABLE contractor CASCADE CONSTRAINTS;
CREATE TABLE contractor (
    firm_id NUMBER(6) NOT NULL,
    industry VARCHAR(20) NOT NULL,
    cname VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    PRIMARY KEY (firm_id)
)

DROP TABLE firm_add CASCADE CONSTRAINTS;
CREATE TABLE firm_add (
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    fstate VARCHAR(2) NOT NULL,
    zipcode NUMBER(5) NOT NULL,
    firm_id NUMBER(6) NOT NULL,
    FOREIGN KEY (firm_id) REFERENCES contractor(firm_id)
        ON DELETE CASCADE,
    PRIMARY KEY (street, city)
)

DROP TABLE services CASCADE CONSTRAINTS;
CREATE TABLE services (
    service_date DATE NOT NULL,
    firm_id NUMBER(6) NOT NULL,
    FOREIGN KEY (firm_id) REFERENCES contractor(firm_id)
        ON DELETE CASCADE,
    claim_id NUMBER(6) NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id)
        ON DELETE CASCADE,
    PRIMARY KEY (firm_id, claim_id, service_date)
)

DROP TABLE manages CASCADE CONSTRAINTS;
CREATE TABLE manages (
    claim_id NUMBER(6) NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id)
        ON DELETE CASCADE,
    adj_id NUMBER(6) NOT NULL,
    FOREIGN KEY (adj_id) REFERENCES adjuster(emp_id)
        ON DELETE CASCADE,
    PRIMARY KEY (claim_id, adj_id)
)

DROP TABLE dependentt CASCADE CONSTRAINTS;
CREATE TABLE dependentt (
    dname VARCHAR (100) NOT NULL,
    relationship VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    cust_id NUMBER(6) NOT NULL,
    FOREIGN KEY (cust_id) REFERENCES customer(cust_id)
        ON DELETE CASCADE,
    PRIMARY KEY (dname, relationship, cust_id)
)

DROP TABLE invoice CASCADE CONSTRAINTS;
CREATE TABLE invoice (
    trans_id NUMBER(6) NOT NULL,
    due_date DATE NOT NULL,
    payment_type VARCHAR(10),
    policy_id NUMBER(6) NOT NULL,
    FOREIGN KEY (policy_id) REFERENCES polisy(policy_id)
        ON DELETE CASCADE,
    PRIMARY KEY (trans_id)
)

DROP TABLE payment CASCADE CONSTRAINTS;
CREATE TABLE payment (
    payment_id NUMBER(6) NOT NULL,
    amount NUMBER(6,2) NOT NULL,
    paid_date DATE NOT NULL,
    claim_id NUMBER(6) NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claim(claim_id)
        ON DELETE CASCADE,
    PRIMARY KEY (payment_id)
)

