package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FrigateTest {

    @Test
    @DisplayName("getSize returns 4")
    void getSize() {
        Frigate f = new Frigate(Compass.NORTH, new Position(1, 1));
        assertEquals(4, f.getSize().intValue());
    }

    @Test
    @DisplayName("positions are vertical for NORTH (rows increasing)")
    void positions_forNorth() {
        Position base = new Position(2, 3);
        Frigate f = new Frigate(Compass.NORTH, base);
        List<IPosition> pts = f.getPositions();

        assertEquals(4, pts.size(), "Frigate must have 4 positions");

        for (int i = 0; i < 4; i++) {
            IPosition p = pts.get(i);
            assertEquals(base.getRow() + i, p.getRow(), "Row must match for NORTH bearing");
            assertEquals(base.getColumn(), p.getColumn(), "Column must stay same for NORTH bearing");
        }
    }

    @Test
    @DisplayName("positions are horizontal for EAST (columns increasing)")
    void positions_forEast() {
        Position base = new Position(5, 6);
        Frigate f = new Frigate(Compass.EAST, base);
        List<IPosition> pts = f.getPositions();

        assertEquals(4, pts.size(), "Frigate must have 4 positions");

        for (int i = 0; i < 4; i++) {
            IPosition p = pts.get(i);
            assertEquals(base.getRow(), p.getRow(), "Row must stay same for EAST bearing");
            assertEquals(base.getColumn() + i, p.getColumn(), "Column must match for EAST bearing");
        }
    }

    @Test
    @DisplayName("shooting all positions marks them hit and ship is not floating")
    void shooting_allPositions_sinksShip() {
        Frigate f = new Frigate(Compass.SOUTH, new Position(3, 3));
        List<IPosition> pts = f.getPositions();

        assertEquals(4, pts.size());

        // Ensure none are hit initially
        for (IPosition p : pts) {
            assertFalse(p.isHit(), "Position should initially be unhit");
            assertTrue(f.occupies(p), "Frigate should occupy its own positions");
            f.shoot(p); // mark hit via ship API
            assertTrue(p.isHit(), "Position must be hit after shooting");
        }

        // After all positions hit, ship should not be floating
        assertFalse(f.stillFloating(), "Frigate should sink after all its positions are hit");
    }
}