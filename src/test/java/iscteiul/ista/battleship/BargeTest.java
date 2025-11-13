package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes unitários para a entidade Barge")
class BargeTest {

    private Barge barge;
    private Position origin;

    @BeforeAll
    void beforeAll() {
        // Executa uma vez antes de todos os testes (exemplo de uso)
    }

    @AfterAll
    void afterAll() {
        // Executa uma vez depois de todos os testes (exemplo de uso)
    }

    @BeforeEach
    void setUp() {
        origin = new Position(2, 3);
        barge = new Barge(Compass.NORTH, origin);
    }

    @AfterEach
    void tearDown() {
        barge = null;
        origin = null;
    }

    @Test
    @DisplayName("getSize deve retornar 1 para uma Barge")
    void getSize() {
        assertEquals(1, barge.getSize(), "Barge deve ter tamanho 1");
    }

    @Test
    @DisplayName("Propriedades iniciais: categoria, posição e orientação")
    void initialProperties() {
        assertAll("propriedades iniciais",
                () -> assertEquals("Barca", barge.getCategory(), "Categoria incorreta"),
                () -> assertEquals(origin.getRow(), barge.getPosition().getRow(), "Linha da posição incorreta"),
                () -> assertEquals(origin.getColumn(), barge.getPosition().getColumn(), "Coluna da posição incorreta"),
                () -> assertEquals(Compass.NORTH, barge.getBearing(), "Orientação incorreta")
        );
    }

    @Test
    @DisplayName("occupies reconhece quando a posição é ocupada e quando não é")
    void occupies() {
        Position same = new Position(2, 3);
        Position other = new Position(5, 5);

        assertAll("occupies",
                () -> assertTrue(barge.occupies(same), "Deve ocupar a posição igual"),
                () -> assertFalse(barge.occupies(other), "Não deve ocupar posição diferente")
        );
    }

    @Test
    @DisplayName("stillFloating muda para false após ser atingida na sua única posição")
    void shooting() {
        Position target = new Position(2, 3);
        assertTrue(barge.stillFloating(), "Inicialmente a barca deve estar flutuando");

        barge.shoot(target);

        assertAll("pós tiro",
                () -> assertTrue(barge.getPositions().get(0).isHit(), "A posição deve ficar marcada como atingida"),
                () -> assertFalse(barge.stillFloating(), "Após ser atingida a barca não deve continuar flutuando")
        );
    }

    @ParameterizedTest
    @EnumSource(value = Compass.class)
    @DisplayName("Construtor aceita diferentes rumos (bearings)")
    void bearingParameterized(Compass compass) {
        Barge b = new Barge(compass, new Position(0, 0));
        assertEquals(compass, b.getBearing(), "Bearing deve ser o mesmo passado no construtor");
    }

    @Nested
    @DisplayName("Comportamento relativo à proximidade")
    class ProximityTests {

        @Test
        @DisplayName("tooCloseTo com posição adjacente devolve true")
        void adjacentPosition() {
            Position adj = new Position(1, 2); // diagonal-adjacente a (2,3)
            assertTrue(barge.tooCloseTo(adj), "Posição adjacente deve ser considerada muito próxima");
        }

        @Test
        @DisplayName("tooCloseTo com outro navio adjacente devolve true")
        void adjacentShip() {
            Barge other = new Barge(Compass.NORTH, new Position(1, 2));
            assertTrue(barge.tooCloseTo(other), "Navio adjacente deve ser considerado muito próximo");
        }
    }

    @Disabled("Exemplo: teste desativado - não utilizado no pipeline")
    @Test
    @DisplayName("Teste desativado de exemplo")
    void disabledTest() {
        // exemplo de teste que permanece desativado
    }

}