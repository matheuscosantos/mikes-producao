CREATE TABLE historico_producao_status
(
    status         VARCHAR(50) PRIMARY KEY,
    proximo_status VARCHAR(50) NOT NULL
);