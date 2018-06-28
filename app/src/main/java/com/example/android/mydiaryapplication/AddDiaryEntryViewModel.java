package com.example.android.mydiaryapplication;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.mydiaryapplication.database.AppDatabase;
import com.example.android.mydiaryapplication.database.DiaryEntry;

/**
 * Created by OKUNIYI MONSURU on 6/27/2018.
 */

public class AddDiaryEntryViewModel extends ViewModel {
    private LiveData<DiaryEntry> diaryEntry;

    public AddDiaryEntryViewModel(AppDatabase database, int id) {
        diaryEntry = database.diaryDao().loadDiaryEntryById(id);
    }


    public LiveData<DiaryEntry> getDiaryEntry() {
        return diaryEntry;
    }
}
