/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.jms.impl.wrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link javax.enterprise.inject.spi.Annotated}s that declare transitive
 * annotations to {@link JmsDestination} with the actual {@link JmsDestination}.
 * 
 * @author Jordan Ganoff
 */
public abstract class JmsDestinationAnnotatedWrapper implements Annotated
{
   private Annotated decorated;
   private Set<Annotation> annotations;
   private Map<Class<? extends Annotation>, Annotation> mappedAnnotations;

   public JmsDestinationAnnotatedWrapper(Annotated decorated)
   {
      this.decorated = decorated;
      annotations = new HashSet<Annotation>();
      mappedAnnotations = new HashMap<Class<? extends Annotation>, Annotation>();
      for (Annotation a : decorated.getAnnotations())
      {
         // Replace all annotations that define JmsDestination with the actual
         // JmsDestination so we can produce them generically
         JmsDestination d = a.annotationType().getAnnotation(JmsDestination.class);
         if (d != null)
         {
            mappedAnnotations.put(a.getClass(), null);
            mappedAnnotations.put(d.getClass(), d);
            annotations.add(d);
         }
         else
         {
            annotations.add(a);
         }
      }
      annotations = Collections.unmodifiableSet(annotations);
   }

   /**
    * The set of annotations for an injection target should be decorated if
    * transitively define {@link JmsDestination}.
    */
   protected static boolean needsDecorating(Annotated annotated)
   {
      for (Annotation a : annotated.getAnnotations())
      {
         if (a.annotationType().getAnnotation(JmsDestination.class) != null)
         {
            return true;
         }
      }
      return false;
   }

   protected Annotated decorated()
   {
      return decorated;
   }

   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      if (mappedAnnotations.containsKey(annotationType))
      {
         return annotationType.cast(mappedAnnotations.get(annotationType));
      }
      return decorated.getAnnotation(annotationType);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      if (mappedAnnotations.containsKey(annotationType))
      {
         return mappedAnnotations.get(annotationType) != null;
      }
      return decorated.isAnnotationPresent(annotationType);
   }

   public Set<Annotation> getAnnotations()
   {
      return annotations;
   }

   public Type getBaseType()
   {
      return decorated.getBaseType();
   }

   public Set<Type> getTypeClosure()
   {
      return decorated.getTypeClosure();
   }
}
