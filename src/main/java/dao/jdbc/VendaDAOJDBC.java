
package dao.jdbc;
 
import dao.VendaDAO;
import model.*;
 
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
public class VendaDAOJDBC implements VendaDAO {
 
    private final ClienteDAOJDBC clienteDAO = new ClienteDAOJDBC();
    private final ProdutoDAOJDBC produtoDAO = new ProdutoDAOJDBC();
 
    /**
     * Insere a venda e todos os seus itens em uma única transação.
     * Se qualquer item falhar, a venda inteira é desfeita (rollback),
     * evitando gravar uma venda "pela metade".
     */
    @Override
    public void inserir(Venda venda) {
        String sqlVenda = "INSERT INTO venda (cliente_id, data_venda, status, total_bruto, desconto, total_liquido) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
 
        Connection conexao = null;
        try {
            conexao = ConexaoFactory.criarConexao();
            conexao.setAutoCommit(false); // inicia a transação
 
            try (PreparedStatement stmtVenda = conexao.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setInt(1, venda.getCliente().getId());
                stmtVenda.setTimestamp(2, Timestamp.valueOf(venda.getDataVenda()));
                stmtVenda.setString(3, venda.getStatus().name());
                stmtVenda.setDouble(4, venda.getTotalBruto());
                stmtVenda.setDouble(5, venda.getDesconto());
                stmtVenda.setDouble(6, venda.getTotalLiquido());
                stmtVenda.executeUpdate();
 
                try (ResultSet chaves = stmtVenda.getGeneratedKeys()) {
                    if (chaves.next()) {
                        venda.setId(chaves.getInt(1));
                    }
                }
            }
 
            try (PreparedStatement stmtItem = conexao.prepareStatement(sqlItem)) {
                for (ItemVenda item : venda.getItens()) {
                    stmtItem.setInt(1, venda.getId());
                    stmtItem.setInt(2, item.getProduto().getId());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.setDouble(5, item.getSubtotal());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }
 
            conexao.commit(); // confirma tudo de uma vez
 
        } catch (SQLException e) {
            if (conexao != null) {
                try {
                    conexao.rollback(); // desfaz tudo se algo deu errado
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Erro ao desfazer transação de venda.", rollbackEx);
                }
            }
            throw new RuntimeException("Erro ao inserir venda.", e);
        } finally {
            if (conexao != null) {
                try {
                    conexao.setAutoCommit(true);
                    conexao.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
 
    @Override
    public void atualizarStatus(Venda venda) {
        String sql = "UPDATE venda SET status = ? WHERE id = ?";
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setString(1, venda.getStatus().name());
            stmt.setInt(2, venda.getId());
            stmt.executeUpdate();
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da venda.", e);
        }
    }
 
    @Override
    public Venda buscarPorId(int id) {
        String sql = "SELECT * FROM venda WHERE id = ?";
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setInt(1, id);
 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVenda(rs, conexao);
                }
                return null;
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar venda por ID.", e);
        }
    }
 
    @Override
    public List<Venda> listarPorCliente(int clienteId) {
        String sql = "SELECT * FROM venda WHERE cliente_id = ? ORDER BY data_venda DESC";
        return listarComFiltro(sql, clienteId);
    }
 
    @Override
    public List<Venda> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT * FROM venda WHERE data_venda BETWEEN ? AND ? ORDER BY data_venda DESC";
        List<Venda> vendas = new ArrayList<>();
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vendas.add(mapearVenda(rs, conexao));
                }
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas por período.", e);
        }
 
        return vendas;
    }
 
    @Override
    public List<Venda> listarPorStatus(StatusVenda status) {
        String sql = "SELECT * FROM venda WHERE status = ? ORDER BY data_venda DESC";
        List<Venda> vendas = new ArrayList<>();
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setString(1, status.name());
 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vendas.add(mapearVenda(rs, conexao));
                }
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas por status.", e);
        }
 
        return vendas;
    }
 
    private List<Venda> listarComFiltro(String sql, int parametroInt) {
        List<Venda> vendas = new ArrayList<>();
 
        try (Connection conexao = ConexaoFactory.criarConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
 
            stmt.setInt(1, parametroInt);
 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vendas.add(mapearVenda(rs, conexao));
                }
            }
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas.", e);
        }
 
        return vendas;
    }
 
    /**
     * Monta o objeto Venda e busca também os itens relacionados a ela.
     * Usa reflection não é necessário: usamos os setters normais,
     * inclusive para reconstituir a lista de itens.
     */
    private Venda mapearVenda(ResultSet rs, Connection conexao) throws SQLException {
        Venda venda = new Venda();
        venda.setId(rs.getInt("id"));
 
        Cliente cliente = clienteDAO.buscarPorId(rs.getInt("cliente_id"));
        venda.setCliente(cliente);
 
        venda.setDataVenda(rs.getTimestamp("data_venda").toLocalDateTime());
        venda.setStatus(StatusVenda.valueOf(rs.getString("status")));
        venda.setTotalBruto(rs.getDouble("total_bruto"));
        venda.setDesconto(rs.getDouble("desconto"));
        venda.setTotalLiquido(rs.getDouble("total_liquido"));
 
        List<ItemVenda> itens = buscarItensDaVenda(venda.getId(), conexao);
        venda.setItens(itens);
 
        return venda;
    }
 
    private List<ItemVenda> buscarItensDaVenda(int vendaId, Connection conexao) throws SQLException {
        String sql = "SELECT * FROM item_venda WHERE venda_id = ?";
        List<ItemVenda> itens = new ArrayList<>();
 
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, vendaId);
 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemVenda item = new ItemVenda();
                    item.setId(rs.getInt("id"));
                    item.setProduto(produtoDAO.buscarPorId(rs.getInt("produto_id")));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    itens.add(item);
                }
            }
        }
 
        return itens;
    }
}
 
