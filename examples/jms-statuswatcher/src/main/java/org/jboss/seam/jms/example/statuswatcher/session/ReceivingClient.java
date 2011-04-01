package org.jboss.seam.jms.example.statuswatcher.session;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.DurableMessageManager;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.annotations.Durable;
import org.jboss.seam.jms.example.statuswatcher.model.Status;
import org.jboss.seam.jms.example.statuswatcher.qualifiers.StatusTopic;


@SessionScoped
@Named
public class ReceivingClient implements Serializable, MessageListener
{
   private static final long serialVersionUID = 1L;
//   private static final int TIMEOUT = 20;
   private static int uniqueId = 1;
   
   @Inject
   private Logger log;
   
   @Inject
   private DurableMessageManager dmm;
   
   @Inject 
   private StatusManager manager;
   private LinkedList<Status> receivedStatuses;
   
   private boolean followAll = false;
   private String clientSubscription;
   private String clientId;
   
//   @Resource(mappedName = "/ConnectionFactory")
//   private ConnectionFactory connectionFactory;
//
//   private transient Connection connection;
//   private transient Session session;
//   private transient TopicSubscriber subscriber;
   
   @Inject @StatusTopic
   private Topic statusTopic;
   
   
   @PostConstruct
   public void initialize()
   {
      this.receivedStatuses = new LinkedList<Status>();
      ++uniqueId;
      clientSubscription = "subscription" + uniqueId;
      clientId = "client" + uniqueId; 
   }

   @PreDestroy
   public void cleanup() throws Exception
   {
      dmm.unsubscribe(clientId);
//      if (connection != null)
//      {
//         connection.close();
//         connection = null;
//      }
   }
   
//   public void receive() throws Exception
//   {
//      ObjectMessage msg;
//      Message response;
//      if (followAll)
//      {
//         while ((response = subscriber.receive(TIMEOUT)) != null)
//         {
//            msg = (ObjectMessage) response;
//            receivedStatuses.offerFirst((Status) msg.getObject());
//         }
//      }
//   }

   public void changeFollowing(ValueChangeEvent e) throws Exception
   {
      followAll = (Boolean) e.getNewValue();
      if (followAll)
      {
//         connection = connectionFactory.createConnection();
//         connection.setClientID(clientId);
//         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//         subscriber = session.createDurableSubscriber(statusTopic, clientSubscription);
//         connection.start();
//         receive();
         dmm.login(clientId);
         dmm.createDurableSubscriber(statusTopic, clientSubscription , this);
      }
      else
      {
         dmm.unsubscribe(clientId);
//         subscriber.close();
//         session.unsubscribe(clientSubscription);
//         if (connection != null)
//         {
//            connection.close();
//         }
      }
   }

   public void history()
   {
      receivedStatuses = new LinkedList<Status>(manager.getAllStatuses());
   }
   
   public List<Status> getReceivedStatuses() throws Exception
   {
//      if (followAll)
//      {
//         receive();
//      }
      return receivedStatuses;
   }

   public void setReceivedStatuses(LinkedList<Status> receivedStatuses)
   {
      this.receivedStatuses = receivedStatuses;
   }

   public boolean isFollowAll()
   {
      return this.followAll;
   }

   public void setFollowAll(boolean followAll)
   {
      this.followAll = followAll;
   }
   
   @Override
   public void onMessage(Message msg)
   {
      if (msg instanceof ObjectMessage)
      {
         ObjectMessage om = (ObjectMessage) msg;
         try
         {
            log.info("Received status update");
            receivedStatuses.offerFirst((Status) om.getObject());
         }
         catch (JMSException e)
         {
            log.error(e.getMessage());
         }
      }
   }
   
}
