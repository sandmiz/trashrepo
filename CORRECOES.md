# Correções feitas no AutoMarket

## O que impedia o sistema de funcionar
1. **Login/cadastro não funcionavam** — `auth.js` redeclarava `getUsuarioLogado` (já existe em `data.js` como `const`), dando SyntaxError e derrubando o script inteiro.
2. **Home e busca quebravam com usuário logado** — `main.js`/`busca.js` chamavam `getUsuarios()`/`salvarUsuarios()`, que nunca existiram. Favoritos agora usam `getFavoritos()`/`salvarFavoritos()` (em `data.js`).
3. **Banco sem usuários** — com `schema-management=update` o Hibernate não executa o `import.sql`. Criado `DadosIniciais.java` (roda só se a tabela `usuario` estiver vazia); `import.sql` removido.
4. **`mvn package` falhava** — os testes `GreetingResource*` chamavam `/hello`, que não existe (e estavam no pacote errado, `luziania`). Substituídos por `PedidoBOTest` (teste unitário, sem banco).
5. **Pedidos e veículos sem dono / "meus pedidos" mostrando os de todos** — `getUsuarioLogado` não existia nas páginas que só carregam `app-store.js`. Agora existe, e as telas exigem login para gravar.
6. **Perfil só gravava no navegador** — `perfil.js` agora usa `/perfil/buscar` e `/perfil/salvar`.
7. **Envio duplicado** — pedido e anúncio eram enviados 2x (`onsubmit` no HTML + `addEventListener` no JS). O listener extra foi removido (idem filtros do admin).

## Segurança / dados
- `Usuario.senha` agora é `@JsonbTransient`: antes a senha ia no JSON de `/admin-pedidos/listar`, `/pedido-compra/listar` e `/perfil/buscar`.
- `PerfilBO.salvar` alterava a entidade antes de validar a senha atual (o commit gravava mesmo com "Senha atual incorreta"); agora valida antes e checa e-mail duplicado.
- `/pedido-compra/listar?usuarioId=` filtra no servidor (antes baixava os pedidos de todos).
- Texto vindo do banco (marca, descrição, mensagem, nome...) é escapado antes de ir para `innerHTML` (XSS).

## Outros
- `PedidoBO` tinha construtor privado + `createPedidoBO()` (conflita com bean `@RequestScoped`) — removidos.
- Tela admin: contadores, busca, modal de rejeição (`fecharModal`/`confirmarRejeicao` não existiam) e motivo gravado no log.
- Mensagens de erro do backend (string JSON) agora aparecem nas telas (`extrairErro`).
- `corHex` ausente (cor "undefined" nos cards), botão `nav-login-btn` ausente no index, WhatsApp sem injetar texto no `onclick`.
- Log de `CADASTRO_VEICULO` agora registra quem cadastrou.
- `banco_completo.sql` criava as tabelas no banco `postgres` (faltava `\c automarket`); `CRIAR_BANCO.sql` simplificado.
- Removidos arquivos duplicados `*_1.*` e `application_1.properties`.

## Não alterado (decisão sua)
- Senhas em texto puro no banco (usar hash, ex.: BCrypt).
- Qualquer pessoa pode se cadastrar/editar o perfil como `admin`, e `/admin-pedidos` e `/logs` não têm controle de acesso no servidor.
