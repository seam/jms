package org.jboss.seam.jms.example.statuswatcher.session;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.TopicBuilder;
import org.jboss.seam.jms.example.statuswatcher.model.Status;


@SessionScoped
@Named("receivingClient")
public class ReceivingClient implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private StatusManager manager;
    private LinkedList<Status> receivedStatuses;

    private boolean followAll = false;

    @Inject TopicBuilder topicBuilder;

    @PostConstruct
    public void initialize() {
    	log.info("Creating new ReceivingClient.");
        this.receivedStatuses = new LinkedList<Status>();
    }

    public void changeFollowing(ValueChangeEvent e) throws Exception {
        if (followAll) {
        	topicBuilder.destination("jms/statusInfoTopic").listen(new ReceivingClientListener(this));
        } else {

        }
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

    public void notify(Status status) {
        log.info("Received status update");
        receivedStatuses.offerFirst(status);
    }

}
