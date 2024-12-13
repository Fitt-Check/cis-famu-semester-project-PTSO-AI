package com.example.ptsoai.Controller;

import com.example.ptsoai.Model.Users;
import com.example.ptsoai.Service.UserService;
import com.example.ptsoai.util.ApiResponse;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private final Firestore firestore;
    private final UserService service;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service) {
        this.firestore = FirestoreClient.getFirestore();
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addUser(@RequestBody HashMap<String, Object> userData) {
        logger.info("Received addUser request with data: {}", userData);
        DocumentReference docRef = firestore.collection("User").document(String.valueOf(userData.get("userId")));

        try {
            Users user = new Users();
            user.setUserId(String.valueOf(userData.get("userId")));
            user.setEmail(String.valueOf(userData.get("email")));
            user.setUsername(String.valueOf(userData.get("username")));
            user.setProfileImage(String.valueOf(userData.get("profileImage")));
            user.setPasswordHash(String.valueOf(userData.get("passwordHash")));
            user.setCreatedAt(Timestamp.now());
            user.setUpdatedAt(Timestamp.now());

            ApiFuture<WriteResult> result = docRef.set(user);
            result.get();

            logger.info("User created successfully with ID: {}", user.getUserId());
            return ResponseEntity.status(201).body(new ApiResponse<>(true, "User created", user.getUserId(), null));
        } catch (ClassCastException e) {
            logger.error("ClassCastException: Invalid data type in input: {}", e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse<>(false, "Invalid data type in input", null, e));
        } catch (ExecutionException | InterruptedException e) {
            logger.error("ExecutionException | InterruptedException: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<ApiResponse<Users>> getUserById(@PathVariable(name = "userId") String userId) throws ParseException, ExecutionException, InterruptedException {
            try {
                // Log input
                System.out.println("Received request for userId: " + userId);

                Users user = service.getUserById(userId);

                // Log output from service
                System.out.println("User retrieved: " + user);

                if (user != null) {
                    return ResponseEntity.ok(new ApiResponse<>(true, "User found", user, null));
                } else {
                    return ResponseEntity.status(204).body(new ApiResponse<>(true, "User not found", null, null));
                }
            } catch (ExecutionException e) {
                e.printStackTrace(); // Log the exception
                return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
            } catch (InterruptedException e) {
                e.printStackTrace(); // Log the exception
                return ResponseEntity.status(503).body(new ApiResponse<>(false, "Unable to reach firebase", null, e));
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "User not found", null, e));
            }
        }



    // Update user details
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUser(@RequestBody HashMap<String, Object> userData) {
        DocumentReference docRef = firestore.collection("User").document((String) userData.get("userId"));

        try {
            ApiFuture<WriteResult> result = docRef.update(
                    "displayName", userData.get("displayName"),
                    "email", userData.get("email"),
                    "username", userData.get("username"),
                    "profileImage", userData.get("profileImage"),
                    "passwordHash", userData.get("passwordHash"),
                    "updatedAt", Timestamp.now()
            );
            result.get();

            return ResponseEntity.status(201).body(new ApiResponse<>(true, "User updated", (String) userData.get("userId"), null));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "User not found", null, e));
        }
    }

    // Delete a user by ID
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
        DocumentReference docRef = firestore.collection("User").document(userId);
        try {
            // Check if the document exists
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Document exists, proceed with deletion
                ApiFuture<WriteResult> result = docRef.delete();

                result.addListener(() -> {
                    try {
                        System.out.println("User deleted at: " + result.get().getUpdateTime());
                    } catch (Exception e) {
                        System.out.println("Error deleting user: " + e.getMessage());
                    }
                }, Runnable::run);

                return ResponseEntity.status(200).body(new ApiResponse<>(true, "User deleted successfully", null, null));
            } else {
                // Document does not exist
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "User not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }
}
