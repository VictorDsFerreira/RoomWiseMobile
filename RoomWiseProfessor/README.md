# RoomWise Professor (Android nativo)

Aplicação **Kotlin + Jetpack Compose** para o professor ver a **grade de horários** das turmas em que é responsável (`cod_prof`), usando o **mesmo Supabase** e o mesmo RPC `validar_login` que o front-end Angular.

## Abrir no Android Studio

1. **File → Open** e escolha a pasta `android/RoomWiseProfessor` (não a raiz do monorepo Angular, a menos que prefira importar só este módulo).
2. Deixe o Gradle sincronizar. Se pedir JDK 17, aceite (Android Studio embute um JDK compatível).
3. Copie `local.properties.example` para `local.properties` na mesma pasta e preencha:
   - `sdk.dir` — o Android Studio costuma gerar automaticamente ao abrir o projeto.
   - `SUPABASE_URL` e `SUPABASE_ANON_KEY` — os mesmos valores usados em `src/environments/environment.ts` do projeto web (ou o seu `environment.local.ts`).

## Executar

- Ligue um dispositivo ou emulador Android (API 26+).
- Run na configuração `app`.

## O que o app faz

- **Login**: chama `POST /rest/v1/rpc/validar_login` com `p_usuario` e `p_senha`.
- Só permite continuar se `tipo == "professor"` (administradores recebem mensagem para usar o sistema web).
- **Grade**: lista turmas com `cod_prof = usuario_id`, depois carrega `aula` com `cod_turma=in.(...)` e o mesmo `select` embutido que o Angular (`turma`, `professor`, `disciplina`, `categoria`, `sala`).
- Sessão guardada em **DataStore** (reabre direto na grade se já estiver logado).

## Próximos passos sugeridos

- **Pull-to-refresh** na grade (já existe `refresh()` no ViewModel).
- **Ícones adaptativos** e nome da app nas stores.
- **Políticas RLS** no Supabase para que só o professor leia as próprias linhas mesmo via REST (defesa em profundidade).
- **Séries** e exportação ICS (hoje só a lista por dia, como MVP).

## Estrutura

- `data/remote` — OkHttp + PostgREST (sem SDK Supabase Kotlin, para menos dependência de versão).
- `ui/login`, `ui/grade` — Compose + ViewModels.
- `MainActivity` — `NavHost` entre login e grade.
