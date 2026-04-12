package com.melancholicbastard.handyasr.data.db.node

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(node: NodeEntity): Long

    @Update
    suspend fun update(node: NodeEntity)

    @Query("DELETE FROM nodes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM nodes ORDER BY created_at DESC")
    fun getNodesByDateDesc(): Flow<List<NodeEntity>>

    @Query(
        """
        SELECT * FROM nodes
        WHERE title LIKE '%' || :query || '%' COLLATE NOCASE
        OR text LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY created_at DESC
        """
    )
    fun searchByTextOrTitle(query: String): Flow<List<NodeEntity>>
}

