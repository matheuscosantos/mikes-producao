# language: pt

Funcionalidade: Inicia o processo de produção de um pedido

  @InitProduction
  Cenário: Inicia o processo da producação com id do pedido inválido
    Dado que o processo da produção é iniciado com o id do pedido inválido
      | orderId | 1        |
      | status  | RECEIVED |
    Então não deve haver novo registro da produção do pedido
    E não deve haver mensagem na fila de atualização do status do pedido

  @InitProduction
  Cenário: Inicia o processo da producação producação com status inválido
    Dado que o processo da produção é iniciado com o status inválido
      | orderId | 8f631c40-4a28-44d8-b513-8b303ee1229c |
      | status  | RECEIVE                              |
    Então não deve haver novo registro da produção do pedido
    E não deve haver mensagem na fila de atualização do status do pedido

  @InitProduction
  Cenário: Inicia o processo da producação com o status não permitido
    Dado que o processo da produção é iniciado com status 'READY' que não é permitido
      | orderId | 26bfdd39-9e38-4f51-9bac-77ade65771da |
      | status  | READY                                |
    Então não deve haver novo registro da produção do pedido
    E não deve haver mensagem na fila de atualização do status do pedido

  @InitProduction
  Cenário: Inicia o processo da producação com dados válidos
    Dado que o processo da produção é iniciado é iniciado com dados válidos
      | orderId | 26bfdd39-9e38-4f51-9bac-77ade65771da |
      | status  | RECEIVED                             |
    Então deve haver um novo registro da produção do pedido
    E deve haver mensagem na fila de atualização do status do pedido