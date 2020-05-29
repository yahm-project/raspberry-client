package it.unibo.yahm.client

import io.reactivex.rxjava3.core.Observable
import it.unibo.yahm.client.entities.RoadIssueClassificationResult
import retrofit2.http.Body
import retrofit2.http.POST

interface RoadIssueClassifierService {

    @POST("classify")
    fun classify(@Body inputData: FloatArray): Observable<RoadIssueClassificationResult>

}
