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
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.seam.jms.annotations.JmsDestination;

import static org.jboss.seam.jms.impl.wrapper.JmsDestinationAnnotatedWrapper.needsDecorating;

/**
 * Wraps {@link AnnotatedType}s with injection points that have transitive
 * annotations to {@link JmsDestination}. Only the
 * {@link javax.enterprise.inject.spi.Annotated} that define the transitive
 * annotations will be wrapped.
 *
 * @author Jordan Ganoff
 */
public class JmsAnnotatedTypeWrapper<X> implements AnnotatedType<X> {
    private AnnotatedType<X> decorated;
    private Set<AnnotatedField<? super X>> fields;
    private Set<AnnotatedMethod<? super X>> methods;
    private Set<AnnotatedConstructor<X>> constructors;

    public JmsAnnotatedTypeWrapper(AnnotatedType<X> decorated) {
        this.decorated = decorated;

        fields = new HashSet<AnnotatedField<? super X>>();
        for (AnnotatedField<? super X> f : decorated.getFields()) {
            fields.add(decorate(f));
        }
        fields = Collections.unmodifiableSet(fields);

        methods = new HashSet<AnnotatedMethod<? super X>>();
        for (AnnotatedMethod<? super X> m : decorated.getMethods()) {
            methods.add(decorate(m));
        }
        methods = Collections.unmodifiableSet(methods);

        constructors = new HashSet<AnnotatedConstructor<X>>();
        for (AnnotatedConstructor<X> c : decorated.getConstructors()) {
            constructors.add(decorate(c));
        }
        constructors = Collections.unmodifiableSet(constructors);
    }

    /**
     * Decorates the type if any injection targets are transitively annotated
     * with {@link JmsDestination}.
     */
    public static <T> AnnotatedType<T> decorate(AnnotatedType<T> type) {
        for (AnnotatedField<? super T> f : type.getFields()) {
            if (needsDecorating(f)) {
                return new JmsAnnotatedTypeWrapper<T>(type);
            }
        }
        for (AnnotatedMethod<? super T> m : type.getMethods()) {
            for (AnnotatedParameter<? super T> p : m.getParameters()) {
                if (needsDecorating(p)) {
                    return new JmsAnnotatedTypeWrapper<T>(type);
                }
            }
        }
        for (AnnotatedConstructor<? super T> c : type.getConstructors()) {
            for (AnnotatedParameter<? super T> p : c.getParameters()) {
                if (needsDecorating(p)) {
                    return new JmsAnnotatedTypeWrapper<T>(type);
                }
            }
        }
        return type;
    }

    public <T> AnnotatedField<T> decorate(AnnotatedField<T> field) {
        return needsDecorating(field) ? new JmsDestinationFieldWrapper<T>(field) : field;
    }

    public <T> AnnotatedMethod<T> decorate(AnnotatedMethod<T> method) {
        for (AnnotatedParameter<T> p : method.getParameters()) {
            if (needsDecorating(p)) {
                return new JmsDestinationMethodWrapper<T>(method);
            }
        }
        return method;
    }

    public <T> AnnotatedConstructor<T> decorate(AnnotatedConstructor<T> constructor) {
        for (AnnotatedParameter<T> p : constructor.getParameters()) {
            if (needsDecorating(p)) {
                return new JmsDestinationConstructorWrapper<T>(constructor);
            }
        }
        return constructor;
    }

    public Set<AnnotatedField<? super X>> getFields() {
        return fields;
    }

    public Set<AnnotatedConstructor<X>> getConstructors() {
        return constructors;
    }

    public Set<AnnotatedMethod<? super X>> getMethods() {
        return methods;
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

    public Class<X> getJavaClass() {
        return decorated.getJavaClass();
    }

    public Set<Type> getTypeClosure() {
        return decorated.getTypeClosure();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return decorated.isAnnotationPresent(annotationType);
    }
}
