package minijunit.main;

import minijunit.futtato.Osszesites;
import minijunit.futtato.TesztFuttato;
import minijunit.jelentes.Jelentes;
import minijunit.jelentes.KonzolosJelentes;
import minijunit.pelda.JegyTesztek;

public class Main
{
    public static void main(String[] args)
    {
        TesztFuttato futtato = new TesztFuttato();
        Jelentes jelentes = new KonzolosJelentes(System.out);

        Osszesites osszesites = futtato.futtat(JegyTesztek.class);
        jelentes.kiir(osszesites);
    }
}