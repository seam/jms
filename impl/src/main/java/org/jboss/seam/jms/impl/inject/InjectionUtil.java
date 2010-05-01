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
