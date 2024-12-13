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
public class HistoryService {

    private final Firestore firestore;
    private static final String HISTORY_COLLECTION = "History";



    public HistoryService() {
        this.firestore = FirestoreClient.getFirestore();
    }

    private History documentToHistory(DocumentSnapshot document) throws ParseException, ExecutionException, InterruptedException {
        History history = new History();
        history.setHistoryId(document.getId());
        history.setCreatedAt(document.getTimestamp("createdAt"));
        if (!document.exists())
            return null;


        DocumentReference outfitRef = (DocumentReference) document.get("outfitId");
        if (outfitRef != null) {
            DocumentSnapshot outfitSnapshot = outfitRef.get().get();
            if (outfitSnapshot.exists()) {
                OutfitService service = new OutfitService();
                Outfits outfit = service.documentToOutfit(outfitSnapshot);
                history.setOutfitId(outfit);
            }
        }



        DocumentReference userRef = (DocumentReference) document.get("userId");
        if (userRef != null) {
            DocumentSnapshot userSnapshot = userRef.get().get();
            if (userSnapshot.exists()) {
                UserService service = new UserService();
                Users user = service.documentToUser(userSnapshot);
                history.setUserId(user);
            }
        }



        DocumentReference classificationsRef = (DocumentReference) document.get("classificationsId");
        if (classificationsRef != null) {
            DocumentSnapshot classificationsSnapshot = classificationsRef.get().get(); // Retrieve the snapshot
            if (classificationsSnapshot.exists()) {
                ClassificationsService service = new ClassificationsService();
                Classifications classifications = service.documentToClassifications(classificationsSnapshot);
                history.setClassificationsId(classifications);
            }
        }



        return history;

    }



    public List<History> getAllHistory() throws ExecutionException, InterruptedException {
        CollectionReference historyCollection = firestore.collection(HISTORY_COLLECTION);
        ApiFuture<QuerySnapshot> querySnapshot = historyCollection.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        List<History> history = documents.isEmpty() ? null : new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            try {
                History historyItem = documentToHistory(document);
                if (historyItem != null) {
                    history.add(historyItem);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return history;
    }

    /*public History getHistoryById(String historyId) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference historyRef = firestore.collection(HISTORY_COLLECTION).document(historyId);
        DocumentSnapshot historySnap = historyRef.get().get();
        return documentToHistory(historySnap);
    }
     */

    public History getHistoryById(String id) throws ExecutionException, InterruptedException, ParseException {
        DocumentReference docRef = firestore.collection("History").document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return documentToHistory(document);
        }
        return null;
    }



    public String createHistory(RestHistory history) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> writeResult = firestore.collection(HISTORY_COLLECTION).add(history);
        DocumentReference rs = writeResult.get();
        return rs.getId();
    }



    public String updateHistory(String historyId, RestHistory updatedHistory) throws ExecutionException, InterruptedException {
        DocumentReference historyRef = firestore.collection(HISTORY_COLLECTION).document(historyId);
        ApiFuture<WriteResult> writeResult = historyRef.set(updatedHistory);
        writeResult.get();
        return historyId;
    }


    public String deleteHistory(String historyId) throws ExecutionException, InterruptedException {
        DocumentReference historyRef = firestore.collection(HISTORY_COLLECTION).document(historyId);
        ApiFuture<WriteResult> writeResult = historyRef.delete();
        writeResult.get();
        return historyId;
    }


}
