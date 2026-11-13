# Relatório QA — HappFé Test Automation

## 1. Objetivo

Este projeto foi revisto como uma entrega de portfólio para demonstrar competências de QA Tester Automatizador: leitura de requisitos em Gherkin, automação mobile com Appium, Page Object Model, waits explícitos, geração de dados de teste, isolamento de cenários, evidências de falha e validação contínua com Maven.

A aplicação analisada foi o pacote `Happ-Fé_2.0.0_apkcube.apks`, cuja variante base identifica o pacote `br.com.emeraldindigo.happfe`, a activity `br.com.emeraldindigo.happfe.MainActivity` e uma aplicação Flutter. A inspeção estática confirmou que os elementos são principalmente expostos através de `content-desc`, por isso os locators usam accessibility id e XPath baseado em `content-desc`/`text`.

## 2. Diagnóstico inicial

A execução de referência não era reproduzível na sandbox porque o Maven não estava instalado e não havia servidor Appium, ADB ou dispositivo Android disponível. Mesmo assim, o projeto trazia resultados históricos no `target/allure-results` e no Surefire. Esses resultados evidenciaram:

- falhas repetidas em todos os fluxos que tentavam procurar um checkbox de termos que não estava exposto naquela tela;
- erro de login inválido por mensagem não encontrada;
- divergência entre steps Gherkin e métodos do Page Object;
- cinco métodos chamados por `CadastroUsuarioSteps` que não existiam em `CadastroUsuarioPage`;
- testes unitários, quando adicionados, não seriam executados pelo Surefire devido a um filtro que incluía apenas `*Runner.java`;
- comportamento real do campo de celular diferente da expectativa de filtragem de letras no cenário `CAD017`.

## 3. Alterações realizadas

### 3.1 Contrato entre Gherkin e Page Object

Foram implementados no `CadastroUsuarioPage` os métodos que os steps já utilizavam:

- `queroMeCadastrar()`;
- `botaoCadastroInicialEstaVisivel()`;
- `botaoCadastroInicialEstaHabilitado()`;
- `senhaEstaMascarada(String)`.

Também foi adicionado o step `o cadastro não deve ser concluído`, que estava presente nas features mas não tinha binding. Foi criado um alias para o texto utilizado pela feature de compra: `o usuário preencher o cadastro com os dados de um novo usuário único`.

**Porquê:** um projeto de automação entregável precisa falhar na compilação quando existe um contrato quebrado, e não apenas descobrir o problema durante uma execução mobile longa.

### 3.2 Checkbox de termos opcional

O método `aceitarTermosSeNecessario()` deixou de fazer scroll obrigatório até ao checkbox. Agora ele procura primeiro o checkbox identificado por termos e, caso não exista, utiliza um checkbox único apenas quando ele estiver realmente presente. Se o Flutter não expuser esse componente no XML, o fluxo continua para o botão de cadastro.

**Porquê:** o histórico mostrou que a procura obrigatória do checkbox causava erro antes mesmo de o formulário ser submetido. A solução mantém a validação quando o componente existe, mas não transforma um locator opcional em pré-condição universal.

### 3.3 Resultado do cadastro

O step de sucesso deixou de procurar genericamente a palavra `sucesso`. Ele usa `ResultadoCadastro` e aguarda um resultado observável: sucesso, erro de validação, pedido de SMS ou timeout.

**Porquê:** estados de negócio devem ser representados por condições observáveis e não por uma mensagem presumida. O fluxo de SMS existente também fica explícito para diagnóstico.

### 3.4 Configuração por ambiente

`TestConfig` passou a aceitar propriedades JVM e variáveis de ambiente também para:

- URL Appium, plataforma, device e UDID;
- package/activity da aplicação;
- `noReset`, concessão de permissões e timeouts;
- credenciais de teste e endereço de entrega.

A precedência é: propriedade JVM, variável de ambiente e valor default seguro.

**Porquê:** evita editar código para trocar de device ou ambiente e reduz o risco de credenciais serem gravadas no repositório.

### 3.5 Isolamento e evidências

O hook agora chama `ScenarioContext.reset()` no início de cada cenário. Em caso de falha, anexa screenshot e page source XML antes de fechar o driver.

**Porquê:** dados de um cenário não podem contaminar outro, e screenshot + XML permitem explicar rapidamente se a causa foi locator, estado da aplicação, backend ou ambiente.

### 3.6 Qualidade do build

O Surefire foi ajustado para executar tanto o runner Cucumber como classes `*Test.java`. Foram adicionados quatro testes unitários:

- dois para `Rotulos`, incluindo escape de aspas em XPath;
- dois para `TestDataFactory`, validando formato e unicidade dos dados.

**Porquê:** testes puros são rápidos, determinísticos e dão feedback de regressão sem depender de um emulador.

## 4. Validação executada

Comando utilizado:

```bash
mvn -B clean test -Dcucumber.execution.dry-run=true
```

Resultado final:

```text
29 cenários Cucumber — 0 falhas, 0 erros
2 testes RotulosTest — 0 falhas, 0 erros
2 testes TestDataFactoryTest — 0 falhas, 0 erros
TOTAL: 33 testes — BUILD SUCCESS
```

O `dry-run` valida compilação, parsing das features e correspondência entre steps e definições sem iniciar Appium.

## 5. Execução mobile real

A execução E2E real ainda requer:

1. Android SDK Platform Tools e um device/emulador;
2. Appium 2.x e driver UiAutomator2;
3. instalação dos APKs split do `.apks`;
4. backend de staging acessível;
5. conta de teste ou configuração de cadastro suportada;
6. mecanismo de teste para SMS/OTP, se o backend exigir.

Exemplos:

```bash
mvn test -Dcucumber.filter.tags="@LOG002 or @LOG003 or @LOG004"
mvn test -Dcucumber.filter.tags="@CAD001 or @CAD008 or @CAD014"
mvn test -Dcucumber.filter.tags="@CAD002"
```

O cenário de compra deve ser executado apenas em staging: o pagamento em dinheiro cria pedido real e pode exigir loja aberta, localização, valor mínimo e posterior limpeza.

## 6. Pendências honestas

- O cenário `CAD017` deve ser revisitado com o produto: o histórico indica que letras permaneceram no campo, enquanto a feature esperava filtragem automática. Isso é uma possível descoberta de defeito, não deve ser mascarado pela automação.
- Login social, Stripe/Pix, SMS/OTP e disponibilidade de loja não foram falsamente declarados como cobertos ponta a ponta.
- A entrega não inclui segredo, credencial real ou APK dentro do ZIP final.

## 7. Competências demonstradas

A solução evidencia separação Page Object/steps, Gherkin em português, locators orientados a acessibilidade, waits explícitos, dados sintéticos, configuração externa, hooks de evidência, isolamento de cenário, testes unitários e documentação de riscos. As decisões foram mantidas simples para serem explicadas numa entrevista técnica.
