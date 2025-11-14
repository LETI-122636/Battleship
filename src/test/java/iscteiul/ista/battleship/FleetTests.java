package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FleetTests {

    @Test
    void addShip_fails_whenFleetIsFull() {
        Fleet f = new Fleet();
        for (int i = 0; i <= IFleet.FLEET_SIZE; i++) {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(i, i));
            if (s == null) return;
            f.addShip(s);
        }
        Ship extra = Ship.buildShip("barca", Compass.NORTH, new Position(20, 20));
        if (extra == null) return;
        assertFalse(f.addShip(extra));
    }

    @Test
    void addShip_fails_whenOutsideBoard() {
        Fleet f = new Fleet();
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(-10, 200));
        if (s == null) return;
        assertFalse(f.addShip(s));
    }

    @Test
    void addShip_fails_onCollision() {
        Fleet f = new Fleet();
        Ship s1 = Ship.buildShip("barca", Compass.NORTH, new Position(5, 5));
        Ship s2 = Ship.buildShip("barca", Compass.NORTH, new Position(5, 5));
        if (s1 == null || s2 == null) return;
        assertTrue(f.addShip(s1));
        assertFalse(f.addShip(s2));
    }

    @Test
    void getShipsLike_empty_whenNoMatch() {
        Fleet f = new Fleet();
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(1,1));
        if (s == null) return;
        f.addShip(s);
        assertTrue(f.getShipsLike("CategoriaInexistente").isEmpty());
    }

    @Test
    void shipAt_findsCorrectShip() {
        Fleet f = new Fleet();
        Ship s1 = Ship.buildShip("barca", Compass.NORTH, new Position(3,3));
        Ship s2 = Ship.buildShip("barca", Compass.NORTH, new Position(6,6));
        if (s1 == null || s2 == null) return;
        f.addShip(s1);
        f.addShip(s2);
        assertEquals(s2, f.shipAt(new Position(6,6)));
        assertNull(f.shipAt(new Position(99,99)));
    }
}
