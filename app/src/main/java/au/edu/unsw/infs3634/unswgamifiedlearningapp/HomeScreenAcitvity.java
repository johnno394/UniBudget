package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Home screen activity that hosts fragments
public class HomeScreenAcitvity extends AppCompatActivity {

    private static final String TAG = "HomeScreenActivity";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_acitvity);

        //hide action bar
        getSupportActionBar().hide();

        //instantiate nav bar
        NavigationBarView bottomNav = findViewById(R.id.bottom_navigation);

        // on successful login, default home fragment as the landing page
        // all textview etc instantiations are now in HomeFragment.java
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        //react to bottom bar clicks
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        Log.d(TAG, "home fragment selected");
                        break;
                    case R.id.nav_money:
                        selectedFragment = new GoalFragment();
                        Log.d(TAG, "Goal fragment selected");
                        break;
                    case R.id.nav_learn:
                        selectedFragment = new LearnFragment();
                        Log.d(TAG, "learn fragment selected");
                        break;
                    case R.id.nav_expense:
                        selectedFragment = new ExpenseFragment();
                        Log.d(TAG, "expense fragment selected");
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();
                        Log.d(TAG, "profile fragment selected");
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        });
    }

    //When the back button is pressed
    @Override
    public void onBackPressed() {
        builder = new AlertDialog.Builder(this); //Home is name of the activity
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent i=new Intent();
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                //startActivity(i);
                finish();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert=builder.create();
        alert.show();
    }
}