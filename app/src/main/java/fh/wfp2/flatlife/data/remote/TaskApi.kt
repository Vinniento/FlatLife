package fh.wfp2.flatlife.data.remote

import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequest
import fh.wfp2.flatlife.data.remote.requests.DeleteItemRequest
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import fh.wfp2.flatlife.data.remote.responses.AddItemResponse
import fh.wfp2.flatlife.data.room.entities.Task
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApi {

    @POST("/tasks/addTask")
    suspend fun addTask(
        @Body task: Task
    ): Response<AddItemResponse> //TODO: wegen doppelter ID vl immer wieder neu einfügen ?

    @POST("/tasks/updateTask")
    suspend fun updateTask(
        @Body task: Task
    ): Response<ResponseBody>

    @POST("/tasks/deleteTask")
    suspend fun deleteTask(
        @Body request: DeleteItemRequest
    ): Response<ResponseBody>

    @GET("/tasks/getAllTasks")
    suspend fun getAllTasks(): Response<List<Task>>

    @GET("/tasks/deleteAllCompletedTasks")
    suspend fun deleteAllCompletedTasks(): Response<ResponseBody>
}
















