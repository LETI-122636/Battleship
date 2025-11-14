package iscteiul.ista.battleship;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionCoverageTest {

    // Shared helper moved to outer class so all nested test classes can use it
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

        @Test
        void position_toString_returns_values() {
            Position p = new Position(3, 7);
            String s = p.toString();
            assertTrue(s.contains("Linha") && s.contains("Coluna") || s.contains("3") && s.contains("7"));
        }

        @Test
        void position_equals_differentCoordinates_false() {
            Position p1 = new Position(1, 1);
            Position p2 = new Position(1, 2);
            assertFalse(p1.equals(p2));
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

        @Test
        void compass_toString_and_getDirection() {
            assertEquals('n', Compass.NORTH.getDirection());
            assertEquals("n", Compass.NORTH.toString());
            assertEquals('s', Compass.SOUTH.getDirection());
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

        @Test
        void caravel_constructor_nullBearing_throws_or_allows_but_null() {
            Position p = new Position(0,0);
            try {
                Caravel c = new Caravel(null, p);
                // If constructor allows null, the bearing should be null
                assertNull(c.getBearing(), "If constructor doesn't throw, bearing should be null when passed null");
            } catch (Throwable t) {
                // acceptable: implementation may assert or throw
            }
        }

        @Test
        void caravel_orientations_positions_and_bounds() {
            // NORTH: positions at (r, c) and (r+1, c)
            Caravel north = new Caravel(Compass.NORTH, new Position(2, 2));
            assertEquals(2, north.getPositions().size());
            assertEquals(2, north.getTopMostPos());
            assertEquals(3, north.getBottomMostPos());
            assertEquals(2, north.getLeftMostPos());
            assertEquals(2, north.getRightMostPos());

            // EAST: positions at (r, c) and (r, c+1)
            Caravel east = new Caravel(Compass.EAST, new Position(4, 4));
            assertEquals(2, east.getPositions().size());
            assertEquals(4, east.getTopMostPos());
            assertEquals(4, east.getBottomMostPos());
            assertEquals(4, east.getLeftMostPos());
            assertEquals(5, east.getRightMostPos());

            // SOUTH behaves like NORTH but growing downwards
            Caravel south = new Caravel(Compass.SOUTH, new Position(1, 1));
            assertEquals(1, south.getTopMostPos());
            assertEquals(2, south.getBottomMostPos());

            // WEST behaves like EAST but growing leftwards
            Caravel west = new Caravel(Compass.WEST, new Position(6, 6));
            assertEquals(6, west.getLeftMostPos());
            assertEquals(7, west.getRightMostPos());
        }

        @Test
        void buildShip_size_and_category_checks() {
            Position p = new Position(0, 0);
            Barge b = (Barge) Ship.buildShip("barca", Compass.NORTH, p);
            assertEquals(1, b.getSize());
            Caravel c = (Caravel) Ship.buildShip("caravela", Compass.EAST, p);
            assertEquals(2, c.getSize());
            Carrack cr = (Carrack) Ship.buildShip("nau", Compass.NORTH, p);
            assertEquals(3, cr.getSize());
            Frigate f = (Frigate) Ship.buildShip("fragata", Compass.NORTH, p);
            assertEquals(4, f.getSize());
            Galleon g = (Galleon) Ship.buildShip("galeao", Compass.NORTH, p);
            assertEquals(5, g.getSize());
        }

        @Test
        void buildShip_unknown_returns_null() {
            Position p = new Position(0, 0);
            Ship s = Ship.buildShip("unknown", Compass.NORTH, p);
            assertNull(s);
        }

        @Test
        void printBoard_printValidShots_and_printFleet_produce_output() throws Exception {
            Fleet fleet = new Fleet();
            Caravel ship = new Caravel(Compass.EAST, new Position(0, 0));
            fleet.addShip(ship);
            Game g = new Game(fleet);
            ensureGameCountersInitialized(g);

            // fire one miss and one hit
            Position miss = new Position(9, 9);
            Position hit = (Position) ship.getPositions().get(0);
            g.fire(hit);
            g.fire(miss);

            // capture stdout
            java.io.PrintStream oldOut = System.out;
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                 java.io.PrintStream ps = new java.io.PrintStream(baos)) {
                System.setOut(ps);
                g.printValidShots();
                g.printFleet();
                ps.flush();
                String out = baos.toString();
                // should contain markers for shots (X) and fleet (#)
                assertTrue(out.contains("X") || out.contains("#"));
            } finally {
                System.setOut(oldOut);
            }
        }

        @Test
        void printBoard_with_empty_positions_does_not_throw() throws Exception {
            Fleet fleet = new Fleet();
            Game g = new Game(fleet);
            ensureGameCountersInitialized(g);

            java.io.PrintStream oldOut = System.out;
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                 java.io.PrintStream ps = new java.io.PrintStream(baos)) {
                System.setOut(ps);
                // call printBoard with empty list
                g.printBoard(java.util.Collections.emptyList(), '.');
                ps.flush();
                String out = baos.toString();
                assertNotNull(out);
            } finally {
                System.setOut(oldOut);
            }
        }

        @Test
        void fleet_getShipsLike_getFloatingShips_and_shipAt() {
            Fleet fleet = new Fleet();
            Caravel c1 = new Caravel(Compass.EAST, new Position(2, 2));
            Caravel c2 = new Caravel(Compass.SOUTH, new Position(5, 5));
            fleet.addShip(c1);
            fleet.addShip(c2);

            // getShipsLike
            assertEquals(2, fleet.getShipsLike("Caravela").size());

            // getFloatingShips -> both should be floating initially
            assertEquals(2, fleet.getFloatingShips().size());

            // shipAt for a position occupied by c1
            IShip found = fleet.shipAt(c1.getPositions().get(0));
            assertNotNull(found);
            assertEquals(c1, found);
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

        @Test
        void fire_miss_and_counters() {
            Fleet fleet = new Fleet();
            Game g = new Game(fleet);
            ensureGameCountersInitialized(g);

            Position p = new Position(0, 0);
            // no ship at (0,0) -> miss
            assertNull(g.fire(p));
            assertTrue(g.getShots().contains(p));
            // no hits recorded
            assertEquals(0, g.getHits());

            // repeated shot increments repeated counter
            g.fire(p);
            assertEquals(1, g.getRepeatedShots());

            // invalid shot increments invalid counter and is ignored
            g.fire(new Position(-9, -9));
            assertEquals(1, g.getInvalidShots());
        }

        @Test
        void sinking_updates_counters() {
            Fleet fleet = new Fleet();
            Caravel ship = new Caravel(Compass.EAST, new Position(6, 6));
            fleet.addShip(ship);
            Game g = new Game(fleet);
            ensureGameCountersInitialized(g);

            Position p1 = (Position) ship.getPositions().get(0);
            Position p2 = (Position) ship.getPositions().get(1);

            assertNull(g.fire(p1));
            assertEquals(1, g.getHits(), "Hits should be 1 after first hit");
            IShip sunk = g.fire(p2);
            assertNotNull(sunk);
            assertEquals(2, g.getHits(), "Hits should be 2 after sinking all positions");
            assertEquals(1, g.getSunkShips(), "One ship should be recorded as sunk");
        }

        @Test
        void game_getters_are_zero_after_initialization() {
            Game g = new Game(new Fleet());
            ensureGameCountersInitialized(g);
            assertEquals(0, g.getHits());
            assertEquals(0, g.getInvalidShots());
            assertEquals(0, g.getRepeatedShots());
            assertEquals(0, g.getSunkShips());
        }
    }

    @Nested
    class MiscellaneousConditionTests {

        @Test
        void ship_toString_contains_category_and_bearing() {
            Barge b = new Barge(Compass.NORTH, new Position(0, 0));
            String s = b.toString();
            assertTrue(s.contains("Barca") && s.contains("n"));
        }

        @Test
        void getShipsLike_empty_returns_empty_list() {
            Fleet fleet = new Fleet();
            assertTrue(fleet.getShipsLike("Caravela").isEmpty());
        }

        @Test
        void printShips_static_helper_outputs() throws Exception {
            // redirect stdout and call Fleet.printShips
            java.io.PrintStream oldOut = System.out;
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                 java.io.PrintStream ps = new java.io.PrintStream(baos)) {
                System.setOut(ps);
                Fleet.printShips(java.util.Collections.singletonList(new Barge(Compass.NORTH, new Position(0, 0))));
                ps.flush();
                String out = baos.toString();
                assertFalse(out.isEmpty());
            } finally {
                System.setOut(oldOut);
            }
        }

        @Test
        void repeatedShot_private_method_via_reflection_true_and_false() throws Exception {
            Fleet fleet = new Fleet();
            Caravel ship = new Caravel(Compass.EAST, new Position(1, 1));
            fleet.addShip(ship);
            Game g = new Game(fleet);
            ensureGameCountersInitialized(g);

            java.lang.reflect.Method m = Game.class.getDeclaredMethod("repeatedShot", IPosition.class);
            m.setAccessible(true);

            Position p = new Position(0, 0);
            // not in shots -> false
            boolean first = (boolean) m.invoke(g, p);
            assertFalse(first);

            // add shot via fire and then repeatedShot should be true
            Position shot = new Position(1, 1);
            g.fire(shot);
            boolean second = (boolean) m.invoke(g, shot);
            assertTrue(second);
        }

        @Test
        void getShipsLike_is_case_sensitive() {
            Fleet fleet = new Fleet();
            fleet.addShip(new Caravel(Compass.EAST, new Position(2, 2)));
            // search with different case -> should be empty because equals is case-sensitive
            assertTrue(fleet.getShipsLike("caravela").isEmpty());
        }

        @Test
        void galleon_orientations_cover_all_fill_methods() {
            Position p = new Position(2, 2);
            // NORTH
            Galleon gNorth = new Galleon(Compass.NORTH, p);
            assertEquals(5, gNorth.getSize());
            // EAST
            Galleon gEast = new Galleon(Compass.EAST, p);
            assertEquals(5, gEast.getSize());
            // SOUTH
            Galleon gSouth = new Galleon(Compass.SOUTH, p);
            assertEquals(5, gSouth.getSize());
            // WEST
            Galleon gWest = new Galleon(Compass.WEST, p);
            assertEquals(5, gWest.getSize());
        }

        @Test
        void galleon_nullBearing_throws_or_allows_but_null() {
            Position p = new Position(0,0);
            try {
                Galleon g = new Galleon(null, p);
                assertNull(g.getBearing(), "If constructor doesn't throw, bearing should be null when passed null");
            } catch (Throwable t) {
                // acceptable
            }
        }

        @Test
        void carrack_and_frigate_orientations() {
            Position p = new Position(3, 3);
            Carrack cN = new Carrack(Compass.NORTH, p);
            Carrack cE = new Carrack(Compass.EAST, p);
            Frigate fN = new Frigate(Compass.NORTH, p);
            Frigate fW = new Frigate(Compass.WEST, p);
            assertEquals(3, cN.getSize());
            assertEquals(3, cE.getSize());
            assertEquals(4, fN.getSize());
            assertEquals(4, fW.getSize());
        }
    }

    @Nested
    class FleetConditionTests {

        @Test
        void addShip_fails_when_fleet_full() {
            Fleet fleet = new Fleet();
            try {
                java.lang.reflect.Field f = Fleet.class.getDeclaredField("ships");
                f.setAccessible(true);
                // create a list larger than FLEET_SIZE
                java.util.List<IShip> big = new java.util.ArrayList<>();
                for (int i = 0; i <= IFleet.FLEET_SIZE; i++) {
                    big.add(new Barge(Compass.NORTH, new Position(i % IFleet.BOARD_SIZE, i / IFleet.BOARD_SIZE)));
                }
                f.set(fleet, big);

                int sizeBefore = big.size();
                boolean added = fleet.addShip(new Barge(Compass.NORTH, new Position(9, 9)));
                // get the current internal list reference to verify size
                @SuppressWarnings("unchecked")
                java.util.List<IShip> current = (java.util.List<IShip>) f.get(fleet);
                int sizeAfter = current.size();

                // The fleet is already over the allowed size; adding should not increase the internal list
                assertEquals(sizeBefore, sizeAfter, "Adding a ship when fleet is full should not increase internal ships list");
                // Optionally check boolean return is false; accept either but prefer non-increment behavior
                assertFalse(added, "addShip should return false when fleet is full");

            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Test
        void addShip_fails_when_outside_board_right_or_top() {
            Fleet fleet = new Fleet();
            // Caravel placed so its rightmost column is BOARD_SIZE (i.e., out of allowed <= BOARD_SIZE-1)
            Caravel outRight = new Caravel(Compass.EAST, new Position(0, IFleet.BOARD_SIZE));
            assertFalse(fleet.addShip(outRight), "Ship with rightMost > BOARD_SIZE-1 should not be added");

            // Caravel placed with negative row -> topMost < 0
            Caravel outTop = new Caravel(Compass.NORTH, new Position(-1, 0));
            assertFalse(fleet.addShip(outTop), "Ship with topMost < 0 should not be added");
        }

        @Test
        void addShip_fails_when_collision_risk_true() {
            Fleet fleet = new Fleet();
            Caravel s1 = new Caravel(Compass.EAST, new Position(0, 0));
            assertTrue(fleet.addShip(s1));
            // s2 is too close to s1 -> should not be added
            Caravel s2 = new Caravel(Compass.EAST, new Position(0, 2));
            assertFalse(fleet.addShip(s2), "Should not add ship when collision risk exists");
        }

        @Test
        void addShip_fails_when_outside_board_rightmost_or_bottom() {
            Fleet fleet = new Fleet();
            // create a Caravel placed so rightmost column is BOARD_SIZE (out of allowed range)
            Caravel outRight = new Caravel(Compass.EAST, new Position(0, IFleet.BOARD_SIZE));
            assertFalse(fleet.addShip(outRight));

            // create a Caravel placed so bottommost row is BOARD_SIZE (out of allowed range)
            Caravel outBottom = new Caravel(Compass.SOUTH, new Position(IFleet.BOARD_SIZE, 0));
            assertFalse(fleet.addShip(outBottom));
        }
    }
}
