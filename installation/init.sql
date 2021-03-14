DROP TABLE policy;
CREATE TABLE policy (
    policy_id NUMBER(6) NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    quoted_price NUMBER(6,2),
    cancelled BOOLEAN NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
    PRIMARY KEY policy_id,
)

DROP TABLE item;
CREATE TABLE item (
    item_id NUMBER(6) NOT NULL,
    category VARCHAR(20) NOT NULL,
    approx_value NUMBER(6,2) NOT NULL,
    FOREIGN KEY policy_id REFERENCES policy NOT NULL,
    PRIMARY KEY item_id,
)

DROP TABLE customer;
CREATE TABLE customer (
    cust_id NUMBER(6) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    lastname VARCHAR(50) NOT NULL,
    suffix VARCHAR(10),
    birth_date DATE NOT NULL,
    FOREIGN KEY agent_id REFERENCES agent NOT NULL,
    PRIMARY KEY cust_id,
)

DROP TABLE phone_num;
CREATE TABLE phone_num (
    numb NUMBER(10) NOT NULL,
    kind VARCHAR(10) NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
    PRIMARY KEY numb,
)

DROP TABLE address;
CREATE TABLE address (
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zipcode NUMBER(5) NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
    PRIMARY KEY (street, city),
)

DROP TABLE contractor;
CREATE TABLE contractor (
    firm_id NUMBER(6) NOT NULL,
    industry VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    PRIMARY KEY firm_id,
)

DROP TABLE services;
CREATE TABLE services (
    service_date DATE NOT NULL,
    FOREIGN KEY firm_id REFERENCES contractor NOT NULL,
    FOREIGN KEY claim_id REFERENCES claim NOT NULL,
    PRIMARY KEY (firm_id, claim_id, service_date),
)

DROP TABLE claim;
CREATE TABLE claim (
    claim_id NUMBER(6) NOT NULL,
    claim_title VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description VARCHAR(280) NOT NULL,
    occurred_date DATE NOT NULL,
    submitted_date DATE NOT NULL,
    FOREIGN KEY policy_id REFERENCES policy NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
    PRIMARY KEY claim_id,
)

DROP TABLE manages;
CREATE TABLE manages (
    FOREIGN KEY claim_id REFERENCES claim NOT NULL,
    FOREIGN KEY adj_id REFERENCES adjuster NOT NULL,
    PRIMARY KEY (claim_id, adj_id),
)

DROP TABLE agent;
CREATE TABLE agent (
    agent_id NUMBER(6) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY agent_id,
)

DROP TABLE adjuster;
CREATE TABLE adjuster (
    adj_id NUMBER(6) NOT NULL,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(20),
    PRIMARY KEY adj_id,
)

DROP TABLE dependent;
CREATE TABLE dependent (
    name VARCHAR (100) NOT NULL,
    relationship VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
    PRIMARY KEY (name, cust_id),
)

DROP TABLE invoice;
CREATE TABLE invoice (
    trans_id NUMBER(6) NOT NULL,
    due_date DATE NOT NULL,
    payment_type VARCHAR(10),
    FOREIGN KEY policy_id REFERENCES policy NOT NULL,
    PRIMARY KEY trans_id,
)

DROP TABLE payment;
CREATE TABLE payment (
    payment_id NUMBER(6) NOT NULL,
    amount NUMBER(6,2) NOT NULL,
    paid_date DATE NOT NULL,
    FOREIGN KEY claim_id REFERENCES claim NOT NULL,
    PRIMARY KEY payment_id,
)