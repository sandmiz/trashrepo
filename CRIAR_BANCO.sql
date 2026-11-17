-- ============================================================
-- Execute este script CONECTADO ao banco "postgres" (padrão)
-- NÃO execute conectado em "automarket" (ainda não existe)
--
--   psql -U postgres -d postgres -f CRIAR_BANCO.sql
--
-- Ou no pgAdmin: Query Tool no banco "postgres" -> cole e execute.
-- Se o banco já existir, o PostgreSQL avisa "already exists": pode ignorar.
-- As tabelas e os dados iniciais são criados pela própria aplicação
-- (Hibernate + DadosIniciais.java) na primeira subida do Quarkus.
-- ============================================================

CREATE DATABASE automarket OWNER postgres ENCODING 'UTF8';
