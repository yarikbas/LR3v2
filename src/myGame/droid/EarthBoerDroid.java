package myGame.droid;

import java.util.List;
import java.util.Random;

/**
 * Клас {@code earthBoerDroid} — наземний дроїд типу "бурильник".
 * <p>
 * Має підвищений запас HP та високу силу атаки.
 * Унікальна здатність — {@link #drillTunnel()}, яка дозволяє переміститися
 * в нову випадкову позицію на мапі (імітація прокопаного тунелю).
 *
 * <p>Меню дій дроїда включає:
 * <ul>
 *   <li>Атака буром (звичайна атака з високим уроном)</li>
 *   <li>Пробурити тунель (телепортація на випадкову позицію)</li>
 *   <li>Переміститися на сусідню клітинку (виклик {@link #changePosition()})</li>
 * </ul>
 */
public class EarthBoerDroid extends BaseDroid {

    /**
     * Створює нового дроїда "BoerDroid" із фіксованими характеристиками:
     * <ul>
     *   <li>Max HP = 200</li>
     *   <li>Attack = 70</li>
     *   <li>Move speed = 1</li>
     *   <li>Range = 1</li>
     *   <li>Element = "earth"</li>
     * </ul>
     */
    public EarthBoerDroid() {
        super("BoerDroid", 200, 200, 1, 1, 70, "earth");
    }

    /**
     * Унікальна здатність бурильника:
     * переміщення у випадкову позицію на арені (0..9).
     * Використовує {@link Random} для генерації нової координати.
     */
    public void drillTunnel() {
        Random rand = new Random();
        this.setCurrentPosition(rand.nextInt(10));
    }

    /**
     * Відображає меню доступних дій і виконує обрану.
     * <p>
     * Варіанти:
     * <ol>
     *   <li>Атака буром — наносить шкоду першому ворогу в радіусі</li>
     *   <li>Пробурити тунель — випадкове переміщення {@link #drillTunnel()}</li>
     *   <li>Переміститися — випадкове зміщення ліворуч/праворуч {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників (потенційні цілі для атаки)
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Earth Boer Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Др-дрррр (нанести шкоду буром)");
        System.out.println("2. Пробурити тунель (опинитись на новому випадковому місці)");
        System.out.println("3. Підійти");
        System.out.print("Оберіть дію: ");

        int choice = sc.nextInt();
        if (choice == 0) {
            return "stop";
        }
        StringBuilder actionResult = new StringBuilder();

        switch (choice) {
            case 1:
                for (BaseDroid def : defenders) {
                    boolean res = this.giveDamage(def);
                    if (res) {
                        actionResult.append(this.getName())
                                .append(" завдає шкоди буром по ")
                                .append(def.getName())
                                .append("! Дрррр!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" намагається пробурити удар, але промахується!\n");
                    }
                }
                break;

            case 2:
                drillTunnel();
                actionResult.append(this.getName())
                        .append(" бурить тунель і зникає під землею... з’являється в іншому місці!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" змінює позицію, гуркочучи буром!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }
}
