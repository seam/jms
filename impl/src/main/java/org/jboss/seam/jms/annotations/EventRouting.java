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
package org.jboss.seam.jms.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Identifies an Event Routing configuration method. May be applied to a method
 * of a bean class.
 * </p>
 * <p/>
 * <p>
 * The method may return {@link EventBridge} or {@code List<}{@link EventBridge}{@code>}.
 * </p>
 * <p/>
 * <pre>
 * &#064;Inject
 * EventBridge bridge;
 *
 * &#064;EventRouting
 * public static Route eventRoutingConfig()
 * {
 *    return bridge.createRoute(EGRESS, Object.class).addQualifier(SPECIAL).connectTo(Topic.class, myTopic);
 * }
 * </pre>
 *
 * @author Jordan Ganoff
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface EventRouting {
}
