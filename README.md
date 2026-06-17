# WTC — Aplicativo Android

Aplicativo Android da **WTC**, uma plataforma de relacionamento e mensageria com clientes (CRM,
campanhas e conversas), com uma camada de Trust & Safety / Compliance sobre a mensageria. Feito em
Kotlin com Jetpack Compose, consumindo a API REST da plataforma.

É um projeto mobile, então roda em emulador ou dispositivo Android. O backend fica em um
repositório separado: https://github.com/gabrielmontrone/wtc

A mensageria é o produto; em cima dela há controles de proteção de dados, detecção de atividade
suspeita e auditoria.

---

## Funcionalidades

Fluxo ponta a ponta integrado à API:

- **Login / Welcome** — autenticação JWT e "Continuar com Google" (Credential Manager).
- **Contatos** — listagem de clientes (CRM) com busca e filtros.
- **Conversas e mensagens** — a partir de um contato, você abre as conversas do cliente, vê o
  histórico e responde. Inclui:
  - Atalho `/`: digite `/` seguido do nome de uma campanha para inserir a mensagem pronta.
  - Envio de fotos: escolhe a imagem, ela sobe por multipart para o backend e aparece no chat (Coil).
  - Scan DLP: alerta antes de enviar dados sensíveis (detalhes em Trust & Safety, abaixo).
- **Segmentos** — segmentação de clientes.
- **Campanhas / Criar Campanha** — métricas de envio e criação de campanhas.
- **Auditoria** — trilha de ações e eventos de compliance (tela voltada ao operador).
- **Notificações push** — via Firebase Cloud Messaging.

## Trust & Safety / Compliance

São controles aplicados sobre a mensageria sem mexer na regra de negócio. Toda mensagem passa por
um pipeline de detecção e auditoria:

- **DLP (Data Loss Prevention)** — um analisador determinístico (`RiskAnalyzer`) examina o conteúdo
  e sinaliza CPF, CNPJ, números de cartão (validados por Luhn) e links suspeitos (não-HTTPS,
  encurtadores, host por IP). Cada mensagem recebe um nível de risco (`NONE/LOW/MEDIUM/HIGH`) e
  suas flags. Há também um espelho on-device (`MessageRiskAnalyzer`) que avisa o operador antes do
  envio ("Enviar mesmo assim?"), mas o backend é a fonte da verdade: ele persiste o resultado na
  mensagem e o chat mostra um selo de alerta nos casos sinalizados.
- **Auditoria** — ações sensíveis (login, envio de mensagem, criação de cliente) e, em especial,
  mensagens suspeitas (`SUSPICIOUS_MESSAGE`) são gravadas em uma trilha imutável, consultável na
  tela de Auditoria (`GET /api/v1/audit`).
- **Controle de acesso (RBAC) e isolamento por conta** — o OPERADOR acessa o console completo (CRM,
  campanhas, segmentos, auditoria); o CLIENTE vê apenas a própria conversa. Cada contato/conversa
  tem dono, e o backend valida a participação a cada requisição (`AccessControlService` responde
  403 em acesso cruzado), evitando vazamento entre contas.
- **Proteção de dados** — a sessão (JWT) fica em `EncryptedSharedPreferences` (AES-256) e o TLS é
  obrigatório fora do ambiente de desenvolvimento.

## Arquitetura

Clean Architecture + MVVM, aplicada de forma consistente em todas as telas:

- `domain/` — modelos sem dependência de framework, interfaces de repositório e use cases (regra de
  negócio e validação de entrada).
- `data/` — `WtcApi` (Retrofit), DTOs `@Serializable` com mapeadores `toDomain()`, implementações de
  repositório, `AuthInterceptor` (token Bearer) e `SessionStorage`.
- `ui/<feature>/` — `UiState` + `ViewModel` (`StateFlow`) + uma `Screen` stateless extraída de uma
  `Route` stateful, com estados explícitos de carregando / erro / vazio / conteúdo e `@Preview`.
- Injeção de dependência com Hilt (KSP); coroutines com `@IoDispatcher` injetado.

A separação em camadas mantém a regra de negócio independente de Android e de rede, e deixa os
ViewModels e use cases testáveis isoladamente.

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
├── *Activity.kt       # hosts Compose finos (@AndroidEntryPoint), inclui AuditoriaActivity
├── WtcApplication.kt  # @HiltAndroidApp
└── ui/theme/          # tema Compose (Color, Type, Theme)
```

## Build e execução

### Rodando com o APK e o backend local

O caminho mais rápido para testar: subir o backend localmente e abrir o app, sem alterar nenhuma
linha de código.

1. Suba o backend com Docker (não precisa de configuração). O repositório é
   https://github.com/gabrielmontrone/wtc:
   ```bash
   git clone https://github.com/gabrielmontrone/wtc.git
   cd wtc
   docker compose up --build      # API em http://localhost:8080
   ```
2. Instale o APK (anexado à release do repositório, ou gere com o comando da seção
   [Gerando o APK de demonstração](#gerando-o-apk-de-demonstração)). Ele já vem apontando para
   `http://10.0.2.2:8080/`.
3. Abra o app:
   - No emulador, funciona de imediato, porque `10.0.2.2` é o `localhost` da máquina host visto de
     dentro do emulador.
   - Em um celular físico (na mesma Wi-Fi do PC), abra o ícone de engrenagem na tela inicial e troque
     a URL para `http://<IP-do-PC>:8080/` — descubra o IP com `ipconfig` (Windows) ou `ifconfig`
     (Linux/Mac). Não precisa recompilar.
4. Registre uma conta no app (use o papel OPERADOR para ver todos os recursos) e siga o
   [roteiro de demonstração](DEMO.md).

A URL do servidor pode ser trocada em tempo de execução pelo ícone de engrenagem ("Servidor da
API"), então o mesmo APK serve tanto para o emulador quanto para o celular. O valor inicial vem do
build (`BuildConfig.BASE_URL`).

### Rodando a partir do código-fonte

Pré-requisitos: Android Studio recente, um emulador ou dispositivo, e a API WTC em execução (ver
[repositório do backend](https://github.com/gabrielmontrone/wtc)).

1. Abra o projeto no Android Studio e aguarde o sync do Gradle. O `google-services.json` já vem no
   repositório.
2. Sem nenhuma configuração, a URL inicial do backend vem de `BuildConfig.BASE_URL` (por padrão, o
   ambiente publicado), injetada no build em [`app/build.gradle.kts`](app/build.gradle.kts) — não há
   URL fixa no código-fonte. Para mudar esse padrão sem editar código, defina `apiBaseUrl` em
   `local.properties` (que é git-ignored) ou passe `-PapiBaseUrl=...` para o Gradle:
   ```properties
   # Backend local visto pelo emulador (10.0.2.2 = localhost da máquina host):
   apiBaseUrl=http://10.0.2.2:8080/
   ```
   Em qualquer build a URL ainda pode ser trocada em tempo de execução pelo ícone de engrenagem.
3. Opcional: para o "Continuar com Google", habilite o provedor Google no Firebase, registre o SHA-1
   do app e preencha o Web client ID em `app/src/main/res/values/strings.xml`
   (`google_web_client_id`) e no backend (`GOOGLE_CLIENT_ID`). Sem isso, o botão fica desabilitado
   com uma mensagem explicando o motivo.
4. Execute o app no emulador ou dispositivo.

### Gerando o APK de demonstração

```bash
./gradlew :app:assembleDebug -PapiBaseUrl=http://10.0.2.2:8080/
# Saída: app/build/outputs/apk/debug/app-debug.apk
```

O build debug permite tráfego HTTP em texto puro para qualquer host, o que é necessário para apontar
a um IP de LAN a partir de um celular. O build release mantém o TLS obrigatório, conforme o
`network_security_config.xml`.

## Testes, qualidade e CI

- Testes unitários cobrindo todos os ViewModels e use cases, usando repositórios fake em memória
  (`MainDispatcherRule` + `runTest`) e exercitando os caminhos de sucesso, erro, vazio e validação.
- Análise estática com ktlint + detekt (ajustados para Compose), que bloqueia o build em violações.
- CI no GitHub Actions executa `ktlintCheck`, `detekt`, `testDebugUnitTest` e `assembleDebug`.

```bash
./gradlew ktlintCheck detekt        # estilo + análise estática
./gradlew :app:testDebugUnitTest    # testes unitários
./gradlew :app:assembleDebug        # build
```

## Segurança e observabilidade

- Token JWT persistido com `EncryptedSharedPreferences` (AES-256, chave no Android Keystore).
- `network-security-config` permite tráfego em texto puro apenas no ambiente de desenvolvimento
  (`10.0.2.2` / `localhost`); TLS obrigatório no restante.
- `exported=false` em todas as activities que não são launcher.
- RBAC e isolamento por conta: recursos de operador exigem o papel OPERADOR e os dados são
  segregados por dono, com validação de participação no backend (403 em acesso cruzado).
- Firebase Crashlytics para relatórios de crash (coleta desabilitada em debug).
- DLP e auditoria: detecção de dados sensíveis e trilha de eventos (ver
  [Trust & Safety / Compliance](#trust--safety--compliance)).

## Convenções

- Commits pequenos e descritivos, agrupados por contexto.
- Pull requests revisados; build de CI verde como pré-requisito de merge.
- Estilo de código garantido por ktlint/detekt.

## Roadmap

- Testes instrumentados / Compose UI e testes de integração com MockWebServer.
- Modularização (`:core`, `:data`, `:domain`, `:feature-*`).
- Design tokens de tema + `strings.xml`; R8 e assinatura de release.
