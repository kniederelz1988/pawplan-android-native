package de.kniederelz.pawplan.appointments.sources.factories

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.sources.FirestoreAppointmentRatingDataSource
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.sources.factories.AppointmentRatingDataSourceFactory
import javax.inject.Singleton

@Singleton
class FirestoreAppointmentRatingDataSourceFactory(
    private val firestore: FirebaseFirestore
) : AppointmentRatingDataSourceFactory {
    override fun createPagingSource(dogId: String): PagingSource<DocumentSnapshot, AppointmentRating> =
        FirestoreAppointmentRatingDataSource(firestore, dogId)
}