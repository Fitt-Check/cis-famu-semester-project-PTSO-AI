package com.example.ptsoai.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class History extends AHistory{

    private Classifications classificationId;
    private Outfits outfitId;
    private Users userId;

    public History(Classifications classificationId, Outfits outfitId, Users userId) {
        this.classificationId = classificationId;
        this.outfitId = outfitId;
        this.userId = userId;
    }

    public void setClassificationsId(Classifications classificationId) {
        this.classificationId = classificationId;
    }

    public void add(History history) {
    }
}
