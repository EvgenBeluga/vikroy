package com.vikroy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.NONE)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    // === UserDetails ===

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // === Явные геттеры для полей, которые Lombok может не сгенерировать ===

    public String getRole() {
        return role;
    }

    public LocalDate getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public boolean isPremiumActive() {
        return "PREMIUM".equals(role)
                && subscriptionEndDate != null
                && !subscriptionEndDate.isBefore(LocalDate.now());
    }
}