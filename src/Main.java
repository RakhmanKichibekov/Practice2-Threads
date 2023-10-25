import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String[] TARGET_HASHES = {
            "1115dd800feaacefdf481f1f9070374a2a81e27880f187396db67958b207cbad",
            "3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b",
            "74e1bb62f8dabb8125a58852b63bdf6eaef667cb56ac7f7cdba6d7305c50a22f"
    };
    private static final int PASSWORD_LENGTH = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите количество потоков: ");
        final int NUM_THREADS = scanner.nextInt(); // Количество потоков
        System.out.println("Считаем...");
        // Включаем таймер
        long startTime = System.currentTimeMillis();
        // Создаем пул потоков для многопоточного режима
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        // Определяем диапазон символов для перебора
        int numCharacters = CHARACTERS.length();
        int[] password = new int[PASSWORD_LENGTH];
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password[i] = 0;
        }

        boolean passwordsFound = false;
        while (!passwordsFound) {
            // Сформируем текущий пароль
            StringBuilder currentPassword = new StringBuilder();
            for (int index : password) {
                currentPassword.append(CHARACTERS.charAt(index));
            }

            // Получим хэш текущего пароля
            String currentHash = getSHA256Hash(currentPassword.toString());

            // Проверим, совпадает ли хэш с целевыми
            for (String targetHash : TARGET_HASHES) {
                if (currentHash.equals(targetHash)) {
                    System.out.println("Найден пароль: " + currentPassword.toString() + " для хэша: " + targetHash);
                }
            }

            // Увеличим пароль
            int indexToIncrement = PASSWORD_LENGTH - 1;
            while (indexToIncrement >= 0) {
                if (password[indexToIncrement] < numCharacters - 1) {
                    password[indexToIncrement]++;
                    break;
                } else {
                    password[indexToIncrement] = 0;
                    indexToIncrement--;
                }
            }

            // Если все комбинации перебраны, завершаем цикл
            if (indexToIncrement < 0) {
                passwordsFound = true;
            }
        }

        // Выводим время выполнения
        long endTime = System.currentTimeMillis();
        System.out.println("Время выполнения: " + (endTime - startTime) + " миллисекунд");
        System.out.println("Готово");

        // Завершаем пул потоков
        executorService.shutdown();
    }

    private static String getSHA256Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());

            // Преобразуем байты в шестнадцатеричную строку
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexStringBuilder.append(hex);
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
