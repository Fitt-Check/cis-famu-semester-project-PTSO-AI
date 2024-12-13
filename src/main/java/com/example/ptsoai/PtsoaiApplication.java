package com.example.ptsoai;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;


import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PtsoaiApplication {

    public static void main(String[] args) {
        try {
            FileInputStream serviceAccount = new FileInputStream("C:/Users/Owner/OneDrive/Documents/GitHub/e-commerce-website-Kearrah/PTSOAI/src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SpringApplication.run(PtsoaiApplication.class, args);
    }

}
