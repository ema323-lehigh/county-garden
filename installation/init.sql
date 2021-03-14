--DROP TABLE policy IF EXISTS;
CREATE TABLE policy (
    policy_id NUMBER(6) NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    quoted_price NUMBER(6,2),
    cancelled BOOLEAN NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY policy_id,
)

--DROP TABLE item IF EXISTS;
CREATE TABLE item (
    item_id NUMBER(6) NOT NULL,
    category VARCHAR(20) NOT NULL,
    approx_value NUMBER(6,2) NOT NULL,
    FOREIGN KEY policy_id REFERENCES policy NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY item_id,
)

--DROP TABLE customer IF EXISTS;
CREATE TABLE customer (
    cust_id NUMBER(6) NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    middlename VARCHAR(50),
    lastname VARCHAR(50) NOT NULL,
    suffix VARCHAR(10),
    birth_date DATE NOT NULL,
    FOREIGN KEY agent_id REFERENCES agent NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY cust_id,
)

--DROP TABLE phone_num IF EXISTS;
CREATE TABLE phone_num (
    numb NUMBER(10) NOT NULL,
    kind VARCHAR(10) NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY numb,
)

--DROP TABLE address IF EXISTS;
CREATE TABLE address (
    street VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zipcode NUMBER(5) NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY (street, city),
)

--DROP TABLE contractor IF EXISTS;
CREATE TABLE contractor (
    firm_id NUMBER(6) NOT NULL,
    industry VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    PRIMARY KEY firm_id,
)

--DROP TABLE services IF EXISTS;
CREATE TABLE services (
    service_date DATE NOT NULL,
    FOREIGN KEY firm_id REFERENCES contractor NOT NULL,
        ON DELETE CASCADE,
    FOREIGN KEY claim_id REFERENCES claim NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY (firm_id, claim_id, service_date),
)

--DROP TABLE claim IF EXISTS;
CREATE TABLE claim (
    claim_id NUMBER(6) NOT NULL,
    claim_title VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description VARCHAR(280) NOT NULL,
    occurred_date DATE NOT NULL,
    submitted_date DATE NOT NULL,
    FOREIGN KEY policy_id REFERENCES policy NOT NULL,
        ON DELETE CASCADE,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL,
        ON DELETE CASCADE,
    PRIMARY KEY claim_id,
)

--DROP TABLE manages IF EXISTS;
CREATE TABLE manages (
    FOREIGN KEY claim_id REFERENCES claim NOT NULL,
        ON DELETE CASCADE,
    FOREIGN KEY adj_id REFERENCES adjuster NOT NULL,
        ON DELETE CASCADE,
    PRIMARY KEY (claim_id, adj_id),
)

--DROP TABLE agent IF EXISTS;
CREATE TABLE agent (
    agent_id NUMBER(6) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY agent_id,
)

--DROP TABLE adjuster IF EXISTS;
CREATE TABLE adjuster (
    adj_id NUMBER(6) NOT NULL,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(20),
    PRIMARY KEY adj_id,
)

--DROP TABLE dependent IF EXISTS;
CREATE TABLE dependent (
    name VARCHAR (100) NOT NULL,
    relationship VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    FOREIGN KEY cust_id REFERENCES customer NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY (name, relationship, cust_id),
)

--DROP TABLE invoice IF EXISTS;
CREATE TABLE invoice (
    trans_id NUMBER(6) NOT NULL,
    due_date DATE NOT NULL,
    payment_type VARCHAR(10),
    FOREIGN KEY policy_id REFERENCES policy NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY trans_id,
)

--DROP TABLE payment IF EXISTS;
CREATE TABLE payment (
    payment_id NUMBER(6) NOT NULL,
    amount NUMBER(6,2) NOT NULL,
    paid_date DATE NOT NULL,
    FOREIGN KEY claim_id REFERENCES claim NOT NULL
        ON DELETE CASCADE,
    PRIMARY KEY payment_id,
)