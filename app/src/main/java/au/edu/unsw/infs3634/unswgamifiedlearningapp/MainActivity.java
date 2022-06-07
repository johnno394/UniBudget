package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Main activity screen shows animated logo and then transitions to the login screen after a delay
public class MainActivity extends AppCompatActivity {

    //logging
    private static final String TAG = "MainActivity";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");

    private static int SPLASH_SCREEN = 3200;
    private static Animation topAnim, botAnim;
    private static ImageView logo;
    private static TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);

        //Instantiating elements
        logo = findViewById(R.id.logo);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hides action bar
        getSupportActionBar().hide();

        //Setting Animation
        logo.setAnimation(topAnim);
        welcomeMessage.setAnimation(botAnim);
        Log.d(TAG, "Animated Logo Works");

        //Transition to Login Screen after a Delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Log.d(TAG, "Screen Changed to Login Activity");
            }
        },SPLASH_SCREEN);
    }

}
