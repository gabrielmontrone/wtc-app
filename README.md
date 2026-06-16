# WTC — Aplicativo Android

Aplicativo Android oficial da **WTC**, plataforma de relacionamento e mensageria com clientes
(CRM, campanhas e conversas), com uma **camada de Trust & Safety / Compliance** sobre a
mensageria. Desenvolvido em **Kotlin** com **Jetpack Compose** e integrado à API REST da plataforma.

> 📱 Projeto **mobile** — executa em dispositivo/emulador Android.

A operação de mensageria continua sendo o produto; sobre ela há controles de **proteção de dados,
detecção de atividade suspeita e auditoria** — os mesmos controles que um time de prevenção a
fraudes / AML / KYC embute dentro de produtos reais. Veja
[**Trust & Safety / Compliance**](#trust--safety--compliance).

---

## Funcionalidades

Fluxo ponta a ponta integrado à API:

- **Login / Welcome** — autenticação JWT e **"Continuar com Google"** (Credential Manager).
- **Contatos** — listagem de clientes (CRM) com busca e filtros.
- **Conversas → Mensagens** — _drill-down_ a partir de um contato: conversas do cliente,
  histórico de mensagens e envio de respostas. Inclui:
  - **Atalho `/`** — digite `/` + o nome de uma campanha para inserir a mensagem pronta.
  - **Envio de fotos** — seletor de imagem → upload pré-assinado (S3/MinIO) → exibição _inline_ (Coil).
  - **Scan DLP** — alerta antes de enviar dados sensíveis (ver Trust & Safety abaixo).
- **Segmentos** — segmentação de clientes.
- **Campanhas / Criar Campanha** — métricas de envio e criação de campanhas.
- **Auditoria** — trilha de ações e eventos de _compliance_ (tela voltada ao operador).
- **Notificações push** — via Firebase Cloud Messaging.

## Trust & Safety / Compliance

Camada de controles sobre a mensageria, sem alterar a regra de negócio. Toda mensagem passa por um
**pipeline de detecção + auditoria**:

- **DLP (Data Loss Prevention)** — um analisador determinístico (`RiskAnalyzer`) examina o conteúdo
  e sinaliza **CPF, CNPJ, números de cartão (validados por Luhn) e links suspeitos**
  (não-HTTPS, encurtadores, host por IP). Cada mensagem recebe um **nível de risco**
  (`NONE/LOW/MEDIUM/HIGH`) e _flags_.
  - **Defesa em profundidade:** um espelho _on-device_ (`MessageRiskAnalyzer`) **avisa o operador
    antes do envio** ("Enviar mesmo assim?"); o **backend é a fonte da verdade**, persiste o
    resultado na mensagem e exibe **selos ⚠** no chat.
- **Auditoria** — ações sensíveis (login, envio de mensagem, criação de cliente) e, em especial,
  **mensagens suspeitas** (`SUSPICIOUS_MESSAGE`) são gravadas em uma trilha imutável, consultável
  na tela **Auditoria** (`GET /api/v1/audit`).
- **Proteção de dados** — sessão (JWT) em `EncryptedSharedPreferences` (AES-256); TLS obrigatório
  fora do ambiente de desenvolvimento.

### Mapa de aderência à vaga (prevenção a fraudes / AML / KYC)

| Recurso | Responsabilidade da área |
|---|---|
| Scan DLP + selos de risco | Monitoramento e **identificação de atividades suspeitas** |
| Aviso antes do envio (CPF/CNPJ/cartão) | **Proteção de dados** dos clientes; redução de perdas |
| Trilha de auditoria (tela Auditoria) | **Auditoria/conformidade**; _accountability_ |
| JWT criptografado + TLS + `exported=false` | **Segurança** e controles de conformidade |
| Login com Google (verificação de _ID token_) | Autenticação forte / onboarding |

## Arquitetura

**Clean Architecture + MVVM**, aplicada de forma consistente em todas as telas:

- **`domain/`** — modelos sem dependência de framework, interfaces de repositório e _use cases_
  (regra de negócio + validação de entrada).
- **`data/`** — `WtcApi` (Retrofit), DTOs `@Serializable` com mapeadores `toDomain()`,
  implementações de repositório, `AuthInterceptor` (token Bearer) e `SessionStorage`.
- **`ui/<feature>/`** — `UiState` + `ViewModel` (`StateFlow`) + `Screen` _stateless_ extraída de
  uma `Route` _stateful_, com estados explícitos de **carregando / erro / vazio / conteúdo** e `@Preview`.
- **Injeção de dependência** com **Hilt** (KSP); _coroutines_ com `@IoDispatcher` injetado.

A separação em camadas mantém a regra de negócio independente de Android/rede e torna os
_ViewModels_ e _use cases_ testáveis isoladamente.

## Stack tecnológica

| Área | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | Clean Architecture + MVVM |
| Assíncrono | Coroutines + Flow |
| Rede | Retrofit + OkHttp + kotlinx.serialization |
| Imagens | Coil (carregamento de fotos no chat) |
| Autenticação | JWT + Google Sign-In (androidx.credentials) |
| DI | Hilt (KSP) |
| Firebase | Cloud Messaging + Crashlytics |
| Testes | JUnit4 + coroutines-test |
| Qualidade | ktlint + detekt |
| CI | GitHub Actions |
| Min SDK / Target | 27 / 36 |
| Build | Gradle (Kotlin DSL) + version catalog |

## Estrutura do projeto

```
app/src/main/java/br/com/fiap/wtcapp/
├── ui/<feature>/      # UiState + ViewModel + Route/Screen (login, contatos, conversas,
│                      #   mensagens, campanhas, criarcampanha, segmentos, auditoria, common)
├── domain/            # model, repository (interfaces), usecase, compliance (MessageRiskAnalyzer)
├── data/              # remote (WtcApi, AuthInterceptor, dto), repository (impls), local (SessionStorage)
├── di/                # NetworkModule, RepositoryModule, DispatchersModule
├── api/ApiConfig.kt   # BASE_URL
├── *Activity.kt       # hosts Compose finos (@AndroidEntryPoint) — inclui AuditoriaActivity
├── WtcApplication.kt  # @HiltAndroidApp
└── ui/theme/          # tema Compose (Color, Type, Theme)
```

## Build e execução

**Pré-requisitos:** Android Studio (versão recente), um emulador/dispositivo Android e a API WTC
em execução.

1. Abra o projeto no Android Studio e aguarde o _sync_ do Gradle.
2. Configure a URL do backend em
   [`app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt`](app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt):
   ```kotlin
   object ApiConfig {
       // Backend local visto pelo emulador:
       const val BASE_URL = "http://10.0.2.2:8080/"
       // Backend local a partir de um dispositivo físico (mesma rede Wi-Fi):
       // const val BASE_URL = "http://<IP-da-máquina>:8080/"
       // Ambiente publicado (demo na Render):
       // const val BASE_URL = "https://wtc-ioxk.onrender.com/"
   }
   ```
   > No **emulador** Android, `10.0.2.2` aponta para o `localhost` da máquina host.
   > Em um **dispositivo físico**, use o IP da máquina na rede local (ex.: `192.168.x.x`) —
   > e inclua esse IP em
   > [`network_security_config.xml`](app/src/main/res/xml/network_security_config.xml),
   > já que tráfego HTTP em texto puro só é permitido para os hosts de desenvolvimento listados.

   > ⏱️ **Sobre o ambiente publicado:** a demo `https://wtc-ioxk.onrender.com` roda no
   > **plano gratuito da Render**, que hiberna após ~15 min ociosos — a primeira requisição
   > após o período ocioso pode levar **~30–60s** para "acordar" o servidor (as seguintes são
   > rápidas). **Para desenvolvimento, recomenda-se rodar o backend localmente** (instantâneo
   > e offline). Consulte o [README do backend](https://github.com/gabrielmontrone/wtc) para
   > subir a API e exemplos de uso (`curl`/Swagger).
3. Adicione o `google-services.json` do projeto Firebase em `app/`.
4. _(Opcional)_ **Login com Google** — habilite o provedor Google no Firebase, registre o SHA-1 do
   app e preencha o **Web client ID** em `app/src/main/res/values/strings.xml`
   (`google_web_client_id`) e no backend (`GOOGLE_CLIENT_ID`). Sem isso, o botão fica desabilitado
   com uma mensagem clara.
5. Execute o app (▶) no emulador/dispositivo.

## Testes, qualidade e CI

- **Testes unitários** — cobertura de todos os ViewModels e _use cases_ por meio de
  repositórios _fake_ em memória (`MainDispatcherRule` + `runTest`), exercitando os caminhos de
  sucesso / erro / vazio / validação.
- **Análise estática** — ktlint + detekt (ajustados para Compose), bloqueando o build em violações.
- **CI** — GitHub Actions executa `ktlintCheck → detekt → testDebugUnitTest → assembleDebug`.

```bash
./gradlew ktlintCheck detekt        # estilo + análise estática
./gradlew :app:testDebugUnitTest    # testes unitários
./gradlew :app:assembleDebug        # build
```

## Segurança e observabilidade

- **Token JWT** persistido com `EncryptedSharedPreferences` (AES-256, chave no Android Keystore).
- **`network-security-config`** permite tráfego em texto puro apenas para o ambiente de
  desenvolvimento (`10.0.2.2` / `localhost`); TLS obrigatório no restante.
- **`exported=false`** em todas as activities que não são _launcher_.
- **Firebase Crashlytics** para relatórios de crash (coleta desabilitada em _debug_).
- **DLP + auditoria** — detecção de dados sensíveis e trilha de eventos
  (ver [Trust & Safety / Compliance](#trust--safety--compliance)).

## Convenções

- Commits pequenos e descritivos, agrupados por contexto.
- _Pull requests_ revisados; build de CI verde como pré-requisito de merge.
- Estilo de código garantido por ktlint/detekt.

## Roadmap

- Testes **instrumentados / Compose UI** e testes de integração com MockWebServer.
- **Modularização** (`:core`, `:data`, `:domain`, `:feature-*`).
- _Design tokens_ de tema + `strings.xml`; R8 + assinatura de release.
