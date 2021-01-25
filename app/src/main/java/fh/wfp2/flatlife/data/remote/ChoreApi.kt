package fh.wfp2.flatlife.data.remote

import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.remote.responses.AddItemResponse
import fh.wfp2.flatlife.data.room.entities.Chore
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChoreApi {

    @POST("/chores/addItem")
    suspend fun addChore(
        @Body chore: Chore
    ): Response<AddItemResponse> //TODO: wegen doppelter ID vl immer wieder neu einfügen ?

    @POST("/chores/updateItem")
    suspend fun updateChore(
        @Body chore: Chore
    ): Response<ResponseBody>

    @POST("/chores/deleteItem")
    suspend fun deleteChore(
        @Body request: DeleteItemRequest
    ): Response<ResponseBody>

    @GET("/chores/getAllItems")
    suspend fun getAllChores(): Response<List<Chore>>

    @GET("/chores/deleteAllCompletedItems")
    suspend fun deleteAllCompletedChores(): Response<ResponseBody>
}
















