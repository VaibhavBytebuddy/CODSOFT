package com.example.todobudyy.Adaptor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todobudyy.AddNewTask;
import com.example.todobudyy.MainActivity;
import com.example.todobudyy.Model.ToDoModel;
import com.example.todobudyy.R;
import com.example.todobudyy.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdaptor extends RecyclerView.Adapter<ToDoAdaptor.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;

    public ToDoAdaptor(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
        this.todoList = new ArrayList<>();  // Initialize the todoList here
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                db.updateStatus(item.getId(), 1);
            } else {
                db.updateStatus(item.getId(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTasks(List<ToDoModel> toDoList) {
        this.todoList = toDoList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void setItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void deleteItem(int position) {
        ToDoModel task = todoList.get(position);
        db.deleteTask(task.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, todoList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }
}
