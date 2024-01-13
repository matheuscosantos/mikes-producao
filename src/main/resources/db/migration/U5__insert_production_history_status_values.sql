DELETE FROM historico_producao_status WHERE status = 'ready' AND proximo_status = 'finished';
DELETE FROM historico_producao_status WHERE status = 'preparing' AND proximo_status = 'ready';
DELETE FROM historico_producao_status WHERE status = 'received' AND proximo_status = 'preparing';
