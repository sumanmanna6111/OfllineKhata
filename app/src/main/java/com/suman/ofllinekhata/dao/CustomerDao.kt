package com.suman.ofllinekhata.dao

import androidx.room.*
import com.suman.ofllinekhata.entity.CustomerEntity
import org.jetbrains.annotations.NotNull

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer order by time desc")
    suspend fun getAll(): List<CustomerEntity>

    @Query("SELECT SUM(`amount`) FROM customer WHERE amount < 0")
    suspend fun getTotalCredit(): Float?

    @Query("SELECT * FROM customer WHERE id IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<CustomerEntity>

    @Query("SELECT * FROM customer WHERE name LIKE :first AND " +
            "number LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): CustomerEntity

    @Query("UPDATE customer SET amount=:balance WHERE id = :uid")
    suspend fun update(balance: Float, uid: Int)

    @Insert
    suspend fun insertAll(vararg users: CustomerEntity)

    @Delete
    suspend fun delete(user: CustomerEntity)
}