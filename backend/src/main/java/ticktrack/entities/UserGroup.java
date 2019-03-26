package ticktrack.entities;


import javax.persistence.*;
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
    private Set<User> members;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<Ticket> tickets;

    public UserGroup(String name, Set<User> members) {
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

    public Set<User> getMembers() {
        return members;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }
}
