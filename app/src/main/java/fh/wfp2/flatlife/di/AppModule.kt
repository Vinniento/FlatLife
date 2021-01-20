package fh.wfp2.flatlife.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import fh.wfp2.flatlife.other.Constants.BASE_URL
import fh.wfp2.flatlife.other.Constants.DATABASE_NAME
import fh.wfp2.flatlife.other.Constants.ENCRYPTED_SHARED_PREF_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import fh.wfp2.flatlife.data.remote.BasicAuthInterceptor
import fh.wfp2.flatlife.data.remote.TaskApi
import fh.wfp2.flatlife.data.room.FlatLifeRoomDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    ): TaskApi {
        val client = OkHttpClient.Builder()
            //.addInterceptor(basicAuthInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TaskApi::class.java)
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















