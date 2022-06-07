package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");
    DatabaseReference expenseRef, moduleRef, goalRef, goalRefForProgressBar, expenseTowardsGoalRef;


    private static String uUsername, uFirstName, uLevel, uCoins, uCurrentGoal;
    private static ProgressBar learningProgressBar, expenseProgressBar;
    private static TextView levelTv, coinsTv, currentGoalProgressTv, currentGoalTotalTv, expenseNumberTv, moduleProgressBarPercentageTv, currentGoalStatusTv, userFirstName;
    private static ImageView userBadgeImg;
    private static Button cashInGoal;
    private static TextView tvDaysRemaining;

    // Decimal Format
    DecimalFormat df = new DecimalFormat("#.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        levelTv = view.findViewById(R.id.levelTv);
        coinsTv = view.findViewById(R.id.coinsTv);
        learningProgressBar = view.findViewById(R.id.learningProgressBar);
        expenseProgressBar = view.findViewById(R.id.progressBarCurrentExpense);


        currentGoalProgressTv = view.findViewById(R.id.currentGoalProgressTv);
        currentGoalTotalTv = view.findViewById(R.id.currentGoalTotalTv);
        expenseNumberTv = view.findViewById(R.id.expenseNumberTv);
        moduleProgressBarPercentageTv = view.findViewById(R.id.moduleProgressBarPercentageTv);
        currentGoalStatusTv = view.findViewById(R.id.currentGoalStatusTv);
        userBadgeImg = view.findViewById(R.id.userBadgeImage);
        userFirstName = view.findViewById(R.id.tvUserFullName);
        cashInGoal = view.findViewById(R.id.cashInGoal);

        // days remaining in goal
        tvDaysRemaining = view.findViewById(R.id.tvDaysRemaining);


        //Retrieve user name from login activity
        uUsername = getActivity().getIntent().getStringExtra("FLAG");
        Log.d(TAG, "Intent received: " + uUsername);
        Log.d(TAG, "Loading user information");
        prePopulateUserFields(uUsername);
        Log.d(TAG, "Loading user expense and goal data");
        countTotalExpenseForUser();
        countExpenseTowardsGoal();
        Log.d(TAG, "Loading module progress");
        loadUserModuleProgressBar();
        Log.d(TAG, "Current Goal " + currentGoalTotalTv.getText().toString());
        Log.d(TAG, "Current Exp " + currentGoalProgressTv.getText().toString());


        //User can get coins and exp for reaching their goal
        //Resets goal and prompts user to make a new goal
        //Default is disabled, once goal is met it is enabled. On click disable button again
        cashInGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Cashing in goal");

                goalRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername);

                goalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear goal
                        goalRef.child("goals").child("current goal").setValue(0);
                        goalRef.child("goals").child("g date").setValue("0000-00-00");
                        goalRef.child("goals").child("start date").setValue("0000-00-00");
                        goalRef.child("goals").child("g status").setValue("Not Completed");
                        Log.d(TAG, "Current Goal Cleared");

                        //reward user
                        int tempCoins = snapshot.child("stats").child("coins").getValue(Integer.class);
                        int tempExp = snapshot.child("stats").child("exp").getValue(Integer.class);
                        goalRef.child("stats").child("coins").setValue(tempCoins + 100);
                        goalRef.child("stats").child("exp").setValue(tempExp + 250);
                        Log.d(TAG, "User rewarded");
                        //disable button
                        cashInGoal.setEnabled(false);

                        Toast.makeText(getContext(), "Cashed in! Create a new goal!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
    //Method to count all expenses within a date
    private void countExpenseTowardsGoal() {
        Log.d(TAG, "counting total user expenses within the goal timeframe");
        expenseTowardsGoalRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername);

        expenseTowardsGoalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tempStartDate, tempEndDate;
                Double tempTotalExpense = 0.00, tempGoal = 0.00;

                Date startDate, endDate, expenseDate, currentDate;

                tempStartDate = snapshot.child("goals").child("start date").getValue(String.class);
                tempEndDate = snapshot.child("goals").child("g date").getValue(String.class);
                tempGoal = snapshot.child("goals").child("current goal").getValue(Double.class);

                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //Convert to Date format
                try {
                    startDate = DATE_FORMAT.parse(tempStartDate);
                    endDate = DATE_FORMAT.parse(tempEndDate);
                    currentDate = new Date();
                    Log.d(TAG, "current date and time is: " + currentDate);
                    Log.d(TAG, "Goal Start Date: " + startDate.toString() + " Goal End Date: " + endDate.toString());

                    //**************************************************
                    long daysTillGoal = endDate.getTime() - currentDate.getTime();
                    Log.d(TAG, "Milliseconds till goal is: "+ daysTillGoal);
                    long difference_In_Days = (daysTillGoal / (1000 * 60 * 60 * 24)) % 365;
                    Log.d(TAG, "Days till goal is: "+ difference_In_Days);
                    tvDaysRemaining.setText(difference_In_Days + " Days left");
                    //**************************************************

                    for (DataSnapshot ds : snapshot.child("expenses").getChildren()) {
                            expenseDate = DATE_FORMAT.parse(ds.child("date").getValue(String.class));
                            if (expenseDate.after(startDate) && expenseDate.before(endDate)) {
                                Double temp = ds.child("amount").getValue(Double.class);
                                tempTotalExpense += temp;
                            } else if (expenseDate.compareTo(startDate) == 0) {
                                Double temp = ds.child("amount").getValue(Double.class);
                                tempTotalExpense += temp;
                            }
                    }
                    Log.d(TAG, "Expense towards goal:  " + tempTotalExpense.toString());
                    currentGoalProgressTv.setText("$" + df.format(tempTotalExpense));

                    //Checks if the user has reached their goal or not
                    if (tempGoal == 0.00) {
                        currentGoalStatusTv.setText("Go to Goal Tab to set your next goal!");
                    } else if (tempTotalExpense <= tempGoal && currentDate.after(endDate) && tempTotalExpense != 0.00) {
                        currentGoalStatusTv.setText("Goal Achieved! Good Work!");
                        Log.d(TAG, "Current goal achieved");
                        cashInGoal.setEnabled(true);
                    } else if (tempTotalExpense >= tempGoal) {
                        currentGoalStatusTv.setText("You spent too much! =(");
                        cashInGoal.setEnabled(false);
                    } else {
                        Log.d(TAG, "Current goal in progress");
                    }

                    //load progress bar
                    loadExpenseProgressBar();

                } catch (ParseException e) { e.printStackTrace(); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    //Counts all the modules that the user has "competed"
    private void loadUserModuleProgressBar() {
        moduleRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("modules");

        moduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int moduleCompletedCount = 0;
                Log.d(TAG, "Total modules is 5");
                //Loop to count how many modules the user has "completed"
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("status").getValue(String.class).equals("Completed")) {
                        moduleCompletedCount += 1;
                    }
                }
                System.out.println(moduleCompletedCount);
                learningProgressBar.setProgress(moduleCompletedCount * 20);
                moduleProgressBarPercentageTv.setText(String.valueOf(moduleCompletedCount * 20 + "%"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Loads user data using username and populates text fields
    private void prePopulateUserFields(String uUsername) {
        //Retrieve user info and set it into profile
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                uFirstName = String.valueOf(snapshot.child("users").child(uUsername).child("first name").getValue());
                uLevel = String.valueOf(snapshot.child("users").child(uUsername).child("stats").child("level").getValue());
                uCoins = String.valueOf(snapshot.child("users").child(uUsername).child("stats").child("coins").getValue());
                uCurrentGoal = df.format(snapshot.child("users").child(uUsername).child("goals").child("current goal").getValue());
                userBadgeImg.setImageResource(snapshot.child("users").child(uUsername).child("stats").child("badge img").getValue(Integer.class));

                userFirstName.setText("Hey " + uFirstName);
                levelTv.setText("Level " + uLevel);
                coinsTv.setText(uCoins + " Coins");
                //currentGoalTotalTv.setText(uCurrentGoal);
                currentGoalTotalTv.setText("$" + uCurrentGoal);
                Log.d(TAG, "User information loaded");



         }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //All code below is used to count how many expenses for the user and the total amount of expenses
    private void countTotalExpenseForUser() {

        expenseRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");

        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Count how many expenses the users has
                int numberOfExpensesCount = (int) snapshot.getChildrenCount();
                Log.d(TAG, "Total expense count = " + numberOfExpensesCount);
                //Loop to count how much money spent total for number of expenses ids
                Log.d(TAG, "counting total user expenses");
                //Long sum = Long.valueOf(0);
                double sum = Double.valueOf(0.0);
                for (DataSnapshot ds: (snapshot.getChildren())) {
                    sum += ds.child("amount").getValue(Double.class);
                    System.out.println(sum);
                }

                expenseNumberTv.setText("$" + df.format(sum));
                //Remove this once calculated new expense
                //currentGoalProgressTv.setText("$" + df.format(sum));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void loadExpenseProgressBar() {
        expenseRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername);
        goalRefForProgressBar = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("goals");

        // Listener to set progress bar max
        goalRefForProgressBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double currentGoal = snapshot.child("current goal").getValue(Double.class);
                int intCurrentGoal = (int) currentGoal;
                Log.d(TAG, "Current goal is set as: "+ intCurrentGoal);
                expenseProgressBar.setMax(intCurrentGoal);

                // Listener to set progress bar for expense as a proportion of goal
                expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tempStartDate, tempEndDate;
                        double tempTotalExpense = 0.00, tempGoal = 0.00;
                        Date startDate, endDate, expenseDate;

                        tempStartDate = snapshot.child("goals").child("start date").getValue(String.class);
                        tempEndDate = snapshot.child("goals").child("g date").getValue(String.class);
                        tempGoal = snapshot.child("goals").child("current goal").getValue(Double.class);

                        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                        //Convert to Date format
                        try {
                            startDate = DATE_FORMAT.parse(tempStartDate);
                            endDate = DATE_FORMAT.parse(tempEndDate);

                            for (DataSnapshot ds : snapshot.child("expenses").getChildren()) {
                                expenseDate = DATE_FORMAT.parse(ds.child("date").getValue(String.class));

                                if (expenseDate.after(startDate) && expenseDate.before(endDate)) {
                                    Double temp = ds.child("amount").getValue(Double.class);
                                    tempTotalExpense += temp;
                                } else if (expenseDate.compareTo(startDate) == 0) {
                                    Double temp = ds.child("amount").getValue(Double.class);
                                    tempTotalExpense += temp;
                                }
                            }
                            int flag = (int) tempTotalExpense;

                            expenseProgressBar.setProgress(flag);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                        @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
