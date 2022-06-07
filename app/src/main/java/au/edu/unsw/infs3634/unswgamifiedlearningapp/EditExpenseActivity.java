package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//This activity changes the values/name/category of a selected expense
public class EditExpenseActivity extends AppCompatActivity {

    private static final String TAG = "EditExpenseActivity";

    private static DatabaseReference expenseRef, rootRef;

    private static int DELAY = 250;

    private static double expenseAmount;
    private static String expenseName, uUsername;
    private static EditText expenseNameEditText, expenseAmountEditText;
    private static ImageView categoryTravel, categoryFood, categoryShopping, categoryEntertainment, categoryOther, backArrow;
    private static Button addExpenseButton;
    private static TextView categoryTV, expFragExpID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        expenseAmountEditText = findViewById(R.id.expenseAmountEditText);
        expenseNameEditText = findViewById(R.id.expenseNameEditText);
        categoryEntertainment = findViewById(R.id.categoryEntertainment);
        categoryShopping = findViewById(R.id.categoryShopping);
        categoryFood = findViewById(R.id.categoryFood);
        categoryTravel = findViewById(R.id.categoryTravel);
        categoryOther = findViewById(R.id.categoryOther);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        categoryTV = findViewById(R.id.categoryTV);
        expFragExpID = findViewById(R.id.expFragExpID);
        backArrow = findViewById(R.id.backArrow);

        //hide action bar
        getSupportActionBar().hide();

        //retrieve values
        Intent i = getIntent();
        uUsername = i.getStringExtra("username");
        expenseAmount = Double.parseDouble(i.getStringExtra("expense amount"));
        expenseName = i.getStringExtra("expense name");

        //Prepopulate user fields
        expenseNameEditText.setText(expenseName);
        expenseAmountEditText.setText(Double.toString(expenseAmount));

        Log.d(TAG, "Retrieved Values are: " + uUsername + expenseName + expenseAmount);

        //Retrieve expense ID
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");
        Query query = rootRef.orderByChild("desc").equalTo(expenseName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String expenseID = ds.getKey();
                    Log.d(TAG, expenseID);
                    //Set expense id into a hidden textView for it to be referenced
                    expFragExpID.setText(expenseID.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //When a user clicks on an option, changed selected option text to match
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
        ////////////////////////////////////////////////////////////////////////////////////////////

        //On button click check if fields have been changed and then edit the entry in the database
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "edit expense button clicked");
                if (checkUserFieldsHaveChanged(expenseName, expenseAmount) == true && checkCategorySelected() == true) {

                    Log.d(TAG, "Fields have been changed");
                    String tempExpName, tempCategory, tempExpenseId;
                    double tempExpAmount;
                    tempExpenseId = expFragExpID.getText().toString();
                    tempExpName = expenseNameEditText.getText().toString().trim();
                    tempExpAmount = Double.parseDouble(String.valueOf(expenseAmountEditText.getText()));
                    tempCategory = categoryTV.getText().toString().trim();

                    //Edit database entry
                    Log.d(TAG, "Adding data to database");
                    expenseRef = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");
                    expenseRef.child(tempExpenseId).child("desc").setValue(tempExpName);
                    expenseRef.child(tempExpenseId).child("amount").setValue(tempExpAmount);
                    expenseRef.child(tempExpenseId).child("category").setValue(tempCategory.toLowerCase(Locale.ROOT));
                    Toast.makeText(getApplicationContext(), "Expense updated", Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });

        // Back button click listener
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Method to check if the user has changed the expense details from the original
    private boolean checkUserFieldsHaveChanged(String expenseName, double expenseAmount) {
        if(expenseNameEditText.getText().equals(expenseName) && expenseAmountEditText.getText().equals(Double.toString(expenseAmount))) {
            Toast.makeText(getApplicationContext(), "Please update expense fields", Toast.LENGTH_SHORT).show();
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
}