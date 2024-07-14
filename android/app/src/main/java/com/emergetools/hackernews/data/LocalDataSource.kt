package com.emergetools.hackernews.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmark")
data class LocalBookmark(
  @PrimaryKey val id: Long,
  @ColumnInfo val title: String,
  @ColumnInfo val author: String,
  @ColumnInfo val score: Int,
  @ColumnInfo val commentCount: Int,
  @ColumnInfo val timestamp: Long,
  @ColumnInfo val bookmarked: Boolean,
  @ColumnInfo val url: String?,
)

@Dao
interface BookmarkDao {
  @Query("SELECT * from bookmark")
  fun getAllBookmarks(): Flow<List<LocalBookmark>>

  @Upsert
  suspend fun addBookmark(bookmark: LocalBookmark)

  @Delete
  suspend fun deleteBookmark(bookmark: LocalBookmark)
}

@Database(entities = [LocalBookmark::class], version = 1)
abstract class HackerNewsDatabase: RoomDatabase() {
  abstract fun bookmarkDao(): BookmarkDao
}


