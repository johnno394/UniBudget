package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//This activity adds a new expense into the recyclerview and database for the user
public class AddExpenseActivity extends AppCompatActivity {

    private static final String TAG = "AddExpenseActivity";

    DatabaseReference expenseRef, databaseReference;

    private static int DELAY = 400;

    private static EditText expenseNameEditText, expenseAmountEditText;
    private static ImageView categoryTravel, categoryFood, categoryShopping, categoryEntertainment, categoryOther, backArrow;
    private static String uUsername;
    private static Button addExpenseButton;
    private static TextView categoryTV, expFragExpID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        //Instantiating elements
        expenseAmountEditText = findViewById(R.id.expenseAmountEditText);
        expenseNameEditText = findViewById(R.id.expenseNameEditText);
        categoryEntertainment = findViewById(R.id.categoryEntertainment);
        categoryShopping = findViewById(R.id.categoryShopping);
        categoryFood = findViewById(R.id.categoryFood);
        categoryTravel = findViewById(R.id.categoryTravel);
        categoryOther = findViewById(R.id.categoryOther);
        addExpenseButton = findViewById(R.id.addExpenseButton1);
        categoryTV = findViewById(R.id.categoryTV);
        expFragExpID = findViewById(R.id.expFragExpID);
        backArrow = findViewById(R.id.backArrow);

        //Hide action bar
        getSupportActionBar().hide();

        //Retrieve username
        Intent i = getIntent();
        uUsername = i.getStringExtra("username");
        calculateExpenseId(uUsername);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Change category tv depending on the selected category picture
        categoryTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryTV.setText("Travel");
                Toast.makeText(getApplicationContext(), "Selected Category: Travel", Toast.LENGTH_SHORT).show();
            }
        });

        categoryFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 categoryTV.setText("Food");
                 Toast.makeText(getApplicationContext(), "Selected Category: Food", Toast.LENGTH_SHORT).show();
            }
        });

        categoryShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 categoryTV.setText("Shopping");
                 Toast.makeText(getApplicationContext(), "Selected Category: Shopping", Toast.LENGTH_SHORT).show();
            }
        });

        categoryEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 categoryTV.setText("Entertainment");
                 Toast.makeText(getApplicationContext(), "Selected Category: Entertainment", Toast.LENGTH_SHORT).show();
            }
        });

        categoryOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 categoryTV.setText("Other");
                 Toast.makeText(getApplicationContext(), "Selected Category: Other", Toast.LENGTH_SHORT).show();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////

        //Closes activity and goes back to the expense fragment
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //On button click check if fields are entered and enter it in the database
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "add expense button clicked");
                if (checkUserFieldsAreEmpty() == true && checkCategorySelected() == true) {
                    Log.d(TAG, "Fields arnt empty");
                    String tempExpName = expenseNameEditText.getText().toString().trim();

                    //Checks if the expense name meets the criteria and then add it to the firebase
                    checkExpenseNameIsUniqueThenAddData(tempExpName);
                    //Close activity
                    finish();
                }
            }
        });
    }

    //Method to check if the user has entered anything into the fields
    private boolean checkUserFieldsAreEmpty() {
        if(expenseNameEditText.getText().equals("") || expenseAmountEditText.getText().equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all text fields", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    //Method to check if the user has selected a category
    private boolean checkCategorySelected() {
        if (categoryTV.getText().equals("Category")) {
            Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //Method loops through and counts how many expenses there are for the user to get the next expense id
    private void calculateExpenseId(String uUsername){
        expenseRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");

        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Count how many expenses the users has
                int numberOfExpensesCount = (int) snapshot.getChildrenCount();
                Log.d(TAG, "Total expense count = " + numberOfExpensesCount);
                expFragExpID.setText(String.valueOf(numberOfExpensesCount + 1));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Checks the database to see if expense name is unique
    private void checkExpenseNameIsUniqueThenAddData(String tempExpName) {

        Log.d(TAG, "Checking if expense name exists");
        DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");
        tempRef.orderByChild("desc").equalTo(tempExpName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    Log.d(TAG, "Expense name already exists");
                    Toast.makeText(getApplicationContext(), "Expense name already exists, please enter a new one", Toast.LENGTH_SHORT).show();

                } else {
                    //Temp variables
                    String tempCategory, tempExpenseId;
                    double tempExpAmount;

                    tempExpAmount = Double.parseDouble(String.valueOf(expenseAmountEditText.getText()));
                    tempCategory = categoryTV.getText().toString().trim();
                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    //Count how many entries there are currently and put it into a hidden textview
                    tempExpenseId = expFragExpID.getText().toString();
                    Log.d(TAG, "Next expense ID to be added is " + tempExpenseId);

                    //add to the database
                    Log.d(TAG, "Adding data to database");
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");
                    databaseReference.child("expense id " + tempExpenseId).child("desc").setValue(tempExpName);
                    databaseReference.child("expense id " + tempExpenseId).child("amount").setValue(tempExpAmount);
                    databaseReference.child("expense id " + tempExpenseId).child("category").setValue(tempCategory.toLowerCase(Locale.ROOT));
                    databaseReference.child("expense id " + tempExpenseId).child("date").setValue(date);
                    Toast.makeText(getApplicationContext(), "Expense added", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "New expense added into database");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}