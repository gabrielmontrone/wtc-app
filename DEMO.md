# WTC — Roteiro de demonstração

Guia prático para ver na tela os recursos da camada de Trust & Safety / Compliance e as demais
funcionalidades. Para a visão de arquitetura e do projeto como um todo, veja o [README](README.md).

## Pré-requisitos (uma vez)

1. Baixe o `wtc-demo.apk` na página de releases e instale no emulador ou no celular:
   https://github.com/gabrielmontrone/wtc-app/releases/latest. No celular, aceite a instalação de
   "fontes desconhecidas" quando pedir.
2. Abra o app. Por padrão ele já usa o backend publicado (`https://wtc-ioxk.onrender.com/`), então
   funciona sem subir mais nada. A primeira requisição pode levar de 30 a 60 segundos enquanto a
   Render "acorda" o servidor; as seguintes são rápidas.
   - Para rodar contra um backend local, suba-o com Docker (`docker compose up --build` no repositório
     https://github.com/gabrielmontrone/wtc) e troque a URL pelo ícone de engrenagem na tela inicial:
     `http://10.0.2.2:8080/` no emulador ou `http://<IP-do-PC>:8080/` no celular. Detalhes no
     [README](README.md#build-e-execução).
3. Faça login. Alguns recursos pedem uma conta OPERADOR (campanhas, por exemplo); outros funcionam
   com qualquer conta.

## Papéis (o menu muda conforme a conta)

- OPERADOR: console completo, com Contatos (CRM), Campanhas, Segmentos e Auditoria.
- CLIENTE: vê apenas a própria conversa ("Minha conversa"); não acessa CRM, campanhas, segmentos
  nem auditoria — e o backend também bloqueia esses acessos com 403.

Os dados são isolados por conta: um operador só enxerga os contatos e conversas que ele mesmo criou.
Para a demonstração, crie contatos novos usando a conta de operador.

## Mapa de navegação

```
OPERADOR: Welcome -> Login -> Home -> Contatos -> (toca um contato) -> Conversas -> (toca uma conversa) -> Mensagens
                                   -> Campanhas / Segmentos
                                   -> Auditoria

CLIENTE:  Welcome -> Login -> Home -> Minha conversa -> Mensagens
```

---

## Recursos e como acessá-los

### 1. "Continuar com Google" (tela de Login)

Logo abaixo de "Entrar" há o botão "Continuar com Google". Ao tocá-lo, abre o seletor de conta
Google e o app entra (ou cria a conta, como CLIENTE no primeiro acesso). Esse recurso depende do
Web Client ID configurado no Firebase, do Google Play Services no aparelho e de o backend ter o
`GOOGLE_CLIENT_ID` definido. No backend publicado essa variável pode não estar configurada — nesse
caso o endpoint responde 503 (serviço indisponível) com uma mensagem clara, em vez de travar, e o app
exibe um erro de login. Para a demonstração, o login por e-mail e senha é o caminho garantido.

### 2. Atalho `/` de campanhas (tela de Mensagens)

A partir da Home, vá em Contatos, toque num contato e depois numa conversa para chegar ao campo
"Responder (use / para campanhas)". Digite `/` (ou `/` seguido de parte do nome de uma campanha) e
toque numa sugestão: o conteúdo da campanha entra no campo. As sugestões só aparecem na conta
OPERADOR, já que campanhas são restritas ao operador.

### 3. Envio de foto (tela de Mensagens)

À esquerda do campo de texto há o botão "+". Toque nele, escolha uma imagem, confira a prévia (com
opção de remover) e toque em Enviar; a foto aparece no chat. O arquivo é enviado por multipart e
guardado no próprio backend (MongoDB), então funciona com o stack local do Docker, sem storage
externo.

### 4. Scan DLP: aviso e selos (tela de Mensagens)

Este é o recurso central da camada de compliance. Digite algo sensível — por exemplo
`meu CPF é 529.982.247-25` ou um cartão `4111 1111 1111 1111` — e toque em Enviar: aparece o aviso
"Dados sensíveis detectados", com as opções de enviar mesmo assim ou cancelar. As mensagens com
dados sensíveis exibem um selo de alerta no balão (CPF, cartão de crédito etc.), em vermelho quando
o risco é alto.

### 5. Trilha de auditoria (tela Auditoria, apenas OPERADOR)

Na Home com conta de operador, abra o card Auditoria. A tela mostra os eventos recentes (login,
envio de mensagem, criação de cliente) e destaca os `SUSPICIOUS_MESSAGE` gerados pelo scan DLP. Para
ver isso funcionando, envie uma mensagem com CPF ou cartão (passo 4) e abra a Auditoria: o evento
aparece no topo.

### 6. Bastidores (sem tela)

- Swagger em português: `http://localhost:8080/swagger-ui.html`.
- O backend carrega o `.env` automaticamente, então dá para rodar localmente sem exportar variáveis
  à mão.

---

## Checklist para gravar um GIF de demonstração

1. Login com Google (3s): toque no botão, escolha a conta e chegue na Home.
2. DLP em ação (8s): abra uma conversa, digite uma mensagem com CPF e cartão, toque em Enviar, mostre
   o diálogo de aviso, escolha enviar mesmo assim e mostre o selo de alerta no balão.
3. Atalho `/` (4s): digite `/` e escolha uma campanha (conta operador).
4. Foto (4s): toque no "+", escolha uma imagem, envie e mostre a foto no chat.
5. Auditoria (5s): volte à Home, abra Auditoria e mostre o `SUSPICIOUS_MESSAGE` no topo.

Dica: grave em torno de 720p, mantenha o emulador no tema escuro e use dados fictícios.
