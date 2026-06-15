# WTC — App Android

Cliente Android nativo da plataforma de relacionamento e mensageria **WTC**.
Desenvolvido em **Kotlin** com **Jetpack Compose**, consumindo a
**[API WTC (backend)](https://github.com/gabrielmontrone/wtc)** via REST.

> 📱 Este é um aplicativo **mobile** — roda em um dispositivo/emulador Android, não no navegador.
> O recrutador pode revisar o **código-fonte aqui** e testar a **[API ao vivo](https://github.com/gabrielmontrone/wtc#-live-demo)** diretamente.

---

## 🎯 Como o projeto atende à vaga

| Requisito da vaga | Situação | Onde no projeto |
|---|---|---|
| **Clean Architecture + MVVM** | ✅ | Camadas `domain/` · `data/` · `ui/` com MVVM em todas as telas |
| **Lifecycle / ViewModel / Flow** | ✅ | `@HiltViewModel` expondo `StateFlow`, `collectAsStateWithLifecycle`, `viewModelScope` |
| **Coroutines + APIs RESTful** | ✅ | Retrofit + `suspend` + `withContext(IO)` contra endpoints reais |
| **Testes automatizados** | 🟡 Parcial | 28 testes **unitários** (ViewModels + use cases). Instrumentados/Compose UI no roadmap |
| **Git / code review** | ✅ | Histórico limpo em commits por etapa, prontos para revisão |
| CI/CD *(diferencial)* | ✅ | GitHub Actions: ktlint → detekt → testes → APK |
| Observabilidade / crash reporting *(diferencial)* | ✅ | Firebase Crashlytics |
| Segurança | ✅ | `EncryptedSharedPreferences`, `network-security-config`, `exported=false` |
| Modularização *(diferencial)* | ⏳ Roadmap | Módulo único `:app` hoje |
| Play Store *(diferencial)* | ⏳ Roadmap | Falta R8 + assinatura de release |

## 🧭 Funcionalidades

Fluxo real ponta a ponta, integrado à API:

- **Login / Welcome** — autenticação JWT contra a API WTC.
- **Contatos** — listagem de clientes (CRM) com busca e filtros.
- **Conversas → Mensagens** — _drill-down_ a partir de um contato: conversas do cliente,
  histórico de mensagens e envio de respostas.
- **Segmentos** — segmentação de clientes.
- **Campanhas / Criar Campanha** — métricas de envio e criação de campanhas.
- **Notificações push** — via Firebase Cloud Messaging.

## 🧱 Arquitetura

**Clean Architecture + MVVM**, aplicada de forma consistente em todas as telas:

- **`domain/`** — modelos sem dependência de framework, interfaces de repositório e _use cases_
  (regra de negócio + validação de entrada).
- **`data/`** — `WtcApi` (Retrofit), DTOs `@Serializable` com mapeadores `toDomain()`,
  implementações de repositório, `AuthInterceptor` (token Bearer) e `SessionStorage`.
- **`ui/<feature>/`** — `UiState` + `ViewModel` (`StateFlow`) + `Screen` _stateless_ extraída de
  uma `Route` _stateful_, com estados explícitos de **carregando / erro / vazio / conteúdo** e `@Preview`.
- **Injeção de dependência** com **Hilt** (KSP); _coroutines_ com `@IoDispatcher` injetado.

## 🛠️ Stack tecnológica

| Área | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | Clean Architecture + MVVM |
| Assíncrono | Coroutines + Flow |
| Rede | Retrofit + OkHttp + kotlinx.serialization |
| DI | Hilt (KSP) |
| Firebase | Cloud Messaging + Crashlytics |
| Testes | JUnit4 + coroutines-test |
| Qualidade | ktlint + detekt |
| CI | GitHub Actions |
| Min SDK / Target | 27 / 36 |
| Build | Gradle (Kotlin DSL) + version catalog |

## 🗂️ Estrutura do projeto

```
app/src/main/java/br/com/fiap/wtcapp/
├── ui/<feature>/      # UiState + ViewModel + Route/Screen (login, contatos,
│                      #   conversas, mensagens, campanhas, criarcampanha, segmentos, common)
├── domain/            # model, repository (interfaces), usecase
├── data/              # remote (WtcApi, AuthInterceptor, dto), repository (impls), local (SessionStorage)
├── di/                # NetworkModule, RepositoryModule, DispatchersModule
├── api/ApiConfig.kt   # BASE_URL
├── *Activity.kt       # hosts Compose finos (@AndroidEntryPoint)
├── WtcApplication.kt  # @HiltAndroidApp
└── ui/theme/          # tema Compose (Color, Type, Theme)
```

## ✅ Qualidade, testes e CI

- **Testes unitários** — 28 testes cobrindo todos os ViewModels e _use cases_ por meio de
  repositórios _fake_ em memória (`MainDispatcherRule` + `runTest`), exercitando os caminhos de
  sucesso / erro / vazio / validação.
- **Análise estática** — ktlint + detekt (ajustados para Compose), bloqueando o build em violações.
- **CI** — GitHub Actions roda `ktlintCheck → detekt → testDebugUnitTest → assembleDebug`.

Comandos locais:

```bash
./gradlew ktlintCheck detekt        # estilo + análise estática
./gradlew :app:testDebugUnitTest    # testes unitários
./gradlew :app:assembleDebug        # build
```

## 🔒 Segurança & 📈 Observabilidade

- **Token JWT** persistido com `EncryptedSharedPreferences` (AES-256, chave no Android Keystore).
- **`network-security-config`** permite tráfego em texto puro apenas para o backend de
  desenvolvimento (`10.0.2.2` / `localhost`); TLS obrigatório no restante.
- **`exported=false`** em todas as activities que não são _launcher_.
- **Firebase Crashlytics** para relatórios de crash (coleta desabilitada em _debug_).

## 🚀 Rodando localmente

**Pré-requisitos:** Android Studio (versão recente), um emulador/dispositivo Android e o
[backend WTC](https://github.com/gabrielmontrone/wtc) em execução.

1. Abra o projeto no Android Studio e aguarde o _sync_ do Gradle.
2. Aponte o app para o seu backend em
   [`app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt`](app/src/main/java/br/com/fiap/wtcapp/api/ApiConfig.kt):
   ```kotlin
   object ApiConfig {
       // Backend local visto pelo emulador:
       const val BASE_URL = "http://10.0.2.2:8080/"
       // Ou um backend publicado:
       // const val BASE_URL = "https://<seu-servico>.onrender.com/"
   }
   ```
   > No **emulador** Android, `10.0.2.2` aponta para o `localhost` da sua máquina.
   > Em um **dispositivo físico**, use o IP da sua rede local ou a URL HTTPS publicada.
3. Execute o app (▶) no emulador/dispositivo.

> **Firebase:** o app espera um `google-services.json` em `app/`. Use o arquivo do seu próprio
> projeto Firebase ao clonar este repositório.

## 🧩 Histórico de desenvolvimento

Entregue em commits limpos e _bisectáveis_, organizados por etapa:

1. **Higiene & correções de build**
2. **Clean Architecture + MVVM** (fatia vertical no Login)
3. **Testes, análise estática & CI**
4. **Migração de todas as telas para a API real** (Clean Arch + MVVM)
5. **Segurança & relatórios de crash**

## 🗺️ Roadmap

- Testes **instrumentados / Compose UI** (+ testes de integração com MockWebServer)
- **Modularização** (`:core`, `:data`, `:domain`, `:feature-*`)
- **Polimento** — _design tokens_ de tema + `strings.xml`, nomes em inglês, R8 + assinatura de release

---

_Projeto desenvolvido como parte das atividades acadêmicas na FIAP._
