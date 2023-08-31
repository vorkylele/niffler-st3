package guru.qa.niffler.db.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

public class AuthorityEntity {

    private UUID id;

    private Authority authority;

    private UserEntity user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

}
