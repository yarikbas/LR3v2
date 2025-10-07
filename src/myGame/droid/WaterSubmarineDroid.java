package myGame.droid;

import java.util.List;
import java.util.Random;

/**
 * Клас {@code waterSubmarineDroid} — водний дроїд-підводник.
 * <p>
 * Має високий запас здоров'я та підвищену атаку.
 * Його унікальна здатність — {@link #positionNearEnemy(List)},
 * яка дозволяє переміститися ближче до випадкового ворога
 * (на відстань у 2 позиції).
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Торпеда — завдає шкоди цілі в радіусі</li>
 *   <li>Приплив — переміщує дроїда на 2 клітинки від випадкового супротивника</li>
 *   <li>Переміщення — випадкове зміщення {@link #changePosition()}</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class WaterSubmarineDroid extends BaseDroid {

    /**
     * Створює дроїда "SubmarineDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 175</li>
     *   <li>Current HP = 175</li>
     *   <li>Move speed = 1</li>
     *   <li>Range = 2</li>
     *   <li>Attack = 80</li>
     *   <li>Element = "water"</li>
     * </ul>
     */
    public WaterSubmarineDroid() {
        super("SubmarineDroid", 175, 175, 1, 2, 80, "water");
    }

    /**
     * Переміщує дроїда на 2 клітинки від випадково обраного ворога.
     * <p>Використовується як унікальна здатність "приплив".</p>
     *
     * @param enemys список ворожих дроїдів
     */
    public void positionNearEnemy(List<BaseDroid> enemys) {
        Random rand = new Random();
        this.setCurrentPosition(
                enemys.get(rand.nextInt(enemys.size())).getCurrentPosition() - 2
        );
    }

    /**
     * Відображає меню дій та виконує вибір гравця.
     * <ol>
     *   <li>Торпеда — одиночна атака по першій доступній цілі</li>
     *   <li>Приплив — переміщення на 2 клітинки від випадкового ворога {@link #positionNearEnemy(List)}</li>
     *   <li>Підійти — переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Water Submarine Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Торпеда (нанести шкоду ракетою)");
        System.out.println("2. Приплив (стає на 2 позиції від противника)");
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
                                .append(" випускає торпеду у ")
                                .append(def.getName())
                                .append("!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" промахнувся торпедою повз ")
                                .append(def.getName())
                                .append("!\n");
                    }
                }
                break;

            case 2:
                positionNearEnemy(defenders);
                actionResult.append(this.getName())
                        .append(" несподівано виринає поруч із ворогом!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" змінює підводну позицію!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }
}
