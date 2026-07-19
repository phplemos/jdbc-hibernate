
package dao.hibernate;
 
import dao.ProdutoDAO;
import model.Produto;
import org.hibernate.Session;
import org.hibernate.Transaction;
 
import java.util.List;
 
public class ProdutoDAOHibernate implements ProdutoDAO {
 
    @Override
    public void inserir(Produto produto) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(produto);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao inserir produto.", e);
        }
    }
 
    @Override
    public void atualizar(Produto produto) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(produto);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao atualizar produto.", e);
        }
    }
 
    @Override
    public Produto buscarPorId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Produto.class, id);
        }
    }
 
    @Override
    public List<Produto> listarTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Produto order by nome", Produto.class).list();
        }
    }
 
    @Override
    public List<Produto> listarPorStatus(boolean ativo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Produto where ativo = :ativo order by nome", Produto.class)
                    .setParameter("ativo", ativo)
                    .list();
        }
    }
 
    @Override
    public List<Produto> listarPorCategoria(String categoria) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Produto where categoria = :categoria order by nome", Produto.class)
                    .setParameter("categoria", categoria)
                    .list();
        }
    }
 
    @Override
    public void deletar(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Produto produto = session.get(Produto.class, id);
            if (produto != null) {
                session.remove(produto);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Erro ao deletar produto.", e);
        }
    }
}
 
