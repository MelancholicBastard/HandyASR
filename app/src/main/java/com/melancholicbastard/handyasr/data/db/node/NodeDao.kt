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

    @Query("DELETE FROM nodes")
    suspend fun deleteAll()

    @Query("SELECT * FROM nodes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): NodeEntity?

    @Query("SELECT * FROM nodes ORDER BY created_at DESC")
    fun getAll(): Flow<List<NodeEntity>>

    @Query(
        """
        SELECT * FROM nodes
        WHERE (
            (:query IS NULL OR :query = '')
            OR title LIKE '%' || :query || '%' COLLATE NOCASE
            OR text LIKE '%' || :query || '%' COLLATE NOCASE
        )
        AND (
            :startTimestamp IS NULL
            OR (:startTimestamp <= created_at AND created_at < :endTimestamp)
        )
        ORDER BY created_at DESC
        """
    )
    fun searchNodesBy(
        startTimestamp: Long?,
        endTimestamp: Long?,
        query: String?
    ): Flow<List<NodeEntity>>
}

