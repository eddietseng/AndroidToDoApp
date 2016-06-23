package com.eddietseng.todo;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditItemActivity extends AppCompatActivity {
    EditText descriptionET;
    EditText dateET;
    RadioGroup priorityGroup;
    TodoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if( actionBar != null )
            actionBar.setDisplayHomeAsUpEnabled( true );

        item = (TodoItem) getIntent().getSerializableExtra("item");
        Log.i( "edit activity" , "item: " + item.toString() );

        descriptionET = (EditText) findViewById(R.id.et_name);
        dateET = (EditText) findViewById(R.id.et_date);
        priorityGroup = (RadioGroup) findViewById(R.id.radioPriority);

        descriptionET.setText( item.getDescription(), TextView.BufferType.EDITABLE);
        dateET.setText( item.getDate(), TextView.BufferType.EDITABLE);

        switch(item.getPriority()){
            case 0:
                priorityGroup.check(R.id.radioButton_high);
                break;

            case 1:
                priorityGroup.check(R.id.radioButton_medium);
                break;

            case 2:
                priorityGroup.check(R.id.radioButton_low);
                break;

            default:
                Log.i( "EditItemActivity" , "can't handle priority: " + item.getPriority() );
                break;
        }

    }

    public void onSubmit(View v) {
        this.finish();
    }

    public void onEditTime(View v) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Integer hr = 0, min = 0;
                if (Build.VERSION.SDK_INT >= 23 ) {
                    hr = timePicker.getHour();
                    min = timePicker.getMinute();
                }
                else {
                    hr = timePicker.getCurrentHour();
                    min = timePicker.getCurrentMinute();
                }

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        hr, min );

                DateFormat format = DateFormat.getDateTimeInstance();

                dateET.setText( format.format(calendar.getTime()), TextView.BufferType.EDITABLE);
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void onEditItem(View v) {
        item.setDescription(descriptionET.getText().toString());
        item.setDate(dateET.getText().toString());
        RadioButton selectedRadioButton =
                (RadioButton)findViewById(priorityGroup.getCheckedRadioButtonId());

        if( selectedRadioButton.getText() != null ) {
            switch (selectedRadioButton.getText().toString()) {
                case "HIGH":
                    item.setPriority(0);
                    break;

                case "MEDIUM":
                    item.setPriority(1);
                    break;

                case "LOW":
                    item.setPriority(2);
                    break;

                default:
                    Log.i("EditItemActivity", "can't handle priority: " +
                            selectedRadioButton.getText().toString());
            }
        }

        Intent data = new Intent();
        data.putExtra("item", item);
        setResult(RESULT_OK, data);
        finish();
    }
}
