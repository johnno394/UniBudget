package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

//Users can purchase badges with their coins
public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    DatabaseReference badgeRef, profileRef;

    private static TextView selectedBadge, shopIntroTV, badge1Tv, badge2Tv, badge3Tv, badge4Tv;
    private static Button purchaseBtn;
    private static ImageView backToProfileArrow, shopProfileImg, badge1, badge2, badge3, badge4;

    private static String uUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        //hide action bar
        getSupportActionBar().hide();

        //Retrieve username
        Intent i = getIntent();
        uUsername = i.getStringExtra("username");

        //Instantiate elements
        selectedBadge = findViewById(R.id.selectedBadge);
        shopIntroTV = findViewById(R.id.shopIntroTV);
        badge1Tv = findViewById(R.id.badge1Tv);
        badge2Tv = findViewById(R.id.badge2Tv);
        badge3Tv = findViewById(R.id.badge3Tv);
        badge4Tv = findViewById(R.id.badge4Tv);
        purchaseBtn = findViewById(R.id.purchaseBtn);
        backToProfileArrow = findViewById(R.id.backToProfileArrow);
        shopProfileImg = findViewById(R.id.shopProfileImg);
        badge1 = findViewById(R.id.badge1);
        badge2 = findViewById(R.id.badge2);
        badge3 = findViewById(R.id.badge3);
        badge4 = findViewById(R.id.badge4);

        //Populate user data
        prePopulateViewText();

        //Set badge listeners - when clicked change selected badge tv
        badge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBadge.setText("Selected Badge: A Spare Coin");
            }
        });
        badge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBadge.setText("Selected Badge: Small Change");
            }
        });
        badge3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBadge.setText("Selected Badge: Piggy Bank");
            }
        });
        badge4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBadge.setText("Selected Badge: Cash Pig");
            }
        });

        //return to profile screen
        backToProfileArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //When the user presses the purchase button, check if they already own the badge and if they have enough coins.
        //If so, add to database and set for text view
        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                badgeRef = FirebaseDatabase.getInstance().getReference("users").child(uUsername).child("stats");

                badgeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentBadge = snapshot.child("badge").getValue(Integer.class);
                        int currentCoins = snapshot.child("coins").getValue(Integer.class);
                        int requiredCoins = 0;
                        int selectedBadgeId;
                        int newBadgeDrawable = 0;
                        Log.d(TAG, "Current coins: " + currentCoins + " Current Badge: " + currentBadge);
                        //Check selected badge
                        switch (selectedBadge.getText().toString()) {
                            case "Selected Badge: A Spare Coin":
                                selectedBadgeId = 1;
                                requiredCoins = 300;
                                newBadgeDrawable = R.drawable.badge1;
                                break;
                            case "Selected Badge: Small Change":
                                selectedBadgeId = 2;
                                requiredCoins = 1000;
                                newBadgeDrawable = R.drawable.badge2;
                                break;
                            case "Selected Badge: Piggy Bank":
                                selectedBadgeId = 3;
                                requiredCoins = 5000;
                                newBadgeDrawable = R.drawable.badge3;
                                break;
                            case "Selected Badge: Cash Pig":
                                selectedBadgeId = 4;
                                requiredCoins = 9999;
                                newBadgeDrawable = R.drawable.badge4;
                                break;
                            default:
                                selectedBadgeId = -1;
                                break;
                        }
                        Log.d(TAG, "Selected badge id = " + selectedBadgeId + "Required Coins = " +requiredCoins);

                        //The user has selected the badge they currently have
                        if (selectedBadgeId == currentBadge) {
                            Toast.makeText(getApplicationContext(),"You already own this badge", Toast.LENGTH_SHORT).show();
                            //The user has not selected a badge
                        } else if (selectedBadgeId == -1 ) {
                            Toast.makeText(getApplicationContext(),"Please select a badge", Toast.LENGTH_SHORT).show();
                            //If user wants to upgrade there badge
                        } else if (selectedBadgeId > currentBadge) {
                            //Check if the user has enough coins
                            if (currentCoins >= requiredCoins) {
                                currentCoins = currentCoins - requiredCoins;
                                Log.d(TAG, "new current coin count: " + currentCoins);
                                badgeRef.child("coins").setValue(currentCoins);
                                badgeRef.child("badge img").setValue(newBadgeDrawable);
                                badgeRef.child("badge").setValue(selectedBadgeId);
                                Toast.makeText(getApplicationContext(),"Purchase Successful", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),"Not enough coins! Keep saving!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),"You are better than this!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    //Pre-populates user information
    private void prePopulateViewText() {

        profileRef =  FirebaseDatabase.getInstance().getReference("users").child(uUsername);

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Populates views text
                String tempFirstName = String.valueOf(snapshot.child("first name").getValue());
                String tempCoins = String.valueOf(snapshot.child("stats").child("coins").getValue());
                shopIntroTV.setText("Hi " + tempFirstName + " you have " + tempCoins + " coins available to use!");
                shopProfileImg.setImageResource(snapshot.child("stats").child("badge img").getValue(Integer.class));
                Log.d(TAG, "text views populated");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}