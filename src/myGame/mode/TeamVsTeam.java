package myGame.mode;

import myGame.droid.*;
import myGame.map.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.asList;
import static myGame.droid.DroidFactory.DroidConstruct.*;

/**
 * Режим гри "Команда проти команди" для битви між двома командами дроїдів.
 * Клас реалізує логіку гри для режиму команда проти команди, включаючи вибір дроїдів,
 * управління боєм, систему бонусів за елементи карти та логування всіх подій гри.
 *
 * <p>Особливості режиму:
 * <ul>
 *   <li>Гравці створюють команди довільного розміру (1-6 дроїдів)</li>
 *   <li>Автоматичний вибір карти з випадковим середовищем</li>
 *   <li>Система бонусів за відповідність елементу дроїда та карти</li>
 *   <li>Автоматична розстановка команд на карті</li>
 *   <li>Покрокова битва з логуванням у файл</li>
 * </ul>
 */
public class TeamVsTeam {

    // ---------- Конфіг ----------
    /** Максимальний індекс дроїда у каталозі */
    private static final int MAX_INDEX = 7;
    /** Максимальна кількість раундів гри */
    private static final int MAX_ROUNDS = 200;
    /** Форматувальник часу для імені лог-файлу */
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    // ---------- Стан ----------
    /** Команда A - перша команда дроїдів */
    private final List<BaseDroid> teamA = new ArrayList<>();
    /** Команда B - друга команда дроїдів */
    private final List<BaseDroid> teamB = new ArrayList<>();
    /** Поточна карта гри */
    private final BaseMap map;

    // ---------- Логер ----------
    private PrintWriter log;
    private boolean writingToFile = false;
    private boolean loggerReady = false;
    private final StringBuilder preLogBuffer = new StringBuilder();

    private final String logDirectory;
    private final boolean appendIfExists;

    // ---------- Конструктори ----------

    /**
     * Конструктор за замовчуванням, використовує поточну директорію для логів.
     */
    public TeamVsTeam() { this(".", false); }

    /**
     * Конструктор з вказанням директорії для логів.
     *
     * @param logDirectory директорія для збереження лог-файлів
     */
    public TeamVsTeam(String logDirectory) { this(logDirectory, false); }

    /**
     * Основний конструктор для створення режиму команда проти команди.
     *
     * @param logDirectory директорія для збереження лог-файлів
     * @param appendIfExists чи додавати до існуючого лог-файлу (true) чи перезаписувати (false)
     */
    public TeamVsTeam(String logDirectory, boolean appendIfExists) {
        this.logDirectory = (logDirectory == null || logDirectory.isBlank()) ? "." : logDirectory;
        this.appendIfExists = appendIfExists;

        this.map = chooseMap();
        BaseDroid.arenaMin = map.getMinPosition();
        BaseDroid.arenaMax = map.getMaxPosition();

        Scanner sc = new Scanner(System.in);

        logln("\n=== Налаштування Команда vs Команда ===");
        logln("Мапа: " + map.getClass().getSimpleName()
                + " (element=" + map.getElement() + ", bonus=" + map.getBonus() + ")");
        logln("Арена: [" + map.getMinPosition() + " .. " + map.getMaxPosition() + "]");

        int sizeA = askIntLogged(sc, "Вкажіть розмір Команди A (1..6): ", 1, 6);
        int sizeB = askIntLogged(sc, "Вкажіть розмір Команди B (1..6): ", 1, 6);

        logln("\nВиберіть дроїдів для Команди A:");
        fillTeamInteractive(sc, teamA, sizeA);

        logln("\nВиберіть дроїдів для Команди B:");
        fillTeamInteractive(sc, teamB, sizeB);

        // Розстановка і бонуси — як доповнення до oneVsOne
        spawnTeams();
        teamA.forEach(this::applyBonus);
        teamB.forEach(this::applyBonus);

        // Ініціалізуємо логер з унікальною назвою та зливаємо пролог
        initLoggerWithUniqueName();

        logln("Мапа: " + map.getClass().getSimpleName()
                + " (element=" + map.getElement() + ", bonus=" + map.getBonus() + ")");
        logln("Арена: [" + map.getMinPosition() + " .. " + map.getMaxPosition() + "]");
        printStatus();
    }

    // ---------- Вибір карти ----------

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

    // ---------- Логер ----------

    /**
     * Ініціалізує логер з унікальним ім'ям файлу на основі команд та часу.
     * Автоматично переносить попередні повідомлення з буфера у файл.
     */
    private void initLoggerWithUniqueName() {
        String firstA = teamA.isEmpty() ? "A" : sanitizeFileName(teamA.get(0).getName());
        String firstB = teamB.isEmpty() ? "B" : sanitizeFileName(teamB.get(0).getName());
        String ts = LocalDateTime.now().format(TS);

        // Формуємо ім'я файлу напряму
        String fileName = String.format(
                "team_vs_team_%dv%d_%s_vs_%s_%s.log",
                teamA.size(), teamB.size(), firstA, firstB, ts
        );

        File dir = new File(logDirectory);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("Не вдалося створити директорію для логів: " + dir.getAbsolutePath());
        }

        File f = new File(dir, fileName);

        try {
            this.log = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(f, appendIfExists), StandardCharsets.UTF_8),
                    true
            );
            this.writingToFile = true;
            this.loggerReady = true;

            // зливаємо те, що було до ініціалізації логера
            if (preLogBuffer.length() > 0) {
                log.print(preLogBuffer.toString());
                log.flush();
                preLogBuffer.setLength(0);
            }

            logln("\n=== Логи бою збережено у файл: " + f.getAbsolutePath() + " ===");
            logln("Склади: A=" + teamA.size() + ", B=" + teamB.size());
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
            log.flush();
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

    // ---------- Перехоплення і запис меню каталогу (як у oneVsOne) ----------

    /**
     * Відображає каталог дроїдів та записує його у лог.
     * Використовує перехоплення System.out для запису виводу у буфер.
     */
    private void showAndRecordCatalog() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            catalogDroid();              // друкує в «підмінений» System.out
        } finally {
            System.setOut(originalOut);  // повертаємо консоль
        }
        String menu = baos.toString(StandardCharsets.UTF_8);
        logRaw(menu);                    // показати і записати у лог/буфер, як у oneVsOne
    }

    // ---------- Інтерактивний ввід з логуванням ----------

    /**
     * Запитує у користувача ціле число з логуванням вводу.
     *
     * @param sc об'єкт Scanner для вводу
     * @param prompt текст підказки
     * @param min мінімальне допустиме значення
     * @param max максимальне допустиме значення
     * @return коректне ціле число у вказаному діапазоні
     */
    private int askIntLogged(Scanner sc, String prompt, int min, int max) {
        while (true) {
            logRaw(prompt);
            if (!sc.hasNextInt()) {
                String garbage = sc.next();
                logln(garbage); // ехо того, що ввів користувач
                logln("Некоректне число. Спробуйте ще раз.");
                continue;
            }
            int val = sc.nextInt();
            logln(String.valueOf(val)); // ехо валідного вводу
            if (val < min || val > max) {
                logln("Діапазон: [" + min + ";" + max + "]. Спробуйте ще раз.");
                continue;
            }
            return val;
        }
    }

    /**
     * Заповнює команду дроїдами через інтерактивний вибір.
     *
     * @param sc об'єкт Scanner для вводу
     * @param team команда для заповнення
     * @param size кількість дроїдів у команді
     */
    private void fillTeamInteractive(Scanner sc, List<BaseDroid> team, int size) {
        for (int i = 0; i < size; i++) {
            logln("\nОберіть дроїда #" + (i + 1) + ":");
            showAndRecordCatalog();
            int idx = askIntLogged(sc, "Введіть індекс [0.." + MAX_INDEX + "]: ", 0, MAX_INDEX);
            BaseDroid d = yourDroid(idx);
            team.add(d);
            logln("Додано: " + d.getName() + "{" + d.getElement() + "}");
        }
    }

    // ---------- Ігрова логіка (дзеркально до oneVsOne) ----------

    /**
     * Запускає бій між командами. У кожному раунді спочатку ходить команда A,
     * потім команда B. Гра триває до знищення однієї з команд або досягнення ліміту раундів.
     */
    public void start() {
        logln("\n=== Автобій Команда vs Команда ===");
        logln("Мапа: " + map.getClass().getSimpleName());
        printStatus();

        int round = 1;
        while (teamAlive(teamA) && teamAlive(teamB) && round <= MAX_ROUNDS) {
            logln("\n--- Раунд " + round + " ---");

            if(teamStep(teamA, teamB) == 1){
                break;
            }
            if (!teamAlive(teamB)) break;

            if(teamStep(teamB, teamA) == 1) {
                break;
            }

            printStatus();
            round++;
        }
        printWinnerWithRoundLimit();
        closeLog();
    }

    /**
     * Виконує ходи всіх живих дроїдів команди.
     *
     * @param attackers команда, яка атакує
     * @param defenders команда, яка захищається
     */
    private int teamStep(List<BaseDroid> attackers, List<BaseDroid> defenders) {
        for (BaseDroid atk : attackers) {
            if (atk == null || !atk.isAlive()) continue;
            if (!teamAlive(defenders)) break;

            String out = atk.actionMenu(asList(atk), defenders);
            if (log != null) {
                log.println(out);
            }
            if(out == "stop"){
                return 1;
            }
            System.out.println(out);
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
     * Перевіряє, чи є в команді хоча б один живий дроїд.
     *
     * @param team команда для перевірки
     * @return true, якщо в команді є живий дроїд, false - якщо ні
     */
    private boolean teamAlive(List<BaseDroid> team) {
        for (BaseDroid d : team) if (alive(d)) return true;
        return false;
    }

    /**
     * Виводить поточний статус обох команд.
     */
    private void printStatus() {
        logln("\nПоточний стан:");

        logln("[Команда A]");
        for (BaseDroid d : teamA) {
            System.out.println(d);
            if (log != null) log.println(d);
        }

        logln("[Команда B]");
        for (BaseDroid d : teamB) {
            System.out.println(d);
            if (log != null) log.println(d);
        }
    }

    /**
     * Визначає та виводить переможця гри з урахуванням ліміту раундів.
     */
    private void printWinnerWithRoundLimit() {
        boolean aAlive = teamAlive(teamA);
        boolean bAlive = teamAlive(teamB);
        if (aAlive && !bAlive) {
            logln("\nПереможець: Команда A 🎉");
        } else if (!aAlive && bAlive) {
            logln("\nПереможець: Команда B 🎉");
        } else if (!aAlive && !bAlive) {
            logln("\nНічия — обидві команди знищені.");
        } else {
            logln("\nНічия — досягнуто ліміту раундів.");
        }
    }

    /**
     * Застосовує бонус карти до дроїда, якщо його елемент співпадає з елементом карти.
     *
     * @param droid дроїд, до якого застосовується бонус
     */
    private void applyBonus(BaseDroid droid) {
        if (!droid.getElement().equalsIgnoreCase(map.getElement())) return;

        int bonus = map.getBonus();
        switch (map.getElement().toLowerCase()) {
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
        }
    }

    // ---------- Розстановка ----------

    /**
     * Розставляє команди на протилежних кінцях карти.
     */
    private void spawnTeams() {
        int min = map.getMinPosition();
        int max = map.getMaxPosition();
        int width = Math.max(1, max - min);

        int leftEnd = min + width / 3;
        int rightStart = max - width / 3;

        placeLine(teamA, min, leftEnd);
        placeLine(teamB, rightStart, max);
    }

    /**
     * Розставляє дроїдів команди вздовж лінії від from до to.
     *
     * @param team команда для розстановки
     * @param from початкова позиція
     * @param to кінцева позиція
     */
    private void placeLine(List<BaseDroid> team, int from, int to) {
        if (team.isEmpty())
            return;
        if (from > to) {
            int t = from;
            from = to;
            to = t;
        }
        int span = Math.max(0, to - from);
        for (int i = 0; i < team.size(); i++) {
            int pos = (span == 0) ? from : from + (i * span / Math.max(1, team.size() - 1));
            team.get(i).setCurrentPosition(pos);
        }
    }
}