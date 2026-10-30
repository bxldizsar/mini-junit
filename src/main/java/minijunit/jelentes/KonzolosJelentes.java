package minijunit.jelentes;

import java.io.PrintStream;
import minijunit.futtato.Allapot;
import minijunit.futtato.Osszesites;
import minijunit.futtato.TesztEredmeny;

public class KonzolosJelentes implements Jelentes
{
    private final PrintStream ki;

    public KonzolosJelentes(PrintStream ki)
    {
        this.ki = ki;
    }

    @Override
    public void kiir(Osszesites osszesites)
    {
        ki.println("== " + osszesites.osztalyNev() + " ==");
        for(TesztEredmeny e : osszesites.eredmenyek())
        {
            ki.println(sor(e));
        }
        ki.println("-----");
        ki.printf("sikeres: %d, bukott: %d, hibas: %d, osszesen: %d (%d ms)%n",
                osszesites.darab(Allapot.SIKERES),
                osszesites.darab(Allapot.BUKOTT),
                osszesites.darab(Allapot.HIBAS),
                osszesites.eredmenyek().size(),
                osszesites.osszIdo().toMillis());
    }

    private String sor(TesztEredmeny e)
    {
        // ascii jelek, mert a windowsos konzol nem mindig utf-8
        return switch(e.allapot())
        {
            case SIKERES -> "[ OK ] " + e.nev();
            case BUKOTT -> "[BUKO] " + e.nev() + ": " + e.uzenet();
            case HIBAS -> "[HIBA] " + e.nev() + ": " + e.uzenet();
        };
    }
}