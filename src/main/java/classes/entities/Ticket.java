package classes.entities;

import classes.enums.TicketPriority;
import classes.enums.TicketStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table
public class Ticket{
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
   private long ID;
   @Column(nullable = false)
   private TicketPriority priority;
   @ManyToOne
   private User assignee;
   @Column(nullable = false)
   private String summary;
   @Column(nullable = false)
   private String description;
   @Column
   private TicketStatus status;
   @Column
   private Timestamp openDate;
   @ManyToOne(optional = false)
   private User creator;
   @Column
   private String resolution;
   @Column
   private Timestamp deadline;
   @Column
   private Timestamp closeDate;

   @ManyToOne
   private UserGroup group;

   @ManyToOne
   private Category category;

   @OneToMany(mappedBy = "ticket")
   private Set<Comment> commentList;

   public Ticket(String summary,String description,TicketPriority priority,Category category){
      this.summary = summary;
      this.description = description;
      this.priority = priority;
      this.category = category;
   }

   public Ticket() {
   }

   public void setAssignee(User assignee) {
      this.assignee = assignee;
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

   public long getID() {
      return ID;
   }

   public TicketPriority getPriority() {
      return priority;
   }

   public User getAssignee() {
      return assignee;
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

   public UserGroup getGroup() {
      return group;
   }

   public Category getCategory() {
      return category;
   }

   public Set<Comment> getCommentList() {
      return commentList;
   }
}
