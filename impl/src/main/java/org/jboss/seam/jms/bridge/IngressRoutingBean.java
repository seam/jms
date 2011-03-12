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
package org.jboss.seam.jms.bridge;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

/**
 * Translates received JMS messages from the configured destinations into CDI
 * events that match the provided {@link Route} configuration.
 * 
 * @author Jordan Ganoff
 * 
 */
public class IngressRoutingBean implements Bean
{

   public Class getBeanClass()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set getInjectionPoints()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set getQualifiers()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Class getScope()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set getStereotypes()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Set getTypes()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isAlternative()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isNullable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public Object create(CreationalContext creationalContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void destroy(Object instance, CreationalContext creationalContext)
   {
      // TODO Auto-generated method stub

   }

}
