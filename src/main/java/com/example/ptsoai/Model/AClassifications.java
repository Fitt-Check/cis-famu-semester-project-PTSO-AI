package com.example.ptsoai.Model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.protobuf.util.Timestamps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AClassifications {

    private String category;
    @DocumentId
    private String classificationId;
    private int confidenceScore;
    private Timestamp createdAt;
    private ArrayList<String> suggestions;


    public void setCreatedAt(String createdAt) throws ParseException {

        this.createdAt = Timestamp.fromProto(Timestamps.parse(createdAt));
    }

    public void setCreatedAtTimestamp(Timestamp createdAt) {
        this.createdAt = createdAt;
    }




}