# language: pt

Funcionalidade: Cadastro de novo usuário no aplicativo
  Como um usuário ainda não cadastrado
  Quero realizar meu cadastro no aplicativo
  Para poder acessar as funcionalidades disponíveis para usuários cadastrados

  Contexto:
    Dado que o aplicativo está aberto na tela inicial
    E o botão "Não tem cadastro? Cadastrar" está disponível

  @CAD001
  Cenário: Acessar a tela de cadastro de novo usuário
    Quando o usuário clicar no botão "Não tem cadastro? Cadastrar"
    Então a tela de cadastro deve ser apresentada
    E o campo "Nome" deve estar disponível
    E o campo "E-mail" deve estar disponível
    E o campo "Celular" deve estar disponível
    E o campo "Senha" deve estar disponível
    E o campo "Confirmar senha" deve estar disponível

  @CAD002 @cadastro_sucesso
  Cenário: Cadastrar novo usuário com dados válidos
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o cadastro com os dados de um novo usuário único
    E o usuário clicar no botão "Quero me cadastrar"
    Então o cadastro do usuário deve ser realizado com sucesso

  @CAD009
  Cenário: Tentar realizar o cadastro sem informar o nome
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "E-mail" com "joao.silva@email.com"
    E o usuário preencher o campo "Celular" com "11999999999"
    E o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário preencher o campo "Confirmar senha" com "Senha@123"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD010
  Cenário: Tentar realizar o cadastro sem informar o e-mail
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Nome" com "João da Silva"
    E o usuário preencher o campo "Celular" com "11999999999"
    E o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário preencher o campo "Confirmar senha" com "Senha@123"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD011
  Cenário: Tentar realizar o cadastro sem informar o celular
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Nome" com "João da Silva"
    E o usuário preencher o campo "E-mail" com "joao.silva@email.com"
    E o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário preencher o campo "Confirmar senha" com "Senha@123"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD012
  Cenário: Tentar realizar o cadastro sem informar a senha
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Nome" com "João da Silva"
    E o usuário preencher o campo "E-mail" com "joao.silva@email.com"
    E o usuário preencher o campo "Celular" com "11999999999"
    E o usuário preencher o campo "Confirmar senha" com "Senha@123"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD013
  Cenário: Tentar realizar o cadastro sem confirmar a senha
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Nome" com "João da Silva"
    E o usuário preencher o campo "E-mail" com "joao.silva@email.com"
    E o usuário preencher o campo "Celular" com "11999999999"
    E o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário não preencher o campo "Confirmar senha"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD014
  Esquema do Cenário: Validar formato inválido de e-mail
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "E-mail" com "<email>"
    E o usuário preencher os demais campos obrigatórios com dados válidos
    E o usuário tentar concluir o cadastro
    Então o aplicativo deve informar que o e-mail informado é inválido

    Exemplos:
      | email                |
      | joao                 |
      | joao@                |
      | @email.com           |
      | joao@email           |
      | joao email@email.com |

  @CAD015
  Cenário: Aceitar endereço de e-mail válido
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "E-mail" com "joao.silva@email.com"
    Então o campo "E-mail" deve aceitar o valor informado
    E nenhuma mensagem de formato inválido deve ser apresentada

  @CAD016 @regra_a_confirmar
  Cenário: Informar número de celular com quantidade insuficiente de dígitos
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Celular" com "119999"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD017
  Cenário: Informar caracteres não numéricos no campo celular
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Celular" com "ABCDEF"
    Então o campo "Celular" não deve aceitar caracteres inválidos
    E o aplicativo deve manter somente o formato permitido para celular

  @CAD018
  Cenário: Informar número de celular válido
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Celular" com "11999999999"
    Então o campo "Celular" deve aceitar o valor informado

  @CAD019 @regra_a_confirmar
  Cenário: Informar senha com quantidade de caracteres inferior ao permitido
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Senha" com "123"
    E o usuário tentar concluir o cadastro
    Então o cadastro não deve ser concluído

  @CAD020
  Cenário: Informar senha e confirmação de senha diferentes
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário preencher o campo "Confirmar senha" com "Senha@456"
    E o usuário tentar concluir o cadastro
    Então o aplicativo deve informar que as senhas não são iguais

  @CAD021
  Cenário: Informar senha e confirmação de senha iguais
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o campo "Senha" com "Senha@123"
    E o usuário preencher o campo "Confirmar senha" com "Senha@123"
    Então nenhuma mensagem de divergência de senha deve ser apresentada

  @CAD023
  Cenário: Acessar a tela de login através do botão "Já tenho cadastro. Fazer login"
    Dado que o usuário está na tela de cadastro
    Quando o usuário clicar no botão "Já tenho cadastro. Fazer login"
    Então o aplicativo deve direcionar o usuário para a tela de login

  @CAD024
  Cenário: Retornar para a tela de login sem realizar o cadastro
    Dado que o usuário está na tela de cadastro
    E nenhum dado foi informado nos campos de cadastro
    Quando o usuário clicar no botão "Já tenho cadastro. Fazer login"
    Então o aplicativo deve abandonar o fluxo de cadastro
    E o aplicativo deve apresentar a tela de login

  @CAD025
  Cenário: Validar disponibilidade do botão "Quero me cadastrar"
    Dado que o usuário está na tela de cadastro
    Então o botão "Quero me cadastrar" deve estar visível
    E o botão "Quero me cadastrar" deve estar habilitado

  @CAD026
  Cenário: Validar disponibilidade do botão "Já tenho cadastro. Fazer login"
    Dado que o usuário está na tela de cadastro
    Então o botão "Já tenho cadastro. Fazer login" deve estar visível
    E o botão "Já tenho cadastro. Fazer login" deve estar habilitado
