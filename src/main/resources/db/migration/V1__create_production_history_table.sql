CREATE TABLE historico_producao
(
    id            VARCHAR(36) PRIMARY KEY,
    id_pedido     VARCHAR(36)  NOT NULL,
    status        VARCHAR(50) NOT NULL,
    criado_em     TIMESTAMP    NOT NULL,
    atualizado_em TIMESTAMP    NOT NULL
);