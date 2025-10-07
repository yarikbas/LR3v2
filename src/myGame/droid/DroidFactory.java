package myGame.droid;

/**
 * Фабрика для створення та відображення різних типів дроїдів.
 * Містить методи для створення екземплярів дроїдів за індексом
 * та відображення каталогу доступних дроїдів.
 */
public class DroidFactory {

    /**
     * Фабричний клас для створення та відображення дроїдів.
     * Надає статичні методи для роботи з дроїдами.
     */
    public class DroidConstruct {
        /**
         * Створює та повертає екземпляр дроїда за вказаним індексом.
         *
         * @param i індекс дроїда у діапазоні 0-7
         * @return екземпляр відповідного дроїда
         * @see BaseDroid
         */
        public static BaseDroid yourDroid(int i) {
            switch (i) {
                case 0: return new EarthHammerDroid();
                case 1: return new EarthBoerDroid();
                case 2: return new FireBurningDroid();
                case 3: return new FireFlashDroid();
                case 4: return new WaterStormDroid();
                case 5: return new WaterSubmarineDroid();
                case 6: return new WindFlyingDroid();
                default: return new WindShadowDroid();
            }
        }

        /**
         * Виводить у консоль каталог всіх доступних дроїдів з їх характеристиками.
         * Включає інформацію про здоров'я, атаку, швидкість, дальність та елемент кожного дроїда.
         *
         * @see BaseDroid#toString()
         */
        public static void catalogDroid() {
            BaseDroid[] droids = {
                    new EarthHammerDroid(),
                    new EarthBoerDroid(),
                    new FireBurningDroid(),
                    new FireFlashDroid(),
                    new WaterStormDroid(),
                    new WaterSubmarineDroid(),
                    new WindFlyingDroid(),
                    new WindShadowDroid()
            };

            System.out.println("Каталог доступних дроїдів:\n");
            for (int i = 0; i < droids.length; i++) {
                System.out.println(i + ". " + droids[i]);
                System.out.println("----------------------------");
            }
        }
    }
}