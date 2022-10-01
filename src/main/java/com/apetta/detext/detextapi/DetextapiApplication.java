package com.apetta.detext.detextapi;

import com.apetta.detext.detextapi.entity.TranslationObject;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class DetextapiApplication {

	// xrhsimo gia to set up tou firebase admin ston server
	// https://firebase.google.com/docs/admin/setup
	// kai h sunexeia:
	// https://firebase.google.com/docs/database/admin/start
	// kai edw: (h kai oxi...)
	// https://firebase.google.com/docs/database/rest/start

	public static void main(String[] args) throws IOException {
		ClassLoader classLoader = DetextapiApplication.class.getClassLoader();
		// Fetch the service account key JSON file contents
		File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getFile());

		// Initialize the app with a service account, granting admin privileges
		FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				// The database URL depends on the location of the database
				.setDatabaseUrl("https://detext-app-default-rtdb.firebaseio.com")
				.build();
		FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
		SpringApplication.run(DetextapiApplication.class, args);
	}
}
