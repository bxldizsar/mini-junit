package minijunit.annotacio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// a teszt csak akkor sikeres, ha ezt a kivetelt dobja
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VartKivetel
{
    Class<? extends Throwable> value();
}