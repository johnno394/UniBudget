package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

//This activity allows the user to change their details such as name and password
public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "Edit Profile Activity";

    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");
    DatabaseReference profileDb;

    private static String rUsername, rFirstname, rLastname, rPassword;
    private static ImageView backBtn;
    private static Button btnSaveChanges;
    private static EditText etFirstName, etLastName, etUserName, etPassword;
    private static TextView tvChangesAlert;
    private static String noChangesDetectedTxt = "";
    private static String changesDetectedTxt = "Changes not saved!";
    private static String changesSaved = "Changes saved successfully!";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Retrieve current snapshot
        Intent i = getIntent();
        rUsername = i.getStringExtra("username");

        //Prepopulate user fields
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rFirstname = String.valueOf(snapshot.child("users").child(rUsername).child("first name").getValue());
                rLastname = String.valueOf(snapshot.child("users").child(rUsername).child("last name").getValue());
                rPassword = String.valueOf(snapshot.child("users").child(rUsername).child("password").getValue());
                Log.d(TAG, "First name snap: " + rFirstname);
                Log.d(TAG, "Last name snap: " + rLastname);
                Log.d(TAG, "Password snap: " + rPassword);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Instantiate objects
        backBtn = findViewById(R.id.profileBackArrow);
        etUserName = findViewById(R.id.etUsername);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPassword = findViewById(R.id.etPassword);
        tvChangesAlert = findViewById(R.id.tvSaveChangesPrompt);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Load user details into edit text views
        prePopulateViewText(rUsername);

        // Disabling view elements on load
        viewElementsStateOnCreate();

        // Save button text listener
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkUserFieldsHaveChanged(rFirstname, rLastname, rPassword) == true) {
                    Log.d(TAG, "On save clicked, fields have been changed");

                    String tempFname, tempLname, tempPword;
                    tempFname = etFirstName.getText().toString();
                    tempLname = etLastName.getText().toString();
                    tempPword = etPassword.getText().toString();
                    Log.d("TAG", tempFname + tempLname + tempPword);

                    //Edit database entry
                    Log.d(TAG, "Adding data to database");
                    profileDb = FirebaseDatabase.getInstance().getReference().child("users").child(rUsername);
                    profileDb.child("first name").setValue(tempFname);
                    profileDb.child("last name").setValue(tempLname);
                    profileDb.child("password").setValue(tempPword);
                    Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_LONG).show();

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No changes detected", Toast.LENGTH_LONG).show();
                }

            }
        });

        // Back button click listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Method to check if the user has changed any fields
    private boolean checkUserFieldsHaveChanged(String firstName, String lastName, String password) {
        Log.d(TAG, "Checking user fields");
        Log.d(TAG, "1. First name snapshot: " + firstName + " Changed to: " + etFirstName.getText().toString());
        Log.d(TAG, "2. Last name snapshot: " + lastName + " Changed to: " + etLastName.getText().toString());
        Log.d(TAG, "3. Password snapshot: " + password + " Changed to: " + etPassword.getText().toString());
        if(etFirstName.getText().toString().equals(firstName) && etLastName.getText().toString().equals(lastName) && etPassword.getText().toString().equals(password)) {
            Toast.makeText(getApplicationContext(), "No changes detected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, " Everything is still the same. Dont change database");
            return false;
        } else {
            Log.d(TAG, " Fields are different. Proceed with database entry.");
            return true;
        }
    }

    //Disables unwanted design elements on create
    private void viewElementsStateOnCreate() {
        //Batch disable on start
        // Hide top bar
        getSupportActionBar().hide();
        // Disable username change
        etUserName.setEnabled(false);
        // Save button and alert prompt disabled on create
        //btnSaveChanges.setEnabled(false);
        // Prompt is set as empty on create
        tvChangesAlert.setText(noChangesDetectedTxt);
        Log.d(TAG, "On created disables completed");
    }

    //Pre-populates user text views
    private void prePopulateViewText(String username) {

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Populates views text
                etUserName.setText(username);
                etFirstName.setText(String.valueOf(snapshot.child("users").child(username).child("first name").getValue()));
                etLastName.setText(String.valueOf(snapshot.child("users").child(username).child("last name").getValue()));
                etPassword.setText(String.valueOf(snapshot.child("users").child(username).child("password").getValue()));
                Log.d(TAG, "text views populated");

                // Populate snapshot info
                //rFirstname, rLastname, rPassword
                rFirstname = String.valueOf(snapshot.child("users").child(username).child("first name").getValue());
                rLastname = String.valueOf(snapshot.child("users").child(username).child("last name").getValue());
                rPassword = String.valueOf(snapshot.child("users").child(username).child("password").getValue());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void viewElementsStateOnSomethingChanged() {
        etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable save button
                btnSaveChanges.setEnabled(true);
                // Change alert text to remind user to submit changes
                tvChangesAlert.setText(changesDetectedTxt);
                Log.d(TAG, "Detects something changed method activated");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Checks for activity in the edit text view to enable button
        etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable save button
                btnSaveChanges.setEnabled(true);
                // Change alert text to remind user to submit changes
                tvChangesAlert.setText(changesDetectedTxt);
                Log.d(TAG, "Detects something changed method activated");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Checks for activity in the edit text view to enable button
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable save button
                btnSaveChanges.setEnabled(true);
                // Change alert text to remind user to submit changes
                tvChangesAlert.setText(changesDetectedTxt);
                Log.d(TAG, "Detects something changed method activated");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void viewElementsStateAfterSave() {
        // Change alert text to reflect success
        tvChangesAlert.setText(noChangesDetectedTxt);

        // Toast status to user
        Toast t = Toast.makeText(getApplicationContext(), "Recent changes saved!", Toast.LENGTH_LONG);
        t.show();
        Log.d(TAG, "After save method called");
    }

}
