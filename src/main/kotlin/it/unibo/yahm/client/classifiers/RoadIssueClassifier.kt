package it.unibo.yahm.client.classifiers

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import it.unibo.yahm.client.RoadIssueClassifierService
import it.unibo.yahm.client.entities.ObstacleType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RoadIssueClassifier {

    private val classifierService: RoadIssueClassifierService

    init {
        val retrofit = Retrofit.Builder().baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()

        classifierService = retrofit.create(RoadIssueClassifierService::class.java)
    }

    fun classify(inputData: FloatArray): ObstacleType {
        return classifierService.classify(inputData).blockingFirst().result
    }

}
