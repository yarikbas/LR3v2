package myGame.droid;

import java.util.List;

/**
 * Клас {@code fireBurningDroid} — вогняний дроїд-дамер.
 * <p>
 * Має високу силу атаки на середній дистанції. Унікальна масова здібність —
 * {@link #flameThrower(List)}, яка намагається завдати шкоди всім противникам у радіусі,
 * окрім тих, що мають стихію "fire".
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Вогняний кулак — одиночна атака по цілі в радіусі</li>
 *   <li>Вогнемет — масова атака по всіх невогняних цілях у радіусі</li>
 *   <li>Переміщення — випадковий крок ліворуч/праворуч</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class FireBurningDroid extends BaseDroid {

    /**
     * Створює дроїда "BurningDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 100</li>
     *   <li>Current HP = 100</li>
     *   <li>Move speed = 2</li>
     *   <li>Range = 2</li>
     *   <li>Attack = 75</li>
     *   <li>Element = "fire"</li>
     * </ul>
     */
    public FireBurningDroid() {
        super("BurningDroid", 100, 100, 2, 2, 75, "fire");
    }

    /**
     * Масова атака вогнеметом: намагається вдарити кожного захисника,
     * який знаходиться в радіусі, пропускаючи цілі зі стихією "fire".
     *
     * @param receivers список можливих цілей (як правило, команда противника)
     */
    public void flameThrower(List<BaseDroid> receivers) {
        for (BaseDroid droid : receivers) {
            // пропускаємо власну стихію
            if ("fire".equals(droid.getElement())) continue;
            // giveDamage вже сам перевіряє inRange(...)
            this.giveDamage(droid);
        }
    }

    /**
     * Відображає меню дій і виконує обрану дію.
     * <ol>
     *   <li>Вогняний кулак — одиночна атака по першій доступній цілі</li>
     *   <li>Вогнемет — масова атака по всіх невогняних цілях у радіусі</li>
     *   <li>Підійти — випадкове переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда противника
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Fire Burning Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Атака вогняним кулаком");
        System.out.println("2. Вогнемет (нанести шкоду вогнем всім невогняним героям на дистанції атаки)");
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
                                .append(" вдаряє вогняним кулаком по ")
                                .append(def.getName())
                                .append("!\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" замахнувся вогнем, але промахнувся по ")
                                .append(def.getName())
                                .append("!\n");
                    }
                }
                break;

            case 2:
                flameThrower(defenders);
                actionResult.append(this.getName())
                        .append(" випускає вогнемет і підпалює все довкола!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" пересувається крізь вогняний дим!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }

}
