
package dao.hibernate;
 
import dao.ClienteDAO;
import model.Cliente;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
import java.util.List;
 
public class ClienteDAOHibernate implements ClienteDAO {
 
    @Override
    public void inserir(Cliente cliente) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(cliente);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao inserir cliente.", e);
        }
    }
 
    @Override
    public void atualizar(Cliente cliente) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(cliente);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao atualizar cliente.", e);
        }
    }
 
    @Override
    public Cliente buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Cliente.class, id);
        }
    }
 
    @Override
    public List<Cliente> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Cliente order by nome", Cliente.class).list();
        }
    }
 
    @Override
    public List<Cliente> listarPorStatus(boolean ativo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Cliente where ativo = :ativo order by nome", Cliente.class)
                    .setParameter("ativo", ativo)
                    .list();
        }
    }
 
    @Override
    public boolean existeCpf(String cpf) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long total = session.createQuery(
                            "select count(c) from Cliente c where c.cpf = :cpf", Long.class)
                    .setParameter("cpf", cpf)
                    .uniqueResult();
            return total != null && total > 0;
        }
    }
 
    @Override
    public void deletar(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) {
                session.remove(cliente);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao deletar cliente.", e);
        }
    }
}
 
