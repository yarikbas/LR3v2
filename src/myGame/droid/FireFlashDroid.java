package myGame.droid;

import java.util.List;
import java.util.Random;

/**
 * Клас {@code fireFlashDroid} — вибуховий вогняний дроїд із високою базовою атакою.
 * <p>
 * Унікальна масова здатність — {@link #volcanicEruption(List, List)}, що завдає шкоди
 * всім дроїдам на випадковій позиції (як союзникам, так і ворогам).
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Вогняний кулак — одиночна атака по цілі в радіусі</li>
 *   <li>Виверження — AoE по випадковій позиції арени</li>
 *   <li>Переміститися — випадкове зміщення на крок {@link #changePosition()}</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class FireFlashDroid extends BaseDroid {

    /**
     * Створює дроїда "FlashDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 75</li>
     *   <li>Current HP = 75</li>
     *   <li>Move speed = 1</li>
     *   <li>Range = 2</li>
     *   <li>Attack = 100</li>
     *   <li>Element = "fire"</li>
     * </ul>
     */
    public FireFlashDroid() {
        super("FlashDroid", 75, 75, 1, 2, 100, "fire");
    }

    /**
     * Завдає шкоди всім дроїдам зі списку, чия позиція збігається з переданою.
     * Використовується як частина механіки виверження вулкана.
     *
     * @param team     список потенційних отримувачів шкоди
     * @param position цільова позиція арени (наприклад, 0..9)
     */
    public void giveVolcanicEruptionDamage(List<BaseDroid> team, int position) {
        for (BaseDroid temp : team) {
            if (temp.getCurrentPosition() == position) {
                temp.receiveDamage(this.getAttack());
            }
        }
    }

    /**
     * Масова атака: обирає випадкову позицію на арені та завдає шкоди всім дроїдам,
     * що стоять на ній (і союзникам, і ворогам).
     *
     * @param team1 перша команда (зазвичай союзники)
     * @param team2 друга команда (зазвичай супротивники)
     */
    public void volcanicEruption(List<BaseDroid> team1, List<BaseDroid> team2) {
        Random rand = new Random();
        int randomNum = rand.nextInt(10); // позиції 0..9
        System.out.println(this.name + " атакує позицію " + randomNum);
        giveVolcanicEruptionDamage(team1, randomNum);
        giveVolcanicEruptionDamage(team2, randomNum);
    }

    /**
     * Відображає меню дій і виконує обрану дію гравця.
     * <ol>
     *   <li>Вогняний кулак — одиночна атака по першій доступній цілі</li>
     *   <li>Виверження — AoE по випадковій позиції {@link #volcanicEruption(List, List)}</li>
     *   <li>Підійти — випадкове переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Fire Flash Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Атака вогняним кулаком");
        System.out.println("2. Виверження (сильний урон юнітам рандомної позиції)");
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
                                .append(" завдає удару вогняним кулаком по ")
                                .append(def.getName())
                                .append("!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" намагається вдарити вогнем, але промахується по ")
                                .append(def.getName())
                                .append("!\n");
                    }
                }
                break;

            case 2:
                volcanicEruption(attackers, defenders);
                actionResult.append(this.getName())
                        .append(" спричиняє виверження лави! Земля тремтить!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" змінює позицію, залишаючи за собою жаркий слід!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }
}
