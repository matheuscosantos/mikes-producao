INSERT INTO historico_producao_status(status, proximo_status) VALUES ('received', 'preparing');
INSERT INTO historico_producao_status(status, proximo_status) VALUES ('preparing', 'ready');
INSERT INTO historico_producao_status(status, proximo_status) VALUES ('ready', 'finished');