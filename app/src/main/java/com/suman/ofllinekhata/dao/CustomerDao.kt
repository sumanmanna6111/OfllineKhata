package com.suman.ofllinekhata.dao

import androidx.room.*
import com.suman.ofllinekhata.entity.CustomerEntity
import org.jetbrains.annotations.NotNull

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer order by time DESC")
    suspend fun getAll(): List<CustomerEntity>

    @Query("SELECT * FROM customer order by id DESC LIMIT 1")
    suspend fun getLastUser(): List<CustomerEntity>

    @Query("SELECT SUM(`amount`) FROM customer WHERE amount < 0")
    suspend fun getTotalCredit(): Float?

    @Query("SELECT SUM(`amount`) FROM customer WHERE amount > 0")
    suspend fun getTotalDebit(): Float?

    @Query("SELECT * FROM customer WHERE id = :userId")
    suspend fun loadAllById(userId: Int): CustomerEntity

    @Query("SELECT * FROM customer WHERE name LIKE :first AND " +
            "number LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): CustomerEntity

    @Query("UPDATE customer SET amount= amount + :balance WHERE id = :userid")
    suspend fun update(balance: Float, userid: Int)

    @Insert
    suspend fun insertAll(vararg users: CustomerEntity)

    @Delete
    suspend fun delete(user: CustomerEntity)
}