

CREATE DATABASE automarket
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TEMPLATE = template0;

-- Troca a conexão para o banco automarket (comando do psql).
-- Se for rodar no pgAdmin/DBeaver, execute o CREATE DATABASE acima, troque a conexão
-- para "automarket" manualmente e rode o restante sem esta linha.
\c automarket

-- =====================================================
-- 2. Tabelas
-- =====================================================

CREATE TABLE IF NOT EXISTS public.usuario (
    id          SERIAL PRIMARY KEY,
    nome        VARCHAR(255),
    sobrenome   VARCHAR(255),
    email       VARCHAR(255) NOT NULL,
    senha       VARCHAR(255),
    telefone    VARCHAR(50),
    cpf         VARCHAR(20),
    cidade      VARCHAR(100),
    estado      VARCHAR(2),
    tipo        VARCHAR(50),   -- cliente | vendedor | admin
    bio         VARCHAR(1000),
    CONSTRAINT usuario_email_uk UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.veiculo (
    id            SERIAL PRIMARY KEY,
    marca         VARCHAR(100),
    modelo        VARCHAR(100),
    versao        VARCHAR(150),
    ano           INTEGER,
    km            INTEGER,
    preco         DOUBLE PRECISION,
    cambio        VARCHAR(50),
    combustivel   VARCHAR(50),
    cor           VARCHAR(50),
    potencia      DOUBLE PRECISION,
    cidade        VARCHAR(100),
    estado        VARCHAR(2),
    descricao     VARCHAR(2000),
    telefone      VARCHAR(50),
    criado_em     TIMESTAMP,
    usuario_id    INTEGER,
    CONSTRAINT fk_veiculo_usuario
        FOREIGN KEY (usuario_id) REFERENCES public.usuario (id)
);

CREATE TABLE IF NOT EXISTS public.pedido (
    id               SERIAL PRIMARY KEY,
    veiculo_id       INTEGER NOT NULL,
    usuario_id       INTEGER,
    valor_proposta   DOUBLE PRECISION,
    forma_pagamento  VARCHAR(100),
    mensagem         VARCHAR(2000),
    status           VARCHAR(50),  -- pendente | aprovado | rejeitado
    criado_em        TIMESTAMP,
    CONSTRAINT fk_pedido_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES public.veiculo (id),
    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (usuario_id) REFERENCES public.usuario (id)
);

CREATE TABLE IF NOT EXISTS public.log_auditoria (
    id        SERIAL PRIMARY KEY,
    data      TIMESTAMP,
    usuario   VARCHAR(255),
    email     VARCHAR(255),
    acao      VARCHAR(255),
    detalhes  VARCHAR(4000),
    origem    VARCHAR(50)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_veiculo_usuario ON public.veiculo (usuario_id);
CREATE INDEX IF NOT EXISTS idx_veiculo_marca ON public.veiculo (marca);
CREATE INDEX IF NOT EXISTS idx_pedido_veiculo ON public.pedido (veiculo_id);
CREATE INDEX IF NOT EXISTS idx_pedido_usuario ON public.pedido (usuario_id);
CREATE INDEX IF NOT EXISTS idx_pedido_status ON public.pedido (status);
CREATE INDEX IF NOT EXISTS idx_log_data ON public.log_auditoria (data DESC);


INSERT INTO public.usuario (nome, sobrenome, email, senha, telefone, cpf, cidade, estado, tipo, bio)
VALUES
('Daniel', 'Lucena', 'daniel.lucena@ifg.edu.br', '123456', '(62) 99999-0001', '111.111.111-11', 'Luziânia', 'GO', 'admin', 'Administrador do sistema'),
('Maria', 'Silva', 'maria@automarket.com', '123456', '(11) 98888-0002', '222.222.222-22', 'São Paulo', 'SP', 'vendedor', 'Vendedora de veículos'),
('João', 'Santos', 'joao@automarket.com', '123456', '(21) 97777-0003', '333.333.333-33', 'Rio de Janeiro', 'RJ', 'cliente', 'Cliente interessado em carros')
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.veiculo (marca, modelo, versao, ano, km, preco, cambio, combustivel, cor, potencia, cidade, estado, descricao, telefone, criado_em, usuario_id)
VALUES
('Volkswagen', 'Polo', '200 TSI Comfortline', 2023, 12800, 89900, 'automatico', 'flex', 'Preto', 116, 'São Paulo', 'SP', 'Polo TSI impecável, único dono.', '(11) 99999-0001', NOW(), 2),
('Fiat', 'Argo', 'Drive 1.3', 2022, 28500, 62500, 'manual', 'flex', 'Branco', 109, 'Goiânia', 'GO', 'Argo Drive em ótimo estado.', '(62) 99999-0002', NOW(), 2),
('Toyota', 'Corolla', 'XEi 2.0', 2021, 42000, 118900, 'automatico', 'flex', 'Prata', 177, 'Curitiba', 'PR', 'Corolla XEi completo, revisões em dia.', '(41) 99999-0003', NOW(), 1);

INSERT INTO public.pedido (veiculo_id, usuario_id, valor_proposta, forma_pagamento, mensagem, status, criado_em)
VALUES
(1, 3, 85000, 'financiamento', 'Tenho interesse, posso visitar a loja?', 'pendente', NOW()),
(2, 3, 60000, 'a vista', 'Proposta à vista.', 'pendente', NOW());

INSERT INTO public.log_auditoria (data, usuario, email, acao, detalhes, origem)
VALUES
(NOW(), 'Daniel Lucena', 'daniel.lucena@ifg.edu.br', 'LOGIN', 'Login realizado com sucesso', 'web'),
(NOW(), 'Maria Silva', 'maria@automarket.com', 'CADASTRO_VEICULO', 'Cadastrou Volkswagen Polo 2023', 'web'),
(NOW(), 'João Santos', 'joao@automarket.com', 'PEDIDO_COMPRA', 'Enviou proposta para veículo id=1', 'web');

-- =====================================================
-- 4. Consultas de teste
-- =====================================================
-- SELECT * FROM usuario;
-- SELECT * FROM veiculo;
-- SELECT p.id, v.marca, v.modelo, p.valor_proposta, p.status
--   FROM pedido p JOIN veiculo v ON v.id = p.veiculo_id;
-- SELECT * FROM log_auditoria ORDER BY data DESC;
