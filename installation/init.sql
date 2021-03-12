CREATE TABLE policy (
    policy_id NUMBER(6),
    policy_type VARCHAR(20),
    quoted_price NUMBER(6,2),
    cancelled BOOLEAN,
    PRIMARY KEY policy_id,
    FOREIGN KEY cust_id REFERENCES customer,
)

CREATE TABLE item (
    item_id NUMBER(6),
    category VARCHAR(20),
    approx_value NUMBER(6,2),
    PRIMARY KEY item_id,
    FOREIGN KEY policy_id REFERENCES policy,
)

CREATE TABLE customer (
    cust_id NUMBER(6),
    firstname VARCHAR(50),
    middlename VARCHAR(50),
    lastname VARCHAR(50),
    suffix VARCHAR(10),
    birth_date DATE,
    street VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(2),
    zipcode NUMBER(5),
    PRIMARY KEY cust_id,
    FOREIGN KEY agent_id REFERENCES agent,
)

CREATE TABLE phone_num (
    numb NUMBER(10),
    kind VARCHAR(10),
    FOREIGN KEY cust_id REFERENCES customer,
)