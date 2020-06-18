package com.banksampah.operator.network

import com.banksampah.operator.model.*
import com.banksampah.operator.network.response.MultiResponse
import com.banksampah.operator.network.response.SingleResponse
import retrofit2.Call
import retrofit2.http.*

interface Services {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("phone_number") phoneNumber: String,
              @Field("password") password: String): Call<SingleResponse<Token>>

    @FormUrlEncoded
    @POST("register/operator")
    fun register(@Field("name") name: String,
                 @Field("phone_number") phoneNumber: String,
                 @Field("password") password: String): Call<SingleResponse<Token>>

    @FormUrlEncoded
    @POST("operator/forgotpassword")
    fun forgotPassword(@Field("phone_number") phoneNumber: String,
                       @Field("password") password: String) : Call<SingleResponse<Token>>

    @FormUrlEncoded
    @POST("user/changepassword")
    fun changePassword(@Header("Authorization") token: String,
                       @Field("password") password: String) : Call<SingleResponse<User>>

    @GET("operator")
    fun getUser(@Header("Authorization") token: String): Call<SingleResponse<User>>

    @FormUrlEncoded
    @POST("user/update")
    fun updateUser(@Header("Authorization") token: String,
                   @Field("name") name: String,
                   @Field("phone_number") phoneNumber: String,
                   @Field("address") address: String) : Call<SingleResponse<User>>

    @GET("customer/unconfirmed")
    fun getCustomersNotConfirmed(@Header("Authorization") token: String): Call<MultiResponse<Customer>>

    @GET("customer/all")
    fun getCustomers(@Header("Authorization") token: String): Call<MultiResponse<Customer>>

    @GET("transaction/history/{id}")
    fun getCustomerHistory(@Header("Authorization") token: String,
                           @Path("id") idCustomer: Int) : Call<MultiResponse<History>>

    @GET("complain")
    fun getComplaints(@Header("Authorization") token: String): Call<MultiResponse<Complaint>>

    @FormUrlEncoded
    @POST("transaction/deposit")
    fun deposit(@Header("Authorization") token: String,
                @Field("id_user") idCustomer: Int,
                @Field("amount") amount: Long) : Call<SingleResponse<History>>

    @FormUrlEncoded
    @POST("transaction/withdraw")
    fun withdraw(@Header("Authorization") token: String,
                @Field("id_user") idCustomer: Int,
                @Field("amount") amount: Long) : Call<SingleResponse<History>>

    @FormUrlEncoded
    @POST("transaction/{id}/edit")
    fun editTransaction(@Header("Authorization") token: String,
                        @Path("id") id: Int,
                        @Field("amount") amount: Long): Call<SingleResponse<History>>

    @GET("transaction/{id}/delete")
    fun deleteTransaction(@Header("Authorization") token: String,
                          @Path("id") id: Int): Call<SingleResponse<History>>
}