package myGame.droid;

import java.util.List;

/**
 * Клас {@code windFlyingDroid} — швидкий повітряний дроїд стихії вітру.
 * <p>
 * Має велику дальність атаки та унікальну здатність бомбардування
 * по траєкторії польоту: дроїд рухається три клітинки й завдає
 * шкоди всім, хто опиниться під ним у кожній точці шляху.
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Порив вітру — одиночна атака по цілі в радіусі</li>
 *   <li>Бомбардування — прохід на 3 позиції з ударами по дорозі</li>
 *   <li>Переміщення — випадковий крок {@link #changePosition()}</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class WindFlyingDroid extends BaseDroid {

    /**
     * Створює дроїда "FlyingDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 105</li>
     *   <li>Current HP = 105</li>
     *   <li>Move speed = 2</li>
     *   <li>Range = 3</li>
     *   <li>Attack = 50</li>
     *   <li>Element = "wind"</li>
     * </ul>
     */
    public WindFlyingDroid() {
        super("FlyingDroid ", 105, 105, 2, 3, 50, "wind");
    }

    /**
     * Завдає шкоди всім дроїдам із переданого списку, які стоять на тій
     * самій позиції, що й цей дроїд зараз (імітація скидання бомби прямо під собою).
     *
     * @param team список потенційних цілей (союзники або вороги)
     */
    public void bombAttack(List<BaseDroid> team) {
        for (BaseDroid d : team) {
            if (this.getCurrentPosition() == d.getCurrentPosition() && this != d) {
                d.receiveDamage(this.getAttack());
            }
        }
    }

    /**
     * Прокладає шлях атаки на 3 кроки в напрямку {@code num} і на кожному кроці
     * виконує {@link #bombAttack(List)} по обох командах.
     *
     * @param team1 перша команда (зазвичай союзники)
     * @param team2 друга команда (зазвичай супротивники)
     * @param num   напрямок руху: {@code +1} — вправо, {@code -1} — вліво
     */
    public void pathForAttack(List<BaseDroid> team1, List<BaseDroid> team2, int num) {
        for (int i = 0; i < 3; i++) {
            this.setCurrentPosition(this.getCurrentPosition() + num);
            bombAttack(team1);
            bombAttack(team2);
        }
    }

    /**
     * Виконує бомбардування по траєкторії: якщо поточна позиція &gt; 5 — рух вліво,
     * якщо &lt; 5 — рух вправо. На кожному з трьох кроків здійснюється
     * удар по обох командах через {@link #bombAttack(List)}.
     *
     * @param team1 перша команда (зазвичай союзники)
     * @param team2 друга команда (зазвичай супротивники)
     */
    public void bombingPath(List<BaseDroid> team1, List<BaseDroid> team2) {
        if (this.currentPosition > (arenaMin + arenaMax) / 2) {
            pathForAttack(team1, team2, -1);
        } else {
            pathForAttack(team1, team2, 1);
        }
    }

    /**
     * Відображає меню дій і виконує обрану дію гравця.
     * <ol>
     *   <li>Порив вітру — одиночна атака по першій доступній цілі</li>
     *   <li>Бомбардування — рух на 3 позиції з ударами на кожному кроці {@link #bombingPath(List, List)}</li>
     *   <li>Підійти — випадкове переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {

        System.out.println("\n Дії Wind Flying Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Порив вітру (нанести шкоду стихією)");
        System.out.println("2. Бомбардування (пролітає 3 позиції та наносить шкоду)");
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
                                .append(" атакує поривом вітру ")
                                .append(def.getName())
                                .append("\n");
                    } else {
                        actionResult.append(this.getName())
                                .append(" не зміг вдарити вітром ")
                                .append(def.getName())
                                .append("\n");
                    }
                }
                break;

            case 2:
                bombingPath(attackers, defenders);
                actionResult.append(this.getName())
                        .append(" пролітає над полем і бомбить позиції!\n");
                break;

            case 3:
                this.changePosition();
                actionResult.append(this.getName())
                        .append(" міняє позицію, використовуючи крила вітру!\n");
                break;

            default:
                System.out.println("Невірний вибір!");
        }

        return actionResult.toString();
    }
}

