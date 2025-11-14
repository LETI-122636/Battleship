package iscteiul.ista.battleship;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FleetConditionTest {

    // Helper to call private Fleet.isInsideBoard via reflection
    private boolean isInsideBoard(Fleet fleet, IShip ship) throws ReflectiveOperationException {
        Method m = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
        m.setAccessible(true);
        return (boolean) m.invoke(fleet, ship);
    }

    // Helper to set the private ships list on a Fleet instance (to simulate size > FLEET_SIZE)
    private void setFleetShips(Fleet fleet, List<IShip> ships) throws ReflectiveOperationException {
        Field f = Fleet.class.getDeclaredField("ships");
        f.setAccessible(true);
        f.set(fleet, ships);
    }

    @Nested
    class AddShipConditions {

        @Test
        void addShip_returnsFalse_when_sizeConditionFalse() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();

            // create a list larger than IFleet.FLEET_SIZE
            int limit = IFleet.FLEET_SIZE + 1;
            List<IShip> big = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                // use simple ships; positions can overlap because we only care about list size here
                big.add(new Barge(Compass.NORTH, new Position(i, 0)));
            }

            // replace private ships list with our big list
            setFleetShips(fleet, big);

            // Now try to add a valid ship; size condition should be false so addShip returns false
            IShip candidate = new Caravel(Compass.EAST, new Position(0, 5));
            assertFalse(fleet.addShip(candidate), "addShip should return false when fleet size condition fails");
        }

        @Test
        void addShip_returnsFalse_when_insideBoardConditionFalse() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();

            // create a ship positioned partially outside the board (top negative)
            IShip outside = new Caravel(Compass.NORTH, new Position(-1, 2));

            // size is OK and no collision, but isInsideBoard should be false -> addShip false
            assertFalse(fleet.addShip(outside));

            // also assert isInsideBoard directly via reflection
            assertFalse(isInsideBoard(fleet, outside));
        }

        @Test
        void addShip_returnsFalse_when_collisionRiskTrue() {
            Fleet fleet = new Fleet();

            // Add a first ship at (0,0) occupying (0,0) and (0,1)
            IShip first = new Caravel(Compass.EAST, new Position(0, 0));
            assertTrue(fleet.addShip(first), "precondition: first ship should be added");

            // Candidate placed adjacent/overlapping: starting at (0,1) -> occupies (0,1) and (0,2)
            IShip candidate = new Caravel(Compass.EAST, new Position(0, 1));
            assertFalse(fleet.addShip(candidate), "addShip should return false when collision risk exists");
        }

        @Test
        void addShip_returnsTrue_when_allConditionsTrue() {
            Fleet fleet = new Fleet();

            // Add a ship well inside board
            IShip s1 = new Caravel(Compass.EAST, new Position(1, 1));
            assertTrue(fleet.addShip(s1));

            // Add a non-conflicting ship also inside
            IShip s2 = new Caravel(Compass.EAST, new Position(3, 3));
            assertTrue(fleet.addShip(s2));
        }

        @Test
        void addShip_allows_when_sizeEqualsFleetSize() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            // build a list with exactly FLEET_SIZE elements
            List<IShip> exact = new ArrayList<>();
            for (int i = 0; i < IFleet.FLEET_SIZE; i++)
                exact.add(new Barge(Compass.NORTH, new Position(i % IFleet.BOARD_SIZE, i / IFleet.BOARD_SIZE)));

            setFleetShips(fleet, exact);

            // Now try to add a valid ship; ships.size() == FLEET_SIZE so addShip should still allow (<=)
            IShip candidate = new Caravel(Compass.EAST, new Position(0, 5));
            assertTrue(fleet.addShip(candidate), "addShip should allow when current size equals FLEET_SIZE (<= check)");
        }
    }

    @Nested
    class IsInsideBoardAtomicConditions {

        @Test
        void leftMostPos_outside_makes_isInsideBoard_false() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            // create a ship whose leftmost column < 0 by starting at negative column and EAST bearing
            IShip ship = new Caravel(Compass.EAST, new Position(2, -1));
            assertFalse(isInsideBoard(fleet, ship));
        }

        @Test
        void rightMostPos_outside_makes_isInsideBoard_false() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            // create a ship whose rightmost column > BOARD_SIZE - 1
            int bigCol = IFleet.BOARD_SIZE; // one past last valid index
            IShip ship = new Caravel(Compass.EAST, new Position(2, bigCol));
            assertFalse(isInsideBoard(fleet, ship));
        }

        @Test
        void topMostPos_outside_makes_isInsideBoard_false() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            // starting row negative causes topMost < 0
            IShip ship = new Caravel(Compass.NORTH, new Position(-1, 5));
            assertFalse(isInsideBoard(fleet, ship));
        }

        @Test
        void bottomMostPos_outside_makes_isInsideBoard_false() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            // create ship with bottomMost > BOARD_SIZE - 1 by starting row at BOARD_SIZE
            int bigRow = IFleet.BOARD_SIZE; // one past last valid index
            IShip ship = new Caravel(Compass.NORTH, new Position(bigRow, 5));
            assertFalse(isInsideBoard(fleet, ship));
        }

        @Test
        void fully_inside_board_isInsideBoard_true() throws ReflectiveOperationException {
            Fleet fleet = new Fleet();
            IShip ship = new Caravel(Compass.NORTH, new Position(2, 2));
            assertTrue(isInsideBoard(fleet, ship));
        }
    }

    @Nested
    class PrintStatusTests {

        @Test
        void printStatus_outputs_expected_sections() {
            Fleet fleet = new Fleet();
            // add one ship of each category to ensure printShipsByCategory prints something
            fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
            fleet.addShip(new Caravel(Compass.EAST, new Position(2, 2)));
            fleet.addShip(new Carrack(Compass.SOUTH, new Position(4, 4)));
            fleet.addShip(new Frigate(Compass.WEST, new Position(6, 6)));
            fleet.addShip(new Galleon(Compass.NORTH, new Position(8, 8)));

            java.io.PrintStream oldOut = System.out;
            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                 java.io.PrintStream ps = new java.io.PrintStream(baos)) {
                System.setOut(ps);
                fleet.printStatus();
                ps.flush();
                String out = baos.toString();
                // The output should contain category names or ship representations
                assertFalse(out.isEmpty());
            } catch (Exception e) {
                fail("Exception while testing printStatus: " + e.getMessage());
            } finally {
                System.setOut(oldOut);
            }
        }
    }

    @Nested
    class ShipTests {

        @Test
        void ship_shoot_with_nonmatching_position_does_not_mark_hits() {
            Caravel ship = new Caravel(Compass.EAST, new Position(3, 3));
            Position outside = new Position(9, 9);
            ship.shoot(outside);
            // none of the positions should be marked as hit
            boolean anyHit = ship.getPositions().stream().anyMatch(IPosition::isHit);
            assertFalse(anyHit);
        }
    }
}
