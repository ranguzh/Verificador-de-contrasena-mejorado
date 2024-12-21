import java.io.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.*;

public class VerificadorDeContrasena {
    public static void main(String[] args) {
        // Mensaje de requisitos de la contraseña
        System.out.println("=== Requisitos de la Contraseña ===");
        System.out.println("1. Longitud mínima de 8 caracteres.");
        System.out.println("2. Al menos un número.");
        System.out.println("3. Al menos dos letras mayúsculas.");
        System.out.println("4. Al menos tres letras minúsculas.");
        System.out.println("5. Al menos un carácter especial.");

        // Recibir la cantidad de contraseñas a verificar
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de contraseñas a verificar: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        List<String> contrasenas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            System.out.print("Ingrese la contraseña #" + (i + 1) + ": ");
            contrasenas.add(scanner.nextLine());
        }

        // ExecutorService para manejar hilos concurrentes
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<String>> resultados = new ArrayList<>();

        // Usando Lambda para crear los hilos de validación
        contrasenas.forEach(contrasena -> {
            Future<String> resultado = executor.submit(() -> {
                return verificarContrasena(contrasena) ?
                        "Contraseña válida: " + contrasena :
                        "Contraseña inválida: " + contrasena;
            });
            resultados.add(resultado);
        });

        // Esperamos la terminación de todos los hilos y los mostramos
        System.out.println("\n=== Resultados de la Verificación ===");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("registro_contraseñas.txt", true))) {
            for (Future<String> resultado : resultados) {
                String resultadoVerificacion = resultado.get();
                System.out.println(resultadoVerificacion);

                // Escribir en el archivo de registro
                writer.write(resultadoVerificacion);
                writer.newLine();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    // Metodo para verificar la contraseña usando expresiones regulares
    public static boolean verificarContrasena(String contrasena) {
        // Expresión regular para validar los requisitos
        String regex = "^(?=(.*[0-9]){1})(?=(.*[A-Z]){2})(?=(.*[a-z]){3})(?=(.*[\\W_]){1})[A-Za-z0-9\\W_]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contrasena);
        return matcher.matches();
    }
}
