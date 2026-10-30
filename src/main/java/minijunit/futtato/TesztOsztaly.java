package minijunit.futtato;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import minijunit.annotacio.Elotte;
import minijunit.annotacio.Teszt;
import minijunit.annotacio.Utana;

// csak a felderites: melyik metodus mire valo
public class TesztOsztaly
{
    private final Class<?> osztaly;
    private final List<Method> tesztek;
    private final List<Method> elotte;
    private final List<Method> utana;

    public TesztOsztaly(Class<?> osztaly)
    {
        if(osztaly == null)
        {
            throw new IllegalArgumentException("az osztaly nem lehet null");
        }
        this.osztaly = osztaly;
        this.tesztek = keres(Teszt.class);
        this.elotte = keres(Elotte.class);
        this.utana = keres(Utana.class);
    }

    public String getNev()
    {
        return osztaly.getSimpleName();
    }

    public List<Method> getTesztek()
    {
        return tesztek;
    }

    public List<Method> getElotte()
    {
        return elotte;
    }

    public List<Method> getUtana()
    {
        return utana;
    }

    // minden teszthez uj peldany, igy nem hatnak egymasra
    public Object ujPeldany() throws ReflectiveOperationException
    {
        Constructor<?> konstruktor = osztaly.getDeclaredConstructor();
        konstruktor.setAccessible(true);
        return konstruktor.newInstance();
    }

    // a getDeclaredMethods sorrendje nem garantalt, ezert nev szerint rendezunk
    private List<Method> keres(Class<? extends Annotation> annotacio)
    {
        List<Method> talalat = Arrays.stream(osztaly.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotacio))
                .sorted(Comparator.comparing(Method::getName))
                .toList();
        // a privat es package-private metodusok is hivhatok legyenek
        for(Method m : talalat)
        {
            m.setAccessible(true);
        }
        return talalat;
    }
}