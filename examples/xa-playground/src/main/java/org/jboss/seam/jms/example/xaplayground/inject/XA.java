package org.jboss.seam.jms.example.xaplayground.inject;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

@Qualifier
@Target({FIELD, METHOD, TYPE, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XA {

}
