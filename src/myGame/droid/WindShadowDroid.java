package myGame.droid;

import java.util.List;

/**
 * Клас {@code windShadowDroid} — вітряний дроїд-скритник.
 * <p>
 * Має підвищену швидкість переміщення та здатність погіршувати "видимість"
 * (ефективну дальність атаки) інших дроїдів, зменшуючи їхній {@code range} до 1.
 *
 * <p>Меню дій:
 * <ul>
 *   <li>Тіньовий удар — одиночна атака по цілі в радіусі</li>
 *   <li>Затемнення — знижує дальність атаки вибраної групи дроїдів до 1</li>
 *   <li>Переміщення — випадкове зміщення {@link #changePosition()}</li>
 * </ul>
 *
 * @author You
 * @since 1.0
 */
public class WindShadowDroid extends BaseDroid {

    /**
     * Створює дроїда "ShadowDroid" з фіксованими параметрами:
     * <ul>
     *   <li>Max HP = 105</li>
     *   <li>Current HP = 105</li>
     *   <li>Move speed = 3</li>
     *   <li>Range = 2</li>
     *   <li>Attack = 60</li>
     *   <li>Element = "wind"</li>
     * </ul>
     */
    public WindShadowDroid() {
        super("ShadowDroid", 105, 105, 3, 2, 60, "wind");
    }

    /**
     * Зменшує дальність атаки ({@code range}) усім дроїдам зі списку до 1.
     * <p>Використовується як ефект "затемнення".</p>
     *
     * @param receivers список дроїдів, на яких накладається ефект
     */
    public void makeBasVision(List<BaseDroid> receivers) {
        for (BaseDroid droid : receivers) {
            droid.setRange(1);
        }
    }

    /**
     * Відображає меню дій і виконує обрану дію гравця.
     * <ol>
     *   <li>Тіньовий удар — одиночна атака по першій доступній цілі</li>
     *   <li>Затемнення — масово зменшує дальність атаки цільової команди {@link #makeBasVision(List)}</li>
     *   <li>Підійти — переміщення {@link #changePosition()}</li>
     * </ol>
     *
     * @param attackers команда, до якої належить цей дроїд
     * @param defenders команда супротивників
     */
    @Override
    public String actionMenu(List<BaseDroid> attackers, List<BaseDroid> defenders) {
        System.out.println("\n Дії Wind Shadow Droid:");
        super.actionMenu(attackers, defenders);
        System.out.println("1. Тіньовий удар ");
        System.out.println("2. Затемнення (зменшує дальність атаки противнику до 1)");
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
                        actionResult.append(this.getName()).append(" б'є з тіні ").append(def.getName()).append("\n");
                    } else {
                        actionResult.append(this.getName()).append(" не б'є з тіні ").append(def.getName()).append("\n");
                    }
                }
                break;
            case 2:
                makeBasVision(defenders);
                actionResult.append(this.getName()).append(" зменшує видимість!").append("\n");
                break;
            case 3:
                this.changePosition();
                actionResult.append(this.getName()).append(" міняє позицію!").append("\n");
                break;
            default:
                System.out.println("Невірний вибір!");
        }
        return actionResult.toString();
    }
}
