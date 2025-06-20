// server/src/main/java/com/example/echobackend/controller/UserController.java
package com.example.echobackend.controller;
import com.example.echobackend.dto.UserDTO; // Import UserDTO
import com.example.echobackend.model.User; // Keep if you use it for other methods
import com.example.echobackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // MODIFIED: Get User by ID to return UserDTO
    @GetMapping("/find/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) { // Change return type to UserDTO
        try {
            UserDTO userDto = userService.getUserById(userId); // Call the service method that returns UserDTO
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) { // Catch RuntimeException for user not found
            System.err.println("Error fetching user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return NOT_FOUND for user not found
        } catch (Exception e) {
            System.err.println("Unexpected error fetching user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Existing: Update User Endpoint (keep as User if you're updating the User entity directly)
    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok("User updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        }
    }

    // Existing: Delete User Endpoint
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (RuntimeException e) {
            // Changed to FORBIDDEN for authorization errors, which is a better fit for deleteUser's RuntimeException
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    // Existing: Get Suggestions Endpoint
    @GetMapping("/suggestions")
    public ResponseEntity<List<User>> getSuggestions() {
        try {
            List<User> suggestions = userService.getSuggestions();
            return ResponseEntity.ok(suggestions);
        } catch (RuntimeException e) {
            System.err.println("Error getting suggestions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Unexpected error getting suggestions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to search for users by username or name.
     * Accessible at GET /api/users/search?query={searchTerm}
     *
     * @param query The search string from the frontend.
     * @return A list of User objects matching the query.
     */
    // NEW: Search Endpoint
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        try {
            List<User> users = userService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
