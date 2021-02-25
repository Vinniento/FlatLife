package fh.wfp2.flatlife.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fh.wfp2.flatlife.data.remote.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.other.Constants.BASE_URL
import fh.wfp2.flatlife.other.Constants.DATABASE_NAME
import fh.wfp2.flatlife.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFlatLifeDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, FlatLifeRoomDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideTaskDao(db: FlatLifeRoomDatabase) = db.taskDao()

    @Singleton
    @Provides
    fun provideChoreDao(db: FlatLifeRoomDatabase) = db.choresDao()

    @Singleton
    @Provides
    fun provideShoppingDao(db: FlatLifeRoomDatabase) = db.shoppingDao()

    @Singleton
    @Provides
    fun provideFinanceDao(db: FlatLifeRoomDatabase) = db.financeActivityDao()

    @Singleton
    @Provides
    fun provideFinanceCategoryDao(db: FlatLifeRoomDatabase) = db.expenseCategoryDao()

    @Singleton
    @Provides
    fun provideAddExpenseDao(db: FlatLifeRoomDatabase) = db.addExpenseDao()

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @Singleton
    @Provides
    fun provideTaskApi(
        okHttpClient: OkHttpClient.Builder,

        basicAuthInterceptor: BasicAuthInterceptor
    ): TaskApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(okHttpClient, basicAuthInterceptor))
        .build()
        .create(TaskApi::class.java)

    @Singleton
    @Provides
    fun provideShoppingApi(
        okHttpClient: OkHttpClient.Builder,

        basicAuthInterceptor: BasicAuthInterceptor
    ): ShoppingApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(okHttpClient, basicAuthInterceptor))
        .build()
        .create(ShoppingApi::class.java)

    @Singleton
    @Provides
    fun provideFinanceApi(
        okHttpClient: OkHttpClient.Builder,

        basicAuthInterceptor: BasicAuthInterceptor
    ): FinanceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(okHttpClient, basicAuthInterceptor))
        .build()
        .create(FinanceApi::class.java)

    @Singleton
    @Provides
    fun provideChoreApi(
        okHttpClient: OkHttpClient.Builder,
        basicAuthInterceptor: BasicAuthInterceptor
    ): ChoreApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(okHttpClient, basicAuthInterceptor))
        .build()
        .create(ChoreApi::class.java)

    @Singleton
    @Provides
    fun provideAuthApi(
        okHttpClient: OkHttpClient.Builder,
        basicAuthInterceptor: BasicAuthInterceptor
    ): AuthApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(okHttpClient, basicAuthInterceptor))
        .build()
        .create(AuthApi::class.java)


    private fun buildHttp3Client(
        okHttpClient: OkHttpClient.Builder,
        basicAuthInterceptor: BasicAuthInterceptor
    ) =
        okHttpClient.addInterceptor(basicAuthInterceptor)
            .build()


    @Singleton
    @Provides
    fun provideHttp3Client(): OkHttpClient.Builder {
        val trustAllCertificates: Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                /*NO-OP*/
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                /*NO-OP*/
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCertificates, SecureRandom())
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
    }


    @Singleton
    @Provides
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}