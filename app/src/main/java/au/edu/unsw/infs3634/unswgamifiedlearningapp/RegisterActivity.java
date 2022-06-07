package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//The activity allows the user to register a new unique user
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");

    private static int delay = 500;
    private EditText usernameTxt, passwordTxt, passwordTxtConfirm;
    private Button signUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTxt = findViewById(R.id.usernameEditTxt);
        passwordTxt = findViewById(R.id.pwdEditText1);
        passwordTxtConfirm = findViewById(R.id.pwdEditText2);
        signUpBtn = findViewById(R.id.signUpBtn);

        //When the user clicks on the sign up button, checks if the user is unique
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                //Get data from edits text fields;
                final String mUsername = usernameTxt.getText().toString().trim();
                final String mPassword = passwordTxt.getText().toString().trim();
                final String mPasswordConfirm = passwordTxtConfirm.getText().toString().trim();
                Log.d(TAG, "Data loaded into strings");

                //Check if fields are empty
                if (mUsername.isEmpty() || mPassword.isEmpty() || mPasswordConfirm.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkPasswordMatches(mPassword, mPasswordConfirm) == true) {

                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Log.d(TAG, "Checking if user exists");
                                //Checks if user is registered
                                if(snapshot.hasChild(mUsername)) {

                                    Toast.makeText(getApplicationContext(),"User Already Exists", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "User exists");

                                } else {

                                    Log.d(TAG, "Username not in use");
                                    Log.d(TAG, "Creating new user");

                                    //Creates new user
                                    createNewUser(mUsername, mPassword);

                                    Toast.makeText(getApplicationContext(),"User created", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "New user created");

                                    //Transition to Login Screen after a Delay
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent =  new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Log.d(TAG, "Screen Changed To LoginActivity");
                                        }},delay);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                    } else {

                        Toast.makeText(getApplicationContext(),"Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //Checks if user inputted two matching passwords
    private boolean checkPasswordMatches(String password, String passwordConfirm) {
        if (password.equals(passwordConfirm)) {
            return true;
        }
        return false;
    }

    //Method to close keyboard after clicking on convert button - quality of life improvement
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //pre populates initial data for a new user
    private void createNewUser(String mUsername, String mPassword) {
        //Creates initial user data
        databaseReference.child("users").child(mUsername).child("username").setValue(mUsername);
        databaseReference.child("users").child(mUsername).child("password").setValue(mPassword);
        databaseReference.child("users").child(mUsername).child("first name").setValue("");
        databaseReference.child("users").child(mUsername).child("last name").setValue("");

        //Creates STATS child values for the user
        databaseReference.child("users").child(mUsername).child("stats").child("level").setValue(1);
        databaseReference.child("users").child(mUsername).child("stats").child("exp").setValue(0.0);
        databaseReference.child("users").child(mUsername).child("stats").child("coins").setValue(100.0);
        databaseReference.child("users").child(mUsername).child("stats").child("badge").setValue(0);
        databaseReference.child("users").child(mUsername).child("stats").child("badge img").setValue(R.drawable.newbie);

        //Creates GOALS child values for for the user
        databaseReference.child("users").child(mUsername).child("goals").child("current goal").setValue(0.0);
        databaseReference.child("users").child(mUsername).child("goals").child("g status").setValue("Not Completed");
        databaseReference.child("users").child(mUsername).child("goals").child("start date").setValue("00-00-0000");
        databaseReference.child("users").child(mUsername).child("goals").child("g date").setValue("00-00-0000");


        //Created Modules child values for the user
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 1").child("name").setValue("Importance of budgeting");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 1").child("status").setValue("Not Started");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 1").child("url").setValue("j1Vwth0B8I8");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 1").child("xp").setValue(100);
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 1").child("preview image").setValue(R.drawable.module_one_image);


        databaseReference.child("users").child(mUsername).child("modules").child("module id: 2").child("name").setValue("How to save");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 2").child("status").setValue("Not Started");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 2").child("url").setValue("2jUZF0gRjJk");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 2").child("xp").setValue(100);
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 2").child("preview image").setValue(R.drawable.module_two_image);


        databaseReference.child("users").child(mUsername).child("modules").child("module id: 3").child("name").setValue("What are interest rates?");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 3").child("status").setValue("Not Started");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 3").child("url").setValue("GHHesANT6OM");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 3").child("xp").setValue(100);
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 3").child("preview image").setValue(R.drawable.module_three_image);

        databaseReference.child("users").child(mUsername).child("modules").child("module id: 4").child("name").setValue("Basics of investing");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 4").child("status").setValue("Not Started");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 4").child("url").setValue("hE2NsJGpEq4");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 4").child("xp").setValue(100);
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 4").child("preview image").setValue(R.drawable.module_four_image);

        databaseReference.child("users").child(mUsername).child("modules").child("module id: 5").child("name").setValue("What is a mortgage?");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 5").child("status").setValue("Not Started");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 5").child("url").setValue("CBIJwb37O_4");
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 5").child("xp").setValue(100);
        databaseReference.child("users").child(mUsername).child("modules").child("module id: 5").child("preview image").setValue(R.drawable.module_five_image);

    }
}