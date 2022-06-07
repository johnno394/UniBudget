package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//This
public class DetailedModuleActivity extends YouTubeBaseActivity {

    private static final String TAG = "DetailedModuleActivity";

    private DatabaseReference moduleRef, urlRef, rewardRef;

    private static String uUsername,  moduleID, moduleUrl;
    private TextView moduleNameTv, introTv, questionTv, optionATv, optionBTv, optionCTv, selectedOptionTv;
    private ImageButton backToModuleListArrow;
    private Button playVideoButton, startQuizButton, submitButton;
    private ConstraintLayout clQuestionCard;
    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mYouTubeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_module);

        //Instantiate elements
        moduleNameTv = findViewById(R.id.moduleNameTv);
        backToModuleListArrow = findViewById(R.id.backToModuleListArrow);
        playVideoButton = findViewById(R.id.playVideoButton);
        mYouTubePlayerView = findViewById(R.id.YTModule);
        introTv = findViewById(R.id.introTv);
        questionTv = findViewById(R.id.questionTv);
        optionATv = findViewById(R.id.optionATv);
        optionBTv = findViewById(R.id.optionBTv);
        optionCTv = findViewById(R.id.optionCTv);
        selectedOptionTv = findViewById(R.id.selectedOptionTv);
        startQuizButton = findViewById(R.id.startQuizButton);
        submitButton = findViewById(R.id.submitButton);
        clQuestionCard = findViewById(R.id.rlQuestionContainer);

        //retrieve values
        Intent i = getIntent();
        uUsername = i.getStringExtra("username");
        moduleID = (i.getStringExtra("module ID"));
        Log.d(TAG, "Retrieved Values are: " + uUsername + " " + moduleID);

        //Reset text views
        resetTvs();

        //Loads youtube video and then hides play video to button and shows the quiz me button
        mYouTubeListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "Loading the correct URL");

                //loop through database to find url
                urlRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("modules").child("module id: " + moduleID);

                urlRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        moduleUrl = snapshot.child("url").getValue(String.class);
                        youTubePlayer.loadVideo(moduleUrl);

                        //Hide play video button and activate the start quiz button
                        playVideoButton.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) { }
        };

        //Plays youtube video upon click
        playVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Play button pressed");
                mYouTubePlayerView.initialize(YoutubeConfig.getApiKey(), mYouTubeListener);
                startQuizButton.setVisibility(View.VISIBLE);
            }
        });

        //Once button is clicked, load the questions depending on which module it is
        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Start Quiz Button Clicked");
                //load questions before making all text visible

                switch (moduleID) {

                    //Module = Importance of budgeting
                    case "1":
                        Log.d(TAG, "Loading Question for Module " + moduleID);
                        //Load text
                        questionTv.setText("Budgets help you:");
                        optionATv.setText("A: Spend more than you earn");
                        optionBTv.setText("B: Spend less than you earn");
                        optionCTv.setText("C: Have less fun");
                        Log.d(TAG, "Questions loaded");
                        break;
                    case "2":
                        Log.d(TAG, "Loading Question for Module " + moduleID);
                        //Load text
                        questionTv.setText("The following are good ways of helping you save money except:");
                        optionATv.setText("A: Cash envelope system");
                        optionBTv.setText("B: Balanced money formula");
                        optionCTv.setText("C: Pay-as-you-go budget");
                        Log.d(TAG, "Questions loaded");
                        break;
                    case "3":
                        Log.d(TAG, "Loading Question for Module " + moduleID);
                        //Load text
                        questionTv.setText("Which of the following is not true:");
                        optionATv.setText("A: 5% p.a Interest on a $10,000 loan means you will pay $5,000 in the first year");
                        optionBTv.setText("B: The interest of your savings in the bank will compound");
                        optionCTv.setText("C: You will need to pay interest if you borrow money from the bank");
                        Log.d(TAG, "Questions loaded");
                        break;
                    case "4":
                        Log.d(TAG, "Loading Question for Module " + moduleID);
                        //Load text
                        questionTv.setText("Which of the following is not true:");
                        optionATv.setText("A: Buying shares in a company means you are a part owner");
                        optionBTv.setText("B: The stock market always goes up");
                        optionCTv.setText("C: You can make money through dividends and capital gains");
                        Log.d(TAG, "Questions loaded");
                        break;
                    case "5":
                        Log.d(TAG, "Loading Question for Module " + moduleID);
                        //Load text
                        questionTv.setText("Which of the following is true:");
                        optionATv.setText("A: The bank can take ownership of your home if you can't repay your loan");
                        optionBTv.setText("B: A down payment usually has to be 50% of the loan");
                        optionCTv.setText("C: Variable interest rates are safer than fixed rates");
                        Log.d(TAG, "Questions loaded");
                        break;
                }

                //Make text fields visible
                startQuizButton.setVisibility(View.INVISIBLE);
                ShowAllQuestionTV();
            }
        });
        //Onclick take the user back to the learn fragment
        backToModuleListArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //When a user clicks on an option, changed selected option text to match
        optionATv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Option A Selected", Toast.LENGTH_SHORT).show();
                selectedOptionTv.setText("Selected Option: A");
                Log.d(TAG, "Option A Selected");
            }
        });

        optionBTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Option B Selected", Toast.LENGTH_SHORT).show();
                selectedOptionTv.setText("Selected Option: B");
                Log.d(TAG, "Option B Selected");
            }
        });

        optionCTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Option C Selected", Toast.LENGTH_SHORT).show();
                selectedOptionTv.setText("Selected Option: C");
                Log.d(TAG, "Option C Selected");
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////

        //Check if submitted answers is correct
        //If TRUE: reward user with coins,exp and set module as completed -> transition back to learning fragment (recycler view should update to not show that)
        //and hides all text fields again
        //If FALSE: Show user toast message to try again and clear selected option TV
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "SelectedOptionTv is currently " + selectedOptionTv.getText().toString());
                Log.d(TAG, "Module ID is: " + moduleID);
                //Checks if answers are correct depending on module and selected option
                switch (moduleID) {
                    case "1":
                    case "4":
                        if (selectedOptionTv.getText().toString().equals("Selected Option: B")) {
                            //User is correct
                            rewardUser();
                            break;
                        } else {
                            incorrectAnswer();
                            break;
                        }
                    case "2":
                        if (selectedOptionTv.getText().toString().equals("Selected Option: C")) {
                            //User is correct
                            rewardUser();
                            break;
                        } else {
                            incorrectAnswer();
                            break;
                        }
                    case "3":
                    case "5":
                        if (selectedOptionTv.getText().toString().equals("Selected Option: A")) {
                            //User is correct
                            rewardUser();
                            break;
                        } else {
                            incorrectAnswer();
                            break;
                        }
                }
            }
        });

        //Load data according to which module is selected
        moduleRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("modules").child("module id: " + moduleID);
        moduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moduleNameTv.setText(snapshot.child("name").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Hides all questions tvs and reset to original view
    private void resetTvs() {

        Log.d(TAG, "Resetting text views to default");
        //Hide all question related TVs
        introTv.setVisibility(View.INVISIBLE);
        questionTv.setVisibility(View.INVISIBLE);
        optionATv.setVisibility(View.INVISIBLE);
        optionBTv.setVisibility(View.INVISIBLE);
        optionCTv.setVisibility(View.INVISIBLE);
        selectedOptionTv.setVisibility(View.INVISIBLE);
        submitButton.setVisibility(View.INVISIBLE);
        startQuizButton.setVisibility(View.INVISIBLE);
        clQuestionCard.setVisibility(View.INVISIBLE);

        //Reshow play vide button
        playVideoButton.setVisibility(View.VISIBLE);
    }

    //Method to show all textviews and buttons related to the quiz
    private void ShowAllQuestionTV() {
        Log.d(TAG, "Making question text views visible");
        //Hide all question related TVs
        introTv.setVisibility(View.VISIBLE);
        questionTv.setVisibility(View.VISIBLE);
        optionATv.setVisibility(View.VISIBLE);
        optionBTv.setVisibility(View.VISIBLE);
        optionCTv.setVisibility(View.VISIBLE);
        selectedOptionTv.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        startQuizButton.setVisibility(View.INVISIBLE);
        clQuestionCard.setVisibility(View.VISIBLE);
    }

    //Method to reward the user exp, coins and set the module to complete -- ONLY IF ANSWER IS CORRECT
    private void rewardUser() {
        Log.d(TAG, "User is correct - Rewarding user");
        rewardRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername);
        rewardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //check if user has completed the module before
                if (snapshot.child("modules").child("module id: " + moduleID).child("status").getValue(String.class).equals("Completed")) {
                    Log.d(TAG, "User has already completed the module - No rewards");
                    Toast.makeText(getApplicationContext(), "You have already completed this module!", Toast.LENGTH_SHORT).show();

                } else {
                    //User has not completed this module before
                    //Temp variables
                    int tempCoins = 0, tempExp = 0, tempLevel = 0;
                    String tempModuleStatus;

                    //Retrieve current values
                    tempCoins = snapshot.child("stats").child("coins").getValue(Integer.class);
                    tempExp = snapshot.child("stats").child("exp").getValue(Integer.class);
                    tempLevel = snapshot.child("stats").child("level").getValue(Integer.class);
                    Log.d(TAG, "Retrieved current user stats:");

                    //Update values
                    tempCoins = tempCoins + 100;
                    tempExp = tempExp + 500;
                    //Calculates the level by dividing by 1000 - each lvl = 1000 exp
                    tempLevel = 1 + Math.round(tempExp/1000);

                    //Set new values
                    rewardRef.child("stats").child("coins").setValue(tempCoins);
                    rewardRef.child("stats").child("exp").setValue(tempExp);
                    rewardRef.child("stats").child("level").setValue(tempLevel);
                    Log.d(TAG, "Updated user stats");

                    //Set module as completed
                    rewardRef.child("modules").child("module id: " + moduleID).child("status").setValue("Completed");
                    Log.d(TAG, "Updated module " + moduleID + "'s status to completed");

                    //Close activity and go back to learn fragment after a delay
                    Log.d(TAG, "Closing activity");
                    Toast.makeText(getApplicationContext(), "Correct Answer! Take you back to learn more!", Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //If user selects the wrong answer for the quiz
    private void incorrectAnswer() {
        Log.d(TAG, "User selected incorrect answer");
        Toast.makeText(getApplicationContext(), "Incorrect Answer! Please Try Again", Toast.LENGTH_SHORT).show();
        selectedOptionTv.setText("Try Again!");
    }

}