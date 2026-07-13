# Roteiro de Implementação - Sistema de Gestão Comercial

Este arquivo serve como um checklist de tarefas para os alunos completarem a implementação do sistema nas versões **JDBC** e **Hibernate**.

---

## 1. Configuração do Ambiente e Banco de Dados
- [ ] Subir a instância do banco de dados PostgreSQL usando o Docker Compose:
  ```bash
  docker compose up -d
  ```
- [ ] Verificar se as tabelas foram criadas corretamente executando o script `schema.sql` ou inspecionando o banco de dados `lp3_comercial`.
- [ ] Testar a compilação do projeto com o comando Maven para garantir que tudo está configurado corretamente:
  ```bash
  # Se o maven estiver instalado localmente
  mvn clean compile
  ```

---

## 2. Implementação da Versão JDBC
Nesta etapa, você deve criar as classes concretas para os DAOs utilizando JDBC puro (e.g., `ClienteJdbcDAO`, `ProdutoJdbcDAO`, etc.).

### 2.1 Conexão com o Banco de Dados
- [ ] Criar uma classe utilitária de conexão (e.g., `ConnectionFactory`) que leia as propriedades do arquivo `src/main/resources/database.properties` e retorne um objeto `java.sql.Connection`.

### 2.2 Repositórios (DAOs) com JDBC
Implemente as seguintes operações utilizando comandos SQL puros e `PreparedStatement`:
- [ ] **ClienteDAO**:
  - [ ] `inserir`: Executar `INSERT INTO cliente (...)`
  - [ ] `atualizar`: Executar `UPDATE cliente SET ... WHERE id = ?`
  - [ ] `buscarPorId`: Buscar cliente e mapear o `ResultSet` manualmente para o objeto `Cliente`.
  - [ ] `listarTodos`: Listar e mapear todos os clientes.
  - [ ] `listarPorStatus`: Filtrar clientes pelo campo `ativo`.
  - [ ] `existeCpf`: Verificar se determinado CPF já está cadastrado no banco.
  - [ ] `deletar`: Executar `DELETE FROM cliente WHERE id = ?` (ou inativação lógica se preferir).
- [ ] **ProdutoDAO**:
  - [ ] `inserir`, `atualizar`, `buscarPorId`, `listarTodos`, `listarPorStatus`, `listarPorCategoria`, `deletar`.
- [ ] **EstoqueDAO**:
  - [ ] `inserir`, `atualizar`.
  - [ ] `buscarPorProduto`: Obter o estoque de um produto específico (necessário para validações de saídas).
  - [ ] `listarTodos`, `listarAbaixoDoMinimo`.
- [ ] **VendaDAO**:
  - [ ] `inserir`: Salvar a venda na tabela `venda`, obter o ID gerado, e salvar todos os itens da venda na tabela `item_venda`.
  - [ ] `atualizarStatus`: Modificar o status da venda (ex: de ABERTA para CONFIRMADA ou CANCELADA).
  - [ ] `buscarPorId`: Buscar a venda e carregar seus respectivos itens (`ItemVenda`) associando-os à venda.
  - [ ] `listarPorCliente`, `listarPorPeriodo`, `listarPorStatus`.

### 2.3 Controle Transacional (JDBC)
- [ ] Garantir que a gravação da venda e a baixa de estoque na confirmação da venda ocorram na mesma transação JDBC (`connection.setAutoCommit(false)`), fazendo `commit()` em caso de sucesso ou `rollback()` em caso de erro.
- [ ] Garantir o mesmo comportamento transacional durante o cancelamento da venda (atualizar status da venda e restaurar quantidade no estoque).

---

## 3. Implementação da Versão Hibernate (JPA)
Nesta etapa, você deve mapear as entidades usando anotações Jakarta/JPA e criar as classes concretas para os DAOs utilizando o Hibernate APIs.

### 3.1 Mapeamento das Entidades (Anotações JPA)
Adicionar as anotações necessárias no pacote `model/` (e.g., `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@OneToOne`, `@ManyToOne`, `@OneToMany`):
- [ ] **Cliente.java**: Mapear para a tabela `cliente`.
- [ ] **Produto.java**: Mapear para a tabela `produto`.
- [ ] **Estoque.java**: Mapear para a tabela `estoque`, criando o relacionamento `@OneToOne` com `Produto`.
- [ ] **Venda.java**: Mapear para a tabela `venda`, criando o relacionamento `@ManyToOne` com `Cliente` e `@OneToMany` (com cascade) com `ItemVenda`.
- [ ] **ItemVenda.java**: Mapear para a tabela `item_venda`, criando os relacionamentos com `Venda` e `Produto`.

### 3.2 Habilitação no Hibernate Configuration
- [ ] Habilitar as classes mapeadas no arquivo `src/main/resources/hibernate.cfg.xml` descomentando as linhas `<mapping class="..."/>`.

### 3.3 Repositórios (DAOs) com Hibernate
Implementar os DAOs concretos (e.g., `ClienteHibernateDAO`) usando `EntityManager` ou `SessionFactory`/`Session`:
- [ ] Criar classe utilitária para gerenciamento do ciclo de vida das sessões do Hibernate (e.g., `HibernateUtil`).
- [ ] Implementar os métodos CRUD usando `session.persist()`, `session.merge()`, `session.find()`, `session.remove()`.
- [ ] Implementar consultas orientadas a objetos utilizando **HQL / JPQL** (como listar todos, filtrar por categoria, listar estoque abaixo do mínimo).
- [ ] Garantir o uso adequado de transações Hibernate (`session.beginTransaction()`, `transaction.commit()`, `transaction.rollback()`) no fluxo de venda e cancelamento.

---

## 4. Conclusão da Interface de Execução (Menu Console)
Finalizar o fluxo da aplicação dentro de `Main.java`.

- [ ] Implementar o loop do menu no método `main`:
  - [ ] Opções para gerenciar **Clientes** (cadastrar, listar todos, ativar/inativar).
  - [ ] Opções para gerenciar **Produtos e Estoque** (cadastrar produto, registrar entrada, listar abaixo do mínimo).
  - [ ] Opções para realizar uma **Venda** (abrir venda para cliente, adicionar múltiplos itens com quantidade, aplicar desconto opcional, confirmar venda).
  - [ ] Opção para **Cancelar** uma venda existente.
  - [ ] Opções de consulta (buscar vendas por período, cliente ou status).
- [ ] Integrar o menu com as instâncias dos serviços (`ClienteService`, `ProdutoService`, etc.), injetando a persistência escolhida (via JDBC ou Hibernate).
- [ ] Tratar adequadamente todas as exceções lançadas pela camada de serviço (`NegocioException` e `EstoqueInsuficienteException`), exibindo mensagens claras e amigáveis para o usuário no terminal.
