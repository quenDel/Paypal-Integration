package com.quendeltechfirm.paypalbraintreetest


import android.os.RemoteException
import android.util.Log
import com.google.gson.GsonBuilder
import com.quendeltechfirm.paypalbraintreetest.Constant.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

class RetrofitConnectionService {

    private val retrofit: Retrofit

    companion object {
        private var instance: RetrofitConnectionService? = null
        @Synchronized
        fun getInstance(): RetrofitConnectionService {
            if (instance == null) {
                instance = RetrofitConnectionService()
            }
            return instance!!
        }
    }

    init {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

        retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Throws(IOException::class, RemoteException::class, SocketTimeoutException::class, InterruptedIOException::class)
    fun getToken(): Response<ResponseBody> {
        val service = retrofit.create(RemoteEndpoints::class.java)
        // Remote call can be executed synchronously since the job calling it is already backgrounded.
        val response = service.getToken().execute()
        if (response == null || !response.isSuccessful || response.errorBody() != null) {
            throw RemoteException(response.errorBody().string())
        }
        Log.d("remote response: " , response.message())
        return response
    }

    @Throws(IOException::class, RemoteException::class, SocketTimeoutException::class, InterruptedIOException::class)
    fun checkout(nonce: String?, amount: String?): Response<ResponseBody> {
        val service = retrofit.create(RemoteEndpoints::class.java)
        // Remote call can be executed synchronously since the job calling it is already backgrounded.
        val response = service.checkout(nonce, amount).execute()
        if (response == null || !response.isSuccessful || response.errorBody() != null) {
            throw RemoteException(response.errorBody().string())
        }
        Log.d("remote response: " , response.message())
        return response
    }
}
