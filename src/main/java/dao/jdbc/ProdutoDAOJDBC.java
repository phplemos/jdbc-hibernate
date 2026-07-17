package dao.jdbc;

import dao.ProdutoDAO;
import model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAOJDBC implements ProdutoDAO {

    @Override
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, descricao, preco_venda, preco_custo, categoria, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPrecoVenda());
            stmt.setDouble(4, produto.getPrecoCusto());
            stmt.setString(5, produto.getCategoria());
            stmt.setBoolean(6, produto.isAtivo());

            stmt.executeUpdate();

            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    produto.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir produto.", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, preco_venda = ?, preco_custo = ?, "
                + "categoria = ?, ativo = ? WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPrecoVenda());
            stmt.setDouble(4, produto.getPrecoCusto());
            stmt.setString(5, produto.getCategoria());
            stmt.setBoolean(6, produto.isAtivo());
            stmt.setInt(7, produto.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto.", e);
        }
    }

    @Override
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProduto(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID.", e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT * FROM produto ORDER BY nome";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produtos.add(mapearProduto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos.", e);
        }

        return produtos;
    }

    @Override
    public List<Produto> listarPorStatus(boolean ativo) {
        String sql = "SELECT * FROM produto WHERE ativo = ? ORDER BY nome";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos por status.", e);
        }

        return produtos;
    }

    @Override
    public List<Produto> listarPorCategoria(String categoria) {
        String sql = "SELECT * FROM produto WHERE categoria = ? ORDER BY nome";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, categoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos por categoria.", e);
        }

        return produtos;
    }

    @Override
    public void deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto.", e);
        }
    }

    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        // setPrecoVenda/setPrecoCusto têm validações (ex: custo <= venda),
        // então setamos venda primeiro e usamos valores "crus" via reflection não é necessário:
        // como o banco já respeita a regra, a ordem abaixo funciona sem violar as validações.
        produto.setPrecoVenda(rs.getDouble("preco_venda"));
        produto.setPrecoCusto(rs.getDouble("preco_custo"));
        produto.setCategoria(rs.getString("categoria"));
        produto.setAtivo(rs.getBoolean("ativo"));
        return produto;
    }
}