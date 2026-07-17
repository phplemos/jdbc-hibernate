
import dao.hibernate.ClienteDAOHibernate;
import dao.hibernate.EstoqueDAOHibernate;
import dao.hibernate.ProdutoDAOHibernate;
import dao.hibernate.HibernateUtil;
import dao.hibernate.VendaDAOHibernate;
import exception.NegocioException;
import model.Cliente;
import model.Estoque;
import model.Produto;
import model.StatusVenda;
import model.Venda;
import service.ClienteService;
import service.EstoqueService;
import service.ProdutoService;
import service.VendaService;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
 
/**
 * Ponto de entrada da aplicação (versão JDBC).
 */
public class MainHibernate {
 
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
 
    private static final ClienteService clienteService = new ClienteService(new ClienteDAOHibernate());
    private static final ProdutoService produtoService = new ProdutoService(new ProdutoDAOHibernate(), new EstoqueDAOHibernate());
    private static final EstoqueService estoqueService = new EstoqueService(new EstoqueDAOHibernate());
    private static final VendaService vendaService = new VendaService(new VendaDAOHibernate(), estoqueService);
 
    public static void main(String[] args) {
        System.out.println("SISTEMA DE GESTAO COMERCIAL (versao Hibernate)");
        boolean continuar = true;
 
        while (continuar) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opcao: ");
 
            try {
                switch (opcao) {
                    case 1 -> cadastrarCliente();
                    case 2 -> listarClientes();
                    case 3 -> cadastrarProduto();
                    case 4 -> listarProdutos();
                    case 5 -> registrarEntradaEstoque();
                    case 6 -> realizarVenda();
                    case 7 -> cancelarVenda();
                    case 8 -> listarVendasPorCliente();
                    case 9 -> listarVendasPorStatus();
                    case 10 -> listarVendasPorPeriodo();
                    case 11 -> listarEstoque();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (NegocioException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
 
        HibernateUtil.shutdown();
        System.out.println("Encerrando o sistema.");
    }
 
    private static void exibirMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1 - Cadastrar cliente");
        System.out.println("2 - Listar clientes");
        System.out.println("3 - Cadastrar produto");
        System.out.println("4 - Listar produtos");
        System.out.println("5 - Registrar entrada de estoque");
        System.out.println("6 - Realizar venda");
        System.out.println("7 - Cancelar venda");
        System.out.println("8 - Listar vendas por cliente");
        System.out.println("9 - Listar vendas por status");
        System.out.println("10 - Listar vendas por periodo");
        System.out.println("11 - Listar estoque");
        System.out.println("0 - Sair");
    }
 
    private static void cadastrarCliente() {
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF (somente numeros, 11 digitos): ");
        String email = lerTexto("Email: ");
        String telefone = lerTexto("Telefone: ");
 
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        clienteService.cadastrar(cliente);
        System.out.println("Cliente cadastrado com ID " + cliente.getId());
    }
 
    private static void listarClientes() {
        List<Cliente> clientes = clienteService.listarTodos();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        clientes.forEach(System.out::println);
    }
 
    private static void cadastrarProduto() {
        String nome = lerTexto("Nome: ");
        String descricao = lerTexto("Descricao: ");
        double precoVenda = lerDouble("Preco de venda: ");
        double precoCusto = lerDouble("Preco de custo: ");
        String categoria = lerTexto("Categoria: ");
        int quantidadeInicial = lerInteiro("Quantidade inicial em estoque: ");
        int quantidadeMinima = lerInteiro("Quantidade minima em estoque: ");
        String localizacao = lerTexto("Localizacao no estoque: ");
 
        Produto produto = new Produto(nome, descricao, precoVenda, precoCusto, categoria);
        produtoService.cadastrar(produto, quantidadeInicial, quantidadeMinima, localizacao);
        System.out.println("Produto cadastrado com ID " + produto.getId());
    }
 
    private static void listarProdutos() {
        List<Produto> produtos = produtoService.listarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        produtos.forEach(System.out::println);
    }
 
    private static void registrarEntradaEstoque() {
        int produtoId = lerInteiro("ID do produto: ");
        int quantidade = lerInteiro("Quantidade a adicionar: ");
        estoqueService.registrarEntrada(produtoId, quantidade);
        System.out.println("Entrada registrada com sucesso.");
    }
 
    private static void realizarVenda() {
        int clienteId = lerInteiro("ID do cliente: ");
        Cliente cliente = clienteService.buscarPorId(clienteId);
 
        Venda venda = vendaService.abrirVenda(cliente);
 
        boolean adicionandoItens = true;
        while (adicionandoItens) {
            int produtoId = lerInteiro("ID do produto (0 para finalizar itens): ");
            if (produtoId == 0) {
                adicionandoItens = false;
                continue;
            }
            Produto produto = produtoService.buscarPorId(produtoId);
            int quantidade = lerInteiro("Quantidade: ");
            vendaService.adicionarItem(venda, produto, quantidade);
            System.out.println("Item adicionado.");
        }
 
        if (venda.getItens().isEmpty()) {
            System.out.println("Venda cancelada: nenhum item adicionado.");
            return;
        }
 
        System.out.println(venda);
        vendaService.confirmarVenda(venda);
        System.out.println("Venda confirmada com ID " + venda.getId());
    }
 
    private static void cancelarVenda() {
        int vendaId = lerInteiro("ID da venda a cancelar: ");
        vendaService.cancelarVenda(vendaId);
        System.out.println("Venda cancelada e estoque restaurado.");
    }
 
    private static void listarVendasPorCliente() {
        int clienteId = lerInteiro("ID do cliente: ");
        List<Venda> vendas = vendaService.listarPorCliente(clienteId);
        exibirVendas(vendas);
    }
 
    private static void listarVendasPorStatus() {
        System.out.println("Status disponiveis: ABERTA, CONFIRMADA, CANCELADA, FATURADA");
        String texto = lerTexto("Status: ");
        StatusVenda status = StatusVenda.valueOf(texto.trim().toUpperCase());
        List<Venda> vendas = vendaService.listarPorStatus(status);
        exibirVendas(vendas);
    }
 
    private static void listarVendasPorPeriodo() {
        LocalDate inicio = LocalDate.parse(lerTexto("Data inicial (dd/MM/yyyy): "), FORMATO_DATA);
        LocalDate fim = LocalDate.parse(lerTexto("Data final (dd/MM/yyyy): "), FORMATO_DATA);
 
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atTime(LocalTime.MAX);
 
        List<Venda> vendas = vendaService.listarPorPeriodo(inicioDateTime, fimDateTime);
        exibirVendas(vendas);
    }
 
    private static void exibirVendas(List<Venda> vendas) {
        if (vendas.isEmpty()) {
            System.out.println("Nenhuma venda encontrada.");
            return;
        }
        vendas.forEach(v -> System.out.println(v + "\n"));
    }
 
    private static void listarEstoque() {
        System.out.println("1 - Ver todo o estoque");
        System.out.println("2 - Ver apenas itens abaixo do minimo");
        int opcao = lerInteiro("Escolha uma opcao: ");
 
        List<Estoque> estoques = (opcao == 2)
                ? estoqueService.listarAbaixoDoMinimo()
                : estoqueService.listarTodos();
 
        if (estoques.isEmpty()) {
            System.out.println("Nenhum item de estoque encontrado.");
            return;
        }
        estoques.forEach(System.out::println);
    }
 
    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }
 
    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        return Integer.parseInt(scanner.nextLine());
    }
 
    private static double lerDouble(String mensagem) {
        System.out.print(mensagem);
        return Double.parseDouble(scanner.nextLine());
    }
}
 
