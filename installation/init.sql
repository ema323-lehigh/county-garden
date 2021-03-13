DROP TABLE policy;
CREATE TABLE policy (
    policy_id NUMBER(6),
    policy_type VARCHAR(20),
    quoted_price NUMBER(6,2),
    cancelled BOOLEAN,
    FOREIGN KEY cust_id REFERENCES customer,
    PRIMARY KEY policy_id,
)

DROP TABLE item;
CREATE TABLE item (
    item_id NUMBER(6),
    category VARCHAR(20),
    approx_value NUMBER(6,2),
    FOREIGN KEY policy_id REFERENCES policy,
    PRIMARY KEY item_id,
)

DROP TABLE customer;
CREATE TABLE customer (
    cust_id NUMBER(6),
    firstname VARCHAR(50),
    middlename VARCHAR(50),
    lastname VARCHAR(50),
    suffix VARCHAR(10),
    birth_date DATE,
    FOREIGN KEY agent_id REFERENCES agent,
    PRIMARY KEY cust_id,
)

DROP TABLE phone_num;
CREATE TABLE phone_num (
    numb NUMBER(10),
    kind VARCHAR(10),
    FOREIGN KEY cust_id REFERENCES customer,
    PRIMARY KEY numb,
)

DROP TABLE address;
CREATE TABLE address (
    street VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(2),
    zipcode NUMBER(5),
    FOREIGN KEY cust_id REFERENCES customer,
    PRIMARY KEY (street, city),
)

DROP TABLE contractor;
CREATE TABLE contractor (
    firm_id NUMBER(6),
    industry VARCHAR(20),
    name VARCHAR(50),
    phone VARCHAR(10),
    PRIMARY KEY firm_id,
)

DROP TABLE services;
CREATE TABLE services (
    service_date DATE,
    FOREIGN KEY claim_id REFERENCES claim,
    FOREIGN KEY firm_id REFERENCES contractor,
    PRIMARY KEY (claim_id, firm_id),
)

DROP TABLE claim;
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
    PRIMARY KEY claim_id,
)

DROP TABLE agent;
CREATE TABLE agent (
    agent_id NUMBER(6),
    name VARCHAR(100),
    PRIMARY KEY agent_id,
)

DROP TABLE adjuster;
CREATE TABLE adjuster (
    adj_id NUMBER(6),
    name VARCHAR(100),
    specialty VARCHAR(20),
    PRIMARY KEY adj_id,
)

DROP TABLE dependent;
CREATE TABLE dependent (
    name VARCHAR (100),
    relationship VARCHAR(20),
    birth_date DATE,
    FOREIGN KEY cust_id REFERENCES customer,
    PRIMARY KEY (name, cust_id),
)