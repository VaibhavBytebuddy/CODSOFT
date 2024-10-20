package com.example.todobudyy;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.todobudyy.Model.ToDoModel;
import com.example.todobudyy.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private boolean isUpdate = false; // Track if it's an update

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase(); // Open the database here

        // Check for update arguments
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if (task != null && task.length() > 0) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        }

        // Enable/disable button based on text input
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button click logic
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString().trim();
                if (!text.isEmpty()) {
                    if (isUpdate && bundle != null) {
                        db.updateTask(bundle.getInt("id"), text); // Update task
                        Log.d("Database", "Updating task ID " + bundle.getInt("id") + " to: " + text);
                    } else {
                        ToDoModel taskModel = new ToDoModel();
                        taskModel.setTask(text);
                        taskModel.setStatus(0); // Set default status
                        db.insertTask(taskModel); // Add the new task to the database
                        Log.d("Database", "Inserting task: " + text);
                    }
                    dismiss(); // Close the dialog
                } else {
                    Toast.makeText(getActivity(), "Task cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSaveButtonState() {
        String text = newTaskText.getText().toString().trim();
        boolean isEmpty = text.isEmpty();
        newTaskSaveButton.setEnabled(!isEmpty);
        newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(),
                isEmpty ? R.color.colorPrimary : R.color.colorPrimaryDark));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        db.close(); // Close the database connection when dialog is dismissed
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
        super.onDismiss(dialog); // Call super method
    }
}
