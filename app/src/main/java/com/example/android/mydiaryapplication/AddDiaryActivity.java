package com.example.android.mydiaryapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.mydiaryapplication.database.AppDatabase;
import com.example.android.mydiaryapplication.database.DiaryEntry;

import java.util.Date;

public class AddDiaryActivity extends AppCompatActivity {

    // Diary entry id stored as a constant value and to be used when updating a previous entry
    public static final String EXTRA_DIARY_ENTRY_ID = "extraDiaryEntryId";

    // default id stored as a constant value and to be used when not updating a previous entry
    private static final int DEFAULT_DIARY_ENTRY_ID = -1;

    private static int mId = DEFAULT_DIARY_ENTRY_ID;

    EditText mTitle;
    EditText mContent;
    Button mButtonSave;

    AppDatabase mAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);

        initializeViews();

        mAD = AppDatabase.getsInstance(getApplicationContext());

        // get the intent that launches this AddDiaryActivity, and obtain the extra id if available
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(EXTRA_DIARY_ENTRY_ID)){
            if(mId == DEFAULT_DIARY_ENTRY_ID){
                Intent parentIntent = getIntent();
                mId = parentIntent.getIntExtra(EXTRA_DIARY_ENTRY_ID, DEFAULT_DIARY_ENTRY_ID);
                AddDiaryViewModelFactory factory = new AddDiaryViewModelFactory(mAD, mId);
                final AddDiaryEntryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddDiaryEntryViewModel.class);
                viewModel.getDiaryEntry().observe(this, new Observer<DiaryEntry>() {
                    @Override
                    public void onChanged(@Nullable DiaryEntry diaryEntry) {
                        viewModel.getDiaryEntry().removeObserver(this);
                        populateUI(diaryEntry);
                    }
                });
            }
        }
    }

    private void populateUI(DiaryEntry diaryEntry) {
        if (diaryEntry != null){
            mTitle.setText(diaryEntry.getTitle());
            mContent.setText(diaryEntry.getDetails());
        }
    }

    /**
     * This method initializes the views, and it is called within onCreate
     */
    private void initializeViews() {
        mTitle = findViewById(R.id.editText_title);
        mContent = findViewById(R.id.editText_content);
        mButtonSave = findViewById(R.id.button_save);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }


    private void onSaveButtonClicked() {
        String title = mTitle.getText().toString();
        String details = mContent.getText().toString();
        Date date = new Date();

        final DiaryEntry diaryEntry = new DiaryEntry(title, details, date);
        AppExecutors appExecutors = AppExecutors.getInstance();
        if (mId == DEFAULT_DIARY_ENTRY_ID) {
            appExecutors.getPrimaryExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    mAD.diaryDao().insertDiary(diaryEntry);
                }
            });
            //display a message that user entry has been saved
            appExecutors.getMainThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddDiaryActivity.this,
                            getResources().getString(R.string.entry_saved),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            diaryEntry.setId(mId);
            appExecutors.getPrimaryExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    mAD.diaryDao().updateDiary(diaryEntry);
                }
            });
            //display a message that user entry has been updated
            appExecutors.getMainThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddDiaryActivity.this,
                            getResources().getString(R.string.entry_updated),
                            Toast.LENGTH_SHORT).show();
                }
            });
            //sets the Id back to the default
            mId = DEFAULT_DIARY_ENTRY_ID;
        }
            finish();
    }
}
