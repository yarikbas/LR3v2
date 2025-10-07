package myGame.droid;

import java.util.List;

/**
 * Клас {@code waterStormDroid} — дроїд стихії води з балансом між атакою та підтримкою.
 * <p>
 * Унікальна здатність — {@link #heal(List)}, що дозволяє відновити
 * здоров'я союзника до максимального значення.
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Хвиля — завдає шкоди ворогу у радіусі атаки</li>
 *   <li>Хіл — повністю відновлює здоров'я першого пораненого союзника</li>
 *   <li>Переміщення — випадкове зміщення {@link #changePosition()}</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class WaterStormDroid extends BaseDroid {

    /**
     * Створює дроїда "StormDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 125</li>
     *   <li>Current HP = 125</li>
     *   <li>Move speed = 2</li>
     *   <li>Range = 2</li>
     *   <li>Attack = 70</li>
     *   <li>Element = "water"</li>
     * </ul>
     */
    public WaterStormDroid() {
        super("StormDroid", 125, 125, 2, 2, 70, "water");
    }

    /**
     * Лікує першого союзника у списку, який має неповний запас здоров'я,
     * відновлюючи його HP до максимального.
     * <p>Як тільки перший поранений знайдений і вилікуваний, метод завершується.</p>
     *
     * @param friends список союзних дроїдів, серед яких шукається поранений
     */
    public String heal(List<BaseDroid> friends) {
        for (BaseDroid temp : friends) {
            if (temp.getMaxHp() != temp.getCurrentHp()) {
                temp.setCurrentHp(temp.getMaxHp());
                System.out.println(this.getName() + " підіймає здоров'я " + temp.getName());
                return this.getName() + " підіймає здоров'я " + temp.getName();
            }
        }
        return this.getName() + "не підіймає нікому здоров'я";
    }

    /**
     * Відображає меню дій і виконує обрану дію гравця.
     * <ol>
     *   <li>Хвиля — атака по першому доступному ворогу у радіусі</li>
     *   <li>Хіл — лікування союзника {@link #heal(List)}</li>
     *   <li>Підійти — переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Water Storm Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Хвиля (нанести шкоду стихією)");
        System.out.println("2. Хіл (повністю відновити здоров'я союзника)");
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
                                .append(" здіймає потужну хвилю проти ")
                                .append(def.getName())
                                .append("!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" спробував накрити хвилею ")
                                .append(def.getName())
                                .append(", але промахнувся!\n");
                    }
                }
                break;

            case 2:
                actionResult.append(heal(attackers))
                        .append("\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" пересувається у нову позицію!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }

}
