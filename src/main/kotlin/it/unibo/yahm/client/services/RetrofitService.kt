package it.unibo.yahm.client.services

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import it.unibo.yahm.client.SpotholeService
import it.unibo.yahm.client.utils.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService() {

    val spotholeService: SpotholeService

    init {
        val baseUrl = if (BuildConfig.DEBUG) {
           Config.POTHOLE_SERVICE_DEVELOPMENT_BASEURL
        } else {
            Config.POTHOLE_SERVICE_PRODUCTION_BASEURL
        }

        val retrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        spotholeService = retrofit.create(SpotholeService::class.java)
    }

}
