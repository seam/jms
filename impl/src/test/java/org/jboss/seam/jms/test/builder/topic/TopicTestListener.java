package org.jboss.seam.jms.test.builder.topic;

import javax.jms.Message;
import javax.jms.MessageListener;

public class TopicTestListener implements MessageListener{
	private boolean observed = false;
	private Class<?> messageClass = null;
	
	public boolean isObserved() { return observed; }
	public Class<?> getMessageClass() { return messageClass; }
	
	@Override
	public void onMessage(Message arg0) {
		observed = true;
		messageClass = arg0.getClass();
	}
}
