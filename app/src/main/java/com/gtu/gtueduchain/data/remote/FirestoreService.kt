package com.gtu.gtueduchain.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val db = FirebaseFirestore.getInstance()
    private val degreesCollection = db.collection("degrees")

    suspend fun saveDegree(
        name: String,
        enrollment: String,
        branch: String,
        cpi: String,
        date: String,
        hash: String
    )
    {
        val degreeMap = hashMapOf(
            "name" to name,
            "enrollment" to enrollment,
            "branch" to branch,
            "cpi" to cpi,
            "date" to date,
            "hash" to hash,
            "timestamp" to System.currentTimeMillis()
        )

        degreesCollection
            .document(enrollment)
            .set(degreeMap)
            .await()
    }

    suspend fun getDegree(enrollment: String): Map<String, Any>? {
        val snapshot = degreesCollection
            .document(enrollment)
            .get()
            .await()

        return if (snapshot.exists()) snapshot.data else null
    }
}
