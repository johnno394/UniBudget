package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

//This activity will check if the user exists and allow them to login
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");

    private static EditText usernameTxt, passwordTxt;
    private static Button signInBtn;
    private static TextView forgotUsernametv, forgotPasswordtv, signuptv;
    private static ImageView forgotUsername, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("UniBudget");

        // initialising widgets
        usernameTxt = findViewById(R.id.usernameEditText);
        passwordTxt = findViewById(R.id.pwdEditText);
        signInBtn = findViewById(R.id.signInButton);
        forgotUsernametv = findViewById(R.id.forgotUsername);
        forgotPasswordtv = findViewById(R.id.forgotPassword);
        signuptv = findViewById(R.id.signUptv);
        forgotUsername = findViewById(R.id.btnForgotUsername);
        forgotPassword = findViewById(R.id.btnForgotPassword);

        // instantiate on click listener for signIn button press
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                //Get user input from username and password text fields
                String username = usernameTxt.getText().toString().trim();
                String pwd = passwordTxt.getText().toString().trim();

                if (username.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Sign In Button Clicked");

                    //Check if user exists
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //check if users exists and if password matches
                            if (snapshot.hasChild(username)) {
                                Log.d(TAG, "user exists");
                                final String getPassword = (snapshot.child(username).child("password").getValue(String.class));
                                Log.d(TAG, "retrieved password for the user: " + getPassword);
                                Log.d(TAG, "entered password for the user: " + pwd);
                                if (getPassword.equals(pwd)) {

                                    Log.d(TAG, "Sign In Successful");
                                    Toast.makeText(getApplicationContext(), "Sign in successful", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), HomeScreenAcitvity.class);

                                    //Passes the username to the home screen activity
                                    i.putExtra("FLAG", username);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        //If the user has forgotten their username
        forgotUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HelpProfileActivity.class);
                startActivity(i);
                Log.d(TAG, "Forgot username: Screen Changed to Help Activity");
            }
        });

        //If the user has forgotten their password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HelpProfileActivity.class);
                startActivity(i);
                Log.d(TAG, "Forgot Password: Screen Changed to Help Activity");
            }
        });

        //Takes user to activity_register
        signuptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                Log.d(TAG, "Screen Changed to Register Activity");
            }
        });

    }
    //Method to close keyboard after clicking on convert button - quality of life improvement
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}