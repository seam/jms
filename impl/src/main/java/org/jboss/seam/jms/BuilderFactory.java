package org.jboss.seam.jms;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.jboss.solder.exception.control.ExceptionToCatch;

/**
 * BuilderFactory is responsible for creating builders - QueueBuilder, DestinationBuilder and TopicBuilder
 * 
 * Supports producers as well as methods.
 *
 * @author John Ament
 */
public class BuilderFactory {
    @Inject Event<ExceptionToCatch> exceptionEvent;
    
    @Produces
    public DestinationBuilder newDestinationBuilder() {
        return new DestinationBuilderImpl(exceptionEvent);
    }
    
    @Produces
    public QueueBuilder newQueueBuilder() {
        return new QueueBuilderImpl(exceptionEvent);
    }
    
    @Produces
    public TopicBuilder newTopicBuilder() {
        return new TopicBuilderImpl(exceptionEvent);
    }
}
