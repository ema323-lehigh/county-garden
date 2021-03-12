CREATE TABLE item (
    item_id NUMBER(6),
    category VARCHAR(20),
    approx_value NUMBER(6,2),
    PRIMARY KEY item_id,
    FOREIGN KEY policy_id REFERENCES policy,
)