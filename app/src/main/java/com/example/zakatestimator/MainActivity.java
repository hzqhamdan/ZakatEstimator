package com.example.zakatestimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {
    private EditText goldWeightInput, goldValueInput;
    private RadioButton keepRadioButton, wearRadioButton;
    private Button calculateButton;  // Remove final initialization here
    private TextView totalGoldValueText, zakatPayableText, totalZakatText;
    private SwitchCompat switchTheme;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Initialize views after setContentView
        RelativeLayout mainLayout = findViewById(R.id.main);
        mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor));

        goldWeightInput = findViewById(R.id.goldweight);
        goldValueInput = findViewById(R.id.goldvalue);
        keepRadioButton = findViewById(R.id.keep);
        wearRadioButton = findViewById(R.id.wear);
        totalGoldValueText = findViewById(R.id.totalgoldvalue);
        zakatPayableText = findViewById(R.id.zakatpayable);
        totalZakatText = findViewById(R.id.totalzakat);

        // Initialize the calculateButton here after setContentView
        calculateButton = findViewById(R.id.button2);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button aboutButton = findViewById(R.id.about);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        // OnClickListener for calculateButton
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateZakat();
            }
        });

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switchTheme = findViewById(R.id.switch1);

        boolean isDarkMode = preferences.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);

        // Check current mode and set the switch state
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        switchTheme.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);

        // Set listener to handle switch state change
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle dark mode based on switch state
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("dark_mode", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("dark_mode", false);
                }
                editor.apply();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the Zakat Estimator app! GitHub: https://github.com/hzqhamdan/ZakatEstimator");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateZakat() {
        // Get user inputs
        String goldWeightStr = goldWeightInput.getText().toString().trim();
        String goldValueStr = goldValueInput.getText().toString().trim();

        if (goldWeightStr.isEmpty() || goldValueStr.isEmpty() ||
                (!keepRadioButton.isChecked() && !wearRadioButton.isChecked())) {
            totalGoldValueText.setText("Please fill all fields and select a gold type.");
            zakatPayableText.setText("");
            totalZakatText.setText("");
            return;
        }

        double goldWeight = Double.parseDouble(goldWeightStr);
        double goldValue = Double.parseDouble(goldValueStr);

        double nisabKeep = 85.0;
        double nisabWear = 200.0;
        double zakatRate = 0.025;

        double zakatPayableWeight;

        if (keepRadioButton.isChecked()) {
            zakatPayableWeight = goldWeight > nisabKeep ? goldWeight - nisabKeep : 0;
        } else {
            zakatPayableWeight = goldWeight > nisabWear ? goldWeight - nisabWear : 0;
        }

        double totalGoldValue = goldWeight * goldValue;
        double totalZakatPayableValue = zakatPayableWeight * goldValue;
        double totalZakat = totalZakatPayableValue * zakatRate;

        totalGoldValueText.setText(String.format("Total Value of Gold: RM %.2f", totalGoldValue));
        zakatPayableText.setText(String.format("Zakat Payable: RM %.2f", totalZakatPayableValue));
        totalZakatText.setText(String.format("Total Zakat: RM %.2f", totalZakat));
    }
}
