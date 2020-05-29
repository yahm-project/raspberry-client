package it.unibo.yahm.client

import io.reactivex.rxjava3.core.Observable
import it.unibo.yahm.client.entities.Evaluations
import it.unibo.yahm.client.entities.Leg
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotholeService {

    @POST("roads/evaluations")
    fun sendEvaluations(@Body evaluations: Evaluations): Observable<Void>

}
