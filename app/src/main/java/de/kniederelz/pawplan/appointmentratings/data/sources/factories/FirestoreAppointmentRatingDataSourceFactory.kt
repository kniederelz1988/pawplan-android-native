package de.kniederelz.pawplan.appointmentratings.data.sources.factories

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointmentratings.data.sources.FirestoreAppointmentRatingDataSource
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointmentratings.domain.sources.factories.AppointmentRatingDataSourceFactory
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.sources.factory.DogDataSourceFactory
import javax.inject.Singleton

@Singleton
class FirestoreAppointmentRatingDataSourceFactory(
    private val firestore: FirebaseFirestore
) : AppointmentRatingDataSourceFactory {
    override fun createPagingSource(dogId: String): PagingSource<DocumentSnapshot, AppointmentRating> =
        FirestoreAppointmentRatingDataSource(firestore, dogId)
}