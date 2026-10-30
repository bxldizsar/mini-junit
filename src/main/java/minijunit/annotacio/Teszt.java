package minijunit.annotacio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// RUNTIME nelkul a reflexio nem latja
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Teszt
{
}