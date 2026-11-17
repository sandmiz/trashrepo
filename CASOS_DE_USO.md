# AutoMarket — Casos de Uso (PostgreSQL)

## Banco de dados

```
Host:     localhost
Porta:    5432
Banco:    automarket
Usuário:  postgres
Senha:    postgres
```

1. Crie o banco e as tabelas:
   ```bash
   psql -U postgres -f banco_completo.sql
   ```
   (ou execute o conteúdo de `banco_completo.sql` no pgAdmin / DBeaver)

2. Confirme em `src/main/resources/application.properties`:
   ```
   quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/automarket
   quarkus.datasource.username=postgres
   quarkus.datasource.password=postgres
   ```

## Usuários de teste (já inseridos pelo script SQL)

| Tipo      | E-mail                     | Senha  |
|-----------|----------------------------|--------|
| **Admin** | daniel.lucena@ifg.edu.br   | 123456 |
| Vendedor  | maria@automarket.com       | 123456 |
| Cliente   | joao@automarket.com        | 123456 |

Cadastro pelo site (`/login` → aba Cadastro) grava no banco com tipo `cliente` por padrão.

## Rotas

| URL               | Função                                      |
|-------------------|---------------------------------------------|
| `/`               | Home                                        |
| `/login`          | Login / cadastro (PostgreSQL)               |
| `/busca`          | Busca de veículos (API `/api/veiculos`)     |
| `/cadastro-veiculo` | Publicar anúncio no banco                 |
| `/pedido-compra`  | Enviar proposta de compra                   |
| `/admin-pedidos`  | **Admin**: listar e aprovar/rejeitar pedidos|
| `/perfil`         | Perfil do usuário logado                    |
| `/logs`           | **Admin**: auditoria no banco + na tela     |

## Como rodar

```bash
./mvnw quarkus:dev
```

Acesse: http://localhost:8080

## Fluxo de demonstração

1. Login como **admin**: `daniel.lucena@ifg.edu.br` / `123456`
2. Login como **cliente**: `joao@automarket.com` / `123456` → envie pedido em `/pedido-compra`
3. Volte como admin em `/admin-pedidos` → aprove ou rejeite
4. Confira em `/logs` os registros gravados na tabela `log_auditoria`

## Arquitetura

- **Entity** → representação da tabela em Java (`model/entity/`)
- **DAO** → acesso às tabelas via JPA/EntityManager (`model/dao/`)
- **BO** → regras de negócio (`model/bo/`)
- **Controller** → endpoints REST + páginas Qute (`controller/`)

Dados de veículos, pedidos, usuários e logs ficam no **PostgreSQL**.  
A sessão do usuário logado fica apenas no `localStorage` do navegador (id, nome, e-mail, tipo) para identificar quem está autenticado nas próximas requisições.
