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
    PRIMARY KEY cust_id,
    FOREIGN KEY agent_id REFERENCES agent,
)

CREATE TABLE phone_num (
    numb NUMBER(10),
    kind VARCHAR(10),
    PRIMARY KEY phone_num,
    FOREIGN KEY cust_id REFERENCES customer,
)

CREATE TABLE address (
    street VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(2),
    zipcode NUMBER(5),
    PRIMARY KEY (street, city),
    FOREIGN KEY cust_id REFERENCES customer,
)

CREATE TABLE contractor (
    firm_id NUMBER(6),
    industry VARCHAR(20),
    name VARCHAR(50),
    phone VARCHAR(10),
    PRIMARY KEY firm_id,
)

CREATE TABLE services (
    service_date DATE,
    FOREIGN KEY claim_id REFERENCES claim,
    FOREIGN KEY firm_id REFERENCES contractor,
    PRIMARY KEY (claim_id, firm_id),
)

CREATE TABLE claim (
    claim_id NUMBER(6),
    claim_title VARCHAR(50),
    location VARCHAR(100),
    description VARCHAR(280),
    submitted_date DATE,
    occurred_date DATE,
    FOREIGN KEY policy_id REFERENCES policy,
    FOREIGN KEY cust_id REFERENCES customer,
    FOREIGN KEY adj_id REFERENCES adjuster,
)

CREATE TABLE agent (
    agent_id NUMBER(6),
    name VARCHAR(100),
    PRIMARY KEY agent_id,
)

CREATE TABLE adjuster (
    adj_id NUMBER(6),
    name VARCHAR(100),
    specialty VARCHAR(20),
    PRIMARY KEY adj_id,
)