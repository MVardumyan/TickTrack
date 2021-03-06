package ticktrack.entities;

import ticktrack.enums.TicketPriority;
import ticktrack.enums.TicketStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "ticketSearch",
        columnList = "summary,description,status,resolution,priority,deadline,closeDate,openDate")
})
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

   @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
   private List<Comment> commentList;

   public Ticket(String summary,String description,TicketPriority priority,User creator,Category category){
      this.summary = summary;
      this.description = description;
      this.priority = priority;
      this.category = category;
      this.creator = creator;
   }

   public Ticket(){}

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

   public void setCloseDate(Timestamp closeDate) {
      this.closeDate = closeDate;
   }

   public void setGroup(UserGroup group) {
      this.group = group;
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

   public List<Comment> getCommentList() {
      return commentList;
   }

   public Timestamp getCloseDate() {
      return closeDate;
   }
}
