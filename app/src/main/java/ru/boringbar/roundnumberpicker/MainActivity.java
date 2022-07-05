package ru.boringbar.roundnumberpicker;

import androidx.appcompat.app.AppCompatActivity;
import ru.boringbar.roundnumberpicker.databinding.ActivityMainBinding;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private RoundNumberPicker timePicker;
    private TextView selectedHourText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ru.boringbar.roundnumberpicker.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        selectedHourText = binding.selectedHour;
        timePicker = binding.timePicker;
        RoundNumberPicker timePickerBack = binding.timePickerBack;
        timePicker.setStartPosition(0);
        timePicker.setOnNumberChangeListener(hour -> {
            timePickerBack.removeCursor();
            setSelectedHourText(hour);
        });
        timePickerBack.setStartPosition(12);
        timePickerBack.setOnNumberChangeListener(hour -> {
            timePicker.removeCursor();
            setSelectedHourText(hour);
        });
    }

    private void setSelectedHourText(int hour) {
        if (selectedHourText != null) {
            String text = hour + ":00";
            selectedHourText.setText(text);
        }
    }

}