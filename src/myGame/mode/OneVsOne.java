package myGame.mode;

import myGame.droid.*;
import myGame.map.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

import static java.util.Arrays.asList;
import static myGame.droid.DroidFactory.DroidConstruct.yourDroid;
import static myGame.droid.DroidFactory.DroidConstruct.*;

/**
 * Режим гри "Один на один" для битви між двома дроїдами.
 * Клас реалізує логіку гри для режиму один на один, включаючи вибір дроїдів,
 * управління боєм, систему бонусів за елементи карти та логування всіх подій гри.
 *
 * <p>Особливості режиму:
 * <ul>
 *   <li>Вибір двох дроїдів для битви</li>
 *   <li>Автоматичний вибір карти з випадковим середовищем</li>
 *   <li>Система бонусів за відповідність елементу дроїда та карти</li>
 *   <li>Покрокова битва з логуванням у файл</li>
 *   <li>Максимум 200 раундів для запобігання нескінченним битвам</li>
 * </ul>
 */
public class OneVsOne {

    /**
     * Перший дроїд-учасник битви
     */
    private final BaseDroid droid1;
    /**
     * Другий дроїд-учасник битви
     */
    private final BaseDroid droid2;
    /**
     * Поточна карта гри
     */
    private final BaseMap map;

    /**
     * Максимальний індекс дроїда у каталозі
     */
    private static final int MAX_INDEX = 7;
    /**
     * Форматувальник часу для імені лог-файлу
     */
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    /**
     * Об'єкт для запису логів
     */
    private PrintWriter log;
    /**
     * Чи ведеться запис у файл (true) чи в консоль (false)
     */
    private boolean writingToFile = false;
    /**
     * Чи готовий логер до запису (запобігає запису до ініціалізації)
     */
    private boolean loggerReady = false;
    /**
     * Буфер для зберігання повідомлень до ініціалізації логера
     */
    private final StringBuilder preLogBuffer = new StringBuilder();

    /**
     * Директорія для збереження лог-файлів
     */
    private final String logDirectory;
    /**
     * Чи додавати до існуючого лог-файлу
     */
    private final boolean appendIfExists;

    /**
     * Конструктор за замовчуванням, використовує поточну директорію для логів.
     */
    public OneVsOne() {
        this(".", false);
    }

    /**
     * Основний конструктор для створення режиму один на один.
     *
     * @param logDirectory   директорія для збереження лог-файлів
     * @param appendIfExists чи додавати до існуючого лог-файлу (true) чи перезаписувати (false)
     */
    public OneVsOne(String logDirectory, boolean appendIfExists) {
        this.logDirectory = (logDirectory == null || logDirectory.isBlank()) ? "." : logDirectory;
        this.appendIfExists = appendIfExists;

        Scanner sc = new Scanner(System.in);

        // Перший дроїд
        logln("\nВиберіть першого дроїда:");
        showAndRecordCatalog();
        int i = readIndex(sc);
        this.droid1 = yourDroid(i);
        logln("Обрано першого: " + droid1.getName());

        // Другий дроїд
        logln("\nВиберіть другого дроїда:");
        showAndRecordCatalog();
        i = readIndex(sc);
        this.droid2 = yourDroid(i);
        logln("Обрано другого: " + droid2.getName());

        this.map = chooseMap();
        BaseDroid.arenaMin = map.getMinPosition();
        BaseDroid.arenaMax = map.getMaxPosition();

        applyBonus(droid1, this.map);
        applyBonus(droid2, this.map);

        initLoggerWithUniqueName();

        logln("Мапа: " + map.getClass().getName()
                + " (element=" + map.getElement() + ", bonus=" + map.getBonus() + ")");
        logln("Арена: [" + map.getMinPosition() + " .. " + map.getMaxPosition() + "]");
    }

    /**
     * Випадковим чином обирає карту для гри.
     *
     * @return випадково обрана карта (cave, ocean, sky або volcano)
     */
    private BaseMap chooseMap() {
        Random rand = new Random();
        return switch (rand.nextInt(4)) {
            case 0 -> new Cave();
            case 1 -> new Ocean();
            case 2 -> new Sky();
            default -> new Volcano();
        };
    }


    /**
     * Ініціалізує логер з унікальним ім'ям файлу на основі імен дроїдів та часу.
     * Автоматично переносить попередні повідомлення з буфера у файл.
     */
    private void initLoggerWithUniqueName() {
        String n1 = sanitizeFileName(droid1.getName());
        String n2 = sanitizeFileName(droid2.getName());
        String ts = LocalDateTime.now().format(TS);

        String fileName = String.format("one_vs_one_%s_vs_%s_%s.log", n1, n2, ts);

        File dir = new File(logDirectory);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Не вдалося створити директорію: " + dir.getAbsolutePath());
        }

        File f = new File(dir, fileName);

        try {
            this.log = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(f, appendIfExists), StandardCharsets.UTF_8),
                    true
            );
            this.writingToFile = true;
            this.loggerReady = true;

            if (preLogBuffer.length() > 0) {
                log.print(preLogBuffer);
                log.flush();
                preLogBuffer.setLength(0);
            }

            logln("\n=== Лог бою збережено у файл: " + f.getAbsolutePath() + " ===");
        } catch (IOException e) {
            System.err.println("Не вдалося відкрити лог-файл (" + f.getAbsolutePath() + "): " + e.getMessage());
            this.log = new PrintWriter(System.out, true);
            this.writingToFile = false;
            this.loggerReady = true;
        }
    }

    /**
     * Очищає рядок для використання в імені файлу.
     *
     * @param s вихідний рядок
     * @return безпечний для файлової системи рядок
     */
    private static String sanitizeFileName(String s) {
        if (s == null) return "unknown";
        return s.replaceAll("[^A-Za-z0-9-_]", "_");
    }

    /**
     * Логує рядок з переходом на новий рядок у консоль та файл/буфер.
     *
     * @param s рядок для логування
     */
    private void logln(String s) {
        System.out.println(s);
        if (!loggerReady) {
            preLogBuffer.append(s).append(System.lineSeparator());
        } else if (log != null) {
            log.println(s);
        }
    }

    /**
     * Логує рядок без переходу на новий рядок у консоль та файл/буфер.
     *
     * @param s рядок для логування
     */
    private void logRaw(String s) {
        System.out.print(s);
        if (!loggerReady) {
            preLogBuffer.append(s);
        } else if (log != null) {
            log.print(s);
        }
    }

    /**
     * Закриває лог-файл, якщо ведеться запис у файл.
     */
    private void closeLog() {
        if (log != null && writingToFile) {
            log.flush();
            log.close();
        }
    }


    /**
     * Відображає каталог дроїдів та записує його у лог.
     * Використовує перехоплення System.out для запису виводу у буфер.
     */
    private void showAndRecordCatalog() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(baos, true);
        try {
            System.setOut(capture);
            catalogDroid();
        } finally {
            System.setOut(originalOut);
        }
        String menu = baos.toString();
        logRaw(menu);
    }

    /**
     * Зчитує індекс дроїда з перевіркою коректності та логуванням вводу.
     *
     * @param sc об'єкт Scanner для вводу
     * @return коректний індекс у діапазоні [0..MAX_INDEX]
     */
    private int readIndex(Scanner sc) {
        int i;
        do {
            logRaw("Введіть індекс [0.." + MAX_INDEX + "]: ");
            while (!sc.hasNextInt()) {
                sc.next();
                logln("Некоректне значення. Спробуйте ще раз.");
                logRaw("Введіть індекс [0.." + MAX_INDEX + "]: ");
            }
            i = sc.nextInt();
            logln(String.valueOf(i));
        } while (i < 0 || i > MAX_INDEX);
        return i;
    }

    // ---------- Ігрова логіка ----------

    /**
     * Застосовує бонус карти до дроїда, якщо його елемент співпадає з елементом карти.
     *
     * @param droid дроїд, до якого застосовується бонус
     * @param map   карта, що надає бонус
     */
    private void applyBonus(BaseDroid droid, BaseMap map) {
        if (droid == null || map == null) return;

        if (!droid.getElement().equalsIgnoreCase(map.getElement())) {
            logln(droid.getName() + " не отримує бонус - елемент не співпадає (" +
                    droid.getElement() + " vs " + map.getElement() + ")");
            return;
        }

        int bonus = map.getBonus();
        String element = map.getElement().toLowerCase();

        switch (element) {
            case "earth" -> {
                droid.setCurrentHp(droid.getCurrentHp() + bonus);
                logln(droid.getName() + " отримує +" + bonus + " HP від карти Earth!");
            }
            case "fire" -> {
                droid.setAttack(droid.getAttack() + bonus);
                logln(droid.getName() + " отримує +" + bonus + " до атаки від карти Fire!");
            }
            case "water" -> {
                droid.setMoveSpeed(droid.getMoveSpeed() + bonus);
                logln(droid.getName() + " отримує +" + bonus + " до швидкості від карти Water!");
            }
            case "wind" -> {
                droid.setRange(droid.getRange() + bonus);
                logln(droid.getName() + " отримує +" + bonus + " до дальності від карти Wind!");
            }
            default -> logln("Невідомий елемент карти: " + element);
        }
    }

    /**
     * Запускає бій між двома дроїдами. У кожному раунді спочатку ходить перший дроїд,
     * потім другий. Гра триває до знищення одного з дроїдів або досягнення ліміту раундів.
     */
    public void start() {
        logln("\n=== Бій 1 на 1 ===");
        logln("Мапа: " + map.getClass().getSimpleName());
        printStatus();

        final int MAX_ROUNDS = 200;
        int round = 1;

        while (alive(droid1) && alive(droid2) && round <= MAX_ROUNDS) {
            logln("\n--- Раунд " + round + " ---");
            if(step(droid1, droid2)== 1) {
                break;
            }
            if (!alive(droid2)) break;
            if (step(droid2, droid1) == 1) {
                break;
            }
            if (!alive(droid1)) break;
            printStatus();
            round++;
        }
        printWinnerWithRoundLimit();
        closeLog();
    }

    /**
     * Виконує хід одного дроїда проти іншого.
     *
     * @param attacker дроїд, який атакує
     * @param defender дроїд, який захищається
     */
    private int step(BaseDroid attacker, BaseDroid defender) {
        String res = attacker.actionMenu(asList(attacker), asList(defender));
        log.println(res);
        if (res == "stop") {
            return 1;
        }
        return 0;
    }

    /**
     * Перевіряє, чи живий дроїд.
     *
     * @param d дроїд для перевірки
     * @return true, якщо дроїд живий, false - якщо ні
     */
    private boolean alive(BaseDroid d) { return d != null && d.isAlive(); }

    /**
     * Виводить поточний статус обох дроїдів.
     */
    private void printStatus() {
        logln("\nПоточний стан:");
        System.out.println(droid1);
        if (log != null) log.println(droid1);
        System.out.println(droid2);
        if (log != null) log.println(droid2);
    }

    /**
     * Визначає та виводить переможця гри з урахуванням ліміту раундів.
     */
    private void printWinnerWithRoundLimit() {
        if (alive(droid1) && !alive(droid2)) {
            logln("\nПереможець: " + droid1.getName());
        } else if (!alive(droid1) && alive(droid2)) {
            logln("\nПереможець: " + droid2.getName());
        } else if (!alive(droid1) && !alive(droid2)) {
            logln("\nНічия — обидва знищені.");
        } else {
            logln("\nНічия — досягнуто ліміту раундів.");
        }
    }
}