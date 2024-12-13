package com.example.ptsoai.Controller;

import com.example.ptsoai.Model.Outfits;
import com.example.ptsoai.Service.OutfitService;
import com.example.ptsoai.Service.OpenAIService; // Import your OpenAIService
import com.example.ptsoai.util.ApiResponse;
import com.example.ptsoai.util.ImageProcessingUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/outfits")
public class OutfitsController {

    private final Firestore firestore;
    private final OutfitService service;
    private final OpenAIService openAIService; // Step 2: Add the OpenAIService dependency
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public OutfitsController(OutfitService service, OpenAIService openAIService) { // Inject OpenAIService
        this.firestore = FirestoreClient.getFirestore();
        this.service = service;
        this.openAIService = openAIService; // Initialize OpenAIService
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addOutfit(@RequestBody HashMap<String, Object> outfitData) {
        logger.info("Received addOutfit request with data: {}", outfitData);

        try {
            Outfits outfit = new Outfits();
            outfit.setOutfitId((String) outfitData.get("outfitId"));
            outfit.setCategory((String) outfitData.get("category"));
            outfit.setImage((String) outfitData.get("image"));

            List<String> suggestions = (List<String>) outfitData.get("suggestions");
            if (suggestions == null) {
                suggestions = new ArrayList<>(); // Initialize empty list if null
            }
            outfit.setSuggestions(suggestions);

            outfit.setCreatedAt(Timestamp.now());
            outfit.setUpdatedAt(Timestamp.now());

            String userId = (String) outfitData.get("userIdPath");
            outfit.setUserId(userId);

            // Add the new outfit document to the "Outfits" collection
            DocumentReference docRef = firestore.collection("Outfits").document(outfit.getOutfitId());
            ApiFuture<WriteResult> result = docRef.set(outfit);
            result.get();

            logger.info("Outfit created successfully with ID: {}", outfit.getOutfitId());
            return ResponseEntity.status(201).body(new ApiResponse<>(true, "Outfit created", outfit.getOutfitId(), null));
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

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<String>> analyzeOutfit(@RequestParam("file") MultipartFile file) {
        try {
            // Step 1: Extract a description of the image (this could be metadata or text)
            String imageDescription = ImageProcessingUtil.extractDescription(file);

            // Step 2: Generate suggestions using OpenAI
            String suggestions = openAIService.getSuggestions(imageDescription);

            // Step 3: Return the suggestions in the response
            logger.info("Suggestions from OpenAI: {}", suggestions);
            return ResponseEntity.ok(new ApiResponse<>(true, "Analysis complete", suggestions, null));

        } catch (Exception e) {
            logger.error("Error analyzing outfit: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Failed to analyze image", null, e.getMessage()));
        }
    }

    @GetMapping("/get/{outfitId}")
    public ResponseEntity<ApiResponse<Outfits>> getOutfitById(@PathVariable(name = "outfitId") String outfitId) {
        try {
            DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Outfits outfit = document.toObject(Outfits.class);
                return ResponseEntity.ok(new ApiResponse<>(true, "Outfit found", outfit, null));
            } else {
                return ResponseEntity.status(204).body(new ApiResponse<>(true, "Outfit not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        }
    }

    @PutMapping("/update/{outfitId}")
    public ResponseEntity<ApiResponse<String>> updateOutfit(@PathVariable(name = "outfitId") String outfitId, @RequestBody HashMap<String, Object> outfitData) {
        try {
            Map<String, Object> updates = new HashMap<>();

            // Update only if the field exists in the request body
            if (outfitData.containsKey("category")) {
                updates.put("category", outfitData.get("category"));
            }
            if (outfitData.containsKey("image")) {
                updates.put("image", outfitData.get("image"));
            }
            if (outfitData.containsKey("suggestions")) {
                updates.put("suggestions", outfitData.get("suggestions"));
            }

            updates.put("updatedAt", Timestamp.now());

            // Update fields in the outfit document
            DocumentReference docRef = firestore.collection("Outfits").document((String) outfitData.get("outfitId"));
            ApiFuture<WriteResult> result = docRef.update(updates);
            result.get();

            return ResponseEntity.ok(new ApiResponse<>(true, "Outfit updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{outfitId}")
    public ResponseEntity<ApiResponse<String>> deleteOutfit(@PathVariable String outfitId) {
        DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
        try {
            // Check if the document exists
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Document exists, proceed with deletion
                ApiFuture<WriteResult> result = docRef.delete();

                result.addListener(() -> {
                    try {
                        System.out.println("Outfit deleted at: " + result.get().getUpdateTime());
                    } catch (Exception e) {
                        System.out.println("Error deleting outfit: " + e.getMessage());
                    }
                }, Runnable::run);

                return ResponseEntity.status(200).body(new ApiResponse<>(true, "Outfit deleted successfully", null, null));
            } else {
                // Document does not exist
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "Outfit not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }
}















/*package com.example.ptsoai.Controller;
//class for the outfits service with reference

import com.example.ptsoai.Model.RestOutfits;
import com.example.ptsoai.Service.OutfitService;
import com.example.ptsoai.util.ApiResponse;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.ExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/outfits")
public class OutfitsController {

    private final Firestore firestore;
    private final OutfitService service;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public OutfitsController(OutfitService service) {
        this.firestore = FirestoreClient.getFirestore();
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addOutfit(@RequestBody HashMap<String, Object> outfitData) {
        logger.info("Received addOutfit request with data: {}", outfitData);

        try {
            RestOutfits outfit = new RestOutfits();
            outfit.setOutfitId((String) outfitData.get("outfitId"));
            outfit.setCategory((String) outfitData.get("category"));
            outfit.setImage((String) outfitData.get("image"));

            // Validate and parse suggestions
            List<DocumentReference> suggestions = (List<DocumentReference>) outfitData.get("suggestions");
            outfit.setSuggestions((DocumentReference) suggestions);

            outfit.setCreatedAt(Timestamp.now());
            outfit.setUpdatedAt(Timestamp.now());

            // Create a reference to the user document
            DocumentReference userRef = firestore.document((String) outfitData.get("userIdPath"));
            outfit.setUserId(userRef);

            // Add the new outfit document to the "Outfits" collection
            DocumentReference docRef = firestore.collection("Outfits").document(outfit.getOutfitId());
            ApiFuture<WriteResult> result = docRef.set(outfit);
            result.get();

            logger.info("Outfit created successfully with ID: {}", outfit.getOutfitId());
           return ResponseEntity.status(201).body(new ApiResponse<>(true, "Outfit created", outfit.getOutfitId(), null));
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

    @GetMapping("/get/{outfitId}")
    public ResponseEntity<ApiResponse<RestOutfits>> getOutfitById(@PathVariable(name = "outfitId") String outfitId) {
        try {
            DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                RestOutfits outfit = document.toObject(RestOutfits.class);
                return ResponseEntity.ok(new ApiResponse<>(true, "Outfit found", outfit, null));
            } else {
                return ResponseEntity.status(204).body(new ApiResponse<>(true, "Outfit not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateOutfit(@RequestBody HashMap<String, Object> outfitData) {
        DocumentReference docRef = firestore.collection("Outfits").document((String) outfitData.get("outfitId"));

        try {
            // Create a map to hold the fields to update
            Map<String, Object> updates = new HashMap<>();
            updates.put("category", outfitData.get("category"));
            updates.put("image", outfitData.get("image"));
            updates.put("suggestions", outfitData.get("suggestions"));
            updates.put("updatedAt", Timestamp.now());

            // Update fields in the outfit document
            ApiFuture<WriteResult> result = docRef.update(updates);
            result.get();

            return ResponseEntity.status(200).body(new ApiResponse<>(true, "Outfit updated", (String) outfitData.get("outfitId"), null));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }

    @DeleteMapping("/delete/{outfitId}")
    public ResponseEntity<ApiResponse<String>> deleteOutfit(@PathVariable String outfitId) {
        DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
        try {
            // Check if the document exists
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Document exists, proceed with deletion
                ApiFuture<WriteResult> result = docRef.delete();

                result.addListener(() -> {
                    try {
                        System.out.println("Outfit deleted at: " + result.get().getUpdateTime());
                    } catch (Exception e) {
                        System.out.println("Error deleting outfit: " + e.getMessage());
                    }
                }, Runnable::run);

                return ResponseEntity.status(200).body(new ApiResponse<>(true, "Outfit deleted successfully", null, null));
            } else {
                // Document does not exist
                return ResponseEntity.status(404).body(new ApiResponse<>(false, "Outfit not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }
}
*/











/*package com.example.ptsoai.Controller;

import com.example.ptsoai.Model.RestOutfits;
import com.example.ptsoai.Model.Outfits;
import com.example.ptsoai.Service.OutfitService;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/api/outfits")
public class OutfitsController {
    private final Firestore firestore;
    private final OutfitService service;
    private static final Logger logger = LoggerFactory.getLogger(OutfitsController.class);

    public OutfitsController(OutfitService service) {
        this.firestore = FirestoreClient.getFirestore();
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addOutfit(@RequestBody HashMap<String, Object> outfitData) {
        logger.info("Received addOutfit request with data: {}", outfitData);

        try {
            RestOutfits outfit = new RestOutfits();
            outfit.setOutfitId((String) outfitData.get("outfitId"));
            outfit.setCategory((String) outfitData.get("category"));
            outfit.setImage((String) outfitData.get("image"));

            // Validate and parse suggestions


            outfit.setCreatedAt(Timestamp.now());
            outfit.setUpdatedAt(Timestamp.now());

            // Create a reference to the user document
            DocumentReference userRef = firestore.document((String) outfitData.get("userIdPath"));
            outfit.setUserId(userRef);

            // Add the new outfit document to the "Outfits" collection
            DocumentReference docRef = firestore.collection("Outfits").document(outfit.getOutfitId());
            ApiFuture<WriteResult> result = docRef.set(outfit);
            result.get();

            logger.info("Outfit created successfully with ID: {}", outfit.getOutfitId());
            return ResponseEntity.status(201).body(new ApiResponse<>(true, "Outfit created", outfit.getOutfitId(), null));
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

    @GetMapping("/get/{outfitId}")
    public ResponseEntity<ApiResponse<RestOutfits>> getOutfitById(@PathVariable(name = "outfitId") String outfitId) {
        try {
            DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                RestOutfits outfit = document.toObject(RestOutfits.class);
                return ResponseEntity.ok(new ApiResponse<>(true, "Outfit found", outfit, null));
            } else {
                return ResponseEntity.status(204).body(new ApiResponse<>(true, "Outfit not found", null, null));
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateOutfit(@RequestBody HashMap<String, Object> outfitData) {
        DocumentReference docRef = firestore.collection("Outfits").document((String) outfitData.get("outfitId"));

        try {
            // Create a map to hold the fields to update
            Map<String, Object> updates = new HashMap<>();
            updates.put("category", outfitData.get("category"));
            updates.put("image", outfitData.get("image"));
            updates.put("suggestions", outfitData.get("suggestions"));
            updates.put("updatedAt", Timestamp.now());

            // Update fields in the outfit document
            ApiFuture<WriteResult> result = docRef.update(updates);
            result.get();

            return ResponseEntity.status(200).body(new ApiResponse<>(true, "Outfit updated", (String) outfitData.get("outfitId"), null));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Internal Server error", null, e));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Unexpected error", null, e));
        }
    }

    @DeleteMapping("/delete/{outfitId}")
    public ResponseEntity<ApiResponse<String>> deleteOutfit(@PathVariable String outfitId) {
        DocumentReference docRef = firestore.collection("Outfits").document(outfitId);
        try {
            // Check if the document exists
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Document exists */
