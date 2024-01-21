package com.suman.ofllinekhata.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.suman.ofllinekhata.room.entity.CustomerEntity
import org.jetbrains.annotations.NotNull

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customer order by time DESC")
    fun getAll(): LiveData<List<CustomerEntity>>
    @Query("SELECT * FROM customer order by id ASC")
    suspend fun getAllCustomerT(): List<CustomerEntity>
    @Query("SELECT id FROM customer order by id DESC LIMIT 1")
    fun getLastUser(): Int?
    @Query("SELECT SUM(`amount`) FROM customer WHERE amount < 0")
    fun getTotalCredit(): LiveData<Float?>
    @Query("SELECT SUM(`amount`) FROM customer WHERE amount > 0")
    fun getTotalDebit(): LiveData<Float?>
    @Query("SELECT * FROM customer WHERE id = :userId")
    fun loadAllById(userId: Int): LiveData<CustomerEntity>
    @Query("SELECT * FROM customer WHERE name LIKE :first AND " +
            "number LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): CustomerEntity
    @Query("UPDATE customer SET amount= amount + :balance, time= :update WHERE id = :userid")
    suspend fun update(balance: Float, update: Long = System.currentTimeMillis() , userid: Int)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg users: CustomerEntity)
    @Delete
    suspend fun delete(user: CustomerEntity)
}