// language: java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    // helper: try a few common casing variants to avoid null if implementation expects e.g. lowercase
    private Ship buildShipFlexible(String kind, Compass bearing, Position base) {
        String[] variants = new String[] {
                kind,
                kind.toLowerCase(Locale.ROOT),
                kind.toUpperCase(Locale.ROOT),
                // Capitalize first letter (e.g. "barca" -> "Barca")
                kind.substring(0,1).toUpperCase(Locale.ROOT) + kind.substring(1).toLowerCase(Locale.ROOT)
        };
        for (String v : variants) {
            Ship s = Ship.buildShip(v, bearing, base);
            if (s != null) return s;
        }
        return null;
    }

    @Test
    @DisplayName("buildShip creates non-null ships for all known kinds and preserves category")
    void buildShip_createsAllKinds() {
        String[] kinds = {"Barca", "Caravela", "Nau", "Fragata", "Galeao"};
        Compass bearing = Compass.NORTH;
        Position base = new Position(1, 1);

        for (String kind : kinds) {
            Ship s = buildShipFlexible(kind, bearing, base);
            assertNotNull(s, () -> "buildShip returned null for kind: " + kind);
            // accept any casing in category returned by implementation
            assertTrue(kind.equalsIgnoreCase(s.getCategory()), "Category must match requested kind (case-insensitive)");
            assertEquals(bearing, s.getBearing(), "Bearing must be preserved");
            assertEquals(base, s.getPosition(), "Anchor position must be preserved");
        }
    }

    @Test
    @DisplayName("bounding helpers (top/bottom/left/right) are consistent with positions list")
    void bounds_areConsistentWithPositions() {
        Ship s = buildShipFlexible("Galeao", Compass.EAST, new Position(3, 2));
        assertNotNull(s);
        assertFalse(s.getPositions().isEmpty(), "Ship must have at least one position for bounds tests");

        int expectedTop = Integer.MAX_VALUE;
        int expectedBottom = Integer.MIN_VALUE;
        int expectedLeft = Integer.MAX_VALUE;
        int expectedRight = Integer.MIN_VALUE;

        for (IPosition p : s.getPositions()) {
            expectedTop = Math.min(expectedTop, p.getRow());
            expectedBottom = Math.max(expectedBottom, p.getRow());
            expectedLeft = Math.min(expectedLeft, p.getColumn());
            expectedRight = Math.max(expectedRight, p.getColumn());
        }

        assertEquals(expectedTop, s.getTopMostPos());
        assertEquals(expectedBottom, s.getBottomMostPos());
        assertEquals(expectedLeft, s.getLeftMostPos());
        assertEquals(expectedRight, s.getRightMostPos());
    }

    @Test
    @DisplayName("occupies, shoot and stillFloating behave correctly for single-hit ships")
    void occupies_and_shoot_affectPositionsAndFloating() {
        Ship barca = buildShipFlexible("Barca", Compass.SOUTH, new Position(5, 5));
        assertNotNull(barca);
        assertFalse(barca.getPositions().isEmpty());

        IPosition p = barca.getPositions().get(0);
        assertTrue(barca.occupies(p), "Ship should occupy its own position");

        // Ensure initial state not hit
        assertFalse(p.isHit(), "Position should not be hit before shooting");

        // Shoot and verify
        barca.shoot(p);
        assertTrue(p.isHit(), "Position should be hit after shooting");

        // If ship had a single position, it should no longer be floating; otherwise stillFloating may be true
        if (barca.getPositions().size() == 1) {
            assertFalse(barca.stillFloating(), "Single-cell ship should sink after its only cell is hit");
        } else {
            boolean anyUnhit = barca.getPositions().stream().anyMatch(pos -> !pos.isHit());
            assertEquals(anyUnhit, barca.stillFloating());
        }
    }

    @Test
    @DisplayName("tooCloseTo returns true for adjacent ships and false for distant ships")
    void tooCloseTo_detectsAdjacency() {
        Ship s1 = buildShipFlexible("Barca", Compass.NORTH, new Position(10, 10));
        Ship s2_adjacent = buildShipFlexible("Barca", Compass.NORTH, new Position(10, 11));
        Ship s3_far = buildShipFlexible("Barca", Compass.NORTH, new Position(15, 15));

        assertNotNull(s1);
        assertNotNull(s2_adjacent);
        assertNotNull(s3_far);

        assertTrue(s1.tooCloseTo(s2_adjacent), "Ships placed adjacent should be considered too close");
        assertFalse(s1.tooCloseTo(s3_far), "Distant ships should not be too close");
    }

    @Test
    @DisplayName("toString includes category, bearing and anchor position")
    void toString_containsKeyElements() {
        Ship s = buildShipFlexible("Fragata", Compass.WEST, new Position(2, 3));
        assertNotNull(s);

        String rep = s.toString().toLowerCase(Locale.ROOT);
        assertTrue(rep.contains(s.getCategory().toLowerCase(Locale.ROOT)), "toString should contain category");
        assertTrue(rep.contains(String.valueOf(s.getBearing()).toLowerCase(Locale.ROOT)), "toString should contain bearing");
        assertTrue(rep.contains(String.valueOf(s.getPosition()).toLowerCase(Locale.ROOT)), "toString should contain anchor position");
    }
}
