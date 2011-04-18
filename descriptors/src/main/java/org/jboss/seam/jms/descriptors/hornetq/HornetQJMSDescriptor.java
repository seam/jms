package org.jboss.seam.jms.descriptors.hornetq;

import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public interface HornetQJMSDescriptor extends Descriptor {
	public QueueDescriptor queue(String name, String jndi);
        public QueueDescriptor queue(String name);

	public TopicDescriptor topic(String name, String jndi);
        public TopicDescriptor topic(String name);
}
