# Mapa do app Happ Fé v2.0.0 (versionCode 45) para a automação

Fonte: análise estática do `.apks` (manifesto + textos compilados em `libapp.so`).
Nada foi executado em dispositivo: itens marcados **[inferido]** precisam de validação no Appium Inspector.
Quando um cenário falha, o relatório anexa **screenshot + page source (XML)** da tela.

## Tecnologia
- **Flutter** (release/AOT): elementos são localizados por `content-desc` (accessibility id), não por `resource-id`.
- Pacote `br.com.emeraldindigo.happfe`, Activity `br.com.emeraldindigo.happfe.MainActivity` (já corretos no `TestConfig`).
- Pagamentos: Stripe (cartão) e Pix. Login social (Google/Facebook) e Firebase presentes.
- Permissões em runtime (localização, notificações, biometria): `autoGrantPermissions=true` já resolve.

## Textos confirmados no app
| Tela | Textos |
|---|---|
| Login | `Faça seu login para acessar`, `Entrar`, `Esqueceu a senha ?`, `Não tem cadastro? Cadastrar`, erro `Erro ao efetuar login.`, `E-mail inválido.` |
| Cadastro | `Faça seu cadastro para acessar`, `Celular (DD) 00000-0000` (máscara), `Li e concordo com Termos e Politicas`, `Quero me cadastrar`, `Já tenho cadastro. Fazer login`, `As senhas não correspondem.` |
| SMS | `Digite o código enviado por SMS para o número:`, `Reenviar o código`, `Qual o número do seu celular ?` |
| Navegação | `Início`, `Promoções`, `Sacola`, `Perfil`; busca `Buscar velas, guias, ervas...` |
| Localização | `Permitir localização`, `Permitir acesso`, `Informe sua localização`, `Digite o endereço`, `Confirmar endereço`, `Continuar sem localização`, `Loja Fechada` |
| Sacola/checkout | `Minha Sacola`, `Fechar Pedido (`, `Adicionar (`, `Valor mínimo do pedido em produtos é`, `Forma de pagamento`, `Dinheiro - Preciso de troco para`, `Finalizar Pedido` |
| Confirmação | `Pedido recebido`, `Acompanhe seu pedido`, `Ver Pedido`, `N° do pedido :` |

**Não existem** no app: `Credenciais inválidas`, `Preencha os campos obrigatórios!`, `Email inválido`, `campo obrigatório`, `Carrinho`, `Adicionar ao carrinho`. As features foram corrigidas para isso.

## Riscos que dependem do ambiente
1. **Código SMS no cadastro.** O app tem fluxo de verificação por SMS. Se ele for exigido para e-mail/senha, o E2E falha com mensagem explícita; é preciso ambiente de teste com código fixo.
2. **Localização.** Sem GPS/endereço o app não lista lojas. Use GPS do aparelho/emulador (`adb emu geo fix -46.65 -23.56`) ou `-Dcompra.endereco="Rua, número, cidade"`.
3. **Loja fechada** → cenário é *ignorado* (skipped), não reprovado. **Sem lojas na região** → reprovado, com mensagem.
4. **Valor mínimo do pedido** → falha com mensagem se 1 item não bastar.
5. **Pagamento em Dinheiro cria pedido real.** Use homologação ou cancele o pedido (`Cancelar pedido`) depois.
6. Cenários `@regra_a_confirmar` (celular curto, senha curta) dependem de regra de negócio não visível no binário.

## Instalar o `.apks` (bundle) no aparelho
```
adb install-multiple base.apk split_config.arm64_v8a.apk split_config.xxxhdpi.apk split_config.pt.apk
```
(ou `bundletool install-apks --apks=Happ-Fé_2_0_0_apkcube_7.apks`)

## Executar
```
mvn test -Dcucumber.filter.tags="@E2E001"                    # cadastro + compra
mvn test -Dcucumber.filter.tags="@cadastro_sucesso"          # só cadastro
mvn test -Dcucumber.filter.tags="not @regra_a_confirmar"     # tudo, sem regras a confirmar
mvn test -Dlogin.email=conta@real.com -Dlogin.senha=Senha@1  # habilita @LOG001
```
Opcionais: `-Dappium.url=... -Ddevice.name=... -Ddevice.udid=...`
