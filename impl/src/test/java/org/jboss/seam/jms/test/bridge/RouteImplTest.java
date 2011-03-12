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
