package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.cloud.FirestoreClient;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

//import static com.example.ptsoai.Model.RestHistory.OUTFITS;

@Data
@NoArgsConstructor
public class RestClassifications extends AClassifications {
    private static final String OUTFITS = "Outfits";

    private static final Firestore db = FirestoreClient.getFirestore();
    private DocumentReference outfitId;

    public RestClassifications(String category, String classificationId, int confidenceScore, Timestamp createdAt, ArrayList<String> suggestions, DocumentReference outfitId) {
        super(category, classificationId, confidenceScore, createdAt, suggestions);
        this.outfitId = outfitId;
    }

    public void setOutfitId(String id) {
        this.outfitId = db.collection(OUTFITS).document(id);
    }

}
