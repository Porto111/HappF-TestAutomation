# language: pt
@e2e @compra
Funcionalidade: Compra de produto com usuário recém-cadastrado
  Como um novo usuário
  Quero me cadastrar e, em seguida, comprar um produto
  Para validar a jornada completa de ponta a ponta

  # Pré-requisitos do ambiente:
  #  - loja aberta e GPS do aparelho (ou -Dcompra.endereco="Rua, número, cidade")
  #  - cadastro sem exigência de código SMS (senão o cenário falha com mensagem explícita)
  #  - o pagamento em dinheiro CRIA um pedido real: use ambiente de homologação ou cancele o pedido depois

  Contexto:
    Dado que o aplicativo está aberto na tela inicial

  @E2E001
  Cenário: Cadastrar novo usuário e comprar um produto com a conta criada
    # Cadastro (dados únicos a cada execução)
    Dado que o usuário está na tela de cadastro
    Quando o usuário preencher o cadastro com os dados de um novo usuário único
    E o usuário clicar no botão "Quero me cadastrar"
    Então o cadastro do usuário deve ser realizado com sucesso
    # Compra com a conta recém-criada
    Dado que o usuário recém-cadastrado está autenticado
    E o usuário estiver na tela inicial com lojas disponíveis
    Quando o usuário selecionar o primeiro produto disponível no catálogo
    E o usuário adicionar o produto à sacola
    E o usuário abrir a sacola
    E o usuário fechar o pedido
    E o usuário escolher pagamento em dinheiro
    E o usuário finalizar o pedido
    Então o pedido deve ser recebido com sucesso
