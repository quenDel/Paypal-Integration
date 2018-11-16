package com.quendeltechfirm.paypalbraintreetest

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface RemoteEndpoints {

    @GET(Constant.GENERATE_TOKEN)
    fun getToken(): Call<ResponseBody>

    @FormUrlEncoded
    @POST(Constant.CHECKOUT)
    fun checkout(@Field("nonce") nonce: String?,
              @Field("amount") amount: String?): Call<ResponseBody>


}