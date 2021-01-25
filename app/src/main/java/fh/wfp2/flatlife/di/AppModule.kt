package fh.wfp2.flatlife.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import fh.wfp2.flatlife.data.remote.*
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import fh.wfp2.flatlife.other.Constants.BASE_URL
import fh.wfp2.flatlife.other.Constants.DATABASE_NAME
import fh.wfp2.flatlife.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
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
        basicAuthInterceptor: BasicAuthInterceptor
    ): TaskApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(basicAuthInterceptor))
        .build()
        .create(TaskApi::class.java)

    @Singleton
    @Provides
    fun provideShoppingApi(
        basicAuthInterceptor: BasicAuthInterceptor
    ): ShoppingApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(basicAuthInterceptor))
        .build()
        .create(ShoppingApi::class.java)

    @Singleton
    @Provides
    fun provideFinanceApi(
        basicAuthInterceptor: BasicAuthInterceptor
    ): FinanceApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(basicAuthInterceptor))
        .build()
        .create(FinanceApi::class.java)

    @Singleton
    @Provides
    fun provideChoreApi(
        basicAuthInterceptor: BasicAuthInterceptor
    ): ChoreApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(basicAuthInterceptor))
        .build()
        .create(ChoreApi::class.java)

    @Singleton
    @Provides
    fun provideAuthApi(
        basicAuthInterceptor: BasicAuthInterceptor
    ): AuthApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(buildHttp3Client(basicAuthInterceptor))
        .build()
        .create(AuthApi::class.java)

    private fun buildHttp3Client(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(basicAuthInterceptor)
            .build()
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