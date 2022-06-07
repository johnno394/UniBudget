package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;

//Activity to show help information
public class HelpProfileActivity extends AppCompatActivity {

    private static final String TAG = "Edit Profile Activity";
    private static String username;
    private static ImageView backBtn;

    DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_profile);

        getSupportActionBar().hide();
        //Retrieve username
        Intent i = getIntent();
        username = i.getStringExtra("username");

        // Instantiate objects
        backBtn = findViewById(R.id.helpBackArrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}