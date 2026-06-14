package com.example.a208464_zahin_drnazatul_project2

import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = Firebase.firestore.apply {
        // Enable persistence so data syncs automatically once the date/SSL issue is fixed
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build())
            .build()
    }
    private val collection = db.collection("applications")

    suspend fun uploadApplication(application: ApplicationEntity): Result<Unit> {
        return try {
            // Create a clean ID: name_job (remove invalid characters)
            val docId = "${application.applicantName}_${application.jobTitle}"
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "_")

            val data = hashMapOf(
                "jobTitle"       to application.jobTitle,
                "company"        to application.company,
                "applicantName"  to application.applicantName,
                "applicantEmail" to application.applicantEmail,
                "coverNote"      to application.coverNote,
                "status"         to application.status,
                "uploadedAt"     to com.google.firebase.Timestamp.now()
            )

            collection.document(docId).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Catching the SSL error here
            Result.failure(e)
        }
    }

    suspend fun deleteApplication(application: ApplicationEntity): Result<Unit> {
        return try {
            val docId = "${application.applicantName}_${application.jobTitle}"
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "_")
            collection.document(docId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
