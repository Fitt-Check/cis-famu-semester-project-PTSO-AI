/*package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestOutfits extends AOutfits {
    @DocumentId

    private DocumentReference userId;
    private DocumentReference suggestions;

    public RestOutfits(String outfitId, String category, Timestamp createdAt, Timestamp updatedAt, String image, DocumentReference userId, DocumentReference suggestions) {
        super(outfitId, category, createdAt, updatedAt, image);
        this.userId = userId;
        this.suggestions = suggestions;
    }


}*/
