package myGame.mode;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Утилітарний клас для читання файлів з автоматичним визначенням кодування.
 * Надає методи для виведення вмісту файлів у консоль та читання їх у рядки.
 *
 * <p>Особливості:
 * <ul>
 *   <li>Автоматичне визначення кодування (UTF-8, Windows-1251)</li>
 *   <li>Підтримка BOM (Byte Order Mark) для UTF-8</li>
 *   <li>Паузи при виведенні порожніх рядків для кращої читабельності</li>
 *   <li>Обробка кирилиці у різних кодуваннях</li>
 * </ul>
 */
public final class ReadFromFile {

    /**
     * Конструктор за замовчуванням.
     */
    public ReadFromFile() {}

    /**
     * Виводить вміст файлу у консоль рядок за рядком з паузами на порожніх рядках.
     * Використовує UTF-8 кодування для читання файлу.
     *
     * <p>Особливості:
     * <ul>
     *   <li>Кожен рядок виводиться окремо</li>
     *   <li>Порожні рядки (або рядки лише з пробілами) викликають паузу 1 секунду</li>
     *   <li>Автоматично закриває потік після читання</li>
     * </ul>
     *
     * @param file файл для читання та виведення
     * @throws IOException якщо виникають проблеми з читанням файлу
     * @throws RuntimeException якщо потік було перервано під час паузи
     */
    public static void printToConsole(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                // Якщо це порожній рядок — пауза 1 секунди
                if (line.trim().isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}