-- Database creation script for PostgreSQL
-- Schema for LP3 Commercial Management System

-- Drop tables if they exist to start fresh (in dependency order)
DROP TABLE IF EXISTS item_venda CASCADE;
DROP TABLE IF EXISTS venda CASCADE;
DROP TABLE IF EXISTS estoque CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;

-- Create Table for Cliente
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Table for Produto
CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    preco_venda DECIMAL(10,2) NOT NULL,
    preco_custo DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create Table for Estoque
CREATE TABLE estoque (
    id SERIAL PRIMARY KEY,
    produto_id INTEGER NOT NULL UNIQUE,
    quantidade_disponivel INTEGER NOT NULL DEFAULT 0,
    quantidade_minima INTEGER NOT NULL DEFAULT 0,
    localizacao VARCHAR(100),
    ultima_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE
);

-- Create Table for Venda
CREATE TABLE venda (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    total_bruto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_liquido DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Create Table for ItemVenda
CREATE TABLE item_venda (
    id SERIAL PRIMARY KEY,
    venda_id INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_venda_venda FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_venda_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);
