package com.example.josemarrima.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.josemarrima.journalapp.JournalEntry;


public class AddTaskViewModel extends ViewModel {

    private LiveData<JournalEntry> journal;

    public AddTaskViewModel(AppDatabase database, int taskId) {
        journal = database.journalDao().loadTaskById(taskId);
    }

    public LiveData<JournalEntry> getTask() {
        return journal;
    }
}
