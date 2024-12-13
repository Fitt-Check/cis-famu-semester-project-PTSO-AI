package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Outfits {
    @DocumentId
    private String outfitId;
    private String category;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String image;
    private String userId;
    private List<String> suggestions;




    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


}




/*package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Outfits extends AOutfits {

    private Users userId;
    private List<String> suggestions;

   /* public Outfits(String outfitId, String category, Timestamp createdAt, Timestamp updatedAt, String image, Users userId, List<String> suggestions) {
        super(outfitId, category, createdAt, updatedAt, image);
        this.userId = userId;
        this.suggestions = suggestions;
    }

    public Outfits(String id, String category, Timestamp createdAt, Timestamp updatedAt, String image, String userId, List<String> suggestions) {
    }
}*/