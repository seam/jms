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
