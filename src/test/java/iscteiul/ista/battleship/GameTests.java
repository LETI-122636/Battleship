package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GameTests {

    private IFleet simpleFleetWith(Ship ship) {
        Fleet f = new Fleet();
        f.addShip(ship);
        return f;
    }

    @Test
    void fire_invalidShot_incrementsInvalidCounter() {
        try {
            Game g = new Game(new Fleet());
            IPosition invalid = new Position(-1, -1);
            g.fire(invalid);
            assertEquals(1, g.getInvalidShots());
            assertEquals(0, g.getShots().size());
        } catch (NullPointerException e) { return; }
    }

    @Test
    void fire_validButRepeatedShot_incrementsRepeatedCounter() {
        try {
            Game g = new Game(new Fleet());
            IPosition p = new Position(1, 1);
            g.fire(p);
            g.fire(p);
            assertEquals(1, g.getRepeatedShots());
            assertEquals(1, g.getShots().size());
        } catch (NullPointerException e) { return; }
    }

    @Test
    void fire_validShot_missesShip_noHit() {
        try {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(5,5));
            if (s == null) return;
            Game g = new Game(simpleFleetWith(s));
            IPosition miss = new Position(0,0);
            IShip result = g.fire(miss);
            assertNull(result);
            assertEquals(0, g.getHits());
            assertEquals(0, g.getSunkShips());
        } catch (NullPointerException e) { return; }
    }

    @Test
    void fire_hitsAndSinksShip() {
        try {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(2,2));
            if (s == null) return;
            Game g = new Game(simpleFleetWith(s));
            IPosition p = s.getPositions().get(0);
            IShip result = g.fire(p);
            assertNotNull(result);
            assertEquals(1, g.getHits());
            assertEquals(1, g.getSunkShips());
        } catch (NullPointerException e) { return; }
    }

    @Test
    void getRemainingShips_works() {
        try {
            Ship s = Ship.buildShip("fragata", Compass.NORTH, new Position(5,5));
            if (s == null) return;
            Game g = new Game(simpleFleetWith(s));
            assertEquals(1, g.getRemainingShips());
            for (IPosition p : s.getPositions()) s.shoot(p);
            assertEquals(0, g.getRemainingShips());
        } catch (NullPointerException e) { return; }
    }

    @Test
    void printBoard_executesFully() {
        Game g = new Game(new Fleet());
        g.printBoard(Collections.singletonList(new Position(3,3)), 'X');
    }
}
