package com.suman.ofllinekhata.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.suman.ofllinekhata.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM tran")
    suspend fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM tran WHERE uid = :userid ORDER BY id DESC")
    suspend fun getCustomerTran(userid: Int): List<TransactionEntity>

    @Query("SELECT * FROM tran WHERE id = :id")
    suspend fun getTranDetails(id: Int):  TransactionEntity

    @Query("SELECT SUM(`amount`) FROM tran WHERE uid = :userid")
    suspend fun getTotal(userid: Int): Float?

    @Query("SELECT * FROM tran WHERE id IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<TransactionEntity>

    @Query("SELECT * FROM tran WHERE amount LIKE :first AND " +
            "amount LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): TransactionEntity

    @Insert
    suspend fun insertAll(vararg transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}