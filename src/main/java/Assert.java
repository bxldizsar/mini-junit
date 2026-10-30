package minijunit.assertion;

import java.util.Objects;

public final class Assert
{
    private Assert()
    {
    }

    public static void egyenlo(Object vart, Object kapott)
    {
        if(!Objects.equals(vart, kapott))
        {
            throw new AssertHiba("vart: <" + vart + ">, kapott: <" + kapott + ">");
        }
    }

    public static void egyenlo(double vart, double kapott, double tures)
    {
        if(Math.abs(vart - kapott) > tures)
        {
            throw new AssertHiba("vart: <" + vart + ">, kapott: <" + kapott + "> (tures: " + tures + ")");
        }
    }

    public static void igaz(boolean feltetel)
    {
        if(!feltetel)
        {
            throw new AssertHiba("vart: <true>, kapott: <false>");
        }
    }

    public static void hamis(boolean feltetel)
    {
        if(feltetel)
        {
            throw new AssertHiba("vart: <false>, kapott: <true>");
        }
    }

    public static void nemNull(Object ertek)
    {
        if(ertek == null)
        {
            throw new AssertHiba("vart: nem null, kapott: <null>");
        }
    }

    public static void dobja(Class<? extends Throwable> vartTipus, Runnable kod)
    {
        try
        {
            kod.run();
        }
        catch(Throwable t)
        {
            if(vartTipus.isInstance(t))
            {
                return;
            }
            throw new AssertHiba("vart kivetel: <" + vartTipus.getSimpleName()
                    + ">, kapott: <" + t.getClass().getSimpleName() + ">");
        }
        throw new AssertHiba("vart kivetel: <" + vartTipus.getSimpleName() + ">, de nem dobott semmit");
    }
}