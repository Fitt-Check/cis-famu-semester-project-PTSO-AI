package com.example.ptsoai.Controller;

import com.example.ptsoai.Model.Classifications;
import com.example.ptsoai.Model.RestClassifications;
import com.example.ptsoai.Service.ClassificationsService;
import com.example.ptsoai.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/classifications")
public class ClassificationsController {

    @Autowired
    private ClassificationsService classificationsService;

    /**
     * Fetch all classifications from Firestore.
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllClassifications() {
        try {
            List<Classifications> classifications = classificationsService.getAllClassifications();
            return ResponseEntity.ok(new ApiResponse(true, "Classifications fetched successfully", classifications, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to fetch classifications", null, e.getMessage()));
        }
    }

    /**
     * Fetch a specific classification by ID.
     */
    @GetMapping("/get/{classificationsId}")
    public ResponseEntity<ApiResponse> getClassificationsById(@PathVariable(name="classificationsId") String id) {
        try {
            Classifications classification = classificationsService.getClassificationsById(id);
            if (classification != null) {
                return ResponseEntity.ok(new ApiResponse(true, "Classification fetched successfully", classification, null));
            } else {
                return ResponseEntity.status(404).body(new ApiResponse(false, "Classification not found", null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to fetch classification", null, e.getMessage()));
        }
    }


    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createClassifications(@RequestBody RestClassifications restClassifications) {
        try {
            String classificationId = classificationsService.createClassifications(restClassifications);
            return ResponseEntity.ok(new ApiResponse(true, "Classification created successfully", classificationId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to create classification", null, e.getMessage()));
        }
    }


    @PutMapping("/update/{classificationsId}")
    public ResponseEntity<ApiResponse> updateClassifications(@PathVariable(name="classificationsId") String id, @RequestBody RestClassifications updatedClassifications) {
        try {
            String updatedId = classificationsService.updateClassifications(id, updatedClassifications);
            return ResponseEntity.ok(new ApiResponse(true, "Classification updated successfully", updatedId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to update classification", null, e.getMessage()));
        }
    }


    @DeleteMapping("/delete/{classificationsId}")
    public ResponseEntity<ApiResponse> deleteClassifications(@PathVariable(name="classificationsId") String id) {
        try {
            String deletedId = classificationsService.deleteClassifications(id);
            return ResponseEntity.ok(new ApiResponse(true, "Classification deleted successfully", deletedId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to delete classification", null, e.getMessage()));
        }
    }
}
