package org.jboss.seam.jms.example.statuswatcher.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import javax.jms.Topic;
import org.jboss.seam.jms.TopicBuilder;
import org.jboss.seam.jms.example.statuswatcher.model.Status;
import org.jboss.solder.logging.Logger;


@SessionScoped
@Named("receivingClient")
public class ReceivingClient implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private StatusManager manager;
    private LinkedList<Status> receivedStatuses;
    private List<Integer> pendingStatuses;

    private boolean followAll = false;

    @Inject TopicBuilder topicBuilder;
    @EJB StatusManager statusManager;

    @Resource(mappedName="java:/jms/statusInfoTopic")
    Topic statusInfoTopic;
    
    
    @PostConstruct
    public void initialize() {
    	log.debug("Creating new ReceivingClient.");
    	this.pendingStatuses = new ArrayList<Integer>();
        this.receivedStatuses = new LinkedList<Status>();
        topicBuilder.destination(statusInfoTopic).listen(new ReceivingClientListener(this));
    }
    
    public String receive() {
    	for(Integer statusId: this.pendingStatuses) {
    		Status status = statusManager.find(statusId);
            log.debug("Received status update");
            receivedStatuses.offerFirst(status);
    	}
    	this.pendingStatuses.clear();
    	return "/watchstatus.xhtml";
    }
    
    public String send() {
    	return "/sendstatus.xhtml";
    }

    public void history() {
        receivedStatuses = new LinkedList<Status>(manager.getAllStatuses());
    }

    public List<Status> getReceivedStatuses() throws Exception {
        return receivedStatuses;
    }

    public void setReceivedStatuses(LinkedList<Status> receivedStatuses) {
        this.receivedStatuses = receivedStatuses;
    }

    public boolean isFollowAll() {
        return this.followAll;
    }

    public void setFollowAll(boolean followAll) {
        this.followAll = followAll;
    }

    public void notify(Integer statusId) {
    	this.pendingStatuses.add(statusId);
    }

}
