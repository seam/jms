/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.seam.jms.test.bridge.intf;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.jboss.logging.Logger;

/**
 *
 * @author johnament
 */
public class ObserverListener implements javax.jms.MessageListener{
    private Logger log = Logger.getLogger(ObserverListener.class);
    @Override
    public void onMessage(Message msg) {
        if(msg instanceof ObjectMessage) {
            try{
                log.debugf("The message received was %s",((ObjectMessage)msg).getObject().toString());
            } catch (Exception e) {

            }
        }
    }

}
