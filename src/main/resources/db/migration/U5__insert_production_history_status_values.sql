DELETE FROM historico_producao_status WHERE status = 'READY' AND proximo_status = 'FINISHED';
DELETE FROM historico_producao_status WHERE status = 'PREPARING' AND proximo_status = 'READY';
DELETE FROM historico_producao_status WHERE status = 'RECEIVED' AND proximo_status = 'PREPARING';
