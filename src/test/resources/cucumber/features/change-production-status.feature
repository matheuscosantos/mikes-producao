# language: pt

Funcionalidade: Alteração de status da producação do pedido

  @ChangeProductionStatus
  Cenário: Alteração de status da producação com id do pedido inválido
    Dado que a alteração do status da produção é recebido com o id do pedido inválido
      | orderId | 1         |
      | status  | RECEIVED  |
    Então deve retornar o status 400 com o campo "message" igual a "Invalid value for 'OrderId': invalid order id."
    E não deve haver novo registro com a alteração no status da produção

  @ChangeProductionStatus
  Cenário: Alteração de status da producação com status inválido
    Dado que a alteração do status da produção é recebido com o status inválido
      | orderId | 8f631c40-4a28-44d8-b513-8b303ee1229c |
      | status  | RECEIVE  |
    Então deve retornar o status 400 com o campo "message" igual a "Invalid value for 'ProductionStatus': Invalid production status: 'RECEIVE'."
    E não deve haver novo registro com a alteração no status da produção

  @ChangeProductionStatus
  Cenário: Alteração de status da producação com o status não permitido
    Dado que a alteração do status da produção é recebido indo de 'RECEIVED' para 'READY'
      | orderId | 26bfdd39-9e38-4f51-9bac-77ade65771da |
      | status  | READY  |
    Então deve retornar o status 412 com o campo "message" igual a "found 'ProductionHistory' in invalid state: 'status not allowed'"
    E não deve haver novo registro com a alteração no status da produção

  @ChangeProductionStatus
  Cenário: Alteração de status da producação com dados válidos
    Dado que a alteração do status da produção é recebido com dados válidos
      | orderId | 26bfdd39-9e38-4f51-9bac-77ade65771da |
      | status  | PREPARING  |
    Então deve retornar o status 200
    E deve haver um novo registro com a alteração no status da produção
    E deve ter enviado uma mensagem para o tópico de produção