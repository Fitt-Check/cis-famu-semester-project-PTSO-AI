package com.example.ptsoai.Model;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.firebase.cloud.FirestoreClient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestHistory extends AHistory {
    private static final String USERS = "Users";
    private static final String OUTFITS = "Outfits";
    private static final String CLASSIFICATIONS = "Classifications";

    private static final Firestore db = FirestoreClient.getFirestore();
    private DocumentReference classificationId;
    private DocumentReference outfitId;
    private DocumentReference userId;

    public RestHistory(DocumentReference classificationId, DocumentReference outfitId, DocumentReference userId) {
        this.classificationId = classificationId;
        this.outfitId = outfitId;
        this.userId = userId;
    }


    public void setUserId(String id) {
        //Firestore db = FirestoreClient.getFirestore();
        this.userId = db.collection(USERS).document(id);
    }

    public void setOutfitId(String id) {
        this.outfitId = db.collection(OUTFITS).document(id);
    }

    public void setClassificationId(String id) {
        this.classificationId = db.collection(CLASSIFICATIONS).document(id);
    }


}