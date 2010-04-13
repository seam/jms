package org.jboss.seam.jms.impl.inject;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Qualifier;

/**
 * Collection of utilities to facilitate the injection of resources
 * 
 * @author Jordan Ganoff
 */
public class InjectionUtil
{
   /**
    * Find an expected {@link Qualifier} type in a set of {@link Annotation}s.
    * 
    * @param <T> Type of expected {@link Qualifier}
    * @param type Type of expected {@link Qualifier}
    * @param set Set of {@link Annotation}s that should contain the expected
    *           {@link Qualifier} type
    * @return The first qualifier of type {@code T}.
    * @throws IllegalArgumentException if no {@link Qualifier} of type {@code T} could
    *            be found.
    */
   public static final <T extends Annotation> T getExpectedQualifier(Class<T> type, Set<Annotation> set)
   {
      for (Annotation a : set)
      {
         if (type.equals(a.annotationType()))
         {
            return type.cast(a);
         }
      }
      throw new IllegalArgumentException(String.format("Expected qualifier missing [%s]", type));
   }
}
