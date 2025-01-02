package wargames;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralsTest {

    private General general1;
    private General general2;

    @BeforeEach
    public void setUp() {
        general1 = new General("Alexander", 100);
        general2 = new General("Napoleon", 100);
    }

    @Test
    void testSoldierSurvival() {
    Soldier soldier = new Soldier(Soldier.Rank.PRIVATE, 1);
    soldier.loseExperience();
    assertFalse(soldier.isAlive());
    }

    @Test
    public void testBuySoldier() {
        general1.buySoldier(Soldier.Rank.PRIVATE);
        general1.buySoldier(Soldier.Rank.CAPTAIN);

        List<Soldier> army = general1.getArmy();

        assertEquals(2, army.size());
        assertEquals(Soldier.Rank.PRIVATE, army.get(0).getRank());
        assertEquals(Soldier.Rank.CAPTAIN, army.get(1).getRank());
        assertEquals(1, army.get(0).getExperience());
    }

    @Test
    void testBuySoldierInsufficientGold() {
    General general = new General("Alexander", 5);
    assertThrows(IllegalStateException.class, () -> general.buySoldier(Soldier.Rank.PRIVATE));
    }


    @Test
    public void testConductManeuvers() {
        general1.buySoldier(Soldier.Rank.PRIVATE);
        general1.buySoldier(Soldier.Rank.CORPORAL);

        general1.conductManeuvers(general1.getArmy());

        List<Soldier> army = general1.getArmy();
        assertEquals(2, army.size());
        assertEquals(2, army.get(0).getExperience());
        assertEquals(2, army.get(1).getExperience());
        assertEquals(67, general1.getGold());
    }

    @Test
    public void testPromoteSoldier() {
        general1.buySoldier(Soldier.Rank.PRIVATE);

        Soldier soldier = general1.getArmy().get(0);
        for (int i = 0; i < 3; i++) {
            soldier.gainExperience();
        }
        assertEquals(Soldier.Rank.PRIVATE, soldier.getRank());

        soldier.gainExperience();
        assertEquals(Soldier.Rank.CORPORAL, soldier.getRank());
        assertEquals(1, soldier.getExperience());
    }

    @Test
    public void testBattleWin() {
        general1.buySoldier(Soldier.Rank.CAPTAIN);
        general2.buySoldier(Soldier.Rank.PRIVATE);

        int initialGoldGeneral2 = general2.getGold();
        int strength1 = general1.getArmyStrength();
        int strength2 = general2.getArmyStrength();

        assertTrue(strength1 > strength2);

        general1.addGold((int) (general2.getGold() * 0.1));
        general2.removeGold((int) (general2.getGold() * 0.1));
        general1.getArmy().forEach(Soldier::gainExperience);
        general2.getArmy().forEach(Soldier::loseExperience);

        assertEquals(initialGoldGeneral2 * 0.1, general1.getGold() - 70, 0.1);
        assertEquals(initialGoldGeneral2 * 0.9, general2.getGold(), 0.1);
    }

    @Test
    void testSoldierLoseExperience() {
    Soldier soldier = new Soldier(Soldier.Rank.CAPTAIN, 3);
    soldier.loseExperience();
    assertEquals(2, soldier.getExperience());
    }

    @Test
    void testRemoveDeadSoldiers() {
    General general = new General("Alexander", 50);
    Soldier aliveSoldier = new Soldier(Soldier.Rank.PRIVATE, 1);
    Soldier deadSoldier = new Soldier(Soldier.Rank.PRIVATE, 0);

    general.getArmy().add(aliveSoldier);
    general.getArmy().add(deadSoldier);

    general.removeDeadSoldiers();
    assertEquals(1, general.getArmy().size());
    assertTrue(general.getArmy().contains(aliveSoldier));
    }


    @Test
    public void testBattleTie() {
        general1.buySoldier(Soldier.Rank.PRIVATE);
        general2.buySoldier(Soldier.Rank.PRIVATE);

        int strength1 = general1.getArmyStrength();
        int strength2 = general2.getArmyStrength();

        assertEquals(strength1, strength2);

        general1.getArmy().remove(0);
        general2.getArmy().remove(0);

        assertTrue(general1.getArmy().isEmpty());
        assertTrue(general2.getArmy().isEmpty());
    }

    @Test
    public void testSaveAndLoadState() throws Exception {
        general1.buySoldier(Soldier.Rank.MAJOR);

        String filePath = "general1.dat";
        general1.saveState(filePath);

        General loadedGeneral = General.loadState(filePath);

        assertEquals(general1.getName(), loadedGeneral.getName());
        assertEquals(general1.getGold(), loadedGeneral.getGold());
        assertEquals(general1.getArmy().size(), loadedGeneral.getArmy().size());
        assertEquals(general1.getArmy().get(0).getRank(), loadedGeneral.getArmy().get(0).getRank());
    }
}
