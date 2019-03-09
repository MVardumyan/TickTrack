package ticktrack.frontend.attributes;

import common.enums.UserRole;

import java.util.List;

public final class User {
    private String username;
    private UserRole role;
    private List<String> userGroup;

    public User(String username, UserRole role, List<String> userGroup) {
        this.username = username;
        this.role = role;
        this.userGroup = userGroup;
    }

    public List<String> getUserGroups() { return userGroup; }

    public void setUserGroup(List<String> userGroup) { this.userGroup = userGroup; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }
}
