package org.jboss.seam.jms.test;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jboss.seam.jms.annotations.JmsDestination;

@Qualifier
@Retention(RUNTIME)
@Target( { FIELD, METHOD, TYPE, PARAMETER })
@JmsDestination(jndiName = "jms/T")
public @interface MyTopic
{
}
