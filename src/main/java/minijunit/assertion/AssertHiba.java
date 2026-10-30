package minijunit.assertion;

// bukott ellenorzes, ezt kulon kell valasztani a varatlan hibaktol
public class AssertHiba extends RuntimeException
{
    public AssertHiba(String uzenet)
    {
        super(uzenet);
    }
}