package myGame.droid;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Базовий клас для всіх дроїдів гри.
 * <p>
 * Містить спільні характеристики (HP, атака, дальність, позиція, стихія) та базову бойову логіку:
 * отримання/нанесення шкоди, перевірка дальності, переміщення.
 * Наслідники мають реалізувати власне меню дій {@link #actionMenu(List, List)}.
 *
 * <p><b>Безпечні покращення в цій версії:</b></p>
 * <ul>
 *   <li>Один спільний {@code RNG} для всіх екземплярів (без постійних new Random()).</li>
 *   <li>Клемп поточного HP у межах [0..maxHp] у {@link #receiveDamage(int)} та {@link #setCurrentHp(int)}.</li>
 *   <li>Коректна перевірка дальності через абсолютну відстань у {@link #inRange(BaseDroid)}.</li>
 *   <li>Хелпер {@link #isAlive()}.</li>
 *   <li>Косметичний фікс {@link #toString()}.</li>
 * </ul>
 *
 * <p>Ці зміни не вимагають правок у класах-нащадках.</p>
 */
public abstract class BaseDroid {

    /** Спільний генератор випадкових чисел для всіх екземплярів. */
    private static final Random RNG = new Random();

    Scanner sc = new Scanner(System.in);

    /** (Необов'язково) Константи меж арени — можна використовувати у нащадках. */
    public static int arenaMin = 0;
    public static  int arenaMax = 9;

    /** Максимальний запас здоров'я. */
    protected  int maxHp;

    /** Поточний запас здоров'я (0..maxHp). */
    protected int currentHp;

    /** Швидкість переміщення (клітинок за крок). */
    protected int moveSpeed;

    /** Дальність атаки (у клітинках). */
    protected int range;

    /** Сила атаки (базова шкода). */
    protected int attack;

    /** Поточна позиція на осі (умовна координата). */
    protected int currentPosition;

    /** Стихія дроїда (наприклад: "earth", "water", "wind", "fire"). */
    protected final String element;

    /** Ім'я/тип дроїда для відображення. */
    protected String name;

    /**
     * Порожній конструктор за замовчуванням (корисний для серіалізації/тестів).
     * Ініціалізує поля нульовими/дефолтними значеннями.
     */
    public BaseDroid() {
        this.maxHp = 0;
        this.currentHp = 0;
        this.moveSpeed = 0;
        this.range = 0;
        this.attack = 0;
        this.currentPosition = 0;
        this.element = "unknown";
        this.name = "noname";
    }

    /**
     * Повний конструктор базового дроїда.
     *
     * @param name       ім'я/тип дроїда
     * @param maxHp      максимальний HP
     * @param currentHp  поточний HP (буде обрізано до [0..maxHp])
     * @param moveSpeed  швидкість переміщення (клітинки за хід)
     * @param range      дальність атаки (клітинки)
     * @param attack     сила атаки (базова шкода)
     * @param element    стихія дроїда ("earth", "water", "wind", "fire" тощо)
     */
    public BaseDroid(String name, int maxHp, int currentHp, int moveSpeed, int range, int attack, String element) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = Math.max(0, Math.min(currentHp, maxHp)); // клемп у межах
        this.moveSpeed = moveSpeed;
        this.range = range;
        this.attack = attack;
        this.currentPosition = RNG.nextInt((arenaMax - arenaMin) + 1) + arenaMin;
        this.element = element;
    }

    /** @return базова сила атаки дроїда */
    public int getAttack() { return attack; }

    /**
     * Встановити базову силу атаки.
     * @param attack нове значення атаки
     */
    public void setAttack(int attack) { this.attack = attack; }

    /** @return поточна позиція на мапі (умовна координата) */
    public int getCurrentPosition() { return currentPosition; }

    /**
     * Примусово встановити позицію (без перевірки меж).
     * @param currentPosition нова позиція
     */
    public void setCurrentPosition(int currentPosition) { this.currentPosition = currentPosition; }

    /** @return максимальний HP */
    public int getMaxHp() { return maxHp; }

    /** @return поточний HP */
    public int getCurrentHp() { return currentHp; }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public static void setArenaMin(int arenaMin) {
        BaseDroid.arenaMin = arenaMin;
    }

    public static int getArenaMin() {
        return arenaMin;
    }

    public static int getArenaMax() {
        return arenaMax;
    }

    public static void setArenaMax(int arenaMax) {
        BaseDroid.arenaMax = arenaMax;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Встановити поточний HP з обрізанням до діапазону [0..maxHp].
     * @param currentHp новий HP
     */
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, maxHp));
    }

    /** @return швидкість переміщення у клітинках за крок */
    public int getMoveSpeed() { return moveSpeed; }

    /** @return дальність атаки у клітинках */
    public int getRange() { return range; }

    /**
     * Встановити дальність атаки.
     * @param range нова дальність
     */
    public void setRange(int range) { this.range = range; }

    /** @return стихія дроїда ("earth", "water", "wind", "fire" тощо) */
    public String getElement() { return element; }

    /** @return ім'я/тип дроїда */
    public String getName() { return name; }

    /**
     * Чи живий дроїд.
     * @return true якщо поточний HP більше 0
     */
    public boolean isAlive() { return currentHp > 0; }

    /**
     * Отримати шкоду. HP не опуститься нижче нуля.
     * @param damage величина шкоди
     */
    public void receiveDamage(int damage) {
        int safe = Math.max(0, damage);
        currentHp = Math.max(0, currentHp - safe);
    }

    /**
     * Перевірити, чи ціль у радіусі атаки (за модулем різниці позицій).
     * @param defender цільовий дроїд
     * @return true, якщо відстань між дроїдами &le; {@link #range}
     */
    public boolean inRange(BaseDroid defender) {
        int distance = Math.abs(defender.currentPosition - this.currentPosition);
        return distance <= this.range;
    }

    /**
     * Завдати шкоди цілі, якщо вона в радіусі атаки і атакувальник живий.
     * @param defender цільовий дроїд
     * @return true, якщо атака відбулася; false, якщо ціль поза радіусом або атакувальник мертвий
     */
    public boolean giveDamage(BaseDroid defender) {
        if (!isAlive()) return false;
        if (inRange(defender)) {
            defender.receiveDamage(this.attack);
            return true;
        }
        return false;
    }

    /**
     * Випадкове переміщення на {@link #moveSpeed} ліворуч або праворуч без перевірки меж.
     */
    public void changePosition() {
        int dir = RNG.nextInt(2) == 0 ? -1 : 1;
        int next = this.currentPosition + dir * this.moveSpeed;
        this.currentPosition = Math.max(arenaMin, Math.min(arenaMax, next));
    }

    /**
     * Меню дій дроїда (інтерактивна логіка ходу).
     * Наслідники мають реалізувати власний набір дій та їхній вплив на команди.
     *
     * @param attackers команда, до якої належить поточний дроїд (союзники)
     * @param defenders команда супротивника (цілі)
     */
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders){
        System.out.println("Введіть 0 для закінчення гри");
        return "";
    }

    /**
     * Текстове представлення стану дроїда (для логів/дебагу).
     * @return рядок з основними параметрами
     */
    @Override
    public String toString() {
        return name +
                " HP=" + currentHp + "/" + maxHp +
                " ms=" + moveSpeed +
                " range=" + range +
                " atk=" + attack +
                " element=" + element +
                " current position=" + currentPosition;
    }
}
