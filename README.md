# MiniJUnit – saját tesztkeretrendszer annotációval és reflexióval

A b-05 bónuszfeladat megoldása: egy kis, JUnit-szerű tesztkeretrendszer, amely
**annotációk** és **reflexió** segítségével megkeresi és lefuttatja a tesztmetódusokat,
külön kezeli a **bukást** és a **hibát**, és a végén összesítő jelentést ír ki.

## Futtatás

Követelmény: JDK 21 és Maven (NetBeansben elég a projekt megnyitása).

```bash
mvn compile exec:java      # a példatesztek futtatása a saját keretrendszerrel
mvn test                   # a keretrendszer saját JUnit 5 tesztjei (14 db)
```

NetBeansben: **Run Project (F6)** a demóhoz, **Test Project (Alt+F6)** a JUnit tesztekhez.
A főosztály: `minijunit.main.Main`.

## Funkciók

| Elem | Leírás |
|---|---|
| `@Teszt` | tesztmetódus jelölése |
| `@Elotte` | minden teszt előtt lefut |
| `@Utana` | minden teszt után lefut, akkor is, ha a teszt elbukott |
| `@VartKivetel(X.class)` | a teszt akkor sikeres, ha `X` (vagy alosztálya) kivételt dob |
| `Assert` | `egyenlo`, `egyenlo` tűréssel (`double`), `igaz`, `hamis`, `nemNull`, `dobja` |
| `AssertHiba` | sikertelen ellenőrzéskor dobódik, „vart: <…>, kapott: <…>” üzenettel |

Mindegyik annotáció `@Retention(RUNTIME)`, különben a reflexió futásidőben nem látná.

## Hogyan működik a futtató

1. A `Main` átadja a tesztosztályt (`JegyTesztek.class`) a `TesztFuttato.futtat()` metódusnak.
2. A `TesztOsztaly` a `getDeclaredMethods()` és az `isAnnotationPresent()` segítségével
   összegyűjti a `@Teszt`, `@Elotte` és `@Utana` metódusokat, és név szerint rendezi őket,
   mert a JVM nem garantál sorrendet.
3. A futtató **minden tesztmetódushoz új példányt** hoz létre
   (`getDeclaredConstructor().newInstance()`), így a tesztek nem hatnak egymásra.
4. Lefuttatja az összes `@Elotte` metódust, majd a tesztet a `Method.invoke()`-kal.
5. Az `invoke` a teszt saját kivételét `InvocationTargetException`-be csomagolja,
   ezért a futtató a `getCause()`-zal veszi ki az eredeti kivételt.
6. Az eredeti kivételt osztályozza: `AssertHiba` → **BUKOTT**, bármi más → **HIBAS**.
   `@VartKivetel` esetén a várt típus → **SIKERES**, ha pedig nem jön kivétel, vagy más jön, → **BUKOTT**.
7. Az `@Utana` metódusok mindig lefutnak. Ha egy sikeres teszt után az `@Utana` dob kivételt,
   a teszt **HIBAS** lesz.
8. Minden tesztről `TesztEredmeny` készül (név, állapot, üzenet, futási idő),
   ezeket az `Osszesites` fogja össze.
9. A futtató semmit nem ír ki: az `Osszesites`-t a `Jelentes` interfész egy implementációja,
   a `KonzolosJelentes` jeleníti meg.

## Példa kimenet

A `JegyTesztek` a b-03 tanulmányi rendszer `Jegy` recordját teszteli. 11 tesztből
8 sikeres, 2 szándékosan bukik, és 1 szándékosan hibás (a bukás és a hiba különbségének bemutatására).

```
== JegyTesztek ==
[ OK ] a1_ervenyesJegyLetrejon
[ OK ] a2_otosAtmeno
[ OK ] a3_negyesNemAtmeno
[ OK ] a4_ertekMegmarad
[ OK ] a5_sikeresekSzama
[ OK ] a6_atlag
[ OK ] a7_nullaNemErvenyes
[ OK ] a8_tizenegyNemErvenyes
[BUKO] b1_bukoEgyenlo: vart: <6>, kapott: <5>
[BUKO] b2_bukoVartKivetel: vart kivetel: <IllegalArgumentException>, de nem dobott semmit
[HIBA] c1_hibasVaratlanKivetel: java.lang.NumberFormatException: For input string: "hat"
-----
sikeres: 8, bukott: 2, hibas: 1, osszesen: 11 (34 ms)
```

## Felépítés

```
src/main/java/minijunit/
├── annotacio/   Teszt, Elotte, Utana, VartKivetel
├── assertion/   Assert, AssertHiba
├── futtato/     TesztOsztaly, TesztFuttato, TesztEredmeny, Osszesites, Allapot
├── jelentes/    Jelentes (interfész), KonzolosJelentes
├── pelda/       Jegy (a b-03-ból), JegyTesztek
└── main/        Main
src/test/java/minijunit/
├── assertion/   AssertTest (7)
├── futtato/     TesztFuttatoTest (6)
└── jelentes/    KonzolosJelentesTest (1)
```

## SOLID

- **S** – minden osztálynak egy feladata van: a `TesztOsztaly` felderít, a `TesztFuttato` futtat,
  a `KonzolosJelentes` kiír, az `Assert` ellenőriz.
- **O** – új kimeneti formához (pl. HTML-riport) elég egy új `Jelentes` implementáció,
  a futtatóhoz nem kell hozzányúlni.
- **L** – bármelyik `Jelentes` implementáció beilleszthető a `KonzolosJelentes` helyére.
- **I** – a `Jelentes` interfésznek egyetlen metódusa van.
- **D** – a `Main` csak a `Jelentes` interfészt ismeri, a `KonzolosJelentes` kívülről kapja a
  `PrintStream`-et, ezért tesztben memóriába írható.

## Tesztek

A keretrendszert valódi JUnit 5 tesztek ellenőrzik (14 db, mind zöld):

- `AssertTest`: minden `Assert` metódus helyes és hibás ága, valamint az üzenetek tartalma.
- `TesztFuttatoTest`: belső mintaosztályokon ellenőrzi a bukás és a hiba szétválasztását,
  az összesítést, az új példányt tesztenként, az `@Elotte`/`@Utana` sorrendjét,
  a `@VartKivetel` három esetét, a hibás `@Utana`-t és a paraméteres tesztmetódust.
- `KonzolosJelentesTest`: a kimenet formátuma, `ByteArrayOutputStream`-be írva.

## Diagramok

- `docs/osztalydiagram.puml` – osztálydiagram a csomagokkal és a függőségekkel
- `docs/szekvencia.puml` – szekvenciadiagram egy futás menetéről

## Eltérések és megjegyzések

- A konzolon ✔/✘ helyett `[ OK ]` / `[BUKO]` / `[HIBA]` jelölés szerepel, mert a Windowsos
  NetBeans-konzol nem mindig UTF-8, és ott a speciális karakterek helyén `?` jelenne meg.
- A tesztek név szerint futnak (ezért van `a`/`b`/`c` előtag a példatesztek nevében),
  így a kimenet mindig ugyanaz.
- A paraméteres tesztmetódus nem fut le, az eredménye HIBAS.
- A stretch célok (`@Kihagy`, időkorlát, HTML-riport) nincsenek megvalósítva.
