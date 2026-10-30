package minijunit.futtato;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import minijunit.annotacio.VartKivetel;
import minijunit.assertion.AssertHiba;

// futtat es eredmenyt ad, kiirni nem a dolga
public class TesztFuttato
{
    private record Kimenet(Allapot allapot, String uzenet)
    {
        static final Kimenet SIKER = new Kimenet(Allapot.SIKERES, null);

        boolean sikeres()
        {
            return allapot == Allapot.SIKERES;
        }
    }

    public Osszesites futtat(Class<?> tesztOsztaly)
    {
        TesztOsztaly osztaly = new TesztOsztaly(tesztOsztaly);
        List<TesztEredmeny> eredmenyek = new ArrayList<>();
        for(Method teszt : osztaly.getTesztek())
        {
            long kezdet = System.nanoTime();
            Kimenet kimenet = egyetFuttat(osztaly, teszt);
            Duration ido = Duration.ofNanos(System.nanoTime() - kezdet);
            eredmenyek.add(new TesztEredmeny(teszt.getName(), kimenet.allapot(), kimenet.uzenet(), ido));
        }
        return new Osszesites(osztaly.getNev(), eredmenyek);
    }

    // elotte -> teszt -> utana, az utana akkor is lefut, ha a teszt elbukott
    private Kimenet egyetFuttat(TesztOsztaly osztaly, Method teszt)
    {
        if(teszt.getParameterCount() > 0)
        {
            return new Kimenet(Allapot.HIBAS, "a tesztmetodusnak nem lehet parametere");
        }

        Object peldany;
        try
        {
            peldany = osztaly.ujPeldany();
        }
        catch(InvocationTargetException e)
        {
            return new Kimenet(Allapot.HIBAS, "nem lehet peldanyositani: " + e.getCause());
        }
        catch(ReflectiveOperationException e)
        {
            return new Kimenet(Allapot.HIBAS, "nem lehet peldanyositani: " + e);
        }

        Kimenet kimenet = elotteEsTeszt(osztaly, teszt, peldany);
        Kimenet utanaHiba = utana(osztaly, peldany);
        if(kimenet.sikeres() && utanaHiba != null)
        {
            return utanaHiba;
        }
        return kimenet;
    }

    private Kimenet elotteEsTeszt(TesztOsztaly osztaly, Method teszt, Object peldany)
    {
        for(Method m : osztaly.getElotte())
        {
            Throwable hiba = hiv(m, peldany);
            if(hiba != null)
            {
                return osztalyoz(hiba, "elotte: ");
            }
        }

        VartKivetel vart = teszt.getAnnotation(VartKivetel.class);
        Throwable dobott = hiv(teszt, peldany);
        if(vart == null)
        {
            return dobott == null ? Kimenet.SIKER : osztalyoz(dobott, "");
        }
        if(dobott == null)
        {
            return new Kimenet(Allapot.BUKOTT, "vart kivetel: <" + vart.value().getSimpleName()
                    + ">, de nem dobott semmit");
        }
        if(vart.value().isInstance(dobott))
        {
            return Kimenet.SIKER;
        }
        if(dobott instanceof AssertHiba)
        {
            return osztalyoz(dobott, "");
        }
        return new Kimenet(Allapot.BUKOTT, "vart kivetel: <" + vart.value().getSimpleName()
                + ">, kapott: <" + dobott.getClass().getSimpleName() + ">");
    }

    // az elso utana hibat adja vissza, de mindegyik lefut
    private Kimenet utana(TesztOsztaly osztaly, Object peldany)
    {
        Kimenet elsoHiba = null;
        for(Method m : osztaly.getUtana())
        {
            Throwable hiba = hiv(m, peldany);
            if(hiba != null && elsoHiba == null)
            {
                elsoHiba = osztalyoz(hiba, "utana: ");
            }
        }
        return elsoHiba;
    }

    // null, ha nem dobott semmit
    private Throwable hiv(Method metodus, Object peldany)
    {
        try
        {
            metodus.invoke(peldany);
            return null;
        }
        catch(InvocationTargetException e)
        {
            // az invoke becsomagolja a teszt sajat kivetelet
            return e.getCause();
        }
        catch(IllegalAccessException e)
        {
            return e;
        }
    }

    // bukas: AssertHiba, minden mas hiba
    private Kimenet osztalyoz(Throwable t, String elotag)
    {
        if(t instanceof AssertHiba)
        {
            return new Kimenet(Allapot.BUKOTT, elotag + t.getMessage());
        }
        return new Kimenet(Allapot.HIBAS, elotag + t);
    }
}