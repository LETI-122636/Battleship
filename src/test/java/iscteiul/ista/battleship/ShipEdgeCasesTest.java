package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de casos extremos para Ship")
class ShipEdgeCasesTest {

    @Nested
    @DisplayName("Testes de construção de navios")
    class ShipConstructionTests {

        @Test
        @DisplayName("buildShip com categoria desconhecida retorna null")
        void buildShipUnknownCategory() {
            Ship result = Ship.buildShip("CategoriaDesconhecida", Compass.NORTH, new Position(1, 1));
            assertNull(result);
        }

        @Test
        @DisplayName("buildShip com bearing UNKNOWN")
        void buildShipWithUnknownBearing() {
            // Barge aceita UNKNOWN
            assertDoesNotThrow(() -> {
                Ship.buildShip("barca", Compass.UNKNOWN, new Position(1, 1));
            });

            // Outros navios lançam IllegalArgumentException - testamos isso
            String[] shipTypes = {"caravela", "nau", "fragata", "galeao"};

            for (String type : shipTypes) {
                assertThrows(IllegalArgumentException.class, () -> {
                    Ship.buildShip(type, Compass.UNKNOWN, new Position(1, 1));
                });
            }
        }
    }

    @Nested
    @DisplayName("Testes de comportamento de navios")
    class ShipBehaviorTests {

        @Test
        @DisplayName("stillFloating com múltiplas posições")
        void stillFloatingWithMultiplePositions() {
            Caravel caravel = new Caravel(Compass.NORTH, new Position(1, 1));

            caravel.shoot(new Position(1, 1));
            assertTrue(caravel.stillFloating());

            caravel.shoot(new Position(2, 1));
            assertFalse(caravel.stillFloating());
        }

        @Test
        @DisplayName("tooCloseTo com parâmetros null lança exceção")
        void tooCloseToWithNullParameters() {
            Barge ship = new Barge(Compass.NORTH, new Position(1, 1));

            // Testa que lança exceção (pode ser AssertionError ou NullPointerException)
            assertThrows(Throwable.class, () -> ship.tooCloseTo((IPosition) null));
            assertThrows(Throwable.class, () -> ship.tooCloseTo((IShip) null));
        }

        @Test
        @DisplayName("shoot com posição não ocupada")
        void shootUnoccupiedPosition() {
            Barge ship = new Barge(Compass.NORTH, new Position(1, 1));
            Position unoccupied = new Position(9, 9);

            assertDoesNotThrow(() -> ship.shoot(unoccupied));
        }

        @Test
        @DisplayName("occupies com posição null lança exceção")
        void occupiesWithNullPosition() {
            Barge ship = new Barge(Compass.NORTH, new Position(1, 1));
            assertThrows(Throwable.class, () -> ship.occupies(null));
        }

        @Test
        @DisplayName("shoot com posição null lança exceção")
        void shootWithNullPosition() {
            Barge ship = new Barge(Compass.NORTH, new Position(1, 1));
            assertThrows(Throwable.class, () -> ship.shoot(null));
        }
    }

    @Nested
    @DisplayName("Testes de bounding boxes")
    class BoundingBoxTests {

        @Test
        @DisplayName("getTopMostPos com diferentes orientações")
        void getTopMostPos() {
            Caravel horizontal = new Caravel(Compass.EAST, new Position(3, 3));
            Caravel vertical = new Caravel(Compass.SOUTH, new Position(3, 3));

            assertEquals(3, horizontal.getTopMostPos());
            assertEquals(3, vertical.getTopMostPos());
        }

        @Test
        @DisplayName("getBottomMostPos com diferentes orientações")
        void getBottomMostPos() {
            Caravel horizontal = new Caravel(Compass.EAST, new Position(3, 3));
            Caravel vertical = new Caravel(Compass.SOUTH, new Position(3, 3));

            assertEquals(3, horizontal.getBottomMostPos());
            assertEquals(4, vertical.getBottomMostPos());
        }

        @Test
        @DisplayName("getLeftMostPos com diferentes orientações")
        void getLeftMostPos() {
            Caravel horizontal = new Caravel(Compass.EAST, new Position(3, 3));
            Caravel vertical = new Caravel(Compass.SOUTH, new Position(3, 3));

            assertEquals(3, horizontal.getLeftMostPos());
            assertEquals(3, vertical.getLeftMostPos());
        }

        @Test
        @DisplayName("getRightMostPos com diferentes orientações")
        void getRightMostPos() {
            Caravel horizontal = new Caravel(Compass.EAST, new Position(3, 3));
            Caravel vertical = new Caravel(Compass.SOUTH, new Position(3, 3));

            assertEquals(4, horizontal.getRightMostPos());
            assertEquals(3, vertical.getRightMostPos());
        }

        @Test
        @DisplayName("Bounding box com navio de 1 posição")
        void boundingBoxWithSinglePosition() {
            Barge ship = new Barge(Compass.NORTH, new Position(5, 5));

            assertEquals(5, ship.getTopMostPos());
            assertEquals(5, ship.getBottomMostPos());
            assertEquals(5, ship.getLeftMostPos());
            assertEquals(5, ship.getRightMostPos());
        }
    }

    @Nested
    @DisplayName("Testes de construtores")
    class ConstructorTests {

        @Test
        @DisplayName("Construtores com bearing null lança exceção")
        void constructorWithNullBearing() {
            assertThrows(Throwable.class, () -> new Barge(null, new Position(1, 1)));
        }

        @Test
        @DisplayName("Construtores com position null lança exceção")
        void constructorWithNullPosition() {
            assertThrows(Throwable.class, () -> new Barge(Compass.NORTH, null));
        }

        @Test
        @DisplayName("toString retorna string não nula")
        void toStringReturnsNonNull() {
            Barge ship = new Barge(Compass.NORTH, new Position(1, 1));
            String result = ship.toString();
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Testes de cenários complexos")
    class ComplexScenariosTests {

        @Test
        @DisplayName("Navio está muito próximo de si mesmo")
        void shipIsTooCloseToItself() {
            Barge ship = new Barge(Compass.NORTH, new Position(5, 5));
            assertTrue(ship.tooCloseTo(ship));
        }

        @Test
        @DisplayName("Navios em posições não adjacentes")
        void shipsNotTooCloseWhenFarApart() {
            Barge ship1 = new Barge(Compass.NORTH, new Position(1, 1));
            Barge ship2 = new Barge(Compass.NORTH, new Position(8, 8));
            assertFalse(ship1.tooCloseTo(ship2));
        }

        @Test
        @DisplayName("Navios em posições adjacentes")
        void shipsTooCloseWhenAdjacent() {
            Barge ship1 = new Barge(Compass.NORTH, new Position(5, 5));
            Barge ship2 = new Barge(Compass.NORTH, new Position(5, 6));
            assertTrue(ship1.tooCloseTo(ship2));
        }

        @Test
        @DisplayName("Posição adjacente diagonal")
        void diagonalPositionIsTooClose() {
            Barge ship = new Barge(Compass.NORTH, new Position(5, 5));
            Position diagonalPos = new Position(4, 4);
            assertTrue(ship.tooCloseTo(diagonalPos));
        }

        @Test
        @DisplayName("Múltiplos navios de diferentes tipos")
        void multipleShipsDifferentTypes() {
            Barge barge = new Barge(Compass.NORTH, new Position(1, 1));
            Caravel caravel = new Caravel(Compass.EAST, new Position(4, 4));
            Carrack carrack = new Carrack(Compass.SOUTH, new Position(7, 7));

            assertFalse(barge.tooCloseTo(caravel));
            assertFalse(caravel.tooCloseTo(carrack));

            assertEquals(1, barge.getSize());
            assertEquals(2, caravel.getSize());
            assertEquals(3, carrack.getSize());
        }

        @Test
        @DisplayName("Todos os métodos getters retornam valores consistentes")
        void allGettersReturnConsistentValues() {
            Barge ship = new Barge(Compass.NORTH, new Position(2, 3));

            assertEquals("Barca", ship.getCategory());
            assertEquals(Compass.NORTH, ship.getBearing());
            assertEquals(1, ship.getSize());
            assertNotNull(ship.getPosition());
            assertNotNull(ship.getPositions());
            assertEquals(1, ship.getPositions().size());
        }

        @Test
        @DisplayName("Navios com posições sobrepostas são muito próximos")
        void shipsWithOverlappingPositionsAreTooClose() {
            Barge ship1 = new Barge(Compass.NORTH, new Position(5, 5));
            Barge ship2 = new Barge(Compass.NORTH, new Position(5, 5));
            assertTrue(ship1.tooCloseTo(ship2));
        }

        @Test
        @DisplayName("Navio grande próximo de navio pequeno")
        void largeShipCloseToSmallShip() {
            Carrack largeShip = new Carrack(Compass.EAST, new Position(5, 5));
            Barge smallShip = new Barge(Compass.NORTH, new Position(5, 8));
            assertTrue(largeShip.tooCloseTo(smallShip));
        }

        @Test
        @DisplayName("Teste de afundamento progressivo")
        void progressiveSinking() {
            Carrack ship = new Carrack(Compass.NORTH, new Position(1, 1));

            ship.shoot(new Position(1, 1));
            assertTrue(ship.stillFloating());

            ship.shoot(new Position(2, 1));
            assertTrue(ship.stillFloating());

            ship.shoot(new Position(3, 1));
            assertFalse(ship.stillFloating());
        }

        @Test
        @DisplayName("Posições ocupadas são marcadas corretamente")
        void positionsAreOccupiedCorrectly() {
            Caravel ship = new Caravel(Compass.NORTH, new Position(2, 2));

            assertTrue(ship.occupies(new Position(2, 2)));
            assertTrue(ship.occupies(new Position(3, 2)));
            assertFalse(ship.occupies(new Position(4, 2)));
            assertFalse(ship.occupies(new Position(2, 3)));
        }

        @Test
        @DisplayName("Navios com diferentes bearings")
        void shipsWithDifferentBearings() {
            Caravel north = new Caravel(Compass.NORTH, new Position(1, 1));
            Caravel east = new Caravel(Compass.EAST, new Position(3, 3));
            Caravel south = new Caravel(Compass.SOUTH, new Position(5, 5));
            Caravel west = new Caravel(Compass.WEST, new Position(7, 7));

            assertEquals(Compass.NORTH, north.getBearing());
            assertEquals(Compass.EAST, east.getBearing());
            assertEquals(Compass.SOUTH, south.getBearing());
            assertEquals(Compass.WEST, west.getBearing());
        }

        @Test
        @DisplayName("Teste de limites do tabuleiro")
        void boardBoundaryTests() {
            Barge topLeft = new Barge(Compass.NORTH, new Position(0, 0));
            Barge bottomRight = new Barge(Compass.NORTH, new Position(9, 9));

            assertEquals(0, topLeft.getTopMostPos());
            assertEquals(0, topLeft.getLeftMostPos());
            assertEquals(9, bottomRight.getBottomMostPos());
            assertEquals(9, bottomRight.getRightMostPos());
        }

        @Test
        @DisplayName("Teste de todos os tipos de navios")
        void allShipTypes() {
            Barge barge = new Barge(Compass.NORTH, new Position(1, 1));
            Caravel caravel = new Caravel(Compass.EAST, new Position(2, 2));
            Carrack carrack = new Carrack(Compass.SOUTH, new Position(3, 3));
            Frigate frigate = new Frigate(Compass.WEST, new Position(4, 4));
            Galleon galleon = new Galleon(Compass.NORTH, new Position(5, 5));

            assertEquals(1, barge.getSize());
            assertEquals(2, caravel.getSize());
            assertEquals(3, carrack.getSize());
            assertEquals(4, frigate.getSize());
            assertEquals(5, galleon.getSize());
        }
    }
}
//
//
//