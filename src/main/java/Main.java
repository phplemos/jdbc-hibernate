import model.*;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação.
 * A implementação do menu deve ser concluída pelos alunos.
 * Nesta etapa, o foco é conectar a interface com os serviços.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("SISTEMA DE GESTÃO COMERCIAL");
        System.out.println("Execução iniciada.");
        // TODO: implementar o menu principal e os fluxos de operação.
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        return Integer.parseInt(scanner.nextLine());
    }

    private static double lerDouble(String mensagem) {
        System.out.print(mensagem);
        return Double.parseDouble(scanner.nextLine());
    }
}
