package dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe responsável por criar conexões com o banco MySQL.
 * As credenciais são lidas de src/main/resources/database.properties,
 * em vez de ficarem escritas diretamente no código.
 */
public class ConexaoFactory {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConexaoFactory.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException("Arquivo database.properties não encontrado em src/main/resources.");
            }
            props.load(input);

            // Registra o driver do MySQL explicitamente
            Class.forName(props.getProperty("db.driver"));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar database.properties.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do MySQL não encontrado. Verifique o pom.xml.", e);
        }
    }

    public static Connection criarConexao() {
        try {
            String url = props.getProperty("db.url");
            String usuario = props.getProperty("db.username");
            String senha = props.getProperty("db.password");
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados.", e);
        }
    }
}