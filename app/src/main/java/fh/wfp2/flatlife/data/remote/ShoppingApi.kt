package fh.wfp2.flatlife.data.remote

import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.remote.responses.AddItemResponse
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShoppingApi {

    @POST("/shopping/addItem")
    suspend fun addItem(
        @Body item: ShoppingItem
    ): Response<AddItemResponse> //TODO: wegen doppelter ID vl immer wieder neu einfügen ?

    @POST("/shopping/updateItem")
    suspend fun updateItem(
        @Body item: ShoppingItem
    ): Response<ResponseBody>

    @POST("/shopping/deleteItem")
    suspend fun deleteItem(
        @Body request: DeleteItemRequest
    ): Response<ResponseBody>

    @GET("/shopping/getAllItems")
    suspend fun getAllItems(): Response<List<ShoppingItem>>

    @GET("/shopping/deleteAllBoughtItems")
    suspend fun deleteAllBoughtItems(): Response<ResponseBody>
}
















