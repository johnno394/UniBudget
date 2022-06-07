package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> {

    private ArrayList<Modules> moduleList;
    private OnItemClickListener moduleListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public ModuleAdapter(ArrayList<Modules> moduleList) {
        this.moduleList = moduleList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView moduleRowID, moduleTitle;
        public ImageView modulePreviewImage;

        public ViewHolder(@NonNull View itemView, OnItemClickListener moduleListener) {
            super(itemView);

            moduleRowID = itemView.findViewById(R.id.moduleRowID);
            moduleTitle = itemView.findViewById(R.id.moduleTitle);
            modulePreviewImage = itemView.findViewById(R.id.modulePreviewImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moduleListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            moduleListener.onItemClick(view, position);
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_row, parent, false);
        ViewHolder vh = new ViewHolder(v, moduleListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Modules currentItem = moduleList.get(position);

        holder.modulePreviewImage.setImageResource(currentItem.getPreviewImg());
        holder.moduleRowID.setText(String.valueOf("Module ID: " + currentItem.getModuleID()));
        holder.moduleTitle.setText(currentItem.getModuleTitle());
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        moduleListener = listener;
    }


}
