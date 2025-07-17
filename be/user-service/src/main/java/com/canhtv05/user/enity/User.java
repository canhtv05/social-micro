package com.canhtv05.user.enity;

import com.canhtv05.user.common.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity implements UserDetails {

    @Column(name = "user_name")
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "email", unique = true)
    String email;

    @JsonIgnore
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    UserStatus userStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getDescription()))
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.userStatus.equals(UserStatus.BANNED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.userStatus.equals(UserStatus.ACTIVE);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass())
            return false;
        User user = (User) object;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password)
                && Objects.equals(email, user.email) && Objects.equals(refreshToken, user.refreshToken)
                && userStatus == user.userStatus && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, refreshToken, userStatus, roles);
    }
}
