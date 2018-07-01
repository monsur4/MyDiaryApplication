package com.example.android.mydiaryapplication;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.mydiaryapplication.database.AppDatabase;
import com.example.android.mydiaryapplication.database.DiaryEntry;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class DiaryEntries extends AppCompatActivity implements DiaryAdapter.ItemClickListener {

    FloatingActionButton fab;
    AppDatabase mAD;
    RecyclerView mRecyclerView;
    DiaryAdapter adapter;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entries);

        //hide the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiaryEntries.this, AddDiaryActivity.class);
                startActivity(intent);
            }
        });

        mAD = AppDatabase.getsInstance(getApplicationContext());

        //initialize the recyclerview and use a linear layout as the recycler views layout manager
        mRecyclerView = findViewById(R.id.recyclerviewEntries);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initialize the adapter and attach it to the recyclerview
        adapter = new DiaryAdapter(this, this);
        mRecyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(divider);

        //sets the lists in the adapter to the diary entries in the database
        retrieveDiaryFromViewModel();

        /*
         *This google sign-in object makes a request for the user ID and basic profile information,
         *as well as the users email addresses
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /*Creates a google sign-in client object with the google sign-in options requested
        * above
        */
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void retrieveDiaryFromViewModel(){
        MyViewModel viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        viewModel.getDiaryEntries().observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(@Nullable List<DiaryEntry> diaryEntries) {
                adapter.setDiaryEntries(diaryEntries);
            }
        });
    }

    /**
     * This listener launches the AddDiaryActivity when a previous entry into the diary is selected
     */

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(DiaryEntries.this, AddDiaryActivity.class);
        intent.putExtra(AddDiaryActivity.EXTRA_DIARY_ENTRY_ID, itemId);
        startActivity(intent);
    }

    /**
     * add the sign_out button as an option Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out){
            //make a call to firebase sign out if the sign out options was clicked
            FirebaseAuth.getInstance().signOut();
            //signs out of the current google account
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(DiaryEntries.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(DiaryEntries.this,
                            getResources().getString(R.string.sign_out_successful),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
