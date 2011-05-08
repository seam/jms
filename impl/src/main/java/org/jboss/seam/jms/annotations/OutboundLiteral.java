package org.jboss.seam.jms.annotations;

import javax.enterprise.util.AnnotationLiteral;

public class OutboundLiteral extends AnnotationLiteral<Outbound>
	implements Outbound {

	public static final OutboundLiteral INSTANCE = new OutboundLiteral();
}
