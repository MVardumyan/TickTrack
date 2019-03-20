package ticktrack.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class PasswordLink {
    @Id
    @GeneratedValue
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Timestamp validDate;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getLink() {
        return link;
    }

    public Timestamp getValidDate() {
        return validDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setValidDate(Timestamp validDate) {
        this.validDate = validDate;
    }
}
