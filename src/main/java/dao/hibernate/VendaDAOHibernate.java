
package dao.hibernate;
 
import dao.VendaDAO;
import model.StatusVenda;
import model.Venda;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
import java.time.LocalDateTime;
import java.util.List;
 
public class VendaDAOHibernate implements VendaDAO {
 
    /**
     * Graças ao cascade = CascadeType.ALL configurado em Venda.itens,
     * salvar a venda já salva todos os itens automaticamente,
     * dentro da mesma transação — sem precisar de SQL manual para cada item.
     */
    @Override
    public void inserir(Venda venda) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(venda);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao inserir venda.", e);
        }
    }
 
    @Override
    public void atualizarStatus(Venda venda) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(venda);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao atualizar status da venda.", e);
        }
    }
 
    @Override
    public Venda buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Venda.class, id);
        }
    }
 
    @Override
    public List<Venda> listarPorCliente(int clienteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Venda where cliente.id = :clienteId order by dataVenda desc", Venda.class)
                    .setParameter("clienteId", clienteId)
                    .list();
        }
    }
 
    @Override
    public List<Venda> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Venda where dataVenda between :inicio and :fim order by dataVenda desc",
                            Venda.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fim", fim)
                    .list();
        }
    }
 
    @Override
    public List<Venda> listarPorStatus(StatusVenda status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Venda where status = :status order by dataVenda desc", Venda.class)
                    .setParameter("status", status)
                    .list();
        }
    }
}
 
