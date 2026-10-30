package minijunit.assertion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AssertTest
{
    @Test
    void egyenloNemDobHaEgyezik()
    {
        assertDoesNotThrow(() -> Assert.egyenlo("alma", "alma"));
        assertDoesNotThrow(() -> Assert.egyenlo(null, null));
    }

    @Test
    void egyenloUzenetebenVartEsKapott()
    {
        AssertHiba hiba = assertThrows(AssertHiba.class, () -> Assert.egyenlo(3, 4));
        assertTrue(hiba.getMessage().contains("vart: <3>"));
        assertTrue(hiba.getMessage().contains("kapott: <4>"));
    }

    @Test
    void egyenloTuressel()
    {
        assertDoesNotThrow(() -> Assert.egyenlo(0.3, 0.1 + 0.2, 0.0001));
        assertThrows(AssertHiba.class, () -> Assert.egyenlo(1.0, 1.1, 0.01));
    }

    @Test
    void igazHamisNemNull()
    {
        assertThrows(AssertHiba.class, () -> Assert.igaz(false));
        assertThrows(AssertHiba.class, () -> Assert.hamis(true));
        assertThrows(AssertHiba.class, () -> Assert.nemNull(null));
        assertDoesNotThrow(() -> Assert.nemNull(""));
    }

    @Test
    void dobjaElfogadjaAlosztalyt()
    {
        assertDoesNotThrow(() -> Assert.dobja(RuntimeException.class,
                () -> Integer.parseInt("x")));
    }

    @Test
    void dobjaBukikHaNemDob()
    {
        AssertHiba hiba = assertThrows(AssertHiba.class,
                () -> Assert.dobja(IllegalStateException.class, () -> { }));
        assertTrue(hiba.getMessage().contains("nem dobott"));
    }

    @Test
    void dobjaBukikRosszTipusnal()
    {
        AssertHiba hiba = assertThrows(AssertHiba.class,
                () -> Assert.dobja(IllegalStateException.class, () -> Integer.parseInt("x")));
        assertTrue(hiba.getMessage().contains("NumberFormatException"));
    }
}