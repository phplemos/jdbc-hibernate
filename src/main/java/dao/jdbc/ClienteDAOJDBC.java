package dao.jdbc;

import dao.ClienteDAO;
import model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJDBC implements ClienteDAO {

    @Override
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, email, telefone, ativo, data_cadastro) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setBoolean(5, cliente.isAtivo());
            stmt.setTimestamp(6, Timestamp.valueOf(cliente.getDataCadastro()));

            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente pelo banco e devolve pro objeto Java
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    cliente.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir cliente.", e);
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, email = ?, telefone = ?, ativo = ? "
                + "WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setBoolean(5, cliente.isAtivo());
            stmt.setInt(6, cliente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente.", e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
                return null; // o Service já trata esse caso lançando NegocioException
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por ID.", e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes.", e);
        }

        return clientes;
    }

    @Override
    public List<Cliente> listarPorStatus(boolean ativo) {
        String sql = "SELECT * FROM cliente WHERE ativo = ? ORDER BY nome";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes por status.", e);
        }

        return clientes;
    }

    @Override
    public boolean existeCpf(String cpf) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE cpf = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de CPF.", e);
        }
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente.", e);
        }
    }

    /**
     * Converte uma linha do ResultSet em um objeto Cliente.
     * Centralizar essa conversão evita repetir o mesmo código em cada método de busca.
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setAtivo(rs.getBoolean("ativo"));
        LocalDateTime dataCadastro = rs.getTimestamp("data_cadastro").toLocalDateTime();
        cliente.setDataCadastro(dataCadastro);
        return cliente;
    }
}