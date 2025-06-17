package com.example.echobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Lombok: Generates getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // IMPORTANT: Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Stored as hashed password

    private String name;
    private String coverPic;
    private String profilePic;
    private String city;
    private String website;

    // --- UserDetails Interface Implementations ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For simplicity, we are assigning a default "ROLE_USER".
        // In a real application, you might have roles stored in the database
        // and fetch them here (e.g., from a 'roles' field in the User entity).
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // getPassword() and getUsername() are already provided by Lombok's @Data annotation
    // (given your field names match 'password' and 'username').
    // If you remove @Data, you'd need to manually implement:
    // @Override
    // public String getPassword() { return password; }
    // @Override
    // public String getUsername() { return username; }


    @Override
    public boolean isAccountNonExpired() {
        // You can implement account expiration logic here if needed.
        // For now, return true (account never expires).
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // You can implement account locking logic here (e.g., after multiple failed login attempts).
        // For now, return true (account is never locked).
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // You can implement password expiration logic here.
        // For now, return true (credentials never expire).
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You can implement account activation/deactivation logic here.
        // For now, return true (account is always enabled).
        return true;
    }

    // IMPORTANT: Custom constructor for DTO mapping in AuthController
    // This is the constructor that was causing the error in AuthController previously.
    // It's good to keep it for flexibility, but ensure it matches your DTO.
    // If you plan to only use DTOs for responses, you might not need this specific constructor
    // in the User entity itself if you're using a mapper like ModelMapper or manually setting properties.
    // For now, if you are explicitly calling 'new User(...)' in AuthController, this constructor
    // (or a similar one) must exist. However, for a user *response*, a dedicated DTO is better.
    // The UserResponse DTO we created previously makes this specific constructor in User entity less necessary
    // for response purposes. Keep @AllArgsConstructor as Lombok handles it.
}