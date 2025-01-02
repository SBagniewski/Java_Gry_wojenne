package wargames;

import java.io.*;
import java.util.*;

class Soldier implements Serializable {
    private static final long serialVersionUID = 1L;
    enum Rank {
        PRIVATE(1), CORPORAL(2), CAPTAIN(3), MAJOR(4);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public Rank promote() {
            return this.ordinal() < Rank.values().length - 1 ? Rank.values()[this.ordinal() + 1] : this;
        }
    }

    private Rank rank;
    private int experience;

    public Soldier(Rank rank, int experience) {
        this.rank = rank;
        this.experience = experience;
    }

    public Rank getRank() {
        return rank;
    }

    public int getExperience() {
        return experience;
    }

    public int getStrength() {
        return rank.getValue() * experience;
    }

    public void gainExperience() {
        experience++;
        if (experience >= rank.getValue() * 5) {
            rank = rank.promote();
            experience = 1;
        }
    }

    public void loseExperience() {
        experience = Math.max(0, experience - 1);
    }

    public boolean isAlive() {
        return experience > 0;
    }

    @Override
    public String toString() {
        return rank + "(EXP: " + experience + ")";
    }
}

class General implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Soldier> army;
    private int gold;

    public General(String name, int initialGold) {
        this.name = name;
        this.gold = initialGold;
        this.army = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Soldier> getArmy() {
        return army;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void removeGold(int amount) {
        gold = Math.max(0, gold - amount);
    }

    public void conductManeuvers(List<Soldier> participants) {
        int cost = participants.stream().mapToInt(s -> s.getRank().getValue()).sum();
        if (cost > gold) {
            throw new IllegalStateException("Not enough gold to conduct maneuvers.");
        }
        removeGold(cost);
        participants.forEach(Soldier::gainExperience);
    }

    public void buySoldier(Soldier.Rank rank) {
        int cost = 10 * rank.getValue();
        if (gold >= cost) {
            removeGold(cost);
            army.add(new Soldier(rank, 1));
        } else {
            throw new IllegalStateException("Not enough gold to buy soldier.");
        }
    }

    public int getArmyStrength() {
        return army.stream().mapToInt(Soldier::getStrength).sum();
    }

    public void removeDeadSoldiers() {
        army.removeIf(s -> !s.isAlive());
    }

    public void saveState(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        }
    }

    public static General loadState(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (General) ois.readObject();
        }
    }

    @Override
    public String toString() {
        return "General " + name + " (Gold: " + gold + ", Army: " + army + ")";
    }
}

class Secretary {
    private List<String> reports;

    public Secretary() {
        this.reports = new ArrayList<>();
    }

    public void log(String message) {
        reports.add(message);
    }

    public void printReports() {
        reports.forEach(System.out::println);
    }
}