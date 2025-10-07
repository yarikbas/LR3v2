package myGame;

import myGame.droid.baseDroid;
import myGame.io.GameIO;
import myGame.map.ArenaMap;
import myGame.mode.Battle;
import myGame.mode.BattleResult;
import myGame.mode.OneVsOneBattle;
import myGame.mode.TeamVsTeamBattle;
import myGame.util.BattleLogger;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Точка входу: питає режим, карту, дає можливість зберегти/прочитати гру.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== MyGame ===");
        System.out.println("1) 1 vs 1");
        System.out.println("2) Team vs Team");
        System.out.println("3) Read game log from file");
        System.out.print("Choose: ");

        int mode = safeInt(sc);

        if (mode == 3) {
            System.out.print("Enter filename to read (e.g. game.log): ");
            String fname = sc.next();
            List<String> lines = GameIO.readLog(Paths.get(fname));
            System.out.println("\n=== LOG CONTENT ===");
            lines.forEach(System.out::println);
            return;
        }

        // випадкова карта (або можна додати вибір)
        ArenaMap map = ArenaMap.random();
        System.out.println("Map: " + map.name());

        Battle battle;
        if (mode == 1) {
            battle = new OneVsOneBattle(map);
        } else if (mode == 2) {
            battle = new TeamVsTeamBattle(map, 3); // 3х3 як приклад
        } else {
            System.out.println("Unknown mode.");
            return;
        }

        BattleResult result = battle.run(sc); // тут взаємодія з твоїми actionMenu()
        printResult(result);

        // Запропонувати зберегти лог
        System.out.print("\nSave game log to file? (y/n): ");
        String yn = sc.next();
        if (yn.equalsIgnoreCase("y")) {
            System.out.print("Enter filename (e.g. game.log): ");
            String fname = sc.next();
            GameIO.saveLog(Paths.get(fname), result.getLogger());
            System.out.println("Saved to " + fname);
        }
    }

    private static int safeInt(Scanner sc) {
        while (!sc.hasNextInt()) { sc.next(); }
        return sc.nextInt();
    }

    private static void printResult(BattleResult result) {
        System.out.println("\n=== BATTLE RESULT ===");
        System.out.println("Mode: " + result.getMode());
        System.out.println("Map: " + result.getMap().name());
        System.out.println("Winner: " + result.getWinner());
        System.out.println("\n--- Teams ---");
        dumpTeam("Attackers", result.getAttackersFinal());
        dumpTeam("Defenders", result.getDefendersFinal());

        System.out.println("\n--- Log (last 20 lines) ---");
        List<String> tail = result.getLogger().tail(20);
        tail.forEach(System.out::println);
    }

    private static void dumpTeam(String title, List<baseDroid> team) {
        System.out.println(title + ":");
        for (baseDroid d : team) {
            System.out.println(" - " + d.getName() + " HP=" + d.getCurrentHp() + "/" + d.getMaxHp()
                    + " pos=" + d.getCurrentPosition() + " el=" + d.getElement());
        }
    }
}
