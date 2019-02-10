package classes.entities;

import classes.enums.TicketPriority;
import classes.enums.TicketStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table
public class Ticket{
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   int ID;
   @Column(nullable = false)
   TicketPriority priority;
   @ManyToOne
   User asignee;
   @Column(nullable = false)
   String summary;
   @Column(nullable = false)
   String description;
   @Column
   TicketStatus status;
   @Column
   Timestamp openDate;
   @ManyToOne
   User creator;
   @Column
   String resolution;
   @Column
   Timestamp deadline;

   @ManyToOne
   Group group;

   @ManyToOne
   Category category;

   @OneToMany
   List<Comment> commentList;

   public Ticket(String summary,String description,TicketPriority priority,Category category){
      this.summary = summary;
      this.description = description;
      this.priority = priority;
      this.category = category;
   }

   public Ticket() {
   }

   public void setAsignee(User asignee) {
      this.asignee = asignee;
   }

   public void setComment(Comment comment) {
      this.commentList.add(comment);
   }

   public void setStatus(TicketStatus status) {
      this.status = status;
   }

   public void setOpenDate(Timestamp openDate) {
      this.openDate = openDate;
   }

   public void setCreator(User creator) {
      this.creator = creator;
   }

   public void setResolution(String resolution) {
      this.resolution = resolution;
   }

   public void setDeadline(Timestamp deadline) {
      this.deadline = deadline;
   }

   public void setPriority(TicketPriority priority) {
      this.priority = priority;
   }

   public void setCategory(Category category) {
      this.category = category;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setSummary(String summary) {
      this.summary = summary;
   }

   public int getID() {
      return ID;
   }

   public TicketPriority getPriority() {
      return priority;
   }

   public User getAsignee() {
      return asignee;
   }

   public String getSummary() {
      return summary;
   }

   public String getDescription() {
      return description;
   }

   public TicketStatus getStatus() {
      return status;
   }

   public Timestamp getOpenDate() {
      return openDate;
   }

   public User getCreator() {
      return creator;
   }

   public String getResolution() {
      return resolution;
   }

   public Timestamp getDeadline() {
      return deadline;
   }

   public Group getGroup() {
      return group;
   }

   public Category getCategory() {
      return category;
   }

   public List<Comment> getCommentList() {
      return commentList;
   }
}