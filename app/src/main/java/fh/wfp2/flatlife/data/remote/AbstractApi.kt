package fh.wfp2.flatlife.data.remote

import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.remote.responses.AddItemResponse
import fh.wfp2.flatlife.data.room.entities.Task
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AbstractApi<T> {

    @POST("/addItem")
    suspend fun addTask(
        @Body instance: T
    ): Response<AddItemResponse> //TODO: wegen doppelter ID vl immer wieder neu einfügen ?

    @POST("/updateTask")
    suspend fun updateTask(
        @Body instance: T
    ): Response<ResponseBody>

    @POST("/deleteTask")
    suspend fun deleteItem(
        @Body request: DeleteItemRequest
    ): Response<ResponseBody>

    @GET("/getAllItems")
    suspend fun getAllItems(): Response<List<T>>

}