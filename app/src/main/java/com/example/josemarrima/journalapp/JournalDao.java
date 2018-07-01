package com.example.josemarrima.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal")
    LiveData<List<JournalEntry>> loadAllJournals();

    @Insert
    void insertTask(JournalEntry journalEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(JournalEntry taskEntry);

    @Delete
    void deleteTask(JournalEntry taskEntry);

    @Query("SELECT * FROM journal WHERE id = :id")
    LiveData<JournalEntry> loadTaskById(int id);
}
