/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.jms.impl.wrapper;

import static org.jboss.seam.jms.impl.wrapper.JmsDestinationAnnotatedWrapper.needsDecorating;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link AnnotatedCallable}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}. Only the
 * {@link javax.enterprise.inject.spi.Annotated} that define the transitive
 * annotations will be wrapped.
 * 
 * @author Jordan Ganoff
 */
public abstract class JmsDestinationCallableWrapper<X> implements AnnotatedCallable<X>
{

   private AnnotatedCallable<X> decorated;
   private List<AnnotatedParameter<X>> parameters;

   public JmsDestinationCallableWrapper(AnnotatedCallable<X> decorated)
   {
      this.decorated = decorated;

      parameters = new ArrayList<AnnotatedParameter<X>>();
      for (AnnotatedParameter<X> p : decorated.getParameters())
      {
         parameters.add(decorate(p));
      }
      parameters = Collections.unmodifiableList(parameters);
   }

   private AnnotatedParameter<X> decorate(AnnotatedParameter<X> parameter)
   {
      return needsDecorating(parameter) ? new JmsDestinationParameterWrapper<X>(parameter) : parameter;
   }

   protected AnnotatedCallable<X> decorated()
   {
      return decorated;
   }

   public List<AnnotatedParameter<X>> getParameters()
   {
      return parameters;
   }

   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      return decorated.getAnnotation(annotationType);
   }

   public Set<Annotation> getAnnotations()
   {
      return decorated.getAnnotations();
   }

   public Type getBaseType()
   {
      return decorated.getBaseType();
   }

   public AnnotatedType<X> getDeclaringType()
   {
      return decorated.getDeclaringType();
   }

   public Set<Type> getTypeClosure()
   {
      return decorated.getTypeClosure();
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      return decorated.isAnnotationPresent(annotationType);
   }

   public boolean isStatic()
   {
      return decorated.isStatic();
   }
}
