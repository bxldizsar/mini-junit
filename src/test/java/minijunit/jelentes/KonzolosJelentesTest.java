package minijunit.jelentes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import minijunit.futtato.Allapot;
import minijunit.futtato.Osszesites;
import minijunit.futtato.TesztEredmeny;
import org.junit.jupiter.api.Test;

class KonzolosJelentesTest
{
    @Test
    void kiirjaASorokatEsAzOsszesitest()
    {
        ByteArrayOutputStream bajtok = new ByteArrayOutputStream();
        Jelentes jelentes = new KonzolosJelentes(new PrintStream(bajtok, true, StandardCharsets.UTF_8));
        Osszesites o = new Osszesites("Minta", List.of(
                new TesztEredmeny("jo", Allapot.SIKERES, null, Duration.ofMillis(1)),
                new TesztEredmeny("rossz", Allapot.BUKOTT, "vart: <1>, kapott: <2>", Duration.ofMillis(1)),
                new TesztEredmeny("csunya", Allapot.HIBAS, "java.lang.IllegalStateException", Duration.ofMillis(1))));

        jelentes.kiir(o);

        String kimenet = bajtok.toString(StandardCharsets.UTF_8);
        assertTrue(kimenet.contains("== Minta =="));
        assertTrue(kimenet.contains("[ OK ] jo"));
        assertTrue(kimenet.contains("[BUKO] rossz: vart: <1>, kapott: <2>"));
        assertTrue(kimenet.contains("[HIBA] csunya"));
        assertTrue(kimenet.contains("sikeres: 1, bukott: 1, hibas: 1, osszesen: 3"));
    }
}