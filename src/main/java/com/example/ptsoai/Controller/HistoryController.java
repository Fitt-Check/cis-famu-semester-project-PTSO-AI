package com.example.ptsoai.Controller;

import com.example.ptsoai.Model.History;
import com.example.ptsoai.Model.RestHistory;
import com.example.ptsoai.Service.HistoryService;
import com.example.ptsoai.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    // GET: Retrieve all history records
    @GetMapping
    public ResponseEntity<ApiResponse> getAllHistory() {
        try {
            List<History> historyList = historyService.getAllHistory();
            return ResponseEntity.ok(new ApiResponse(true, "Fetched all history records successfully", historyList, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error fetching history records", null, e.getMessage()));
        }
    }

    // GET: Retrieve a specific history record by ID
    @GetMapping("/get/{historyId}")
    public ResponseEntity<ApiResponse> getHistoryById(@PathVariable String historyId) {
        try {
            History history = historyService.getHistoryById(historyId);
            if (history != null) {
                return ResponseEntity.ok(new ApiResponse(true, "History record found", history, null));
            } else {
                return ResponseEntity.status(404).body(new ApiResponse(false, "History record not found", null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error retrieving history record", null, e.getMessage()));
        }
    }




    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createHistory(@RequestBody RestHistory restHistory) {
        try {
            String historyId = historyService.createHistory(restHistory);
            return ResponseEntity.ok(new ApiResponse(true, "History record created successfully", historyId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error creating history record", null, e.getMessage()));
        }
    }




    @PutMapping("/update/{historyId}")
    public ResponseEntity<ApiResponse> updateHistory(@PathVariable String historyId, @RequestBody RestHistory updatedHistory) {
        try {
            String updatedId = historyService.updateHistory(historyId, updatedHistory);
            return ResponseEntity.ok(new ApiResponse(true, "History record updated successfully", updatedId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error updating history record", null, e.getMessage()));
        }
    }




    @DeleteMapping("/delete/{historyId}")
    public ResponseEntity<ApiResponse> deleteHistory(@PathVariable String historyId) {
        try {
            String deletedId = historyService.deleteHistory(historyId);
            return ResponseEntity.ok(new ApiResponse(true, "History record deleted successfully", deletedId, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error deleting history record", null, e.getMessage()));
        }
    }
}
