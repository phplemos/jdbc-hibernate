
package dao.hibernate;
 
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
 
/**
 * Classe responsável por criar e manter uma única instância do SessionFactory
 * durante toda a execução da aplicação (é uma classe "cara" de criar,
 * então deve existir uma só, reaproveitada por todos os DAOs).
 *
 * É o equivalente, na versão Hibernate, ao ConexaoFactory da versão JDBC.
 */
public class HibernateUtil {
 
    private static final SessionFactory sessionFactory = buildSessionFactory();
 
    private static SessionFactory buildSessionFactory() {
        try {
            // Lê automaticamente o arquivo hibernate.cfg.xml em src/main/resources
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Erro ao criar o SessionFactory: " + ex);
        }
    }
 
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
 
    public static void shutdown() {
        getSessionFactory().close();
    }
}
 
