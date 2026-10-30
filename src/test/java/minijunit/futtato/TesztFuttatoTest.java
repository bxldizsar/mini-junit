package minijunit.futtato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import minijunit.annotacio.Elotte;
import minijunit.annotacio.Teszt;
import minijunit.annotacio.Utana;
import minijunit.annotacio.VartKivetel;
import minijunit.assertion.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TesztFuttatoTest
{
    // a mintaosztalyok ide naplozzak, mi futott le
    static final List<String> NAPLO = new ArrayList<>();

    private TesztFuttato futtato;

    @BeforeEach
    void setUp()
    {
        NAPLO.clear();
        futtato = new TesztFuttato();
    }

    public static class Vegyes
    {
        @Teszt
        void sikeres()
        {
            Assert.igaz(true);
        }

        @Teszt
        void bukik()
        {
            Assert.egyenlo(1, 2);
        }

        @Teszt
        void hibas()
        {
            throw new IllegalStateException("bumm");
        }

        // nincs annotacio, nem futhat le
        void nemTeszt()
        {
            NAPLO.add("nemTeszt");
        }
    }

    public static class Eletciklus
    {
        private int szamlalo;

        @Elotte
        void elotte()
        {
            NAPLO.add("elotte");
        }

        @Utana
        void utana()
        {
            NAPLO.add("utana");
        }

        @Teszt
        void elso()
        {
            szamlalo++;
            NAPLO.add("elso:" + szamlalo);
        }

        @Teszt
        void masodik()
        {
            szamlalo++;
            NAPLO.add("masodik:" + szamlalo);
            Assert.igaz(false);
        }
    }

    public static class Kivetelek
    {
        @Teszt
        @VartKivetel(IllegalArgumentException.class)
        void jo()
        {
            throw new IllegalArgumentException();
        }

        @Teszt
        @VartKivetel(IllegalArgumentException.class)
        void nemDob()
        {
        }

        @Teszt
        @VartKivetel(IllegalArgumentException.class)
        void masikat()
        {
            throw new IllegalStateException();
        }
    }

    public static class HibasUtana
    {
        @Utana
        void utana()
        {
            throw new IllegalStateException("takaritas hiba");
        }

        @Teszt
        void teszt()
        {
        }
    }

    public static class Parameteres
    {
        @Teszt
        void parameter(int x)
        {
        }
    }

    private TesztEredmeny keres(Osszesites o, String nev)
    {
        return o.eredmenyek().stream()
                .filter(e -> e.nev().equals(nev))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void bukasEsHibaKulonvalik()
    {
        Osszesites o = futtato.futtat(Vegyes.class);

        assertEquals(3, o.eredmenyek().size());
        assertEquals(Allapot.SIKERES, keres(o, "sikeres").allapot());
        assertEquals(Allapot.BUKOTT, keres(o, "bukik").allapot());
        assertEquals(Allapot.HIBAS, keres(o, "hibas").allapot());
        assertTrue(keres(o, "hibas").uzenet().contains("bumm"));
        assertTrue(NAPLO.isEmpty());
    }

    @Test
    void osszesitesSzamol()
    {
        Osszesites o = futtato.futtat(Vegyes.class);

        assertEquals(1, o.darab(Allapot.SIKERES));
        assertEquals(1, o.darab(Allapot.BUKOTT));
        assertEquals(1, o.darab(Allapot.HIBAS));
        assertEquals("Vegyes", o.osztalyNev());
    }

    @Test
    void mindenTesztUjPeldanytKapEsElotteUtanaFut()
    {
        futtato.futtat(Eletciklus.class);

        // a szamlalo mindket tesztben 1, tehat uj peldany volt
        assertEquals(List.of("elotte", "elso:1", "utana", "elotte", "masodik:1", "utana"), NAPLO);
    }

    @Test
    void vartKivetelKezelese()
    {
        Osszesites o = futtato.futtat(Kivetelek.class);

        assertEquals(Allapot.SIKERES, keres(o, "jo").allapot());
        assertEquals(Allapot.BUKOTT, keres(o, "nemDob").allapot());
        assertEquals(Allapot.BUKOTT, keres(o, "masikat").allapot());
        assertTrue(keres(o, "masikat").uzenet().contains("IllegalStateException"));
    }

    @Test
    void hibasUtanaTonkretesziASikerest()
    {
        Osszesites o = futtato.futtat(HibasUtana.class);

        assertEquals(Allapot.HIBAS, keres(o, "teszt").allapot());
        assertTrue(keres(o, "teszt").uzenet().startsWith("utana:"));
    }

    @Test
    void parameteresTesztHibas()
    {
        Osszesites o = futtato.futtat(Parameteres.class);

        assertEquals(Allapot.HIBAS, keres(o, "parameter").allapot());
    }
}