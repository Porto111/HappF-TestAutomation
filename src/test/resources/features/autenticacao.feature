# language: pt
@login
Funcionalidade: Autenticação no HappFé
  # Mensagens conforme o app v2.0.0: "Erro ao efetuar login." e "E-mail inválido."
  # O cenário de login válido exige uma conta real: -Dlogin.email=... -Dlogin.senha=...
  # (o fluxo completo com conta recém-criada está em compra_apos_cadastro.feature)

  Contexto:
    Dado que o aplicativo HappFé foi aberto

  @LOG001
  Cenário: Login com credenciais válidas
    Quando eu informo as credenciais válidas de teste
    E eu clico no botão entrar
    Então devo ver a tela principal do aplicativo

  @LOG002
  Cenário: Login com credenciais inválidas
    Quando eu preencho o email "invalido.happfe@email.com"
    E eu preencho a senha "SenhaErrada@123"
    E eu clico no botão entrar
    Então devo ver a mensagem de erro "Erro ao efetuar"

  @LOG003
  Cenário: Login sem preencher campos
    Quando eu clico no botão entrar
    Então devo permanecer na tela de login

  @LOG004
  Cenário: Login com email inválido
    Quando eu preencho o email "emailinvalido"
    E eu preencho a senha "Teste@123"
    E eu clico no botão entrar
    Então devo ver a mensagem de erro "E-mail inválido"
