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
@DisplayName("Testes unitários para a entidade Caravel")
class CaravelTest {

    private Position origin;
    private Caravel caravel;

    @BeforeEach
    void setUp() {
        origin = new Position(3, 4);
        caravel = new Caravel(Compass.NORTH, origin);
    }

    @AfterEach
    void tearDown() {
        caravel = null;
        origin = null;
    }

    @Test
    @DisplayName("getSize deve retornar 2 para uma Caravel")
    void getSize() {
        assertEquals(2, caravel.getSize(), "Caravel deve ter tamanho 2");
    }

    @Nested
    @DisplayName("Posicionamento consoante o bearing")
    class PositioningTests {

        @ParameterizedTest
        @EnumSource(names = {"NORTH", "SOUTH"}, mode = EnumSource.Mode.MATCH_ANY)
        @DisplayName("Bearing NORTH ou SOUTH -> posições verticais consecutivas")
        void verticalBearings(Compass bearing) {
            Caravel c = new Caravel(bearing, new Position(5, 6));
            assertAll("vertical placement",
                    () -> assertEquals(2, c.getPositions().size(), "Deve ter 2 posições"),
                    () -> assertTrue(c.occupies(new Position(5, 6)), "Deve ocupar a posição inicial"),
                    () -> assertTrue(c.occupies(new Position(6, 6)), "Deve ocupar a posição seguinte na linha")
            );
        }

        @ParameterizedTest
        @EnumSource(names = {"EAST", "WEST"}, mode = EnumSource.Mode.MATCH_ANY)
        @DisplayName("Bearing EAST ou WEST -> posições horizontais consecutivas")
        void horizontalBearings(Compass bearing) {
            Caravel c = new Caravel(bearing, new Position(7, 2));
            assertAll("horizontal placement",
                    () -> assertEquals(2, c.getPositions().size(), "Deve ter 2 posições"),
                    () -> assertTrue(c.occupies(new Position(7, 2)), "Deve ocupar a posição inicial"),
                    () -> assertTrue(c.occupies(new Position(7, 3)), "Deve ocupar a posição seguinte na coluna")
            );
        }

        @Test
        @DisplayName("occupies reconhece posições ocupadas e não ocupadas")
        void occupies() {
            assertAll(
                    () -> assertTrue(caravel.occupies(new Position(3, 4)), "Deve ocupar origem"),
                    () -> assertTrue(caravel.occupies(new Position(4, 4)), "Deve ocupar a segunda posição"),
                    () -> assertFalse(caravel.occupies(new Position(0, 0)), "Não deve ocupar posição distante")
            );
        }
    }

    @Nested
    @DisplayName("Comportamento perante tiros e estado de flutuação")
    class ShootingTests {

        @Test
        @DisplayName("Ainda flutuando até todas as posições serem atingidas")
        void stillFloatingAndSinking() {
            Position p1 = new Position(3, 4);
            Position p2 = new Position(4, 4);

            assertTrue(caravel.stillFloating(), "Inicialmente deve estar flutuando");

            caravel.shoot(p1);
            assertAll("após um tiro",
                    () -> assertTrue(caravel.stillFloating(), "Deve continuar flutuando se apenas parte for atingida"),
                    () -> assertTrue(caravel.getPositions().get(0).isHit() || caravel.getPositions().get(1).isHit(), "Pelo menos uma posição deve estar marcada como atingida")
            );

            caravel.shoot(p2);
            assertFalse(caravel.stillFloating(), "Deve afundar quando todas as posições forem atingidas");
        }
    }

    @Nested
    @DisplayName("Validação do construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Construtor com bearing nulo lança NullPointerException")
        void nullBearingThrows() {
            try {
                new Caravel(null, new Position(0, 0));
                fail("Construtor deveria lançar exceção quando bearing é nulo");
            } catch (Throwable t) {
                // Dependendo se assertions estão ativas, a superclasse pode lançar AssertionError
                assertTrue(t instanceof NullPointerException || t instanceof AssertionError,
                        () -> "Esperado NullPointerException ou AssertionError, foi: " + t.getClass());
            }
        }

        @Test
        @DisplayName("Construtor com bearing UNKNOWN lança IllegalArgumentException")
        void unknownBearingThrows() {
            assertThrows(IllegalArgumentException.class, () -> new Caravel(Compass.UNKNOWN, new Position(1, 1)), "Deve lançar IllegalArgumentException para bearing desconhecido");
        }
    }

    @Test
    @DisplayName("tooCloseTo devolve true para navio adjacente")
    void tooCloseToAdjacentShip() {
        Barge nearby = new Barge(Compass.NORTH, new Position(2, 3)); // adjacent to (3,4)
        assertTrue(caravel.tooCloseTo(nearby), "Deve considerar navio adjacente como demasiado próximo");
    }

    @Disabled("Exemplo de teste desativado")
    @Test
    @DisplayName("Teste desativado de exemplo")
    void disabledExample() {
        // Exemplo
    }

}