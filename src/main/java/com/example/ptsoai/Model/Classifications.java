package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Classifications extends AClassifications {

    private Outfits outfitId;

    public Classifications(String category, String classificationId, int confidenceScore, Timestamp createdAt, ArrayList<String> suggestions, Outfits outfitId) {
        super(category, classificationId, confidenceScore, createdAt, suggestions);
        this.outfitId = outfitId;
    }
}