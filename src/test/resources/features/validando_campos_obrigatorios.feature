# language: pt

Funcionalidade: Validação dos campos obrigatórios do cadastro

  Contexto:
    Dado que o usuário está na tela de cadastro

  @CAD008
  Cenário: Tentar realizar o cadastro sem preencher nenhum campo obrigatório
    Quando o usuário não preencher nenhum campo
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

