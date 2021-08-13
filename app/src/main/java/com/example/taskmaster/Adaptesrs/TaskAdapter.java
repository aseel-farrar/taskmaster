package com.example.taskmaster.Adaptesrs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> tasks;
    private OnTaskItemClickListener listener;
    

    public interface OnTaskItemClickListener {
        void onItemClicked(int position);
        void onDeleteItem(int position);
    }

    public TaskAdapter(List<Task> tasks, OnTaskItemClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskName.setText(task.getTaskTitle());
        holder.taskState.setText(task.getTaskState());

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView taskImage;
        private TextView taskName;
        private TextView taskState;
        private ImageView delete;

        ViewHolder(@NonNull View itemView, OnTaskItemClickListener listener) {
            super(itemView);

            taskImage = itemView.findViewById(R.id.task_image);
            taskName = itemView.findViewById(R.id.task_name);
            taskState = itemView.findViewById(R.id.task_state);
            delete = itemView.findViewById(R.id.delete);

            // Go to task details
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(getAdapterPosition());
                }
            });

            // delete task
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteItem(getAdapterPosition());
                }
            });
        }
    }
}
