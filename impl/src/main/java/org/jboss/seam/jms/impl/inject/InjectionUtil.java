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
