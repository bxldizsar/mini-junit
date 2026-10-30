package minijunit.futtato;

import java.time.Duration;

public record TesztEredmeny(String nev, Allapot allapot, String uzenet, Duration ido)
{
    public TesztEredmeny
    {
        if(nev == null || allapot == null || ido == null)
        {
            throw new IllegalArgumentException("a nev, az allapot es az ido nem lehet null");
        }
    }

    public boolean sikeres()
    {
        return allapot == Allapot.SIKERES;
    }
}