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
package org.jboss.seam.jms.test.bridge;

import static org.jboss.seam.jms.bridge.RouteType.EGRESS;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteImpl;
import org.junit.Assert;
import org.junit.Test;

public class RouteImplTest
{
   @Qualifier public @interface TestQualifier {}
   
   @SuppressWarnings("serial")
   @Test
   public void addQualifier()
   {
      Route r = new RouteImpl(EGRESS, Object.class);
      Annotation qualifier = new AnnotationLiteral<TestQualifier>() {};
      r.addQualifiers(qualifier);
      Assert.assertFalse(r.getQualifiers().isEmpty());
      Assert.assertEquals(1, r.getQualifiers().size());
      Assert.assertEquals(qualifier, r.getQualifiers().iterator().next());
   }
   
   @SuppressWarnings("serial")
   @Test(expected=IllegalArgumentException.class)
   public void addQualifier_non_qualifier()
   {
      Route r = new RouteImpl(EGRESS, Object.class);
      r.addQualifiers(new AnnotationLiteral<Deprecated>() {});
   }
}
