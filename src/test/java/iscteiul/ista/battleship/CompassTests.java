package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Compass enum tests")
class CompassTests {

    @Test
    void getDirection_returnsCorrectChar() {
        assertEquals('n', Compass.NORTH.getDirection());
        assertEquals('s', Compass.SOUTH.getDirection());
        assertEquals('e', Compass.EAST.getDirection());
        assertEquals('o', Compass.WEST.getDirection());
        assertEquals('u', Compass.UNKNOWN.getDirection());
    }

    @Test
    void toString_returnsCorrectString() {
        assertEquals("n", Compass.NORTH.toString());
        assertEquals("s", Compass.SOUTH.toString());
        assertEquals("e", Compass.EAST.toString());
        assertEquals("o", Compass.WEST.toString());
        assertEquals("u", Compass.UNKNOWN.toString());
    }

    @Test
    void charToCompass_mapsCharsCorrectly() throws Exception {
        var method = Compass.class.getDeclaredMethod("charToCompass", char.class);
        method.setAccessible(true);
        assertEquals(Compass.NORTH, method.invoke(null, 'n'));
        assertEquals(Compass.SOUTH, method.invoke(null, 's'));
        assertEquals(Compass.EAST, method.invoke(null, 'e'));
        assertEquals(Compass.WEST, method.invoke(null, 'o'));
        assertEquals(Compass.UNKNOWN, method.invoke(null, 'x'));
    }

    @Test
    void enumConstants_existAndConsistent() {
        Compass[] vals = Compass.values();
        assertEquals(5, vals.length);
        assertEquals("NORTH", vals[0].name());
        assertEquals(0, vals[0].ordinal());
    }
}
