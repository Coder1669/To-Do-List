package com.example.todoapp;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.example.todoapp.model.Priority;
import com.example.todoapp.model.SharedViewModel;
import com.example.todoapp.model.Task;
import com.example.todoapp.model.TaskViewModel;
import com.example.todoapp.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    Calendar calendar = Calendar.getInstance();
    private EditText enterTodo;
    private ImageButton calendarButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private SharedViewModel sharedViewModel;
    private boolean isEdit;

    public BottomSheetFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
            Log.d("MY", "onViewCreated: " + isEdit + " " + task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMoth) -> {
            calendar.clear();
            calendar.set(year, month, dayOfMoth);
            dueDate = calendar.getTime();

        });

        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();

            if (!TextUtils.isEmpty(task) && dueDate != null) {
                Task myTask = new Task(task,
                        dueDate, Calendar.getInstance().getTime(),
                        false);
                if (isEdit) {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();
                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);
                } else {
                    TaskViewModel.insert(myTask);
                }
                enterTodo.setText("");
                if (this.isVisible()) {
                    this.dismiss();
                }

            } else {
                Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG)
                        .show();
            }
        });


    }

    @Override
    public void onClick(View view) {
    }
}
