--
-- Tabela: categorias
-- Mapeada da classe Categoria.java
--
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID da categoria, auto-incrementado e chave primária
    nome VARCHAR(255)                      -- Nome da categoria
);

--
-- Tabela: clientes
-- Mapeada da classe Cliente.java
--
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID do cliente, auto-incrementado e chave primária
    nome VARCHAR(255),                    -- Nome do cliente
    telefone VARCHAR(255),                -- Telefone do cliente
    email VARCHAR(255),                   -- Email do cliente
    criado_em DATETIME                    -- Data e hora de criação do registro do cliente
);

--
-- Tabela: usuarios
-- Mapeada da classe Usuario.java
--
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID do usuário, auto-incrementado e chave primária
    nome VARCHAR(255),                    -- Nome do usuário
    email VARCHAR(255),                   -- Email do usuário
    senha VARCHAR(255),                   -- Senha do usuário
    tipo VARCHAR(255),                    -- Tipo de usuário (Enum: GERENTE, VENDEDOR)
    ativo BOOLEAN                         -- Status de atividade do usuário (true/false)
);

--
-- Tabela: produtos
-- Mapeada da classe Produto.java
--
CREATE TABLE produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- ID do produto, auto-incrementado e chave primária
    quantidade_estoque INT,                  -- Quantidade de produtos em estoque
    nome VARCHAR(255),                       -- Nome do produto
    descricao TEXT,                          -- Descrição detalhada do produto (TEXT para textos longos)
    preco DECIMAL(10, 2),                    -- Preço do produto (10 dígitos no total, 2 após a vírgula)
    codigo_barras VARCHAR(255) UNIQUE,       -- Código de barras (deve ser único)
    categoria_id BIGINT,                     -- Chave estrangeira para a tabela 'categorias'
    status BOOLEAN,                          -- Status do produto (ex: ativo/inativo)
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) -- Definição da chave estrangeira
);

--
-- Tabela: movimentacoes_estoque
-- Mapeada da classe MovimentacaoEstoque.java
--
CREATE TABLE movimentacoes_estoque (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- ID da movimentação, auto-incrementado e chave primária
    produto_id BIGINT NOT NULL,                 -- Chave estrangeira para a tabela 'produtos' (não nulo)
    tipo VARCHAR(255) NOT NULL,                 -- Tipo de movimentação (Enum: ENTRADA, SAIDA)
    quantidade INT NOT NULL,                    -- Quantidade movimentada (não nulo)
    data DATETIME NOT NULL,                     -- Data e hora da movimentação (não nulo)
    usuario_id BIGINT NOT NULL,                 -- Chave estrangeira para a tabela 'usuarios' (não nulo)
    FOREIGN KEY (produto_id) REFERENCES produtos(id), -- Definição da chave estrangeira
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)  -- Definição da chave estrangeira
);

--
-- Tabela: vendas
-- Mapeada da classe Venda.java
--
CREATE TABLE vendas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- ID da venda, auto-incrementado e chave primária
    cliente_id BIGINT NOT NULL,                 -- Chave estrangeira para a tabela 'clientes' (não nulo)
    usuario_id BIGINT NOT NULL,                 -- Chave estrangeira para a tabela 'usuarios' (vendedor, não nulo)
    data_venda DATETIME NOT NULL,               -- Data e hora da venda (não nulo)
    valor_total DECIMAL(10, 2) NOT NULL,        -- Valor total da venda (10 dígitos, 2 após a vírgula, não nulo)
    forma_pagamento VARCHAR(255) NOT NULL,      -- Forma de pagamento (não nulo)
    valor_recebido DECIMAL(10, 2),              -- Valor recebido (pode ser nulo, para casos como pagamento a prazo)
    FOREIGN KEY (cliente_id) REFERENCES clientes(id), -- Definição da chave estrangeira
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)  -- Definição da chave estrangeira
);

--
-- Tabela: itens_venda
-- Mapeada da classe ItemVenda.java
--
CREATE TABLE itens_venda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,       -- ID do item de venda, auto-incrementado e chave primária
    venda_id BIGINT NOT NULL,                   -- Chave estrangeira para a tabela 'vendas' (não nulo)
    produto_id BIGINT NOT NULL,                 -- Chave estrangeira para a tabela 'produtos' (não nulo)
    quantidade INT NOT NULL,                    -- Quantidade do produto no item de venda (não nulo)
    preco_unitario DECIMAL(10, 2) NOT NULL,     -- Preço unitário do produto no momento da venda (não nulo)
    sub_total DECIMAL(10, 2) NOT NULL,          -- Subtotal do item de venda (quantidade * preco_unitario, não nulo)
    FOREIGN KEY (venda_id) REFERENCES vendas(id),     -- Definição da chave estrangeira
    FOREIGN KEY (produto_id) REFERENCES produtos(id)  -- Definição da chave estrangeira
);

--
-- Tabela: relatorios
-- Mapeada da classe Relatorio.java
--
CREATE TABLE relatorios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,          -- ID do relatório, auto-incrementado e chave primária
    nome_relatorio VARCHAR(100) NOT NULL,          -- Nome do relatório (não nulo, até 100 caracteres)
    tipo_relatorio VARCHAR(50) NOT NULL,           -- Tipo do relatório (Enum: VENDAS_POR_PERIODO, ESTOQUE_ATUAL, etc., não nulo)
    data_geracao DATETIME NOT NULL,                -- Data e hora da geração do relatório (não nulo)
    periodo_inicial DATETIME,                      -- Início do período do relatório (pode ser nulo)
    periodo_final DATETIME,                        -- Fim do período do relatório (pode ser nulo)
    parametros_filtros TEXT,                       -- Parâmetros de filtro usados para gerar o relatório (TEXT para JSON)
    dados_sumarizados TEXT,                        -- Dados sumarizados do relatório (TEXT para JSON)
    usuario_id BIGINT,                             -- Chave estrangeira para o usuário que gerou o relatório (pode ser nulo)
    status VARCHAR(20) NOT NULL,                   -- Status do relatório (Enum: GERADO, PROCESSANDO, etc., não nulo)
    descricao TEXT,                                -- Descrição do relatório
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) -- Definição da chave estrangeira
);