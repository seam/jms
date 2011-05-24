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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.seam.jms.annotations.JmsDestination;

import static org.jboss.seam.jms.impl.wrapper.JmsDestinationAnnotatedWrapper.needsDecorating;

/**
 * Wraps {@link AnnotatedCallable}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}. Only the
 * {@link javax.enterprise.inject.spi.Annotated} that define the transitive
 * annotations will be wrapped.
 *
 * @author Jordan Ganoff
 */
public abstract class JmsDestinationCallableWrapper<X> implements AnnotatedCallable<X> {

    private AnnotatedCallable<X> decorated;
    private List<AnnotatedParameter<X>> parameters;

    public JmsDestinationCallableWrapper(AnnotatedCallable<X> decorated) {
        this.decorated = decorated;

        parameters = new ArrayList<AnnotatedParameter<X>>();
        for (AnnotatedParameter<X> p : decorated.getParameters()) {
            parameters.add(decorate(p));
        }
        parameters = Collections.unmodifiableList(parameters);
    }

    private AnnotatedParameter<X> decorate(AnnotatedParameter<X> parameter) {
        return needsDecorating(parameter) ? new JmsDestinationParameterWrapper<X>(parameter) : parameter;
    }

    protected AnnotatedCallable<X> decorated() {
        return decorated;
    }

    public List<AnnotatedParameter<X>> getParameters() {
        return parameters;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return decorated.getAnnotation(annotationType);
    }

    public Set<Annotation> getAnnotations() {
        return decorated.getAnnotations();
    }

    public Type getBaseType() {
        return decorated.getBaseType();
    }

    public AnnotatedType<X> getDeclaringType() {
        return decorated.getDeclaringType();
    }

    public Set<Type> getTypeClosure() {
        return decorated.getTypeClosure();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return decorated.isAnnotationPresent(annotationType);
    }

    public boolean isStatic() {
        return decorated.isStatic();
    }
}
