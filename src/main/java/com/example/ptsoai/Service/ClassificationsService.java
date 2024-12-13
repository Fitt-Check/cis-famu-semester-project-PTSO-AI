package com.example.ptsoai.Service;

import com.example.ptsoai.Model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClassificationsService {

    private final Firestore firestore;
    private static final String USERS_COLLECTION = "User";
    private static final String CLASSIFICATION_COLLECTION = "Classifications";
    private static final String HISTORY_COLLECTION = "History";
    private static final String OUTFITS_COLLECTION = "Outfits";

    public ClassificationsService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    /**
     * Converts a Firestore DocumentSnapshot to a Classifications object.
     */
    public Classifications documentToClassifications(DocumentSnapshot document) throws ParseException, ExecutionException, InterruptedException {
        Classifications classifications = new Classifications();
        if (!document.exists()) {
            return null;
        }



        // Initialize Classifications object
        classifications.setClassificationId(document.getId());
        classifications.setCategory(document.getString("category"));
        classifications.setConfidenceScore(Math.toIntExact(document.getLong("confidenceScore")));
        classifications.setCreatedAtTimestamp(document.getTimestamp("createdAt"));
        classifications.setSuggestions((ArrayList<String>) document.get("suggestions"));


        // Handle outfitId reference
        DocumentReference outfitRef = (DocumentReference) document.get("outfitId");
        if (outfitRef != null) {
            DocumentSnapshot outfitSnapshot = outfitRef.get().get();
            if (outfitSnapshot.exists()) {
                OutfitService outfitService = new OutfitService();
                Outfits outfit = outfitService.documentToOutfit(outfitSnapshot);
                classifications.setOutfitId(outfit);
            }
        }

        return classifications;
    }

    /**
     * Retrieves all Classifications from Firestore.
     */
    public List<Classifications> getAllClassifications() throws ExecutionException, InterruptedException {
        CollectionReference classificationsCollection = firestore.collection(CLASSIFICATION_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = classificationsCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Classifications> classificationsList = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                Classifications classification = documentToClassifications(document);
                if (classification != null) {
                    classificationsList.add(classification);
                }
            } catch (ParseException e) {
                throw new RuntimeException("Error parsing Classifications document", e);
            }
        }
        return classificationsList;
    }


     // Retrieves a single Classifications by its ID.
    public Classifications getClassificationsById(String classificationsId) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        DocumentSnapshot classificationsSnap = classificationsRef.get().get();
        return documentToClassifications(classificationsSnap);
    }

    // Creates a new Classifications document in Firestore.
    public String createClassifications(RestClassifications restClassifications) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(CLASSIFICATION_COLLECTION).add(restClassifications);
        return writeResult.get().getId();
    }

    // Updates an existing Classifications document in Firestore.
    public String updateClassifications(String classificationsId, RestClassifications updatedClassifications) throws ExecutionException, InterruptedException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        ApiFuture<WriteResult> writeResult = classificationsRef.set(updatedClassifications);
        writeResult.get();
        return classificationsId;
    }


    // Deletes a Classifications document from Firestore.
    public String deleteClassifications(String classificationsId) throws ExecutionException, InterruptedException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        ApiFuture<WriteResult> writeResult = classificationsRef.delete();
        writeResult.get();
        return classificationsId;
    }
}












/*package com.example.ptsoai.Service;

import com.example.ptsoai.Model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClassificationsService {

    private final Firestore firestore;
    private static final String USERS_COLLECTION = "Users";
    private static final String CLASSIFICATION_COLLECTION = "Classification";
    private static final String HISTORY_COLLECTION = "History";
    private static final String OUTFITS_COLLECTION = "Outfits";

    public ClassificationsService(){
        this.firestore = FirestoreClient.getFirestore();
    }




    private Classifications documentToClassifications(DocumentSnapshot document) throws ParseException {
        if (!document.exists()) {
            return null;
        }
        return new Classifications(
                document.getId(),
                document.getString("category"),
                document.getNumber("confidenceScore"),
                document.get("outfitId"),
                document.getArray("suggestions"),
                document.getTimestamp("createdAt"),
        );
    }



    private Classifications documentToClassifications(DocumentSnapshot document) throws ParseException, ExecutionException, InterruptedException {
        Classifications classifications = new Classifications();
        if (!document.exists()) {
            return null;
        }

        DocumentReference outfitRef = (DocumentReference) document.get("outfitId");
        if (outfitRef != null) {
            DocumentSnapshot outfitSnapshot = outfitRef.get().get();
            if (outfitSnapshot.exists()) {
                OutfitService service = new OutfitService();
                Outfits outfit = service.documentToOutfit(outfitSnaphot);
                classifications.setOutfitId(outfit);
            }
        }

        return calssifications;

    }







    public List<Classifications> getAllClassifications() throws ExecutionException, InterruptedException {
        CollectionReference classificationsCollection = firestore.collection(CLASSIFICATION_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = classificationsCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Users> users = documents.isEmpty() ? null : new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                Classifications classifications = documentToClassifications(document);
                if (classifications != null) {
                    classifications.add(classifications);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return classifications;
    }

    public Users getClassificationsById(String classificationsId) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        DocumentSnapshot classificationsSnap = classificationsRef.get().get();
        return documentToClassifications(classificationsSnap);
    }

    public String createClassifications(RestClassifications classifications) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(CLASSIFICATION_COLLECTION).add(classifications);
        DocumentReference rs = writeResult.get();
        return rs.getId();
    }

    public String readClassifications(String classificationsId, Classifications readClassifications) throws ExecutionException, InterruptedException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        ApiFuture<WriteResult> writeResult = classificationsRef.set(readClassifications);
        writeResult.get();
        return classificationsId;
    }

    public String updateClassifications(String classificationsId, RestClassifications updatedClassifications) throws ExecutionException, InterruptedException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        ApiFuture<WriteResult> writeResult = classificationsRef.set(updatedClassifications);
        writeResult.get();
        return classificationsId;
    }


    public String deleteClassifications(String classificationsId) throws ExecutionException, InterruptedException {
        DocumentReference classificationsRef = firestore.collection(CLASSIFICATION_COLLECTION).document(classificationsId);
        ApiFuture<WriteResult> writeResult = classificationsRef.delete();
        writeResult.get();
        return classificationsId;
    }


}
 */


