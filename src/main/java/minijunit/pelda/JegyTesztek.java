package minijunit.pelda;

import java.util.ArrayList;
import java.util.List;
import minijunit.annotacio.Elotte;
import minijunit.annotacio.Teszt;
import minijunit.annotacio.Utana;
import minijunit.annotacio.VartKivetel;
import minijunit.assertion.Assert;

// a sajat keretrendszerrel futo peldatesztek, 2 szandekosan bukik, 1 hibas
public class JegyTesztek
{
    private List<Jegy> jegyek;

    @Elotte
    void letrehoz()
    {
        jegyek = new ArrayList<>();
        jegyek.add(new Jegy(4));
        jegyek.add(new Jegy(5));
        jegyek.add(new Jegy(10));
    }

    @Utana
    void takarit()
    {
        jegyek = null;
    }

    @Teszt
    void a1_ervenyesJegyLetrejon()
    {
        Assert.nemNull(new Jegy(7));
    }

    @Teszt
    void a2_otosAtmeno()
    {
        Assert.igaz(new Jegy(5).sikeres());
    }

    @Teszt
    void a3_negyesNemAtmeno()
    {
        Assert.hamis(new Jegy(4).sikeres());
    }

    @Teszt
    void a4_ertekMegmarad()
    {
        Assert.egyenlo(10, new Jegy(10).ertek());
    }

    @Teszt
    void a5_sikeresekSzama()
    {
        long sikeres = jegyek.stream().filter(Jegy::sikeres).count();
        Assert.egyenlo(2L, sikeres);
    }

    @Teszt
    void a6_atlag()
    {
        double atlag = jegyek.stream().mapToInt(Jegy::ertek).average().orElse(0);
        Assert.egyenlo(6.33, atlag, 0.01);
    }

    @Teszt
    @VartKivetel(IllegalArgumentException.class)
    void a7_nullaNemErvenyes()
    {
        new Jegy(0);
    }

    @Teszt
    void a8_tizenegyNemErvenyes()
    {
        Assert.dobja(IllegalArgumentException.class, () -> new Jegy(11));
    }

    // szandekosan bukik: rossz vart ertek
    @Teszt
    void b1_bukoEgyenlo()
    {
        Assert.egyenlo(6, new Jegy(5).ertek());
    }

    // szandekosan bukik: a harmas ervenyes, nem dob kivetelt
    @Teszt
    @VartKivetel(IllegalArgumentException.class)
    void b2_bukoVartKivetel()
    {
        new Jegy(3);
    }

    // szandekosan hibas: varatlan kivetel, nem AssertHiba
    @Teszt
    void c1_hibasVaratlanKivetel()
    {
        new Jegy(Integer.parseInt("hat"));
    }
}