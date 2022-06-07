package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

//This fragment displays all expenses the user has in a recycler view
public class ExpenseFragment extends Fragment {

    private static final String TAG = "ExpenseFragment";

    DatabaseReference expenseRef;

    private RecyclerView expenseRecyclerView;
    private ExpenseRecyclerViewAdapater expenseRecyclerViewAdapater;
    private RecyclerView.LayoutManager expenseLayoutManager;
    ArrayList<Expense> expenseList =new ArrayList<Expense>();
    private String uUsername;
    private FloatingActionButton goToAddExpenseButton;
    private TextView fragExpTotalExp;

    // Decimal Format
    DecimalFormat df = new DecimalFormat("#.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Instantiate Elements
        goToAddExpenseButton = view.findViewById(R.id.goToAddExpenseButton);
        fragExpTotalExp = view.findViewById(R.id.fragExpTotalExp);

        // Settings recycler view activity
        expenseRecyclerView = view.findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setHasFixedSize(true);
        expenseLayoutManager = new LinearLayoutManager(getContext());

        //Retrieve username from home fragment
        uUsername = getActivity().getIntent().getStringExtra("FLAG");
        Log.d(TAG, "username retrieved is " + uUsername);

        //Load user expenses into new arraylist
        loadUserExpenses();
        Log.d(TAG, "users expenses loaded into expense text view");
        //Load total user expense
        countTotalExpenseForUser();
        Log.d(TAG, "users expenses loaded into expense list");

        //Button to switch to add expense screen
        goToAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pass username data over
                Intent i = new Intent(getActivity(), AddExpenseActivity.class);
                i.putExtra("username", uUsername);
                Log.d(TAG, "switching to add expense screen");
                startActivity(i);
            }
        });
    }

    //Method to load all user expense using data from Firebase
    private void loadUserExpenses() {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uUsername).child("expenses");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            //Temp variables
            String tempName = "";
            String tempCategory = "";
            String tempAmount = "";
            String tempDate = "";
            int tempCategoryImg = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "loading user expenses");
                for (DataSnapshot ds: snapshot.getChildren()) {

                    //tempAmount = String.valueOf(ds.child("amount").getValue(long.class));
                    tempAmount = String.valueOf(ds.child("amount").getValue());
                    tempName = ds.child("desc").getValue(String.class);
                    tempDate = ds.child("date").getValue(String.class);
                    tempCategory = ds.child("category").getValue(String.class);

                    //Load image depending on which category
                    switch (tempCategory) {
                        case "food":
                            tempCategoryImg = R.drawable.dish;
                            break;
                        case "entertainment":
                            tempCategoryImg = R.drawable.theater;
                            break;
                        case "travel":
                            tempCategoryImg = R.drawable.globe;
                            break;
                        case "shopping":
                            tempCategoryImg = R.drawable.shopping_cart;
                            break;
                        case "other":
                            tempCategoryImg = R.drawable.other;
                            break;
                    }

                    expenseList.add(new Expense(tempName, tempAmount, tempDate, tempCategoryImg));
                    Log.d(TAG, "Expense Added Into Arraylist");
                }

                //Load expense list into adapter
                expenseRecyclerViewAdapater = new ExpenseRecyclerViewAdapater(expenseList);
                expenseRecyclerView.setLayoutManager(expenseLayoutManager);
                expenseRecyclerView.setAdapter(expenseRecyclerViewAdapater);
                Log.d(TAG, String.valueOf(expenseList));

                //Switch screens and pass info into edit expense activity
                expenseRecyclerViewAdapater.setOnItemClickListener(new ExpenseRecyclerViewAdapater.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent i = new Intent(getContext(), EditExpenseActivity.class);
                        i.putExtra("username", uUsername);
                        i.putExtra("expense amount", expenseList.get(position).getExpenseAmount());
                        i.putExtra("expense name", expenseList.get(position).getExpenseName());
                        i.putExtra("expense category", String.valueOf(expenseList.get(position).getCategoryImage()));
                        Log.d(TAG, String.valueOf(expenseList.get(position).getCategoryImage()));
                        startActivity(i);
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
                //int sum = 0;
                double sum = 0.0;
                for (DataSnapshot ds: (snapshot.getChildren())) {
                    sum += ds.child("amount").getValue(Double.class);
                    System.out.println(sum);
                }

                // setting textview with 2 decimal place formatting
                fragExpTotalExp.setText("$" + df.format(sum));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
