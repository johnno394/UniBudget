package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;

import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Fragment that allows users to set a new goal
public class GoalFragment extends Fragment implements AdapterView.OnItemSelectedListener {

        private static final String TAG = "GoalFragment";

        DatabaseReference databaseReference;

        private static int DELAY = 400;

        private String uUsername;

        private static Button addBtn;
        private static EditText enterAmountText;
        private static TextView durationTv;
        private Spinner goalLengthSpinner;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_goal, container,false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // instantiate widgets
            // Spinner object
            goalLengthSpinner = view.findViewById(R.id.goalLengthSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.goalLength, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            goalLengthSpinner.setAdapter(adapter);
            goalLengthSpinner.setOnItemSelectedListener(this);
            addBtn = view.findViewById(R.id.addBtn);
            enterAmountText = view.findViewById(R.id.enterAmountEditText);
            enterAmountText.addTextChangedListener(textWatcher);
            durationTv = view.findViewById(R.id.durationTv);

            // set add button to disabled until a duration has been picked by user
            addBtn.setEnabled(false);

            //Retrieve username from home fragment
            uUsername = getActivity().getIntent().getStringExtra("FLAG");
            Log.d(TAG, "username retrieved is " + uUsername);

            //Button to add the new goal into the database
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(TAG, "Add button clicked");
                    // check if any fields are empty
                    if (checkUserFieldsAreEmpty() == true && checkDurationSelected() == true) {
                        Log.d(TAG, "Fields arnt empty");
                        String amount = enterAmountText.getText().toString().trim();
                        checkGoalUniqueThenAddData(amount);
                    }
                }
            });
        }

        //Enables button if the user has entered valid text/numbers
        private TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String edAmountTxt = enterAmountText.getText().toString().trim();
                // setting add button to be enabled when both text fields are not empty
                addBtn.setEnabled(!edAmountTxt.isEmpty());

            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            // get the selected item from user
            String selected = adapterView.getItemAtPosition(position).toString();
            // if no period has been selected inform user with toast messages
            durationTv.setText(selected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) { }

        //Checks if the duration has been selected
        private boolean checkDurationSelected() {
            if (durationTv.getText().equals("No Period Selected")) {
                Toast.makeText(getContext().getApplicationContext(), "Please select a duration", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
        //Method to check if the user fields are empty
        private boolean checkUserFieldsAreEmpty() {
            if(enterAmountText.getText().equals("")) {
                Toast.makeText(getContext().getApplicationContext(), "Please fill in all text fields", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }

    //Checks the database to see if expense name is unique
    private void checkGoalUniqueThenAddData (String goalAmount) {

        Log.d(TAG, "Checking if goal name exists");
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("goals");
        tempRef.orderByChild("current goal").equalTo(goalAmount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Log.d(TAG, "Expense name already exists");
                    Toast.makeText(getContext(), "Expense name already exists, please enter a new one", Toast.LENGTH_SHORT).show();

                } else if (Double.parseDouble(goalAmount) < 0.0) {

                    Log.d(TAG, "goal amount is negative");
                    Toast.makeText(getContext(), "Goal cannot be negative", Toast.LENGTH_SHORT).show();

                } else {
                    //Temp variables
                    String tempDuration;
                    double tempGoalAmount;

                    tempGoalAmount = Double.parseDouble(String.valueOf(enterAmountText.getText()));
                    tempDuration = durationTv.getText().toString().trim();
                    String newDate = getDate(tempDuration);

                    //add to the database
                    Log.d(TAG, "Adding data to database");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("goals");
                    databaseReference.child("current goal").setValue(tempGoalAmount);
                    databaseReference.child("g date").setValue(newDate);

                    // get current date
                    Calendar c = Calendar.getInstance();
                    Date currentDate = c.getTime();
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDateFormatted = DATE_FORMAT.format(currentDate);

                    // adding start date of goal to the database
                    databaseReference.child("start date").setValue(currentDateFormatted);

                    Log.d(TAG, "Goal updated in database");

                    // Toast message to inform user goal has been created
                    Toast.makeText(getContext(),"Goal Created!", Toast.LENGTH_LONG).show();

                    // Clean up UI on success
                    enterAmountText.getText().clear();
                    Log.d(TAG, "text should be cleared");
                    durationTv.setText("No Period Selected");
                    Log.d(TAG, "Invisible text should default");
                    goalLengthSpinner.setSelection(0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //Method that gets the new goal date
    public String getDate(String date) {
        // store current date
        Calendar c = Calendar.getInstance();
        // instantiate current date
        Date future = c.getTime();
        // date format
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (date.equals("1 Week")) {
            // add one week
            c.add(Calendar.DATE, 7);
            future = c.getTime();
        } else if (date.equals("1 Month")) {
            // add one month
            c.add(Calendar.MONTH, 1);
            future = c.getTime();
        } else if (date.equals("3 Months")) {
            // add three months
            c.add(Calendar.MONTH, 3);
            future = c.getTime();
        } else if (date.equals("6 Months")) {
            // add 6 months
            c.add(Calendar.MONTH, 6);
            future = c.getTime();
        } else if (date.equals("1 Year")) {
            // add one year
            c.add(Calendar.YEAR, 1);
            future = c.getTime();
        }
        String futureDate = DATE_FORMAT.format(future);
        return futureDate;
    }
}