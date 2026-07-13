# Sistema de Gestão Comercial (Template Base - JDBC vs. Hibernate)

Este projeto é uma base de código estruturada em Java destinada a estudantes de programação para a implementação prática de persistência de dados relacional. O objetivo principal é desenvolver e comparar duas abordagens técnicas distintas para persistência no banco de dados PostgreSQL:
1. **JDBC (Java Database Connectivity)**: Persistência manual utilizando instruções SQL puras, transações explícitas e mapeamento relacional direto de registros para objetos.
2. **Hibernate / JPA (Java Persistence API)**: Mapeamento Objeto-Relacional (ORM) automatizado usando anotações JPA, gerenciamento de ciclo de vida de entidades e HQL/JPQL.

---

## 🛠️ Tecnologias Necessárias
*   **Java JDK 21** ou superior (testado com OpenJDK 26).
*   **Apache Maven 3.8+** (opcional, para gerenciamento de dependências e compilação rápida).
*   **Docker e Docker Compose** (para executar o banco de dados PostgreSQL em contêiner).

---

## 📂 Estrutura do Projeto
A estrutura recomendada do projeto segue a divisão clássica em camadas:
```text
src/main/
├── java/
│   ├── exception/         # Exceções de negócio (NegocioException, EstoqueInsuficienteException)
│   ├── model/             # Classes de domínio/entidades (Cliente, Produto, Estoque, Venda, ItemVenda, StatusVenda)
│   ├── dao/               # Interfaces com os contratos de persistência (ClienteDAO, ProdutoDAO, etc.)
│   ├── service/           # Regras de negócio e orquestração (ClienteService, VendaService, etc.)
│   └── Main.java          # Classe executável principal (Ponto de entrada)
└── resources/
    ├── database.properties # Configurações da conexão JDBC
    └── hibernate.cfg.xml   # Configurações do Hibernate/SessionFactory
```

---

## 🚀 Como Executar o Projeto

### 1. Iniciar o Banco de Dados (PostgreSQL)
A base do projeto inclui um arquivo `docker-compose.yml` para criar e configurar o contêiner do PostgreSQL na porta `5432` juntamente com a execução automática do script SQL inicial `schema.sql`.

No diretório raiz do projeto, execute:
```bash
docker compose up -d
```

### 2. Compilar o Projeto
Caso possua o Maven instalado:
```bash
mvn clean compile
```

Caso não possua o Maven e queira testar a compilação diretamente pelo compilador do Java (`javac`):
```bash
mkdir -p target/classes && javac -d target/classes $(find src/main/java -name "*.java")
```

### 3. Rodar a Aplicação
Com o Maven:
```bash
mvn exec:java -Dexec.mainClass="Main"
```

Diretamente com o Java (`java`):
```bash
java -cp target/classes Main
```

Você deverá ver a seguinte saída no console indicando que a compilação e execução inicial foram bem-sucedidas:
```text
SISTEMA DE GESTÃO COMERCIAL
Execução iniciada.
```

---

## 📝 Tarefas Pendentes para Conclusão
Para concluir a avaliação, os seguintes itens devem ser implementados (veja o arquivo [TODO.md](TODO.md) para detalhes):
1. **Conexão de Banco**: Criar uma fábrica de conexões JDBC usando as propriedades de `database.properties`.
2. **Implementações JDBC**: Criar as classes do pacote `dao` que implementem as interfaces de repositório (`ClienteDAO`, `ProdutoDAO`, etc.) usando JDBC.
3. **Mapeamento JPA**: Adicionar as anotações do JPA nas classes do pacote `model/`.
4. **Implementações Hibernate**: Criar classes correspondentes que implementem as interfaces DAO usando Hibernate/JPA.
5. **Menu Interativo**: Finalizar o menu de console dentro da classe `Main.java` conectando-o com os serviços correspondentes.
