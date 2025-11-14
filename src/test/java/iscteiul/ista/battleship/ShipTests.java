package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ship behavior tests")
class ShipTests {

    @Nested
    @DisplayName("Position bounds")
    class BoundsTests {
        @Test
        void bounds_areConsistentWithPositions() {
            Ship s = Ship.buildShip("galeao", Compass.EAST, new Position(3, 2));
            assertNotNull(s);
            assertFalse(s.getPositions().isEmpty(), "Ship must have positions for bounds test");

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
        void singleCellBounds_equalAnchor() {
            Ship single = Ship.buildShip("barca", Compass.SOUTH, new Position(7, 8));
            assertNotNull(single);
            List<IPosition> pos = single.getPositions();
            assertFalse(pos.isEmpty());
            IPosition p = pos.get(0);
            assertEquals(p.getRow(), single.getTopMostPos());
            assertEquals(p.getRow(), single.getBottomMostPos());
            assertEquals(p.getColumn(), single.getLeftMostPos());
            assertEquals(p.getColumn(), single.getRightMostPos());
        }
    }

    @Nested
    @DisplayName("Occupancy and shooting")
    class OccupancyTests {
        @Test
        void occupies_and_shoot_affectPositionsAndFloating() {
            Ship barca = Ship.buildShip("barca", Compass.SOUTH, new Position(5, 5));
            if (barca == null) return;
            assertFalse(barca.getPositions().isEmpty());

            IPosition p = barca.getPositions().get(0);
            assertTrue(barca.occupies(p));
            assertFalse(p.isHit());

            barca.shoot(p);
            assertTrue(p.isHit());

            if (barca.getPositions().size() == 1) {
                assertFalse(barca.stillFloating());
            } else {
                boolean anyUnhit = barca.getPositions().stream().anyMatch(pos -> !pos.isHit());
                assertEquals(anyUnhit, barca.stillFloating());
            }
        }

        @Test
        void doubleShoot_isIdempotent() {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(2, 2));
            if (s == null) return;
            List<IPosition> positions = s.getPositions();
            if (positions.isEmpty()) return;
            IPosition p = positions.get(0);
            s.shoot(p);
            boolean first = p.isHit();
            s.shoot(p);
            assertEquals(first, p.isHit());
        }

        @Test
        void shootingAllPositions_sinksShip() {
            Ship s = Ship.buildShip("galeao", Compass.WEST, new Position(6, 6));
            assertNotNull(s);
            for (IPosition pos : s.getPositions()) {
                assertFalse(pos.isHit());
                s.shoot(pos);
            }
            assertFalse(s.stillFloating());
        }

        @Test
        void shoot_nonOccupiedPosition_noEffect() {
            Ship s = Ship.buildShip("fragata", Compass.EAST, new Position(1, 1));
            assertNotNull(s);
            Position other = new Position(99, 99);
            try { s.shoot(other); } catch (AssertionError | NullPointerException ignored) {}
            boolean anyHit = s.getPositions().stream().anyMatch(IPosition::isHit);
            assertFalse(anyHit);
        }
    }

    @Nested
    @DisplayName("Adjacency and representation")
    class AdjacencyAndRepresentationTests {
        @Test
        void tooCloseTo_overlap_and_distant() {
            Ship s1 = Ship.buildShip("barca", Compass.NORTH, new Position(10, 10));
            Ship s2 = Ship.buildShip("barca", Compass.NORTH, new Position(10, 10));
            Ship s3 = Ship.buildShip("barca", Compass.NORTH, new Position(20, 20));
            if (s1 == null || s2 == null || s3 == null) return;

            assertTrue(s1.tooCloseTo(s2));
            assertFalse(s1.tooCloseTo(s3));
        }

        @Test
        void toString_containsKeyElements() {
            Ship s = Ship.buildShip("fragata", Compass.WEST, new Position(2, 3));
            if (s == null) return;
            String rep = s.toString();
            assertTrue(rep.contains(s.getCategory()));
            assertTrue(rep.contains(String.valueOf(s.getBearing())));
            assertTrue(rep.contains(String.valueOf(s.getPosition())));
        }

        @Test
        void occupies_falseForOtherPositions() {
            Ship s = Ship.buildShip("galeao", Compass.SOUTH, new Position(3, 3));
            if (s == null) return;
            assertFalse(s.occupies(new Position(0, 0)));
        }

        @Test
        void positions_size_equals_getSize() {
            String[] kinds = {"barca", "fragata", "galeao"};
            for (String kind : kinds) {
                Ship s = Ship.buildShip(kind, Compass.NORTH, new Position(4, 4));
                if (s == null) continue;
                assertEquals(s.getSize(), s.getPositions().size());
            }
        }
    }
}
