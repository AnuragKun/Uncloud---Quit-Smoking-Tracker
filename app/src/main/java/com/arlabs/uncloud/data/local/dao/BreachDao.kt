package com.arlabs.uncloud.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arlabs.uncloud.domain.model.Breach
import kotlinx.coroutines.flow.Flow

@Dao
interface BreachDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreach(breach: Breach)

    @Query("SELECT * FROM breaches ORDER BY timestamp DESC")
    fun getAllBreaches(): Flow<List<Breach>>
    
    @Query("SELECT * FROM breaches WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getBreachesAfter(startTime: Long): Flow<List<Breach>>
}
