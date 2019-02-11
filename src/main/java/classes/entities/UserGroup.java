package classes.entities;


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

    @OneToMany(mappedBy = "group")
    private Set<User> members;

    @OneToMany(mappedBy = "group")
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

    public String getName() {
        return name;
    }

    public Set<User> getMembers() {
        return members;
    }
}
