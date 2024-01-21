INSERT INTO historico_producao_status(status, proximo_status) VALUES ('RECEIVED', 'PREPARING');
INSERT INTO historico_producao_status(status, proximo_status) VALUES ('PREPARING', 'READY');
INSERT INTO historico_producao_status(status, proximo_status) VALUES ('READY', 'FINISHED');