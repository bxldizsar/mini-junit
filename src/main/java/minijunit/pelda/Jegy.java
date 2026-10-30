package minijunit.pelda;

// a b-03 tanulmanyi rendszer Jegy osztalya, ezt teszteljuk
public record Jegy(int ertek)
{
    public static final int MIN = 1;
    public static final int MAX = 10;
    public static final int ATMENO = 5;

    public Jegy
    {
        if(ertek < MIN || ertek > MAX)
        {
            throw new IllegalArgumentException("a jegy " + MIN + " es " + MAX + " kozott lehet: " + ertek);
        }
    }

    public boolean sikeres()
    {
        return ertek >= ATMENO;
    }
}