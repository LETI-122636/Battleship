package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes unitários para a entidade Carrack (Nau)")
class CarrackTest {

    private Carrack carrack;
    private Position origin;

    @BeforeEach
    void setUp() {
        origin = new Position(2, 2);
        carrack = new Carrack(Compass.NORTH, origin);
    }

    @AfterEach
    void tearDown() {
        carrack = null;
        origin = null;
    }

    @Test
    @DisplayName("getSize deve retornar 3 para uma Carrack (Nau)")
    void getSize() {
        assertEquals(3, carrack.getSize(), "Carrack deve ter tamanho 3");
    }

    @Test
    @DisplayName("Posicionamento inicial para bearing NORTH (vertical)")
    void initialPositionsNorth() {
        assertAll("posições iniciais",
                () -> assertTrue(carrack.occupies(new Position(2, 2)), "Deve ocupar (2,2)"),
                () -> assertTrue(carrack.occupies(new Position(3, 2)), "Deve ocupar (3,2)"),
                () -> assertTrue(carrack.occupies(new Position(4, 2)), "Deve ocupar (4,2)")
        );
    }

    @Nested
    @DisplayName("Posicionamento por bearing")
    class BearingPlacement {

        @ParameterizedTest
        @EnumSource(names = {"NORTH", "SOUTH"})
        @DisplayName("NORTH ou SOUTH -> posições verticais consecutivas")
        void verticalPlacements(Compass bearing) {
            Carrack c = new Carrack(bearing, new Position(5, 7));
            assertAll("vertical",
                    () -> assertEquals(3, c.getPositions().size(), "Deve ter 3 posições"),
                    () -> assertTrue(c.occupies(new Position(5, 7))),
                    () -> assertTrue(c.occupies(new Position(6, 7))),
                    () -> assertTrue(c.occupies(new Position(7, 7)))
            );
        }

        @ParameterizedTest
        @EnumSource(names = {"EAST", "WEST"})
        @DisplayName("EAST ou WEST -> posições horizontais consecutivas")
        void horizontalPlacements(Compass bearing) {
            Carrack c = new Carrack(bearing, new Position(8, 1));
            assertAll("horizontal",
                    () -> assertEquals(3, c.getPositions().size(), "Deve ter 3 posições"),
                    () -> assertTrue(c.occupies(new Position(8, 1))),
                    () -> assertTrue(c.occupies(new Position(8, 2))),
                    () -> assertTrue(c.occupies(new Position(8, 3)))
            );
        }
    }

    @Test
    @DisplayName("stillFloating deve ser true até todas as posições serem atingidas")
    void stillFloatingAndSinking() {
        Position p1 = new Position(2, 2);
        Position p2 = new Position(3, 2);
        Position p3 = new Position(4, 2);

        assertTrue(carrack.stillFloating(), "Deve estar flutuando inicialmente");

        carrack.shoot(p1);
        assertTrue(carrack.stillFloating(), "Ainda deve flutuar após um tiro parcial");

        carrack.shoot(p2);
        assertTrue(carrack.stillFloating(), "Ainda deve flutuar após dois tiros parciais");

        carrack.shoot(p3);
        assertFalse(carrack.stillFloating(), "Deve afundar quando todas as posições forem atingidas");
    }

    @Test
    @DisplayName("Construtor com bearing UNKNOWN lança IllegalArgumentException")
    void unknownBearingThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Carrack(Compass.UNKNOWN, new Position(0, 0)), "Deve lançar IllegalArgumentException para bearing desconhecido");
    }

    @Nested
    @DisplayName("Comportamento de proximidade")
    class Proximity {

        @Test
        @DisplayName("tooCloseTo com posição adjacente devolve true")
        void tooCloseToPosition() {
            Position adj = new Position(1, 1); // adj to (2,2)
            assertTrue(carrack.tooCloseTo(adj), "Deve considerar posição adjacente como demasiado próxima");
        }

        @Test
        @DisplayName("tooCloseTo com outro navio adjacente devolve true")
        void tooCloseToShip() {
            Barge other = new Barge(Compass.NORTH, new Position(1, 1));
            assertTrue(carrack.tooCloseTo(other), "Deve considerar navio adjacente como demasiado próximo");
        }
    }

    @Disabled("Exemplo de teste desativado")
    @Test
    @DisplayName("Teste desativado de exemplo")
    void disabledExample() {
        // Exemplo
    }

}