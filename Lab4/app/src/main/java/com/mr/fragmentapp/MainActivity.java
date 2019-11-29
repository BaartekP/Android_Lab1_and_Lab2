package com.mr.fragmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mr.fragmentapp.tasks.TaskListContent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
        TaskFragment.OnListFragmentInteractionListener,
        DeleteDialog.OnDeleteDialogInteractionListener {

    private TaskListContent.Task currentTask;
    private final String CURRENT_TASK_KEY = "CurrentTask";

    private final String CHECKED_ID = "ckeckedId";
    private final String TASKS_SHARED_PREFS = "TasksSharedPrefs";
    private final String TASKS_TEXT_FILE = "tasks.txt";
    private final String TASKS_JSON_FILE = "tasks.json";
    private final String NUM_TASKS = "NumOfTasks";
    private final String TASK = "task_";
    private final String DETAIL = "desc_";
    private final String PIC = "pic_";
    private final String ID = "id_";

    private void saveTaskToSharedPreferences(){
        SharedPreferences tasks = getSharedPreferences(TASKS_SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = tasks.edit();

        editor.clear();

        editor.putInt(NUM_TASKS,TaskListContent.ITEMS.size());
        for (int i = 0; i<TaskListContent.ITEMS.size();i++){
            TaskListContent.Task task  = TaskListContent.ITEMS.get(i);
            editor.putString(TASK + i,task.title);
            editor.putString(DETAIL + i,task.details);
            editor.putString(PIC + i,task.picPath);
            editor.putString(ID + i,task.id);
        }
        editor.apply();
    }

    private void restoreTaskFromSharedPreferences(){
        SharedPreferences tasks = getSharedPreferences(TASKS_SHARED_PREFS,MODE_PRIVATE);
        int numOfTasks = tasks.getInt(NUM_TASKS,0);
        if(numOfTasks != 0){
            TaskListContent.Task.clearList(); //ZMIANA
            for (int i =0 ;i<numOfTasks; i++){
                String title = tasks.getString(TASK+i,"0");
                String details = tasks.getString(DETAIL+i,"0");
                String picPath = tasks.getString(PIC+i,"0");
                String id = tasks.getString(ID+i,"0");
                TaskListContent.addItem(new TaskListContent.Task(id,title,details,picPath));
            }
        }
    }


    private void saveTasksToJson(){
        Gson gson = new Gson();
        String listJson = gson.toJson(TaskListContent.ITEMS);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(TASKS_JSON_FILE,MODE_PRIVATE);
            FileWriter writer = new FileWriter(outputStream.getFD());
            writer.write(listJson);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void restoreFromJson(){
        FileInputStream inputStream;
        int DEFAULT_BUFFER_SIZE = 10000;
        Gson gson = new Gson();
        String readJson;

        try {
            inputStream = openFileInput(TASKS_JSON_FILE);
            FileReader reader = new FileReader(inputStream.getFD());
            char[] buf = new char[DEFAULT_BUFFER_SIZE];
            int n;
            StringBuilder builder =new StringBuilder();
            while ((n=reader.read(buf))>=0){
                String tmp = String.valueOf(buf);
                String substring = (n<DEFAULT_BUFFER_SIZE)? tmp.substring(0,n):tmp;
                builder.append(substring);
            }
            reader.close();
            readJson = builder.toString();
            Type collectionType = new TypeToken<List<TaskListContent.Task>>(){}.getType();
            List<TaskListContent.Task> o = gson.fromJson(readJson,collectionType);
            if(o != null){
                TaskListContent.clearList();
                for(TaskListContent.Task task : o){
                    TaskListContent.addItem(task);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        RadioGroup saveRadioGroup = findViewById(R.id.saveRadioGroup);
        int checkedRadioButtonId = saveRadioGroup.getCheckedRadioButtonId();
        SharedPreferences tasks = getSharedPreferences(TASKS_SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = tasks.edit();
        editor.putInt(CHECKED_ID,checkedRadioButtonId);
        editor.apply();
        switch (checkedRadioButtonId){
            case R.id.prefsRadioButton:
                saveTaskToSharedPreferences();
                break;
            default:
                saveTasksToJson();
                break;
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if(currentTask != null){
            outState.putParcelable(CURRENT_TASK_KEY,currentTask);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null)
            currentTask = savedInstanceState.getParcelable(CURRENT_TASK_KEY);

        SharedPreferences tasks = getSharedPreferences(TASKS_SHARED_PREFS,MODE_PRIVATE);
        int checkedRadioButton = tasks.getInt(CHECKED_ID,R.id.prefsRadioButton);
        RadioGroup saveRadioGroup = findViewById(R.id.saveRadioGroup);
        saveRadioGroup.check(checkedRadioButton);
        switch (checkedRadioButton){
            case R.id.prefsRadioButton:
                restoreTaskFromSharedPreferences();
                break;
            default:
                restoreFromJson();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if(currentTask != null)
                displayTaskInFragment(currentTask);
        }
    }

    private int currentItemPosition = 1;

    private void showDeleteDiag(){
        DeleteDialog.newInstance().show(getSupportFragmentManager(),getString(R.string.delete_dialog_tag));
    }

    public static final String taskExtra = "taskExtra";

    private void startSecondActivity(TaskListContent.Task task, int position){
        Intent intent = new Intent(this,TaskInfoActivity.class);
        intent.putExtra(taskExtra,task);
        startActivity(intent);
    }

    public void addClick(View view) {
        EditText taskTitleEditTxt = findViewById(R.id.taskTitle);
        EditText taskDescriptionEditTxt = findViewById(R.id.taskDescription);
        Spinner drawableSpinner = findViewById( R.id.drawableSpinner);
        String taskTitle = taskTitleEditTxt.getText().toString();
        String taskDescription = taskDescriptionEditTxt.getText().toString();
        String selectedImage = drawableSpinner.getSelectedItem().toString();

        if(taskTitle.isEmpty() && taskDescription.isEmpty()){
            TaskListContent.addItem(new TaskListContent.Task("Task." + TaskListContent.ITEMS.size() + 1,
                    getString(R.string.default_title),
                    getString(R.string.default_description),
                    selectedImage));
        }else {
            if(taskTitle.isEmpty())
                taskTitle =getString(R.string.default_title);
            if(taskDescription.isEmpty())
                taskDescription = getString(R.string.default_description);

            TaskListContent.addItem(new TaskListContent.Task("Task." + TaskListContent.ITEMS.size() +1,
                    taskTitle,
                    taskDescription,
                    selectedImage));
        }

        ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();

        taskTitleEditTxt.setText("");
        taskDescriptionEditTxt.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

    }

    @Override
    public void onListFragmentClickInteraction(TaskListContent.Task task, int position) {
        currentTask = task;
        Toast.makeText(this,getString(R.string.item_selected_msg) + position,Toast.LENGTH_SHORT).show();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            displayTaskInFragment(task);
        } else {
            startSecondActivity(task, position);
        }
    }

    @Override
    public void onListFragmentLongClickInteraction(int position) {
        Toast.makeText(this,getString(R.string.long_click_msg)+position,Toast.LENGTH_SHORT).show();
        showDeleteDiag();
        currentItemPosition = position;
    }

    private void displayTaskInFragment(TaskListContent.Task task) {
     TaskInfoFragment taskInfoFragment =((TaskInfoFragment) getSupportFragmentManager().findFragmentById(R.id.displayFragment));
        if (taskInfoFragment != null) {
            taskInfoFragment.displaytask(task);
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(currentItemPosition != 1 && currentItemPosition < TaskListContent.ITEMS.size()){
            TaskListContent.removeItem(currentItemPosition);
            ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        View view =findViewById(R.id.addButton);
        if(view != null){
            Snackbar.make(view,getString(R.string.delete_cancel_msg), Snackbar.LENGTH_LONG).setAction(getString(R.string.retry_msg), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDiag();
                }
            }).show();
        }
    }
}


