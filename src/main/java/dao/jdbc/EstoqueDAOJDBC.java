
package dao.jdbc;
 
import dao.EstoqueDAO;
import model.Estoque;
import model.Produto;
 
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
public class EstoqueDAOJDBC implements EstoqueDAO {
 
    private final ProdutoDAOJDBC produtoDAO = new ProdutoDAOJDBC();
 
    @Override
    public void inserir(Estoque estoque) {
        String sql = "INSERT INTO estoque (produto_id, quantidade_disponivel, quantidade_minima, "
                + "localizacao, ultima_atualizacao) VALUES (?, ?, ?, ?, ?)";
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
 
            stmt.setInt(1, estoque.getProduto().getId());
            stmt.setInt(2, estoque.getQuantidadeDisponivel());
            stmt.setInt(3, estoque.getQuantidadeMinima());
            stmt.setString(4, estoque.getLocalizacao());
            stmt.setTimestamp(5, Timestamp.valueOf(estoque.getUltimaAtualizacao()));
 
            stmt.executeUpdate();
 
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    estoque.setId(chavesGeradas.getInt(1));
                }
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir estoque.", e);
        }
    }
 
    @Override
    public void atualizar(Estoque estoque) {
        String sql = "UPDATE estoque SET quantidade_disponivel = ?, quantidade_minima = ?, "
                + "localizacao = ?, ultima_atualizacao = ? WHERE produto_id = ?";
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setInt(1, estoque.getQuantidadeDisponivel());
            stmt.setInt(2, estoque.getQuantidadeMinima());
            stmt.setString(3, estoque.getLocalizacao());
            stmt.setTimestamp(4, Timestamp.valueOf(estoque.getUltimaAtualizacao()));
            stmt.setInt(5, estoque.getProduto().getId());
 
            stmt.executeUpdate();
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estoque.", e);
        }
    }
 
    @Override
    public Estoque buscarPorProduto(int produtoId) {
        String sql = "SELECT * FROM estoque WHERE produto_id = ?";
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setInt(1, produtoId);
 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEstoque(rs);
                }
                return null;
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estoque por produto.", e);
        }
    }
 
    @Override
    public List<Estoque> listarTodos() {
        String sql = "SELECT * FROM estoque";
        List<Estoque> lista = new ArrayList<>();
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
 
            while (rs.next()) {
                lista.add(mapearEstoque(rs));
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estoques.", e);
        }
 
        return lista;
    }
 
    @Override
    public List<Estoque> listarAbaixoDoMinimo() {
        String sql = "SELECT * FROM estoque WHERE quantidade_disponivel < quantidade_minima";
        List<Estoque> lista = new ArrayList<>();
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
 
            while (rs.next()) {
                lista.add(mapearEstoque(rs));
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estoques abaixo do mínimo.", e);
        }
 
        return lista;
    }
 
    /**
     * Monta um objeto Estoque a partir da linha do banco.
     * Como Estoque guarda uma referência ao Produto inteiro (não só o ID),
     * buscamos o Produto correspondente antes de montar o objeto.
     */
    private Estoque mapearEstoque(ResultSet rs) throws SQLException {
        int produtoId = rs.getInt("produto_id");
        Produto produto = produtoDAO.buscarPorId(produtoId);
 
        Estoque estoque = new Estoque();
        estoque.setId(rs.getInt("id"));
        estoque.setProduto(produto);
        estoque.setQuantidadeDisponivel(rs.getInt("quantidade_disponivel"));
        estoque.setQuantidadeMinima(rs.getInt("quantidade_minima"));
        estoque.setLocalizacao(rs.getString("localizacao"));
        LocalDateTime ultimaAtualizacao = rs.getTimestamp("ultima_atualizacao").toLocalDateTime();
        estoque.setUltimaAtualizacao(ultimaAtualizacao);
        return estoque;
    }
}
 
