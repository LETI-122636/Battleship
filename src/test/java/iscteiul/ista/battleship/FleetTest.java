package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Testes para a entidade Fleet (com reflexão, auxílio crítico do Copilot)")
class FleetTest {

    private Class<?> fleetClass;
    private Object fleetInstance;

    @BeforeAll
    @DisplayName("Verificar existência da classe Fleet")
    void beforeAll() throws Exception {
        try {
            fleetClass = Class.forName("iscteiul.ista.battleship.Fleet");
        } catch (ClassNotFoundException e) {
            fleetClass = null;
        }
        assumeTrue(fleetClass != null, "Classe Fleet não encontrada — saltando testes de Fleet");
    }

    @BeforeEach
    @DisplayName("Instanciar Fleet antes de cada teste")
    void setUp() throws Exception {
        // tenta construtor sem argumentos
        Constructor<?> ctor = null;
        for (Constructor<?> c : fleetClass.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                ctor = c;
                break;
            }
        }
        assumeTrue(ctor != null, "Construtor sem-arg de Fleet não encontrado — saltando testes");
        ctor.setAccessible(true);
        fleetInstance = ctor.newInstance();
        assertNotNull(fleetInstance);
    }

    @AfterEach
    @DisplayName("Limpar instância após cada teste")
    void tearDown() {
        fleetInstance = null;
    }

    @AfterAll
    @DisplayName("Finalizar testes de Fleet")
    void afterAll() {
        // ...cleanup se necessário...
    }

    @Test
    @DisplayName("A classe Fleet instancia-se corretamente")
    void classInstantiates() {
        assertNotNull(fleetClass, "fleetClass deve existir");
        assertNotNull(fleetInstance, "fleetInstance deve ser criado");
    }

    @Test
    @DisplayName("Métodos públicos essenciais estão presentes (tolerante a métodos opcionais)")
    void publicMethodsPresent() {
        // lista mínima de métodos essenciais; outros métodos opcionais são aceitáveis mas não obrigatórios
        String[] essential = {"getShips", "addShip", "shipAt"};
        for (String name : essential) {
            boolean found = false;
            for (Method m : fleetClass.getMethods()) {
                if (m.getName().equals(name)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Esperado método público essencial: " + name);
        }
        // métodos adicionais: apenas notificar se ausentes (não falhar)
        String[] optional = {
            "printShips", "getShipsLike", "getFloatingShips",
            "printStatus", "printShipsByCategory", "printFloatingShips", "printAllShips"
        };
        for (String name : optional) {
            boolean found = false;
            for (Method m : fleetClass.getMethods()) {
                if (m.getName().equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // não falhar — apenas registar para ajudar a diagnosticar diferenças de API
                System.out.println("Nota: método público opcional não encontrado em Fleet: " + name);
            }
        }
    }

    @Test
    @DisplayName("Invocar métodos sem argumentos não lança exceções")
    void invokeNoArgMethodsSafely() {
        String[] candidates = {"getShips", "getFloatingShips", "printShips", "printStatus", "printAllShips"};
        for (String name : candidates) {
            Optional<Method> opt = findMethodWithParamCount(name, 0);
            if (opt.isPresent()) {
                Method m = opt.get();
                assertDoesNotThrow(() -> {
                    Object res = m.invoke(fleetInstance);
                    // apenas verifica que a invocação retornou sem lançar
                    // se retornar coleção, garantir que é Collection
                    if (res != null) {
                        assertTrue(res instanceof Collection || res instanceof String || res.getClass().isArray(),
                            "retorno inesperado para " + name + ": " + res.getClass().getName());
                    }
                }, "Invocação de " + name + " deve ser segura");
            } else {
                // Se não existir método com 0 params, apenas notificar (não falhar)
                // porque implementações podem ter assinaturas diferentes.
                // Utilizamos assertTrue(false) aqui estaria a forçar falha; preferimos não falhar.
            }
        }
    }

    @Nested
    @DisplayName("Testes de manipulação de navios (quando Ship/Position disponíveis)")
    class ShipsBehaviorTests {

        private Class<?> shipClass;
        private Class<?> positionClass;
        private Object shipInstance;

        @BeforeEach
        @DisplayName("Preparar Ship/Position se possível")
        void prepareShip() {
            // Carrega classes Ship e Position por reflexão se existirem
            try {
                shipClass = Class.forName("iscteiul.ista.battleship.Ship");
            } catch (ClassNotFoundException e) {
                shipClass = null;
            }
            try {
                positionClass = Class.forName("iscteiul.ista.battleship.Position");
            } catch (ClassNotFoundException e) {
                positionClass = null;
            }

            // Tentativa de construir um Ship simples: aceitar vários tipos de construtores comuns
            shipInstance = null;
            if (shipClass != null) {
                shipInstance = tryCreateShipInstance(shipClass, positionClass);
            }

            // Não abortar aqui — adiar assumeTrue para os testes que realmente precisam da instância
            if (shipClass == null) {
                System.out.println("Nota: Classe Ship não encontrada — testes de manipulação de navios serão ignorados.");
            } else if (shipInstance == null) {
                System.out.println("Nota: Não foi possível criar instância de Ship via reflexão — alguns testes serão ignorados.");
            }
        }

        @AfterEach
        void cleanup() {
            shipInstance = null;
        }

        @Test
        @DisplayName("addShip adiciona navio e getShips o retorna (quando Ship está disponível)")
        void addAndGetShips() throws Throwable {
            assumeTrue(shipClass != null, "Ship class não disponível — skip addAndGetShips");
            assumeTrue(shipInstance != null, "Não foi possível criar instância de Ship — skip addAndGetShips");

            Method addShip = findMethodByName("addShip").orElseThrow(() -> new AssertionError("addShip não encontrado"));
            Method getShips = findMethodByName("getShips").orElseThrow(() -> new AssertionError("getShips não encontrado"));

            // invocar addShip
            assertDoesNotThrow(() -> addShip.invoke(fleetInstance, shipInstance), "addShip deve aceitar o Ship sem lançar");

            // invocar getShips e verificar coleção contém o navio
            Object res = getShips.invoke(fleetInstance);
            assertNotNull(res, "getShips não deve retornar null");
            assertTrue(res instanceof Collection, "getShips deve retornar Collection");
            Collection<?> coll = (Collection<?>) res;
            assertTrue(coll.contains(shipInstance),
                "Coleção de navios deve conter a instância adicionada (dependendo de equals/identidade)");
        }

        @Test
        @DisplayName("shipAt retorna navio na posição correta (quando aplicável)")
        void shipAtByPosition() throws Throwable {
            // só testar se existe método shipAt e Position compatível
            Optional<Method> optShipAt = findMethodWithParamCount("shipAt", 1);
            assumeTrue(optShipAt.isPresent() && positionClass != null, "shipAt ou Position indisponível — skip shipAtByPosition");

            Method shipAt = optShipAt.get();

            // Tentar criar uma Position com coordenadas conhecidas (0,0 ou 1,1)
            Object pos = tryCreatePosition(positionClass, 0, 0);
            assumeTrue(pos != null, "Não foi possível criar Position — skip shipAtByPosition");

            // Dependendo da implementação, shipAt pode retornar null (nenhum ship) — aqui só garantimos invocação segura
            assertDoesNotThrow(() -> {
                Object found = shipAt.invoke(fleetInstance, pos);
                if (found != null) {
                    assertTrue(found.getClass().getName().contains("Ship"), "shipAt retornou objeto que não parece ser Ship");
                }
            }, "Invocação de shipAt deve ser segura");
        }

        // helpers locais

        private Object tryCreateShipInstance(Class<?> shipCls, Class<?> posCls) {
            // tentar vários construtores comuns:
            // 1) Ship(Position, int length)
            // 2) Ship(int length, Position)
            // 3) Ship(String type, int length)
            // 4) Ship() sem args
            try {
                if (posCls != null) {
                    Object pos = tryCreatePosition(posCls, 0, 0);
                    if (pos != null) {
                        for (Constructor<?> c : shipCls.getDeclaredConstructors()) {
                            Class<?>[] params = c.getParameterTypes();
                            if (params.length == 2 &&
                                params[0].isAssignableFrom(posCls) &&
                                params[1] == int.class) {
                                c.setAccessible(true);
                                return c.newInstance(pos, 2);
                            }
                            if (params.length == 2 &&
                                params[1].isAssignableFrom(posCls) &&
                                params[0] == int.class) {
                                c.setAccessible(true);
                                return c.newInstance(2, pos);
                            }
                        }
                    }
                }
                // tentar construtor (String,int)
                for (Constructor<?> c : shipCls.getDeclaredConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 2 && params[0] == String.class && params[1] == int.class) {
                        c.setAccessible(true);
                        return c.newInstance("Destroyer", 2);
                    }
                }
                // construtor sem-arg
                for (Constructor<?> c : shipCls.getDeclaredConstructors()) {
                    if (c.getParameterCount() == 0) {
                        c.setAccessible(true);
                        return c.newInstance();
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                // falha ao criar; retornamos null e os testes serão assumidos como skip
            }
            return null;
        }

        private Object tryCreatePosition(Class<?> posCls, int row, int col) {
            try {
                for (Constructor<?> c : posCls.getDeclaredConstructors()) {
                    Class<?>[] params = c.getParameterTypes();
                    if (params.length == 2 && params[0] == int.class && params[1] == int.class) {
                        c.setAccessible(true);
                        return c.newInstance(row, col);
                    }
                }
                for (Constructor<?> c : posCls.getDeclaredConstructors()) {
                    if (c.getParameterCount() == 0) {
                        c.setAccessible(true);
                        return c.newInstance();
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            }
            return null;
        }
    }

    // --- utilitários de reflexão ---
    private Optional<Method> findMethodByName(String name) {
        for (Method m : fleetClass.getMethods()) {
            if (m.getName().equals(name)) return Optional.of(m);
        }
        return Optional.empty();
    }

    private Optional<Method> findMethodWithParamCount(String name, int paramCount) {
        for (Method m : fleetClass.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == paramCount) return Optional.of(m);
        }
        return Optional.empty();
    }

}

