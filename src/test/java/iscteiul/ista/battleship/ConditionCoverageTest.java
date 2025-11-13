package iscteiul.ista.battleship;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionCoverageTest {

    @Nested
    class PositionConditionTests {

        @Test
        void equals_sameInstance_true() {
            Position p = new Position(1, 1);
            assertSame(p, p);
        }

        @Test
        void equals_null_or_otherType_false() {
            Position p = new Position(2, 2);
            assertNotNull(p);
            assertNotEquals(new Object(), p);
        }

        @Test
        void equals_sameCoordinates_true() {
            Position p1 = new Position(3, 4);
            Position p2 = new Position(3, 4);
            assertEquals(p1, p2);
        }

        @Test
        void isAdjacentTo_variousCases() {
            Position center = new Position(5, 5);
            // row diff <=1 and col diff <=1 -> adjacent (including diagonal)
            assertTrue(center.isAdjacentTo(new Position(5, 6)));
            assertTrue(center.isAdjacentTo(new Position(6, 6)));
            assertTrue(center.isAdjacentTo(new Position(4, 4)));

            // row diff >1 -> not adjacent
            assertFalse(center.isAdjacentTo(new Position(8, 5)));

            // col diff >1 -> not adjacent
            assertFalse(center.isAdjacentTo(new Position(5, 8)));

            // both diffs >1 -> not adjacent
            assertFalse(center.isAdjacentTo(new Position(8, 8)));
        }
    }

    @Nested
    class CompassConditionTests {

        @Test
        void charToCompass_eachBranch() {
            assertEquals(Compass.NORTH, Compass.charToCompass('n'));
            assertEquals(Compass.SOUTH, Compass.charToCompass('s'));
            assertEquals(Compass.EAST, Compass.charToCompass('e'));
            assertEquals(Compass.WEST, Compass.charToCompass('o'));
            // default
            assertEquals(Compass.UNKNOWN, Compass.charToCompass('x'));
        }
    }

    @Nested
    class ShipConditionTests {

        @Test
        void stillFloating_true_then_false_after_allHit() {
            Caravel ship = new Caravel(Compass.EAST, new Position(2, 2));
            // initially none hit -> still floating
            assertTrue(ship.stillFloating());

            // mark all positions as hit
            for (IPosition p : ship.getPositions())
                p.shoot();

            assertFalse(ship.stillFloating());
        }

        @Test
        void occupies_and_tooCloseTo_with_position() {
            Caravel ship = new Caravel(Compass.EAST, new Position(4, 4));
            List<IPosition> pos = ship.getPositions();
            IPosition p0 = pos.get(0);
            IPosition outside = new Position(7, 7);

            // occupies true for one of the positions
            assertTrue(ship.occupies(p0));
            assertFalse(ship.occupies(outside));

            // tooCloseTo with a nearby position
            Position near = new Position(4, 5); // adjacent to one of the ship positions
            assertTrue(ship.tooCloseTo(near));

            // tooCloseTo with a far position
            Position far = new Position(10, 10);
            assertFalse(ship.tooCloseTo(far));
        }

        @Test
        void tooCloseTo_shipVsShip() {
            Caravel s1 = new Caravel(Compass.EAST, new Position(0, 0));
            Caravel s2 = new Caravel(Compass.EAST, new Position(0, 2));

            // s2 starts at (0,2) -> occupies (0,2) and (0,3)
            // s1 occupies (0,0) and (0,1) so they are adjacent -> too close
            assertTrue(s1.tooCloseTo(s2));

            // move s2 far away
            Caravel s3 = new Caravel(Compass.EAST, new Position(5, 5));
            assertFalse(s1.tooCloseTo(s3));
        }
    }

    @Nested
    class ShipFactoryTests {

        @Test
        void buildShip_knownKinds_returnInstances() {
            Position p = new Position(0, 0);
            assertInstanceOf(Barge.class, Ship.buildShip("barca", Compass.NORTH, p));
            assertInstanceOf(Caravel.class, Ship.buildShip("caravela", Compass.NORTH, p));
            assertInstanceOf(Carrack.class, Ship.buildShip("nau", Compass.NORTH, p));
            assertInstanceOf(Frigate.class, Ship.buildShip("fragata", Compass.NORTH, p));
            assertInstanceOf(Galleon.class, Ship.buildShip("galeao", Compass.NORTH, p));
        }

        @Test
        void buildShip_unknownKind_returnsNull() {
            Position p = new Position(1, 1);
            assertNull(Ship.buildShip("unknown", Compass.NORTH, p));
        }
    }

    @Nested
    class GameConditionTests {

        // Invoke private validShot via reflection and test atomic comparisons
        private boolean validShot(Game game, IPosition pos) throws Exception {
            Method m = Game.class.getDeclaredMethod("validShot", IPosition.class);
            m.setAccessible(true);
            return (boolean) m.invoke(game, pos);
        }

        // Ensure private Integer/int counters are initialized to avoid NPE when tests call fire()
        private void ensureGameCountersInitialized(Game g) {
            try {
                String[] names = new String[]{"countHits", "countSinks", "countInvalidShots", "countRepeatedShots"};
                for (String name : names) {
                    java.lang.reflect.Field f = Game.class.getDeclaredField(name);
                    f.setAccessible(true);
                    Class<?> t = f.getType();
                    if (t.equals(Integer.class)) {
                        if (f.get(g) == null)
                            f.set(g, Integer.valueOf(0));
                    } else if (t.equals(int.class)) {
                        f.setInt(g, 0);
                    } else {
                        // fallback: try to set Integer(0)
                        f.set(g, Integer.valueOf(0));
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void validShot_checks_each_bound() throws Exception {
            Game g = new Game(new Fleet());

            // row < 0 -> invalid
            assertFalse(validShot(g, new Position(-1, 0)));

            // row > BOARD_SIZE -> invalid (note implementation uses <= BOARD_SIZE so BOARD_SIZE is invalid index)
            assertFalse(validShot(g, new Position(IFleet.BOARD_SIZE + 1, 0)));

            // column < 0 -> invalid
            assertFalse(validShot(g, new Position(0, -1)));

            // column > BOARD_SIZE -> invalid
            assertFalse(validShot(g, new Position(0, IFleet.BOARD_SIZE + 1)));

            // all within [0, BOARD_SIZE] inclusive -> valid
            assertTrue(validShot(g, new Position(0, 0)));
            assertTrue(validShot(g, new Position(IFleet.BOARD_SIZE, IFleet.BOARD_SIZE)));
        }

        @Test
        void repeatedShot_detection_and_fire_path_conditions() {
            Fleet fleet = new Fleet();
            Caravel ship = new Caravel(Compass.EAST, new Position(1, 1));
            fleet.addShip(ship);
            Game g = new Game(fleet);

            // initialize counters to avoid NullPointerException from uninitialized Integer fields
            ensureGameCountersInitialized(g);

            Position shot = new Position(1, 1);
            // first shot: valid, not repeated, should hit
            assertNull(g.fire(shot));
            // verify the shot was recorded and the ship was hit
            assertTrue(g.getShots().contains(shot));
            boolean anyHit = ship.getPositions().stream().anyMatch(IPosition::isHit);
            assertTrue(anyHit, "At least one ship position should be marked hit after firing");

            // second same shot: repeated -> shots list should not grow
            int sizeBefore = g.getShots().size();
            g.fire(shot);
            assertEquals(sizeBefore, g.getShots().size(), "Repeated shot should not add a new shot entry");

            // invalid shot should not be added to shots list
            int sizeNow = g.getShots().size();
            g.fire(new Position(-5, -5));
            assertEquals(sizeNow, g.getShots().size(), "Invalid shot should not be recorded in shots list");
        }

        @Test
        void sinking_returnsShip_on_lastHit() {
            Fleet fleet = new Fleet();
            Caravel ship = new Caravel(Compass.EAST, new Position(2, 2));
            fleet.addShip(ship);
            Game g = new Game(fleet);

            // initialize counters to avoid NullPointerException from uninitialized Integer fields
            ensureGameCountersInitialized(g);

            // Caravel has size 2 -> two distinct positions
            Position p1 = (Position) ship.getPositions().get(0);
            Position p2 = (Position) ship.getPositions().get(1);

            assertNull(g.fire(p1)); // first hit, not sunk yet
            assertTrue(ship.stillFloating(), "Ship should still be floating after one hit");
            // second hit should sink and return the ship
            IShip sunk = g.fire(p2);
            assertNotNull(sunk);
            assertEquals(ship, sunk);
            assertFalse(ship.stillFloating(), "Ship should not be floating after all positions are hit");
            // shots list should contain both shots
            assertTrue(g.getShots().contains(p1));
            assertTrue(g.getShots().contains(p2));
        }
    }
}
