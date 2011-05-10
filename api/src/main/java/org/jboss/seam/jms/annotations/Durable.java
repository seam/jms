package org.jboss.seam.jms.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Durable represents durable subscribers.
 *
 * @author johnament
 */
@Qualifier
@Documented
@Target({FIELD, METHOD, TYPE, PARAMETER})
@Retention(RUNTIME)
public @interface Durable {

}
