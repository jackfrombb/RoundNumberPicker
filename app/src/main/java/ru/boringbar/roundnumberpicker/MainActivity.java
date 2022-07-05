package ru.boringbar.roundnumberpicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.boringbar.roundnumberpicker.databinding.ActivityMainBinding;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RoundNumberPicker timePicker, timePickerBack;
    private TextView selectedHourText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        selectedHourText = findViewById(R.id.selected_hour);
        timePicker = findViewById(R.id.time_picker);
        timePickerBack = findViewById(R.id.time_picker_back);

        timePicker.setStartPosition(0);
        timePicker.setOnNumberChangeListener(new RoundNumberPicker.OnNumberChangeListener() {
            @Override
            public void onChange(int hour) {
                timePickerBack.removeCursor();
                setSelectedHourText(hour);
            }
        });

        timePickerBack.setStartPosition(12);
        timePickerBack.setOnNumberChangeListener(new RoundNumberPicker.OnNumberChangeListener() {
            @Override
            public void onChange(int hour) {
                timePicker.removeCursor();
                setSelectedHourText(hour);
            }
        });
    }

    private void setSelectedHourText(int hour) {
        if (selectedHourText != null) {
            String text = hour + ":00";
            selectedHourText.setText(text);
        }
    }

}