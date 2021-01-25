package fh.wfp2.flatlife.data.remote

import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import fh.wfp2.flatlife.data.remote.responses.AddItemResponse
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FinanceApi {

    @POST("/finance/addItem")
    suspend fun addItem(
        @Body item: FinanceActivity
    ): Response<AddItemResponse>

    @POST("/finance/updateItem")
    suspend fun updateItem(
        @Body item: FinanceActivity
    ): Response<ResponseBody>

    @POST("/finance/deleteItem")
    suspend fun deleteItem(
        @Body request: DeleteItemRequest
    ): Response<ResponseBody>

    @GET("/finance/getAllActivities")
    suspend fun getAllActivities(): Response<List<FinanceActivity>>

    @GET("/finance/getAllCategories")
    suspend fun getAllExpenseCategories(): Response<List<ExpenseCategory>>

    @POST("/finance/addCategory")

    suspend fun addCategory(
        @Body item: ExpenseCategory
    ): Response<AddItemResponse>

}
















