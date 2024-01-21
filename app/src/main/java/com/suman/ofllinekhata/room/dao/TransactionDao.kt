package com.suman.ofllinekhata.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.suman.ofllinekhata.room.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM tran")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM tran order by id ASC")
    suspend fun getAllTransactionT(): List<TransactionEntity>
    @Query("SELECT * FROM tran WHERE uid = :userid ORDER BY id DESC")
    fun getCustomerTran(userid: Int): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM tran WHERE id = :id")
    fun getTranDetails(id: Int): LiveData<TransactionEntity>

    @Query("UPDATE tran SET received = :received, clear = :clear WHERE id = :id AND received = 0")
    suspend fun dueReceived(received:Int, clear:Long, id: Int)

    @Query("SELECT SUM(`amount`) FROM tran WHERE uid = :userid AND received = 0")
    fun getTotal(userid: Int): LiveData<Float?>

    @Query("SELECT * FROM tran WHERE id IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<TransactionEntity>

    @Query("SELECT * FROM tran WHERE amount LIKE :first AND " +
            "amount LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): TransactionEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg transaction: TransactionEntity)
    @Delete
    suspend fun delete(transaction: TransactionEntity)
}