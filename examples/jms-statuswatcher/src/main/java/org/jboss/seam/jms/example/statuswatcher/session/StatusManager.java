package org.jboss.seam.jms.example.statuswatcher.session;

import java.util.List;

import javax.ejb.Local;

import org.jboss.seam.jms.example.statuswatcher.model.Status;

@Local
public interface StatusManager
{
   public Status addStatusMessage(Status status);
   
   public List<Status> getAllStatuses();
}
