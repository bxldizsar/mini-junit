package minijunit.futtato;

import java.time.Duration;
import java.util.List;

public record Osszesites(String osztalyNev, List<TesztEredmeny> eredmenyek)
{
    public Osszesites
    {
        eredmenyek = List.copyOf(eredmenyek);
    }

    public long darab(Allapot allapot)
    {
        return eredmenyek.stream()
                .filter(e -> e.allapot() == allapot)
                .count();
    }

    public Duration osszIdo()
    {
        return eredmenyek.stream()
                .map(TesztEredmeny::ido)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public boolean mindSikeres()
    {
        return eredmenyek.stream().allMatch(TesztEredmeny::sikeres);
    }
}