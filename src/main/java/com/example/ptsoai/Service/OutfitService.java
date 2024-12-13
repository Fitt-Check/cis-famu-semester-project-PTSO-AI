package com.example.ptsoai.Service;
//userId and Suggestions as strings not references


//import com.example.ptsoai.Model.RestOutfits;
import com.example.ptsoai.Model.Outfits;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

        import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OutfitService {

    private final Firestore firestore;
    private static final String OUTFITS_COLLECTION = "Outfits";
    private static final String USERS_COLLECTION = "User";

    public OutfitService() {
        this.firestore = FirestoreClient.getFirestore();
    }


     // Convert Firestore document to Outfits object.
    public Outfits documentToOutfit(DocumentSnapshot document) throws ExecutionException, InterruptedException {
        if (!document.exists()) {
            return null;
        }

        String userId = document.getString("userId");

        List<String> suggestions = (List<String>) document.get("suggestions");
        if (suggestions == null) {
            suggestions = new ArrayList<>();  // Initialize empty list if null
        }


        return new Outfits(
                document.getId(),
                document.getString("category"),
                document.getTimestamp("createdAt"),
                document.getTimestamp("updatedAt"),
                document.getString("image"),
                userId,
                suggestions
        );



    }



    /*private Outfits documentToOutfit(DocumentSnapshot document) throws ExecutionException, InterruptedException {
        if (!document.exists()) {
            return null;
        }

        // Retrieve User reference
        DocumentReference userRef = (DocumentReference) document.get("userId");
        Users user = null;
        if (userRef != null) {
            DocumentSnapshot userSnapshot = userRef.get().get();
            user = userSnapshot.toObject(Users.class);
        }

        // Retrieve Suggestions
        List<String> suggestions = (List<String>) document.get("suggestions");

        return new Outfits(
                document.getId(),
                document.getString("category"),
                document.getTimestamp("createdAt"),
                document.getTimestamp("updatedAt"),
                document.getString("image"),
                user,
                suggestions
        );
    }*/


     // Get all outfits from Firestore.
    public List<Outfits> getAllOutfits() throws ExecutionException, InterruptedException {
        CollectionReference outfitsCollection = firestore.collection(OUTFITS_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = outfitsCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        List<Outfits> outfits = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Outfits outfit = documentToOutfit(document);
            if (outfit != null) {
                outfits.add(outfit);
            }
        }
        return outfits;
    }


     //Get a specific outfit by ID.
    public Outfits getOutfitById(String outfitId) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        DocumentSnapshot outfitSnap = outfitRef.get().get();
        return documentToOutfit(outfitSnap);
    }


     // Create a new outfit in Firestore.
    public String createOutfit(Outfits Outfit) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(OUTFITS_COLLECTION).add(Outfit);
        return writeResult.get().getId();
    }



     // Update an existing outfit.
    public String updateOutfit(String outfitId, Outfits updatedOutfit) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        ApiFuture<WriteResult> writeResult = outfitRef.set(updatedOutfit, SetOptions.merge());
        writeResult.get();
        return outfitId;
    }


     // Delete an outfit by ID
    public String deleteOutfit(String outfitId) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        ApiFuture<WriteResult> writeResult = outfitRef.delete();
        writeResult.get();  // Wait for the delete operation to complete
        return outfitId;
    }
}

















/*
//Outfit service with userId and suggestions as references
package com.example.ptsoai.Service;

import com.example.ptsoai.Model.History;
import com.example.ptsoai.Model.Outfits;
import com.example.ptsoai.Model.RestOutfits;
import com.example.ptsoai.Model.Users;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OutfitService {

    private final Firestore firestore;
    private static final String OUTFITS_COLLECTION = "Outfits";
    private static final String USERS_COLLECTION = "User";

    public OutfitService() {
        this.firestore = FirestoreClient.getFirestore();
    }


    // Convert Firestore document to Outfits object.

    public Outfits documentToOutfit(DocumentSnapshot document) throws ExecutionException, InterruptedException {
        if (!document.exists()) {
            return null;
        }

        // Retrieve User reference
        DocumentReference userRef = (DocumentReference) document.get("userId");
        Users user = null;
        if (userRef != null) {
            DocumentSnapshot userSnapshot = userRef.get().get();
            user = userSnapshot.toObject(Users.class);
        }

        // Explicitly cast `suggestions` to List<String>
        List<String> suggestions = (List<String>) document.get("suggestions");
        if (suggestions == null) {
            suggestions = new ArrayList<>();  // Initialize empty list if null
        }

        return new Outfits(
                document.getId(),
                document.getString("category"),
                document.getTimestamp("createdAt"),
                document.getTimestamp("updatedAt"),
                document.getString("image"),
                user,
                suggestions
        );
    }



    private Outfits documentToOutfit(DocumentSnapshot document) throws ExecutionException, InterruptedException {
        if (!document.exists()) {
            return null;
        }

        // Retrieve User reference
        DocumentReference userRef = (DocumentReference) document.get("userId");
        Users user = null;
        if (userRef != null) {
            DocumentSnapshot userSnapshot = userRef.get().get();
            user = userSnapshot.toObject(Users.class);
        }

        // Retrieve Suggestions
        List<String> suggestions = (List<String>) document.get("suggestions");

        return new Outfits(
                document.getId(),
                document.getString("category"),
                document.getTimestamp("createdAt"),
                document.getTimestamp("updatedAt"),
                document.getString("image"),
                user,
                suggestions
        );
    }




     //Get all outfits from Firestore.

    public List<Outfits> getAllOutfits() throws ExecutionException, InterruptedException {
        CollectionReference outfitsCollection = firestore.collection(OUTFITS_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = outfitsCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

        List<Outfits> outfits = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Outfits outfit = documentToOutfit(document);
            if (outfit != null) {
                outfits.add(outfit);
            }
        }
        return outfits;
    }


    // Get a specific outfit by ID.

    public Outfits getOutfitById(String outfitId) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        DocumentSnapshot outfitSnap = outfitRef.get().get();
        return documentToOutfit(outfitSnap);
    }


     // Create a new outfit in Firestore.

    public String createOutfit(RestOutfits restOutfit) throws ExecutionException, InterruptedException {
        DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(restOutfit.getUserId());
        restOutfit.setUserId(userRef.getPath());
        ApiFuture<DocumentReference> writeResult = firestore.collection(OUTFITS_COLLECTION).add(restOutfit);
        return writeResult.get().getId();
    }



     // Update an existing outfit.

    public String updateOutfit(String outfitId, RestOutfits updatedOutfit) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        updatedOutfit.setUserId(firestore.collection(USERS_COLLECTION).document(updatedOutfit.getUserId()).getPath());
        ApiFuture<WriteResult> writeResult = outfitRef.set(updatedOutfit, SetOptions.merge());
        writeResult.get();  // Wait for the write operation to complete
        return outfitId;
    }




     // Delete an outfit by ID.

    public String deleteOutfit(String outfitId) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        ApiFuture<WriteResult> writeResult = outfitRef.delete();
        writeResult.get();  // Wait for the delete operation to complete
        return outfitId;
    }
}*/















/*package com.example.ptsoai.Service;

import com.example.ptsoai.Model.History;
import com.example.ptsoai.Model.Outfits;
import com.example.ptsoai.Model.RestOutfits;
import com.example.ptsoai.Model.Users;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class OutfitService {

    private final Firestore firestore;
    private static final String OUTFITS_COLLECTION = "Outfits";


    public OutfitService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    private Outfits documentToOutfit(DocumentSnapshot document) throws ParseException {
        if (!document.exists()) {
            return null;
        }
        return new Outfits(
                document.getId(),
                document.getString("category"),
                document.getTimestamp("createdAt"),
                document.getTimestamp("updatedAt"),
                document.getString("image"),
                document.toObject(Users.class),  // Assuming the Users class can be directly mapped
                (List<Outfits>) document.get("suggestions") // Assuming suggestions is a list of Outfits
        );
    }

    public List<Outfits> getAllOutfits() throws ExecutionException, InterruptedException {
        CollectionReference outfitsCollection = firestore.collection(OUTFITS_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = outfitsCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Outfits> outfits = documents.isEmpty() ? null : new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                Outfits outfit = documentToOutfit(document);
                if (outfit != null) {
                    outfits.add(outfit);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return outfits;
    }

    public Outfits getOutfitById(String outfitId) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        DocumentSnapshot outfitSnap = outfitRef.get().get();
        return documentToOutfit(outfitSnap);
    }


    public String createOutfit(RestOutfits outfit) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(OUTFITS_COLLECTION).add(outfit);
        return writeResult.get().getId();
    }

    public String readOutfits(String outfitsId, Outfits readOutfits) throws ExecutionException, InterruptedException {
        DocumentReference historyRef = firestore.collection(OUTFITS_COLLECTION).document(outfitsId);
        ApiFuture<WriteResult> writeResult = historyRef.set(readOutfits);
        writeResult.get();
        return outfitsId;
    }

    public String updateOutfit(String outfitId, RestOutfits updatedOutfit) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        ApiFuture<WriteResult> writeResult = outfitRef.set(updatedOutfit);
        writeResult.get();  // Wait for the write operation to complete
        return outfitId;
    }

    public String deleteOutfit(String outfitId) throws ExecutionException, InterruptedException {
        DocumentReference outfitRef = firestore.collection(OUTFITS_COLLECTION).document(outfitId);
        ApiFuture<WriteResult> writeResult = outfitRef.delete();
        writeResult.get();  // Wait for the delete operation to complete
        return outfitId;
    }
}

*/