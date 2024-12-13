package com.example.ptsoai.Service;

import com.example.ptsoai.Model.Classifications;
import com.example.ptsoai.Model.History;
import com.example.ptsoai.Model.Outfits;
import com.example.ptsoai.Model.Users;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;
import com.google.firebase.cloud.FirestoreClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final Firestore firestore;
    private static final String USERS_COLLECTION = "User";
    private static final String CLASSIFICATION_COLLECTION = "Classification";
    private static final String HISTORY_COLLECTION = "History";
    private static final String OUTFITS_COLLECTION = "Outfits";

    public UserService(){
        this.firestore = FirestoreClient.getFirestore();
    }

    public Users documentToUser(DocumentSnapshot document) throws ParseException {
        if (!document.exists()) {
            return null;
        }
        return new Users(
                document.getId(),
                document.getString("passwordHash"),
                document.getString("email"),
                document.getString("profileImage"),
                document.getString("username"),
                document.getTimestamp("createdAt"),
                null
        );
    }

    public List<Users> getAllUsers() throws ExecutionException, InterruptedException {
        CollectionReference usersCollection = firestore.collection(USERS_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = usersCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<Users> users = documents.isEmpty() ? null : new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                Users user = documentToUser(document);
                if (user != null) {
                    users.add(user);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }

    /*public Users getUserById(String userId) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference usersRef = firestore.collection(USERS_COLLECTION).document(userId);
        DocumentSnapshot userSnap = usersRef.get().get();
        return documentToUser(userSnap);
    } */
    public Users getUserById(String userId) throws ExecutionException, InterruptedException {
        DocumentReference usersRef = firestore.collection(USERS_COLLECTION).document(userId);

        // Log the user ID being queried
        System.out.println("Fetching user with ID: " + userId);

        DocumentSnapshot userSnap = usersRef.get().get();

        // Log the snapshot status
        System.out.println("Document exists: " + userSnap.exists());

        if (userSnap.exists()) {
            try {
                // Log the data retrieved
                System.out.println("User data: " + userSnap.getData());

                return documentToUser(userSnap);
            } catch (ParseException e) {
                e.printStackTrace(); // Log the exception
                System.out.println("Error parsing user data");
                return null;
            }
        } else {
            System.out.println("User not found with ID: " + userId);
            return null;
        }
    }



    public List<Users> getUserByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(USERS_COLLECTION).whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Users> users = documents.isEmpty() ? null : new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                Users user = documentToUser(document);
                if (user != null) {
                    users.add(user);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }

    public String createUser(Users user) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(USERS_COLLECTION).add(user);
        DocumentReference rs = writeResult.get();
        return rs.getId();
    }

    public String createClassification(Classifications classification) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(CLASSIFICATION_COLLECTION).add(classification);
        return writeResult.get().getId();
    }

    public String createHistory(History history) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(HISTORY_COLLECTION).add(history);
        return writeResult.get().getId();
    }

    public String createOutfit(Outfits outfit) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(OUTFITS_COLLECTION).add(outfit);
        return writeResult.get().getId();
    }


}