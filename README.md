# HappFé — Automação de testes mobile

Projeto de automação Android com **Java 17, Maven, Appium, Selenium e Cucumber**. Os cenários são escritos em Gherkin em português.

> **Estado desta entrega:** o projeto foi ajustado para validar apenas a jornada do utilizador final. `mvn clean test -Dcucumber.execution.dry-run=true` deve ser executado novamente após esta remoção de escopo para confirmar todos os bindings. Não foi possível executar a aplicação num dispositivo real nem concluir uma compra: o ZIP não contém o APK/código da aplicação, e o ambiente de trabalho não tinha dispositivo Android, ADB ou servidor Appium.

## Objetivo e âmbito

O escopo atual cobre **cadastro, autenticação e consulta de produtos** na perspetiva do utilizador final. Foram removidos os cenários, steps e page objects de gestão administrativa de produtos: criação, edição, exclusão e fluxo completo de CRUD.

A feature de cadastro cria um e-mail aleatório no domínio de teste configurado e um número de telemóvel com formato brasileiro. A conta é criada no backend ao executar o cenário positivo; por isso, este cenário está marcado `@staging @mutates-data` e deve ser executado apenas contra uma instalação de teste ligada a um backend de staging.

A **compra com o utilizador recém-criado continua por implementar e por executar**. O material fornecido não permite saber como a aplicação apresenta o catálogo do lado do comprador, detalhe do produto, carrinho, morada, método de pagamento ou confirmação da encomenda. Consultar a secção [O que falta para automatizar a compra](#o-que-falta-para-automatizar-a-compra).

## Estrutura

```text
src/
├── main/java/
│   ├── config/TestConfig.java           # configuração por propriedades JVM/ambiente
│   ├── drivers/DriverFactory.java       # criação/encerramento da sessão Android
│   ├── hooks/Hooks.java                 # ciclo Cucumber e screenshot em falha
│   ├── pages/                           # Page Objects de login/cadastro/produtos
│   └── utils/WaitUtils.java             # esperas explícitas
└── test/
    ├── java/runners/                    # runner JUnit/Cucumber
    ├── java/steps/                      # bindings Gherkin
    └── resources/features/              # cenários em português
```

## Pré-requisitos para uma execução mobile real

1. **Java 17 ou superior** e Maven 3.8+.
2. Android SDK Platform Tools e um emulador ou dispositivo Android acessível ao Appium.
3. Appium 2.x com o driver UiAutomator2 instalado.
4. O APK Android da variante **de teste/staging** instalado no dispositivo. Este projeto recebe `appPackage` e `appActivity`, mas não inclui o APK nem o instala automaticamente.
5. Uma conta de teste existente para autenticação e consulta de produtos (`HAPPFE_TEST_EMAIL` e `HAPPFE_TEST_PASSWORD`).
6. Para o cenário de cadastro positivo, backend de staging, política de cadastro conhecida e, se a aplicação exigir, um mecanismo de teste para confirmação de e-mail/SMS/OTP. O tratamento de OTP ainda não está implementado.

Exemplo de preparação do Appium (ajuste a instalação ao seu sistema):

```bash
npm install --global appium@2
appium driver install uiautomator2
appium --address 127.0.0.1 --port 4723
```

Confirme que o servidor responde em `http://127.0.0.1:4723/status` e que o dispositivo está ligado. Em instalações com vários dispositivos, configure `HAPPFE_UDID`.

## Configuração

Toda a configuração pode ser fornecida por **variável de ambiente** ou por propriedade JVM. A propriedade JVM tem precedência. Não coloque passwords reais no Git nem em ficheiros de relatório.

| Variável | Predefinição | Utilização |
|---|---|---|
| `APPIUM_URL` | `http://127.0.0.1:4723` | URL do servidor Appium. |
| `HAPPFE_PLATFORM` | `Android` | Plataforma Appium. |
| `HAPPFE_DEVICE` | `Android Emulator` | Nome do dispositivo para as capabilities. |
| `HAPPFE_UDID` | vazio | UDID opcional para escolher um dispositivo específico. |
| `HAPPFE_APP_PACKAGE` | `br.com.emeraldindigo.happfe` | `applicationId` da app instalada. |
| `HAPPFE_APP_ACTIVITY` | `br.com.emeraldindigo.happfe.MainActivity` | Activity inicial da app. Confirme estes valores no APK. |
| `HAPPFE_NO_RESET` | `false` | `false` usa o comportamento de reset do Appium; `true` preserva o estado local da app. |
| `HAPPFE_AUTO_GRANT_PERMISSIONS` | `true` | Concede automaticamente permissões Android solicitadas pela app. |
| `HAPPFE_WAIT_SECONDS` | `20` | Timeout de espera explícita. Tem de ser um inteiro positivo. |
| `HAPPFE_TEST_EMAIL` | vazio | E-mail da conta de teste já existente, usado nos cenários de login e consulta. |
| `HAPPFE_TEST_PASSWORD` | vazio | Password da conta de teste já existente. Obrigatória apenas nos testes que fazem login. |
| `HAPPFE_TEST_EMAIL_DOMAIN` | `example.test` | Domínio do e-mail único gerado pelo cenário de cadastro. Use um domínio permitido pelo backend de staging e sem entrega para utilizadores reais. |
| `HAPPFE_TEST_PHONE` | vazio | Telemóvel de teste opcional. Se vazio, o cenário gera um valor aleatório com 11 algarismos. Use apenas dados aceites pela política do ambiente de teste. |
| `HAPPFE_NEW_USER_TEST_PASSWORD` | `QaMobile@12345` | Password sintética da conta criada no cenário positivo. Pode ser substituída no ambiente. |

O ficheiro `.env.example` contém uma base sem segredos. Para o usar com Bash, copie-o para `.env`, preencha-o localmente e carregue-o na sessão:

```bash
cp .env.example .env
# Edite .env localmente; não o adicione ao Git.
set -a
. ./.env
set +a
```

A conta de teste existente é usada para autenticação e consulta; o cenário `@CAD002` cria uma conta nova com e-mail aleatório.

## Comandos

Na raiz do projeto:

```bash
# Compilar as fontes, sem iniciar a app nem executar testes mobile
mvn clean -DskipTests test-compile

# Validar parsing das features e correspondência dos steps, sem interagir com o dispositivo
mvn test -Dcucumber.execution.dry-run=true

# Executar apenas o cenário positivo de criação de utilizador (altera dados no backend)
mvn test -Dcucumber.filter.tags="@CAD002"

# Executar o conjunto de cenários de cadastro
mvn test -Dcucumber.filter.tags="@CAD001 or @CAD002 or @CAD008 or @CAD009 or @CAD010 or @CAD011 or @CAD012 or @CAD013 or @CAD014 or @CAD015 or @CAD016 or @CAD017 or @CAD018 or @CAD019 or @CAD020 or @CAD021 or @CAD023 or @CAD024 or @CAD025 or @CAD026 or @CAD027 or @CAD028"
```

O comando de execução real precisa de um dispositivo e servidor Appium disponíveis e da app de staging instalada. Se o cadastro envolver verificação externa (e-mail, SMS ou OTP), configure o mecanismo de teste suportado pela aplicação antes de correr `@CAD002`.

O runner Cucumber gera HTML em `target/cucumber-report.html`; as propriedades do Cucumber também podem gerar `target/cucumber-reports`. Os resultados Allure ficam em `target/allure-results` quando o plugin correspondente está ativo.


Automação ainda em construção...
