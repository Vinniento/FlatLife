package fh.wfp2.flatlife.data.repositories

import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequest
import fh.wfp2.flatlife.data.remote.AuthApi
import fh.wfp2.flatlife.other.Resource
import kotlinx.coroutines.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(AccountRequest(email, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    suspend fun register(username: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = authApi.register(AccountRequest(username, password))
            if(response.isSuccessful && response.body()!!.successful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }
}