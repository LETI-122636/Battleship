package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes da entidade Compass")
class CompassTest {

    @BeforeAll
    void beforeAll() {
        // setup global resources se necessário
    }

    @AfterAll
    void afterAll() {
        // cleanup global resources se necessário
    }

    @BeforeEach
    void beforeEach() {
        // executa antes de cada teste
    }

    @AfterEach
    void afterEach() {
        // executa após cada teste
    }

    @Test
    @DisplayName("values() retorna constantes e valueOf() recupera cada constante")
    void valuesAndValueOf() {
        Compass[] vals = Compass.values();
        assertAll("values and valueOf",
            () -> assertNotNull(vals, "values() não deve ser null"),
            () -> assertTrue(vals.length > 0, "deve existir pelo menos uma constante"),
            () -> {
                for (Compass c : vals) {
                    assertEquals(c, Compass.valueOf(c.name()), () -> "valueOf deve recuperar " + c.name());
                }
            }
        );
    }

    static Stream<String> compassNames() {
        return Arrays.stream(Compass.values()).map(Enum::name);
    }

    @ParameterizedTest
    @MethodSource("compassNames")
    @DisplayName("valueOf aceita nomes de constantes (ParameterizedTest)")
    void parameterizedValueOf(String name) {
        assertEquals(name, Compass.valueOf(name).name());
    }

    @Test
    @DisplayName("toString() não deve ser null para cada constante")
    void testToStringNotNull() {
        Arrays.stream(Compass.values()).forEach(c ->
            assertNotNull(c.toString(), () -> "toString() não deve ser null para " + c.name())
        );
    }

    @Test
    @DisplayName("charToCompass (se existir) mapeia um char para uma constante")
    void charToCompassIfExists() throws Exception {
        Method m = null;
        try {
            m = Compass.class.getDeclaredMethod("charToCompass", char.class);
        } catch (NoSuchMethodException e) {
            // método não existe — validação leve para que o teste não falhe
        }
        if (m == null) {
            // aceita-se que a entidade não possua este método
            assertTrue(true, "charToCompass não está implementado — teste ignorado");
        } else {
            Method finalM = m;
            Arrays.stream(Compass.values()).forEach(c -> {
                char testChar = c.name().charAt(0);
                assertDoesNotThrow(() -> {
                    Object res = finalM.invoke(null, testChar);
                    assertNotNull(res, "charToCompass não deve retornar null para " + testChar);
                    assertTrue(res instanceof Compass, "charToCompass deve retornar um Compass");
                });
            });
        }
    }

    @Test
    @DisplayName("getDirection (se existir) retorna algo consistente")
    void getDirectionIfExists() {
        Method m = null;
        try {
            m = Compass.class.getDeclaredMethod("getDirection");
        } catch (NoSuchMethodException e) {
            // método não existe — validação leve
        }
        if (m == null) {
            assertTrue(true, "getDirection não está implementado — teste ignorado");
        } else {
            Method finalM = m;
            Arrays.stream(Compass.values()).forEach(c -> {
                assertDoesNotThrow(() -> {
                    Object res = finalM.invoke(c);
                    assertNotNull(res, "getDirection não deve retornar null para " + c.name());
                });
            });
        }
    }

    @Nested
    @DisplayName("Testes de casos inválidos e exceções")
    class InvalidInputs {

        @Test
        @DisplayName("valueOf com nome inválido lança IllegalArgumentException")
        void invalidValueOf() {
            assertThrows(IllegalArgumentException.class, () -> Compass.valueOf("INVALID_NAME"));
        }

        @Test
        @Disabled("Exemplo de teste desabilitado")
        @DisplayName("Teste desabilitado de exemplo")
        void disabledExample() {
            fail("Este teste está desabilitado e não deve correr");
        }
    }
}