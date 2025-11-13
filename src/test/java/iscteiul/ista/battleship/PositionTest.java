package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes para a entidade Position")
class PositionTest {

    private Position p;

    @BeforeAll
    @DisplayName("Inicializar recursos do teste (BeforeAll)")
    void beforeAll() {
        // pode inicializar recursos partilhados aqui
    }

    @AfterAll
    @DisplayName("Limpar recursos do teste (AfterAll)")
    void afterAll() {
        // limpeza global se necessário
    }

    @BeforeEach
    @DisplayName("Criar posição de base antes de cada teste")
    void setUp() {
        p = new Position(2, 3);
    }

    @AfterEach
    @DisplayName("Limpar após cada teste")
    void tearDown() {
        p = null;
    }

    @Test
    @DisplayName("getRow e getColumn retornam valores corretos")
    void getRowAndColumn() {
        assertAll("coords",
            () -> assertEquals(2, p.getRow(), "row deve ser 2"),
            () -> assertEquals(3, p.getColumn(), "column deve ser 3")
        );
    }

    @Test
    @DisplayName("hashCode e equals funcionam para posições iguais e diferentes")
    void testHashCodeAndEquals() {
        Position same = new Position(2, 3);
        Position diff = new Position(4, 5);

        assertAll("equals/hashCode",
            () -> assertEquals(same, p, "objetos com mesmas coordenadas devem ser iguais"),
            () -> assertEquals(p.hashCode(), same.hashCode(), "hashCode deve ser igual para objetos iguais"),
            () -> assertNotEquals(diff, p, "objetos com coordenadas diferentes não devem ser iguais")
        );
    }

    static Stream<Arguments> adjacentProvider() {
        return Stream.of(
            Arguments.of(new Position(2, 4), true), // direita
            Arguments.of(new Position(2, 2), true), // esquerda
            Arguments.of(new Position(3, 3), true), // abaixo
            Arguments.of(new Position(1, 3), true), // acima
            Arguments.of(new Position(3, 4), true), // diagonal -> considerado adjacente pela implementação
            Arguments.of(new Position(5, 3), false)  // distante
        );
    }

    @ParameterizedTest(name = "isAdjacentTo: {0} -> {1}")
    @MethodSource("adjacentProvider")
    @DisplayName("isAdjacentTo (testes parametrizados)")
    void isAdjacentTo(Position other, boolean expected) {
        assertEquals(expected, p.isAdjacentTo(other),
            () -> "Adjacência entre " + p + " e " + other + " deve ser " + expected);
    }

    @Test
    @DisplayName("toString contém coordenadas")
    void testToString() {
        String s = p.toString();
        assertAll("toString",
            () -> assertNotNull(s),
            () -> assertTrue(s.contains("2") || s.contains("row") || s.contains("2, 3"),
                "toString deve mencionar as coordenadas (conteúdo: " + s + ")")
        );
    }

    @Nested
    @DisplayName("Testes relacionados com ocupação e tiro")
    class OccupiedAndShotTests {

        @BeforeEach
        @DisplayName("Ocupar posição antes dos testes deste grupo")
        void occupyPos() {
            // ocupar deve ser seguro de chamar
            assertDoesNotThrow(() -> p.occupy());
        }

        @Test
        @DisplayName("isOccupied e occupy")
        void occupyAndIsOccupied() {
            assertTrue(p.isOccupied(), "Após occupy() a posição deve estar ocupada");
        }

        @Test
        @DisplayName("shoot marca como hit quando ocupado")
        void shootWhenOccupiedMarksHit() {
            assertDoesNotThrow(() -> p.shoot());
            assertTrue(p.isHit(), "Após shoot() numa posição ocupada, isHit deve ser true");
        }

        @Test
        @Disabled("Exemplo de teste temporariamente desativado")
        @DisplayName("Exemplo de teste desativado")
        void disabledExample() {
            fail("Este teste está desativado e não deve ser executado");
        }
    }
}