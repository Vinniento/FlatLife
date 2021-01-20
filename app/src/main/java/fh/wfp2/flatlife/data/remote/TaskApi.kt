package fh.wfp2.flatlife.data.remote

import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequest
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import fh.wfp2.flatlife.data.room.entities.Task
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApi {

    @POST("/register")
    suspend fun register(
        @Body registerRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/login")
    suspend fun login(
        @Body loginRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/addTask")
    suspend fun addTask(
        @Body task: Task
    ): Response<ResponseBody>

    @POST("/deleteTask")
    suspend fun deleteTask(
        @Body task: Task
    ): Response<ResponseBody>

    @GET("/getAllTasks")
    suspend fun getAllTasks(): Response<List<Task>>
}
















