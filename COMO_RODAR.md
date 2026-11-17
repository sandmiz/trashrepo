# AutoMarket — Como corrigir o erro e rodar

## Erro que você viu

```
FATAL: banco de dados "automarket" não existe
```

O Quarkus conecta em `jdbc:postgresql://localhost:5432/automarket`,  
mas o banco **ainda não foi criado** no PostgreSQL.

Há também: `Port already bound: 8080` → outra aplicação (ou outra instância do Quarkus) está usando a porta 8080.

---

## Passo 1 — Criar o banco (obrigatório)

### Opção A — Script (mais fácil no Windows)

1. Abra a pasta do projeto no Explorer  
2. Dê **dois cliques** em `criar_banco.bat`  
3. Digite a senha do usuário `postgres` quando pedir  

### Opção B — Linha de comando

Abra **PowerShell** ou **CMD**:

```bat
"C:\Program Files\PostgreSQL\14\bin\psql.exe" -U postgres -d postgres -c "CREATE DATABASE automarket OWNER postgres ENCODING 'UTF8';"
```

(Ajuste `14` para a versão instalada: 15, 16, 17…)

### Opção C — pgAdmin

1. Abra o **pgAdmin**  
2. Conecte no servidor local  
3. Clique com o botão direito em **Databases** → **Create** → **Database**  
4. Name: `automarket` → Save  

---

## Passo 2 — Liberar a porta 8080 (se necessário)

No PowerShell:

```powershell
netstat -ano | findstr :8080
```

Anote o PID da última coluna e mate o processo:

```powershell
taskkill /PID NUMERO_DO_PID /F
```

Ou no IntelliJ: pare todas as execuções anteriores (botão Stop).

---

## Passo 3 — Subir a aplicação

No IntelliJ, na pasta do projeto (`tiii-pworii`):

```
quarkus:dev
```

Ou no terminal:

```bat
mvnw.cmd quarkus:dev
```

Na **primeira subida**, o Hibernate cria as tabelas (`schema-management.strategy=update`)  
e o `DadosIniciais.java` carrega usuários, veículos, pedidos e logs.

---

## Usuários de teste

| Tipo    | E-mail                   | Senha  |
|---------|--------------------------|--------|
| Admin   | daniel.lucena@ifg.edu.br | 123456 |
| Vendedor| maria@automarket.com     | 123456 |
| Cliente | joao@automarket.com      | 123456 |

Site: http://localhost:8080

---

## Conferência rápida

```bat
psql -U postgres -d automarket -c "\dt"
psql -U postgres -d automarket -c "SELECT id, email, tipo FROM usuario;"
```

Se listar as tabelas e os 3 usuários, está ok.
