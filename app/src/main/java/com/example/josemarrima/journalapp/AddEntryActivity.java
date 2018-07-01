package com.example.josemarrima.journalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.josemarrima.journalapp.AppDatabase;
import com.example.josemarrima.journalapp.JournalEntry;

public class AddEntryActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    private int mTaskId = DEFAULT_TASK_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        titleEditText = findViewById(R.id.editText_title);
        descriptionEditText = findViewById(R.id.editText_message);

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);

                final AddTaskViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);


                viewModel.getTask().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(journalEntry);
                    }
                });
            }
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.entry_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cancel) {

            return true;
        }

        if (id == R.id.action_saving) {
            onSaveButtonClicked();
            return true;
        }

        if (id == R.id.action_settings) {
            //Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            //startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */

    public void onSaveButtonClicked() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();


        final JournalEntry journalEntry = new JournalEntry(title, description);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    mDb.journalDao().insertTask(journalEntry);
                } else {
                    //update task
                    journalEntry.setId(mTaskId);
                    mDb.journalDao().updateTask(journalEntry);
                }
                finish();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param journalEntry the taskEntry to populate the UI
     */
    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return;
        }

        descriptionEditText.setText(journalEntry.getDescription());
        titleEditText.setText(journalEntry.getTitle());
    }


}
