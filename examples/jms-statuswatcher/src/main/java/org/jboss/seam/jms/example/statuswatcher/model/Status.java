package org.jboss.seam.jms.example.statuswatcher.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Status implements Serializable
{
   private static final long serialVersionUID = 1L;
   private static final long MS_PER_SECOND = 1000;
   private static final long MS_PER_MINUTE = 60 * MS_PER_SECOND;
   private static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;
   private static final long MS_PER_DAY = 24 * MS_PER_HOUR;
   private static final SimpleDateFormat df = new SimpleDateFormat("d MMM");

   @Id
   @GeneratedValue(strategy = AUTO)
   @Column(name = "id")
   private int id;

   private String username;

   private String statusMessage;

   @Temporal(TemporalType.TIMESTAMP)
   private Date datetime;

   public Status()
   {
      this.statusMessage = "Enter a new status message...";
   }

   public Status(String username, String message)
   {
      this.username = username;
      this.statusMessage = message;
   }

   public String getStatusMessage()
   {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage)
   {
      this.statusMessage = statusMessage;
   }

   public int getId()
   {
      return this.id;
   }

   public void setsId(int id)
   {
      this.id = id;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public Date getDatetime()
   {
      return datetime;
   }

   public void setDatetime(Date datetime)
   {
      this.datetime = datetime;
   }

   public String getFriendlyDate()
   {
      if (getDatetime() == null)
         return "unknown";

      Date now = new Date();

      long age = now.getTime() - getDatetime().getTime();

      long days = (long) Math.floor(age / MS_PER_DAY);
      age -= (days * MS_PER_DAY);
      long hours = (long) Math.floor(age / MS_PER_HOUR);
      age -= (hours * MS_PER_HOUR);
      long minutes = (long) Math.floor(age / MS_PER_MINUTE);

      if (days < 7)
      {
         StringBuilder sb = new StringBuilder();

         if (days > 0)
         {
            sb.append(days);
            sb.append(days > 1 ? " days " : " day ");
         }

         if (hours > 0)
         {
            sb.append(hours);
            sb.append(hours > 1 ? " hrs " : " hr ");
         }

         if (minutes > 0)
         {
            sb.append(minutes);
            sb.append(minutes > 1 ? " minutes " : " minute ");
         }

         if (hours == 0 && minutes == 0)
         {
            sb.append("just now");
         }
         else
         {
            sb.append("ago");
         }

         return sb.toString();
      }
      else
      {
         return df.format(getDatetime());
      }
   }

   public String toString()
   {
      return "User: " + this.username + ", Time: " + this.getFriendlyDate() + ", Status: " + this.statusMessage;
   }
}
