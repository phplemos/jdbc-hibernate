
package dao.hibernate;
 
import dao.EstoqueDAO;
import model.Estoque;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
import java.util.List;
 
public class EstoqueDAOHibernate implements EstoqueDAO {
 
    @Override
    public void inserir(Estoque estoque) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(estoque);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao inserir estoque.", e);
        }
    }
 
    @Override
    public void atualizar(Estoque estoque) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(estoque);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao atualizar estoque.", e);
        }
    }
 
    @Override
    public Estoque buscarPorProduto(int produtoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Estoque where produto.id = :produtoId", Estoque.class)
                    .setParameter("produtoId", produtoId)
                    .uniqueResult();
        }
    }
 
    @Override
    public List<Estoque> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Estoque", Estoque.class).list();
        }
    }
 
    @Override
    public List<Estoque> listarAbaixoDoMinimo() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Estoque where quantidadeDisponivel < quantidadeMinima", Estoque.class).list();
        }
    }
}
 
