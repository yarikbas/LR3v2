package myGame.map;

/**
 * Абстрактний базовий клас для всіх ігрових карт.
 * Визначає основні властивості карти: елемент, бонус, межі ігрового поля.
 *
 * <p>Кожна карта має:
 * <ul>
 *   <li>Елемент (earth, fire, water, wind) - визначає тип бонусу</li>
 *   <li>Бонус - числове значення бонусу для відповідних дроїдів</li>
 *   <li>Межі позицій - визначають розмір ігрового поля</li>
 * </ul>
 */
public abstract class BaseMap {

    /** Елемент карти (earth, fire, water, wind) */
    protected final String element;

    /** Значення бонусу, що надається дроїдам відповідного елемента */
    protected final int bonus;

    /** Максимальна позиція на карті (включно) */
    protected int maxPosition;

    /** Мінімальна позиція на карті (завжди 0) */
    protected final int minPosition;

    /**
     * Конструктор базової карти.
     *
     * @param element елемент карти (earth, fire, water, wind)
     * @param bonus значення бонусу для дроїдів відповідного елемента
     * @param maxPosition максимальна позиція на карті
     */
    public BaseMap(String element, int bonus, int maxPosition) {
        this.element = element;
        this.bonus = bonus;
        this.maxPosition = maxPosition;
        this.minPosition = 0;
    }

    /**
     * Повертає елемент карти.
     *
     * @return елемент карти (earth, fire, water, wind)
     */
    public String getElement() {
        return element;
    }

    /**
     * Повертає значення бонусу карти.
     *
     * @return числове значення бонусу
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Повертає максимальну позицію на карті.
     *
     * @return максимальна позиція (включно)
     */
    public int getMaxPosition() {
        return maxPosition;
    }

    /**
     * Встановлює максимальну позицію на карті.
     *
     * @param maxPosition нова максимальна позиція
     */
    public void setMaxPosition(int maxPosition) {
        this.maxPosition = maxPosition;
    }

    /**
     * Повертає мінімальну позицію на карті.
     * Завжди повертає 0.
     *
     * @return мінімальна позиція (завжди 0)
     */
    public int getMinPosition() {
        return minPosition;
    }
}