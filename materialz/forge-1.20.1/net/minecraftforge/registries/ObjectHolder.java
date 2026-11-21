package net.minecraftforge.registries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @deprecated */
@Deprecated(
   since = "1.21.4",
   forRemoval = true
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ObjectHolder {
   String registryName();

   String value();
}
