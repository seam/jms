package org.jboss.seam.jms.annotations;

import javax.enterprise.util.AnnotationLiteral;

public class InboundLiteral extends AnnotationLiteral<Inbound>
	implements Inbound {
	public static final InboundLiteral INSTANCE = new InboundLiteral();
}
