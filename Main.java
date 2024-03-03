import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 class PasswordValidator {
    private static final String LOG_FILE = "validation_log.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newCachedThreadPool();

        System.out.println("Ingrese una contraseña para validar (o 'exit' para salir):");
        String input = scanner.nextLine();

        while (!input.equalsIgnoreCase("exit")) {
            final String passwordToValidate = input; // Hacer effectively final
            executorService.execute(() -> {
                boolean isValid = validatePassword(passwordToValidate);
                writeValidationResult(passwordToValidate, isValid);
            });
            System.out.println("Ingrese otra contraseña para validar (o 'exit' para salir):");
            input = scanner.nextLine();
        }

        executorService.shutdown();
    }

    private static boolean validatePassword(String password) {
        // Longitud mínima de 8 caracteres
        if (password.length() < 8) {
            return false;
        }

        // Al menos un número
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Al menos 2 letras mayúsculas
        if (password.replaceAll("[^A-Z]", "").length() < 2) {
            return false;
        }

        // Al menos 3 letras minúsculas
        if (password.replaceAll("[^a-z]", "").length() < 3) {
            return false;
        }

        // Al menos un caracter especial
        Pattern specialCharsPattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = specialCharsPattern.matcher(password);
        return matcher.find();
    }

    private static void writeValidationResult(String password, boolean isValid) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write("Contraseña: " + password + ", Es válida: " + isValid + "\n");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de registro: " + e.getMessage());
        }
    }
}