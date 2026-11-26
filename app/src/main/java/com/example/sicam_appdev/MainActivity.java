package com.example.sicamandroid;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.sicamandroid.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LabCheckLifecycle";
    private static final String KEY_LOG = "key_lifecycle_log";

    public static final String EXTRA_STATION_CODE = "extra_station_code";
    public static final String EXTRA_STATUS = "extra_status";
    public static final String EXTRA_ISSUE_NOTE = "extra_issue_note";

    private static final String MAINTENANCE_PHONE = "";

    private ActivityMainBinding binding;

    private final StringBuilder traceBuilder = new StringBuilder();

    private final String[] stations = {
            "PC-07",
            "PC-08",
            "PC-09"
    };

    private int currentStationIndex = 0;

    private String currentCode = "PC-07";
    private String currentStatus = "Needs Check";
    private String currentNote = "Emulator slow to boot";

    private final ActivityResultLauncher<Intent> detailLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            String updatedStatus = result.getData()
                                    .getStringExtra(EXTRA_STATUS);

                            String updatedNote = result.getData()
                                    .getStringExtra(EXTRA_ISSUE_NOTE);

                            if (updatedStatus != null) {
                                currentStatus = updatedStatus;
                            }

                            if (updatedNote != null) {
                                currentNote = updatedNote;
                            }

                            binding.tvStationStatus.setText(currentStatus);

                            binding.tvStationSpec.setText(
                                    "Note: " + currentNote
                            );

                            Toast.makeText(
                                    this,
                                    "Updated " + currentCode + " -> "
                                            + currentStatus,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            traceBuilder.append(
                    savedInstanceState.getString(KEY_LOG, "")
            );
        }

        recordEvent("onCreate");

        updateStationDisplay();

        // Original Lifecycle Logger
        binding.btnSimulateTick.setOnClickListener(v ->
                recordEvent(
                        "Manual check-in probe (pkg="
                                + getPackageName() + ")"
                )
        );

        binding.btnClearLog.setOnClickListener(v -> {
            traceBuilder.setLength(0);
            binding.tvLifecycleLog.setText("");
        });

        // Enhancement 1: Switch Stations
        binding.btnSwitchStation.setOnClickListener(v -> {

            currentStationIndex++;

            if (currentStationIndex >= stations.length) {
                currentStationIndex = 0;
            }

            currentCode = stations[currentStationIndex];

            currentStatus = "Needs Check";
            currentNote = "No inspection yet";

            updateStationDisplay();

            Toast.makeText(
                    this,
                    "Selected station: " + currentCode,
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Explicit Intent: Inspect Station
        binding.btnInspectStation.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    StationDetailActivity.class
            );

            intent.putExtra(EXTRA_STATION_CODE, currentCode);
            intent.putExtra(EXTRA_STATUS, currentStatus);
            intent.putExtra(EXTRA_ISSUE_NOTE, currentNote);

            detailLauncher.launch(intent);
        });

        // Implicit Intent: Share Report
        binding.btnShareReport.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "LabCheck Report: " + currentCode
            );

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Station: " + currentCode
                            + "\nStatus: " + currentStatus
                            + "\nNote: " + currentNote
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Station Status"
                    )
            );
        });

        // Enhancement 2: Quick Support
        binding.btnQuickSupport.setOnClickListener(v -> {

            Intent supportIntent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + MAINTENANCE_PHONE)
            );

            startActivity(supportIntent);
        });
    }

    private void updateStationDisplay() {

        binding.tvStationCode.setText(
                currentCode + " - Android Studio SDK"
        );

        binding.tvStationStatus.setText(currentStatus);

        binding.tvStationSpec.setText(
                "Note: " + currentNote
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        recordEvent("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordEvent("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordEvent("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        recordEvent("onStop");
    }

    @Override
    protected void onDestroy() {
        recordEvent("onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_LOG, traceBuilder.toString());
    }

    private void recordEvent(String eventName) {

        String ts = new SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        String line = "[" + ts + "] " + eventName + "\n";

        traceBuilder.append(line);

        Log.d(TAG, line.trim());

        if (binding != null) {
            binding.tvLifecycleLog.setText(
                    traceBuilder.toString()
            );
        }
    }
}
