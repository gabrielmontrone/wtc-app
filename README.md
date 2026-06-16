# WTC — Aplicativo Android

Aplicativo Android oficial da **WTC**, plataforma de relacionamento e mensageria com clientes
(CRM, campanhas e conversas). Desenvolvido em **Kotlin** com **Jetpack Compose** e integrado à
API REST da plataforma.

> 📱 Projeto **mobile** — executa em dispositivo/emulador Android.

---

## Funcionalidades

Fluxo ponta a ponta integrado à API:

- **Login / Welcome** — autenticação JWT.
- **Contatos** — listagem de clientes (CRM) com busca e filtros.
- **Conversas → Mensagens** — _drill-down_ a partir de um contato: conversas do cliente,
  histórico de mensagens e envio de respostas.
- **Segmentos** — segmentação de clientes.
- **Campanhas / Criar Campanha** — métricas de envio e criação de campanhas.
- **Notificações push** — via Firebase Cloud Messaging.

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
4. Execute o app (▶) no emulador/dispositivo.

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

## Convenções

- Commits pequenos e descritivos, agrupados por contexto.
- _Pull requests_ revisados; build de CI verde como pré-requisito de merge.
- Estilo de código garantido por ktlint/detekt.

## Roadmap

- Testes **instrumentados / Compose UI** e testes de integração com MockWebServer.
- **Modularização** (`:core`, `:data`, `:domain`, `:feature-*`).
- _Design tokens_ de tema + `strings.xml`; R8 + assinatura de release.
