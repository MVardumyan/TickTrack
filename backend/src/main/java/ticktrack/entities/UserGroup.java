package ticktrack.entities;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean deactivated;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<User> members;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    public UserGroup(String name, List<User> members) {
        this.name = name;
        this.members = members;
    }

    public UserGroup() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public String getName() {
        return name;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
