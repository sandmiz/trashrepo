-- =====================================================
-- RODE NO CONSOLE DO INTELLIJ (Database)
-- Passo 1: conecte no banco "postgres"
-- Passo 2: execute só o CREATE DATABASE
-- Passo 3: mude a conexão para "automarket"
-- Passo 4: execute o resto (tabelas + dados)
-- =====================================================

CREATE DATABASE automarket;

-- ===== A PARTIR DAQUI: conectar em automarket =====

DROP TABLE IF EXISTS pedido CASCADE;
DROP TABLE IF EXISTS veiculo CASCADE;
DROP TABLE IF EXISTS log_auditoria CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

CREATE TABLE usuario (
    id          SERIAL PRIMARY KEY,
    nome        VARCHAR(255),
    sobrenome   VARCHAR(255),
    email       VARCHAR(255) NOT NULL,
    senha       VARCHAR(255),
    telefone    VARCHAR(50),
    cpf         VARCHAR(20),
    cidade      VARCHAR(100),
    estado      VARCHAR(2),
    tipo        VARCHAR(50),
    bio         VARCHAR(1000),
    CONSTRAINT usuario_email_uk UNIQUE (email)
);

CREATE TABLE veiculo (
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
    CONSTRAINT fk_veiculo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE pedido (
    id               SERIAL PRIMARY KEY,
    veiculo_id       INTEGER NOT NULL,
    usuario_id       INTEGER,
    valor_proposta   DOUBLE PRECISION,
    forma_pagamento  VARCHAR(100),
    mensagem         VARCHAR(2000),
    status           VARCHAR(50),
    criado_em        TIMESTAMP,
    CONSTRAINT fk_pedido_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculo (id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE log_auditoria (
    id        SERIAL PRIMARY KEY,
    data      TIMESTAMP,
    usuario   VARCHAR(255),
    email     VARCHAR(255),
    acao      VARCHAR(255),
    detalhes  VARCHAR(4000),
    origem    VARCHAR(50)
);

CREATE INDEX idx_veiculo_usuario ON veiculo (usuario_id);
CREATE INDEX idx_pedido_veiculo ON pedido (veiculo_id);
CREATE INDEX idx_pedido_usuario ON pedido (usuario_id);
CREATE INDEX idx_pedido_status ON pedido (status);

INSERT INTO usuario (nome, sobrenome, email, senha, telefone, cpf, cidade, estado, tipo, bio) VALUES
('Daniel', 'Lucena', 'daniel.lucena@ifg.edu.br', '123456', '(62) 99999-0001', '111.111.111-11', 'Luziânia', 'GO', 'admin', 'Administrador do sistema'),
('Maria', 'Silva', 'maria@automarket.com', '123456', '(11) 98888-0002', '222.222.222-22', 'São Paulo', 'SP', 'vendedor', 'Vendedora de veículos'),
('João', 'Santos', 'joao@automarket.com', '123456', '(21) 97777-0003', '333.333.333-33', 'Rio de Janeiro', 'RJ', 'cliente', 'Cliente interessado em carros');

INSERT INTO veiculo (marca, modelo, versao, ano, km, preco, cambio, combustivel, cor, potencia, cidade, estado, descricao, telefone, criado_em, usuario_id) VALUES
('Volkswagen', 'Polo', '200 TSI Comfortline', 2023, 12800, 89900, 'automatico', 'flex', 'Preto', 116, 'São Paulo', 'SP', 'Polo TSI impecável, único dono.', '(11) 99999-0001', NOW(), 2),
('Fiat', 'Argo', 'Drive 1.3', 2022, 28500, 62500, 'manual', 'flex', 'Branco', 109, 'Goiânia', 'GO', 'Argo Drive em ótimo estado.', '(62) 99999-0002', NOW(), 2),
('Toyota', 'Corolla', 'XEi 2.0', 2021, 42000, 118900, 'automatico', 'flex', 'Prata', 177, 'Curitiba', 'PR', 'Corolla XEi completo, revisões em dia.', '(41) 99999-0003', NOW(), 1);

INSERT INTO pedido (veiculo_id, usuario_id, valor_proposta, forma_pagamento, mensagem, status, criado_em) VALUES
(1, 3, 85000, 'financiamento', 'Tenho interesse, posso visitar a loja?', 'pendente', NOW()),
(2, 3, 60000, 'a vista', 'Proposta à vista.', 'pendente', NOW());

INSERT INTO log_auditoria (data, usuario, email, acao, detalhes, origem) VALUES
(NOW(), 'Daniel Lucena', 'daniel.lucena@ifg.edu.br', 'LOGIN', 'Login realizado com sucesso', 'web'),
(NOW(), 'Maria Silva', 'maria@automarket.com', 'CADASTRO_VEICULO', 'Cadastrou Volkswagen Polo 2023', 'web'),
(NOW(), 'João Santos', 'joao@automarket.com', 'PEDIDO_COMPRA', 'Enviou proposta para veículo id=1', 'web');

-- Conferir
SELECT 'usuario' AS tabela, COUNT(*) AS qtd FROM usuario
UNION ALL SELECT 'veiculo', COUNT(*) FROM veiculo
UNION ALL SELECT 'pedido', COUNT(*) FROM pedido
UNION ALL SELECT 'log_auditoria', COUNT(*) FROM log_auditoria;
