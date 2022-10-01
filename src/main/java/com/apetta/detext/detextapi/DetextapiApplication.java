package com.apetta.detext.detextapi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class DetextapiApplication {

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
