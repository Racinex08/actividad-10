import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PasswordValidator {
    private static final String LOG_FILE = "validation_log.txt";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z]{3,})(?=.*[A-Z]{2,})(?=.*[@#$%^&*()])(?=\\S+$).{8,}$";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ExecutorService executorService = Executors.newCachedThreadPool();

            String password;
            do {
                System.out.println("Ingrese una contraseña para validar (o 'exit' para salir):");
                password = scanner.nextLine();
                final String finalPassword = password;
                if (!finalPassword.equalsIgnoreCase("exit")) {
                    executorService.execute(() -> validateAndLogPassword(finalPassword));
                }
            } while (!password.equalsIgnoreCase("exit"));

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void validateAndLogPassword(String password) {
        boolean isValid = password.matches(PASSWORD_PATTERN);
        logValidationResult(password, isValid);
        System.out.println("Contraseña: " + password + ", Es válida: " + isValid);
    }

    private static void logValidationResult(String password, boolean isValid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write("Contraseña: " + password + ", Es válida: " + isValid + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de registro: " + e.getMessage());
        }
    }
}
