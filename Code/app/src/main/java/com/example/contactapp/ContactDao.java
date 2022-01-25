package com.example.contactapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface ContactDao {
    @Query("Select * from Contact")
    List<Contact> getAll();
    @Insert
    void Insert(Contact...contacts);
    @Delete
    void deleteAll(Contact...contacts);
    @Delete
    void delete(Contact contact);
    @Query("Select * from Contact where id = :id")
    Contact getContactbyID(int id);
    @Query("Update Contact set name = :name, mobile = :mobile, email = :email, avatar = :avatar where id = :id")
    void Update(int id, String name, String mobile, String email, byte[] avatar);
}
