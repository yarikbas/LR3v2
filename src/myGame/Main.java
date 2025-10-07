package myGame;

import myGame.mode.OneVsOne;
import myGame.mode.ReadFromFile;
import myGame.mode.TeamVsTeam;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Головний клас гри, що надає точку входу в програму.
 * Містить головне меню для вибору режимів гри та перегляду логів.
 *
 * <p>Доступні режими:
 * <ul>
 *   <li>1 vs 1 - битва між двома дроїдами</li>
 *   <li>Team vs Team - битва між двома командами дроїдів</li>
 *   <li>Read game log - перегляд збережених логів ігор</li>
 * </ul>
 *
 * @author Yaroslav_Basarab
 * @version 2.5
 * @see OneVsOne
 * @see TeamVsTeam
 * @see ReadFromFile
 * @since 2.0
 */
public class Main {

    /** Директорія для збереження лог-файлів */
    private static final String LOGS_DIR = "logs";
    /** Дозволені розширення файлів для перегляду логів */
    private static final Set<String> ALLOWED = Set.of("log", "txt");

    /**
     * Головний метод програми, точка входу.
     * Виводить головне меню та обробляє вибір користувача.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== MyGame ===");
        System.out.println("1) 1 vs 1");
        System.out.println("2) Team vs Team");
        System.out.println("3) Read game from file");
        System.out.println("4) Exit");
        System.out.print("Choose: ");

        int choice = readIntFromTo(sc, 1, 3);
        switch (choice) {
            case 1 -> new OneVsOne("C:\\Users\\user\\IdeaProjects\\LR3v2\\logs", false).start();
            case 2 -> new TeamVsTeam("C:\\Users\\user\\IdeaProjects\\LR3v2\\logs", false).start();
            case 3 -> readLogMenu(sc);
            case 4 -> System.exit(0);
        }
    }

    /**
     * Меню для вибору та перегляду лог-файлів.
     * Показує список доступних .log та .txt файлів у директорії логів.
     *
     * @param sc об'єкт Scanner для вводу користувача
     */
    private static void readLogMenu(Scanner sc) {
        ensureDir(LOGS_DIR);
        List<Path> files = listFiles(Paths.get(LOGS_DIR), ALLOWED);
        if (files.isEmpty()) {
            System.out.println("У '" + LOGS_DIR + "' немає .log/.txt файлів.");
            return;
        }
        System.out.println("\nДоступні логи:");
        for (int i = 0; i < files.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, files.get(i).getFileName());
        }
        System.out.print("Оберіть файл: ");
        int idx = readIntFromTo(sc, 1, files.size());
        File chosen = files.get(idx - 1).toFile();

        try {
            ReadFromFile.printToConsole(chosen);
        } catch (IOException e) {
            System.err.println("Не вдалося прочитати файл: " + e.getMessage());
        }
    }

    /**
     * Створює директорію, якщо вона не існує.
     *
     * @param name ім'я директорії для створення
     */
    private static void ensureDir(String name) {
        Path p = Paths.get(name);
        if (!Files.exists(p)) try {
            Files.createDirectories(p);
        } catch (IOException ignored) {}
    }

    /**
     * Отримує список файлів з вказаної директорії з дозволеними розширеннями.
     *
     * @param dir шлях до директорії
     * @param exts множина дозволених розширень файлів
     * @return відсортований список шляхів до файлів
     */
    private static List<Path> listFiles(Path dir, Set<String> exts) {
        try (var s = Files.list(dir)) {
            return s.filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.getFileName().toString();
                        int dot = n.lastIndexOf('.');
                        if (dot <= 0 || dot == n.length() - 1) return false;
                        String ext = n.substring(dot + 1).toLowerCase(Locale.ROOT);
                        return exts.contains(ext);
                    })
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Зчитує ціле число з вказаного діапазону з перевіркою коректності.
     *
     * @param sc об'єкт Scanner для вводу
     * @param min мінімальне допустиме значення
     * @param max максимальне допустиме значення
     * @return коректне ціле число у вказаному діапазоні
     */
    private static int readIntFromTo(Scanner sc, int min, int max) {
        while (true) {
            if (!sc.hasNextInt()) {
                System.out.print("(не число) ще раз: ");
                sc.next();
                continue;
            }
            int v = sc.nextInt();
            if (v < min || v > max) {
                System.out.print("[" + min + ".." + max + "] ще раз: ");
                continue;
            }
            return v;
        }
    }
}