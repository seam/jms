package org.jboss.seam.jms.example.statuswatcher.session;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.jms.example.statuswatcher.model.Status;

@Stateless
public class StatusManagerImpl implements StatusManager {
    @PersistenceContext
    private EntityManager em;

    private int MAX_RESULTS = 50;

    public Status addStatusMessage(Status status) {
        if (status.getDatetime() == null) {
            status.setDatetime(new Date());
        }
        em.merge(status);
        return status;
    }

    public List<Status> getAllStatuses() {
        Query q = em.createQuery("SELECT s from Status s ORDER BY datetime DESC");
        q.setMaxResults(MAX_RESULTS);
        @SuppressWarnings("unchecked")
        List<Status> stats = q.getResultList();
        return stats;
    }
}
