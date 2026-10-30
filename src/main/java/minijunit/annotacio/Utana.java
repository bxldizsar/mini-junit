package minijunit.annotacio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// minden teszt utan lefut, akkor is, ha a teszt elbukott
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Utana
{
}