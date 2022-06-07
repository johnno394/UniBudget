package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

//Recycler View will holder expense items
public class ExpenseRecyclerViewAdapater extends RecyclerView.Adapter<ExpenseRecyclerViewAdapater.ViewHolder> {
    // Decimal Formatting
    DecimalFormat df = new DecimalFormat("#.00");

    private ArrayList<Expense> expenseList;
    private OnItemClickListener expenseListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public ExpenseRecyclerViewAdapater(ArrayList<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView expenseNameTv, expenseAmountTv, expenseDateTv;
        public ImageView categoryImg;

        public ViewHolder(@NonNull View itemView, OnItemClickListener expenseListener) {
            super(itemView);

            categoryImg = itemView.findViewById(R.id.categoryImage);
            expenseNameTv = itemView.findViewById(R.id.expenseNameTv);
            expenseAmountTv = itemView.findViewById(R.id.expenseAmountTv);
            expenseDateTv = itemView.findViewById(R.id.expenseDateTv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expenseListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            expenseListener.onItemClick(view, position);
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row_view, parent, false);
        ViewHolder vh = new ViewHolder(v, expenseListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense currentItem = expenseList.get(position);

        // parse expense amount to double so then can format it
        double expenseAmountDouble = Double.parseDouble(currentItem.getExpenseAmount());

        holder.expenseDateTv.setText(currentItem.getExpenseDate());
        holder.expenseNameTv.setText(currentItem.getExpenseName());
        holder.expenseAmountTv.setText("$" + df.format(expenseAmountDouble));
        holder.categoryImg.setImageResource(currentItem.getCategoryImage());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        expenseListener = listener;
    }


}
