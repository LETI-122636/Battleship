package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GalleonTest {

    @Test
    @DisplayName("getSize returns 5")
    void getSize() {
        Galleon g = new Galleon(Compass.NORTH, new Position(1, 1));
        assertEquals(5, g.getSize().intValue());
    }

    @Test
    @DisplayName("positions count equals size and ship occupies all its positions")
    void positions_countAndOccupies() {
        Galleon g = new Galleon(Compass.EAST, new Position(3, 4));
        List<IPosition> pts = g.getPositions();

        assertEquals(g.getSize().intValue(), pts.size(), "Positions list must match declared size");

        Set<String> seen = new HashSet<>();
        for (IPosition p : pts) {
            // no duplicate positions
            String key = p.getRow() + ":" + p.getColumn();
            assertTrue(seen.add(key), "Duplicate position found: " + key);
            // ship should report it occupies that position
            assertTrue(g.occupies(p), "Ship must occupy its own position: " + key);
        }
    }

    @Test
    @DisplayName("bounds are consistent with positions")
    void bounds_areConsistentWithPositions() {
        Galleon g = new Galleon(Compass.SOUTH, new Position(5, 2));
        List<IPosition> pts = g.getPositions();
        assertFalse(pts.isEmpty());

        int expectedTop = Integer.MAX_VALUE;
        int expectedBottom = Integer.MIN_VALUE;
        int expectedLeft = Integer.MAX_VALUE;
        int expectedRight = Integer.MIN_VALUE;

        for (IPosition p : pts) {
            expectedTop = Math.min(expectedTop, p.getRow());
            expectedBottom = Math.max(expectedBottom, p.getRow());
            expectedLeft = Math.min(expectedLeft, p.getColumn());
            expectedRight = Math.max(expectedRight, p.getColumn());
        }

        assertEquals(expectedTop, g.getTopMostPos());
        assertEquals(expectedBottom, g.getBottomMostPos());
        assertEquals(expectedLeft, g.getLeftMostPos());
        assertEquals(expectedRight, g.getRightMostPos());
    }

    @Test
    @DisplayName("shooting all positions marks them hit and ship is sunk")
    void shooting_allPositions_sinksShip() {
        Galleon g = new Galleon(Compass.WEST, new Position(2, 6));
        List<IPosition> pts = g.getPositions();

        // none hit initially
        for (IPosition p : pts) {
            assertFalse(p.isHit(), "Position should initially be unhit");
        }

        // shoot all positions via ship API
        for (IPosition p : pts) {
            g.shoot(p);
            assertTrue(p.isHit(), "Position must be hit after shooting");
        }

        // after all hit, ship should not be floating
        assertFalse(g.stillFloating(), "Galleon should sink after all positions are hit");
    }
}