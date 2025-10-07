package myGame.droid;

import java.util.List;
import java.util.Objects;

/**
 * Клас {@code earthHammerDroid} — наземний дроїд-воїн з величезним молотом.
 * <p>
 * Має середній запас HP і середню силу атаки.
 * Унікальна здатність — {@link #earthquake(List, List)}, яка завдає шкоди всім
 * дроїдам на полі бою, крім тих, що мають стихію "wind".
 *
 * <p>Меню дій включає:
 * <ul>
 *   <li>Атака молотом — удар по одному ворогу в радіусі</li>
 *   <li>Землетрус — шкода всім ворожим та союзним дроїдам, окрім "wind"</li>
 *   <li>Переміщення на нову позицію</li>
 * </ul>
 *
 * @since 1.0
 */
public class EarthHammerDroid extends BaseDroid {

    /**
     * Створює нового дроїда "HammerDroid" із фіксованими характеристиками:
     * <ul>
     *   <li>Max HP = 150</li>
     *   <li>Attack = 50</li>
     *   <li>Move speed = 2</li>
     *   <li>Range = 1</li>
     *   <li>Element = "earth"</li>
     * </ul>
     */
    public EarthHammerDroid() {
        super("HammerDroid", 150, 150, 2, 1, 50, "earth");
    }

    /**
     * Унікальна здатність землетрусу: завдає шкоди одразу обом командам
     * (включно із союзниками), крім дроїдів зі стихією "wind".
     *
     * @param team1 перша команда (може включати союзників)
     * @param team2 друга команда (супротивники)
     */
    public void earthquake(List<BaseDroid> team1, List<BaseDroid> team2) {
        giveEarthquakeDamage(team1);
        giveEarthquakeDamage(team2);
    }

    /**
     * Допоміжний метод, що завдає шкоди всім дроїдам зі списку,
     * крім тих, що мають стихію "wind".
     *
     * @param receivers список дроїдів, які отримують шкоду
     */
    public void giveEarthquakeDamage(List<BaseDroid> receivers) {
        for (BaseDroid droid : receivers) {
            String el = droid.getElement();
            if (!Objects.equals(el, "wind")) {
                droid.receiveDamage(this.getAttack());
            }
        }
    }

    /**
     * Відображає меню дій та виконує вибір гравця.
     * <p>
     * Варіанти:
     * <ol>
     *   <li>Удар молотом — пошкодження першого ворога в радіусі</li>
     *   <li>Землетрус — масова атака {@link #earthquake(List, List)}</li>
     *   <li>Переміщення — випадкове зміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд (союзники)
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Earth Hammer Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Hammer (нанести шкоду молотом)");
        System.out.println("2. Зробити землетрус (шкода всім нелітаючим юнітам)");
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
                                .append(" обрушує молот на ")
                                .append(def.getName())
                                .append("!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" махає молотом, але не влучає по ")
                                .append(def.getName())
                                .append("!\n");
                    }
                }
                break;

            case 2:
                earthquake(attackers, defenders);
                actionResult.append(this.getName())
                        .append(" спричиняє підземний поштовх — землетрус котиться полем!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" змінює позицію, громихаючи бронею!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }
}
