// com.example.gymapp.data.FirebaseManager.kt
package com.example.gymapp.model.gestores

import android.util.Log
import com.example.gymapp.model.entity.Ejercicio
import com.example.gymapp.model.entity.Serie
import com.example.gymapp.model.entity.Workout
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Obtiene los ejercicios de un workout específico
     * @param idWorkout ID del workout
     * @return Lista de ejercicios o null si no se encuentran
     */
    suspend fun obtenerEjerciciosPorWorkout(idWorkout: String?): List<Ejercicio>? {
        return try {
            // Ruta: GymElorrietaBD/gym_01/Workouts/{idWorkout}/Ejercicios
            val ejerciciosRef = db.collection(Constants.COLLECTION_GYM)
                .document(Constants.DOCUMENTO_GYM)
                .collection(Constants.COLLECTION_WORKOUT)
                .document(idWorkout.toString())
                .collection(Constants.COLLECTION_EJERCICIO)

            val querySnapshot: QuerySnapshot = ejerciciosRef.get().await()
            val documentos = querySnapshot.documents

            if (documentos.isEmpty()) {
                println("Ejercicios no encontrados para workout: $idWorkout")
                return emptyList() // Mejor devolver lista vacía que null
            }

            val listaEjercicios = mutableListOf<Ejercicio>()

            for (ejercicioDoc in documentos) {
                // Obtener series del ejercicio
                val seriesSnapshot: QuerySnapshot = ejercicioDoc.reference
                    .collection(Constants.COLLECTION_SERIE)
                    .get()
                    .await()

                val series = seriesSnapshot.documents.map { serieDoc ->
                    Serie(
                        id = serieDoc.id,
                        nombre = serieDoc.getString(Constants.NOMBRE) ?: "",
                        tiempoDuracion = serieDoc.getString(Constants.TIEMPO_DURACION),
                        tiempoDescanso = serieDoc.getString(Constants.TIEMPO_DESCANSO),
                        completado = serieDoc.getBoolean(Constants.COMPLETADO) ?: false
                    )
                }

                val ejercicio = Ejercicio(
                    id = ejercicioDoc.id,
                    nombre = ejercicioDoc.getString(Constants.NOMBRE) ?: "",
                    descripcion = ejercicioDoc.getString(Constants.DESCRIPCION) ?: "",
                    completado = ejercicioDoc.getBoolean(Constants.COMPLETADO) ?: false,
                    series = series.toMutableList()
                )

                listaEjercicios.add(ejercicio)
            }

            listaEjercicios
        } catch (e: Exception) {
            throw FireBaseException("Error al obtener ejercicios: ${e.localizedMessage}")
        }
    }

    /**
     * Obtiene todos los workouts
     */
    suspend fun obtenerWorkouts(): List<Workout> {
        return try {
            val workoutsRef = db.collection(Constants.COLLECTION_GYM)
                .document(Constants.DOCUMENTO_GYM)
                .collection(Constants.COLLECTION_WORKOUT)

            val querySnapshot = workoutsRef.get().await()

            querySnapshot.documents.map { doc ->
                val workout = doc.toObject(Workout::class.java)
                    ?: Workout()
                workout.id = doc.id
                workout
            }
        } catch (e: Exception) {
            throw FireBaseException("Error al obtener workouts: ${e.localizedMessage}")
        }
    }

    fun agregarWorkoutConId(workout: Workout) {
        db.collection(Constants.COLLECTION_GYM)
            .document(Constants.DOCUMENTO_GYM)
            .collection(Constants.COLLECTION_WORKOUT)
            .document(workout.id)
            .set(workout)
            .addOnFailureListener { e ->
                throw FireBaseException("Error al guardar workout: ${e.message}")
            }
    }

    suspend fun obtenerSiguienteIdWorkout(): String {
        return try {
            val snapshot = db.collection(Constants.COLLECTION_GYM)
                .document(Constants.DOCUMENTO_GYM)
                .collection(Constants.COLLECTION_WORKOUT)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (snapshot.isEmpty) {
                "W1"
            } else {
                val ultimoId = snapshot.documents[0].id
                val numero = ultimoId.replace(Regex("\\D+"), "").toIntOrNull() ?: 0
                "W${numero + 1}"
            }
        } catch (e: Exception) {
            throw FireBaseException("Error al obtener ID de workout: ${e.localizedMessage}")
        }
    }

    suspend fun modificarWorkout(workout: Workout) {
        try {
            db.collection(Constants.COLLECTION_GYM)
                .document(Constants.DOCUMENTO_GYM)
                .collection(Constants.COLLECTION_WORKOUT)
                .document(workout.id)
                .set(workout)
                .await()
        } catch (e: Exception) {
            throw FireBaseException("Error al modificar workout: ${e.localizedMessage}")
        }
    }

    suspend fun eliminarWorkoutPorId(workoutId: String) {
        try {
            db.collection(Constants.COLLECTION_GYM)
                .document(Constants.DOCUMENTO_GYM)
                .collection(Constants.COLLECTION_WORKOUT)
                .document(workoutId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw FireBaseException("Error al eliminar workout: ${e.localizedMessage}")
        }
    }
}

// Clase para constantes
object Constants {
    const val COLLECTION_GYM = "GymElorrietaBD"
    const val DOCUMENTO_GYM = "gym_01"
    const val COLLECTION_WORKOUT = "Workouts"
    const val COLLECTION_EJERCICIO = "Ejercicios"
    const val COLLECTION_SERIE = "Series"
    const val NOMBRE = "nombre"
    const val DESCRIPCION = "descripcion"
    const val COMPLETADO = "completado"
    const val TIEMPO_DURACION = "tiempoDuracion"
    const val TIEMPO_DESCANSO = "tiempoDescanso"
}

// Excepción personalizada
class FireBaseException(message: String) : Exception(message)