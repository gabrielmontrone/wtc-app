# WTC — Aplicativo Android

Aplicativo Android oficial da **WTC**, plataforma de relacionamento e mensageria com clientes
(CRM, campanhas e conversas), com uma **camada de Trust & Safety / Compliance** sobre a
mensageria. Desenvolvido em **Kotlin** com **Jetpack Compose** e integrado à API REST da plataforma.

> 📱 Projeto **mobile** — executa em dispositivo/emulador Android.
>
> 🔗 API REST (repositório do backend): **https://github.com/gabrielmontrone/wtc**

Operação de mensageria; sobre ela há controles de **proteção de dados,
detecção de atividade suspeita e auditoria**.

---

## Funcionalidades

Fluxo ponta a ponta integrado à API:

- **Login / Welcome** — autenticação JWT e **"Continuar com Google"** (Credential Manager).
- **Contatos** — listagem de clientes (CRM) com busca e filtros.
- **Conversas → Mensagens** — _drill-down_ a partir de um contato: conversas do cliente,
  histórico de mensagens e envio de respostas. Inclui:
  - **Atalho `/`** — digite `/` + o nome de uma campanha para inserir a mensagem pronta.
  - **Envio de fotos** — seletor de imagem → upload _multipart_ para o backend → exibição _inline_ (Coil).
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
- **Controle de acesso (RBAC) + isolamento por conta** — papéis distintos: o **OPERADOR** acessa
  o console completo (CRM, campanhas, segmentos, auditoria); o **CLIENTE** vê apenas a própria
  conversa. Cada contato/conversa tem dono, e o backend valida participação a cada requisição
  (`AccessControlService` → **403** em acesso cruzado), evitando vazamento entre contas.
- **Proteção de dados** — sessão (JWT) em `EncryptedSharedPreferences` (AES-256); TLS obrigatório
  fora do ambiente de desenvolvimento.

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
├── api/ApiConfig.kt   # BASE_URL (lido de BuildConfig; default = demo publicada)
├── *Activity.kt       # hosts Compose finos (@AndroidEntryPoint) — inclui AuditoriaActivity
├── WtcApplication.kt  # @HiltAndroidApp
└── ui/theme/          # tema Compose (Color, Type, Theme)
```

## Build e execução

### APK + backend local

Cenário de teste: subir o backend localmente e abrir o app — **nenhuma alteração de código**.

1. **Suba o backend** (Docker, sem configuração) — repositório:
   **https://github.com/gabrielmontrone/wtc**
   ```bash
   git clone https://github.com/gabrielmontrone/wtc.git
   cd wtc
   docker compose up --build      # API em http://localhost:8080
   ```
2. **Instale o APK** (anexado à _release_ do repositório, ou gere-o com o comando da seção
   [📦 Gerando o APK de demonstração](#-gerando-o-apk-de-demonstração)). Ele já vem apontando
   para `http://10.0.2.2:8080/`.
3. **Abra o app:**
   - **Emulador** → funciona de imediato (`10.0.2.2` é o `localhost` da máquina host).
   - **Celular físico** (mesma Wi-Fi do PC) → toque no ícone **⚙** na tela inicial e troque a
     URL para `http://<IP-do-PC>:8080/` (descubra com `ipconfig`/`ifconfig`). Sem rebuild.
4. Registre uma conta no app (use papel **OPERADOR** para ver todos os recursos) e siga o
   [roteiro de demonstração](DEMO.md).

> A URL do servidor é **trocável em tempo de execução** (botão ⚙ → "Servidor da API"), então o
> mesmo APK serve para emulador e celular. O valor inicial vem do build (`BuildConfig.BASE_URL`).

### 🛠️ Rodando do código-fonte

**Pré-requisitos:** Android Studio (versão recente), um emulador/dispositivo e a API WTC em
execução (ver [repositório do backend](https://github.com/gabrielmontrone/wtc)).

1. Abra o projeto no Android Studio e aguarde o _sync_ do Gradle. O `google-services.json` já
   acompanha o repositório.
2. **Sem nenhuma configuração**, a URL inicial do backend vem de `BuildConfig.BASE_URL`
   (padrão: ambiente publicado), injetada em tempo de build em
   [`app/build.gradle.kts`](app/build.gradle.kts) — não há URL fixa no código-fonte. Para mudar
   o padrão **sem editar código**, defina `apiBaseUrl` em `local.properties` (git-ignored) ou
   passe `-PapiBaseUrl=...` ao Gradle:
   ```properties
   # Backend local visto pelo emulador (10.0.2.2 = localhost da máquina host):
   apiBaseUrl=http://10.0.2.2:8080/
   ```
   Em qualquer build, a URL ainda pode ser trocada em tempo de execução pelo botão **⚙**.
3. _(Opcional)_ **Login com Google** — habilite o provedor Google no Firebase, registre o SHA-1 do
   app e preencha o **Web client ID** em `app/src/main/res/values/strings.xml`
   (`google_web_client_id`) e no backend (`GOOGLE_CLIENT_ID`). Sem isso, o botão fica desabilitado
   com uma mensagem clara.
4. Execute o app (▶) no emulador/dispositivo.

### 📦 Gerando o APK de demonstração

```bash
./gradlew :app:assembleDebug -PapiBaseUrl=http://10.0.2.2:8080/
# Saída: app/build/outputs/apk/debug/app-debug.apk
```

O build **debug** permite tráfego HTTP em texto puro para qualquer host (necessário para apontar
a um IP de LAN a partir de um celular); o build **release** mantém TLS obrigatório
(`network_security_config.xml`).

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
- **RBAC + isolamento por conta** — recursos de operador exigem papel `OPERADOR`; dados são
  segregados por dono, com validação de participação no backend (403 em acesso cruzado).
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
