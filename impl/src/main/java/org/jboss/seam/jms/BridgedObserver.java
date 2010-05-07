package org.jboss.seam.jms;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer Method to observe events and forward (bridge) them over JMS.
 * 
 * @author Jordan Ganoff
 */
@Named
@ApplicationScoped
public class BridgedObserver implements ObserverMethod<Object>
{
   private Logger log = LoggerFactory.getLogger(getClass());

   private BeanManager bm;
   private JmsForwarding config;

   public BridgedObserver(BeanManager bm, JmsForwarding config)
   {
      this.bm = bm;
      this.config = config;
   }

   public Class<?> getBeanClass()
   {
      return null;
   }

   public Set<Annotation> getObservedQualifiers()
   {
      return config.getQualifiers();
   }

   public Type getObservedType()
   {
      return config.getEventType();
   }

   public Reception getReception()
   {
      return Reception.ALWAYS;
   }

   public TransactionPhase getTransactionPhase()
   {
      return TransactionPhase.AFTER_SUCCESS;
   }

   public void notify(Object evt)
   {
      // FIXME Include qualifiers once CDI 1.0 MR is complete and
      // notify(Event, Set<Annotation>) is added
      forwardEvent(evt, null);
   }

   private void forwardEvent(Object event, Set<Annotation> qualifiers)
   {
      Set<Bean<?>> beans = bm.getBeans(Session.class);
      Bean<?> bean = bm.resolve(beans);
      Session s = (Session) bm.getReference(bean, Session.class, bm.createCreationalContext(bean));
      try
      {
         for (Destination d : config.getDestinations())
         {
            try
            {
               Message m = s.createObjectMessage((Serializable) event);
               // Safe to create producers here always? In an app server these
               // should be cached via JCA managed connection factory but what
               // about other environments?
               s.createProducer(d).send(m);
            }
            catch (JMSException ex)
            {
               log.error("Unable to forward event", ex);
            }
         }
      }
      finally
      {
         try
         {
            s.close();
         }
         catch (JMSException ex)
         {
            log.error("Unable to close session", ex);
         }
      }
   }
}
