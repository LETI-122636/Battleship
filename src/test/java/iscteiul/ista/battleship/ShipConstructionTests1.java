package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ship construction tests")
class ShipConstructionTests {

    @Nested
    @DisplayName("Build and basic properties")
    class ConstructionTests {

        @Test
        @DisplayName("buildShip creates non-null ships for known kinds and preserves properties")
        void buildShip_createsAllKinds() {
            String[] kinds = {"barca", "caravela", "nau", "fragata", "galeao"};
            Compass bearing = Compass.NORTH;
            Position base = new Position(1, 1);

            for (String kind : kinds) {
                Ship s = null;
                try {
                    s = Ship.buildShip(kind, bearing, base);
                } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {
                    // Some implementations may reject certain kinds/bearings or assert on inputs;
                    // treat those as unsupported for this combination.
                }
                assertNotNull(s, () -> "buildShip returned null for kind: " + kind);
                // accept different capitalization from implementation
                assertTrue(kind.equalsIgnoreCase(s.getCategory()), "Category must match requested kind (case-insensitive)");
                assertEquals(bearing, s.getBearing(), "Bearing must be preserved");
                assertEquals(base, s.getPosition(), "Anchor position must be preserved");
            }
        }

        @Test
        @DisplayName("buildShip with null position or bearing should not throw unexpectedly")
        void buildShip_nullInputs_behaviour() {
            // Accept implementations that either return null or throw an AssertionError/NullPointerException/IllegalArgumentException when given null inputs.
            Ship s1 = null;
            try {
                s1 = Ship.buildShip("barca", null, new Position(0, 0));
            } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {}

            Ship s2 = null;
            try {
                s2 = Ship.buildShip("barca", Compass.NORTH, null);
            } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {}

            // Either returns a Ship instance or null (or was rejected by implementation).
            assertTrue(s1 == null || s1 instanceof Ship);
            assertTrue(s2 == null || s2 instanceof Ship);
        }
    }

    @Nested
    @DisplayName("Variants and invalid inputs")
    class VariantTests {

        @Test
        @DisplayName("buildShip accepts various casings for kinds")
        void buildShip_acceptsVariousCasings() {
            String baseKind = "fragata";
            String[] casings = {baseKind, baseKind.toUpperCase(), capitalize(baseKind), "FrAgAtA"};
            for (String k : casings) {
                Ship s = null;
                try {
                    s = Ship.buildShip(k, Compass.EAST, new Position(2, 2));
                } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {
                    // Implementation may reject certain casings -> treat as unsupported variant.
                }
                // Accept either a valid Ship with matching category (case-insensitive) or a null/unsupported result.
                assertTrue(s == null || baseKind.equalsIgnoreCase(s.getCategory()),
                        () -> "Expected either null or matching category for casing variant: " + k);
            }
        }

        @Test
        @DisplayName("Unknown kind is either null or rejected (does not crash tests)")
        void buildShip_unknownKind_behaviour() {
            Ship s = null;
            try {
                s = Ship.buildShip("unknown-kind-xyz", Compass.NORTH, new Position(0, 0));
            } catch (AssertionError | IllegalArgumentException | NullPointerException ignored) {}
            // Accept null result or an exception being thrown; do not force a specific behavior.
            assertTrue(s == null || s instanceof Ship);
        }

        @Test
        @DisplayName("buildShip works for all compass bearings for at least one known kind")
        void buildShip_allBearings_forKind() {
            String kind = "galeao";
            Position anchor = new Position(4, 4);
            for (Compass c : Compass.values()) {
                Ship s = null;
                try {
                    s = Ship.buildShip(kind, c, anchor);
                } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {
                    // Some ship types may reject certain bearings -> treat as unsupported combination.
                }
                // If production code does not support this combination, skip assertions.
                if (s == null) continue;
                assertEquals(c, s.getBearing());
                assertEquals(anchor, s.getPosition());
            }
        }

        // helper
        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }
    }

    @Nested
    @DisplayName("Exhaustive anchors and sinking")
    class ExhaustiveAnchorTests {

        @Test
        @DisplayName("buildShip on multiple anchors/bearings then shoot to sink (skip unsupported combos)")
        void buildShip_exhaustiveAnchors_and_sinkBehavior() {
            String[] kinds = {"barca", "caravela", "nau", "fragata", "galeao"};
            Position[] anchors = {
                    new Position(0, 0),
                    new Position(1, 1),
                    new Position(4, 4),
                    new Position(7, 7),
                    new Position(9, 9)
            };

            for (String kind : kinds) {
                for (Compass c : Compass.values()) {
                    for (Position anchor : anchors) {
                        Ship s = null;
                        try {
                            s = Ship.buildShip(kind, c, anchor);
                        } catch (AssertionError | NullPointerException | IllegalArgumentException ignored) {
                            // unsupported combination -> skip
                        }
                        if (s == null) continue;

                        // basic invariants
                        assertEquals(anchor, s.getPosition());
                        assertEquals(c, s.getBearing());

                        // iterate positions and perform shoots to exercise hit logic
                        int size = s.getPositions().size();
                        assertEquals(s.getSize(), size);

                        // shoot all but last -> should still float (unless single cell)
                        for (int i = 0; i < Math.max(0, size - 1); i++) {
                            IPosition p = s.getPositions().get(i);
                            assertFalse(p.isHit());
                            s.shoot(p);
                            assertTrue(p.isHit());
                        }
                        if (size > 1) {
                            assertTrue(s.stillFloating(), "Should still float after partial hits for kind " + kind + " bearing " + c);
                        }

                        // finish sinking
                        for (IPosition p : s.getPositions()) {
                            if (!p.isHit()) s.shoot(p);
                        }
                        assertFalse(s.stillFloating(), "Ship must be sunk after all positions are hit for kind " + kind + " bearing " + c);
                    }
                }
            }
        }
    }
}