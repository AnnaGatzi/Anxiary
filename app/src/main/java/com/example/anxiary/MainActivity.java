package com.example.anxiary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    String AudioSavePathInDevice = null;
    MediaRecorder _recorder;
    public Timer timer;

    File AccelerationSavePathInDevice = null;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener _SensorEventListener;

    private float x = 0;
    private float y = 0;
    private float z = 0;

    String strx;
    String stry;
    String strz;
    StringBuffer sb2;
    FileOutputStream output1 = null;

    StringBuffer sb3;
    FileOutputStream output2 = null;
    File LabelsSavePathInDevice = null;

    private Button stop_button;
    private Button start_button;

    private Spinner location_spinner;
    private Spinner sleep_mode_spinner;
    private Spinner anxiety_level_spinner;
    private Spinner heart_rate_spinner;

    private ArrayAdapter<CharSequence> location_adapter;
    private ArrayAdapter<CharSequence> sleep_mode_adapter;
    private ArrayAdapter<CharSequence> anxiety_level_adapter;
    private ArrayAdapter<CharSequence> heart_rate_adapter;

    private String location_value = null;
    private String sleep_mode_value = null;
    private String anxiety_level_value = null;
    private String heart_rate_value = null;

    private SimpleDateFormat dateFormat=null;
    private String todayDate=null;


    protected LocationManager locationManager;
    protected MyLocationListener locationListener;
    boolean checkGPS = false;
    boolean checkNetPr = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000;
    Location loc;
    double latitude;
    double longitude;
    String strlat;
    String strlong;
    StringBuffer sb1;
    File LocationSavePathInDevice = null;
    FileOutputStream output = null;
    boolean append = true;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;

    MainActivity cdip;

    File HeartRateSavePathInDevice = null;
    private String h_r = null;
    private String text1 = null;
    private String text2 = null;
    StringBuffer sb4;
    FileOutputStream output3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();
        stop_button = (Button) findViewById(R.id.stop_button);
        start_button = (Button) findViewById(R.id.start_button);
        stop_button.setEnabled(false);
        start_button.setEnabled(true);
        cdip = MainActivity.this;

        location_spinner = (Spinner) findViewById(R.id.location_spinner);
        location_adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        location_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location_spinner.setAdapter(location_adapter);

        sleep_mode_spinner = (Spinner) findViewById(R.id.sleep_mode);
        sleep_mode_adapter = ArrayAdapter.createFromResource(this,
                R.array.sleep_mode_array, android.R.layout.simple_spinner_item);
        sleep_mode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sleep_mode_spinner.setAdapter(sleep_mode_adapter);

        anxiety_level_spinner = (Spinner) findViewById(R.id.anxiety_level);
        anxiety_level_adapter = ArrayAdapter.createFromResource(this,
                R.array.anxiety_level_array, android.R.layout.simple_spinner_item);
        anxiety_level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anxiety_level_spinner.setAdapter(anxiety_level_adapter);

        heart_rate_spinner = (Spinner) findViewById(R.id.heart_rate_spinner);
        heart_rate_adapter = ArrayAdapter.createFromResource(this,
                R.array.heart_rate_array, android.R.layout.simple_spinner_item);
        heart_rate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heart_rate_spinner.setAdapter(heart_rate_adapter);

        stop_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                stop_button.setEnabled(false);
                start_button.setEnabled(true);
                stopListener();
                timer.cancel();
                _recorder.stop();
                _recorder.release();
                locationListener = null;
                locationManager = null;
                accelerometer = null;
                sensorManager = null;
                _SensorEventListener = null;
                x = 0;
                y = 0;
                z = 0;
                _recorder = null;
                onStop();
            }
        });

        start_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

                stop_button.setEnabled(true);
                start_button.setEnabled(false);
                locationListener = new MyLocationListener();
                random = new Random();


                if (checkPermission()) {

                    dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
                    todayDate = dateFormat.format(new Date());

                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + todayDate + "_AudioRecording.mp4";
                    MediaRecorderReady();

                    try {
                        _recorder.prepare();
                        _recorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    LocationSavePathInDevice = new File(
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + todayDate +
                                    "_LocationData.csv");

                    AccelerationSavePathInDevice = new File(
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + todayDate +
                                    "_AccelerationData.csv");

                    LabelsSavePathInDevice = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + todayDate +
                            "_Labels.csv");



                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run()
                        {
                            cdip.runOnUiThread(new Runnable() {

                                public void run() {
                                    getSpinnerValues();
                                    getLocation();
                                    getAccelerometerValues();
                                }

                            });

                        }
                    },0, 4096);

                } else {
                    requestPermission();
                }
            }
        });
    }

    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();}

    public void stopListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void MediaRecorderReady(){
        _recorder=new MediaRecorder();
        _recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        _recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        _recorder.setOutputFile(AudioSavePathInDevice);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = false;
                    boolean RecordPermission = false;
                    boolean GpsPermission = false;
                    boolean CoPermission = false;
                    if (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        StoragePermission = true;
                    }

                    if (ContextCompat.checkSelfPermission(MainActivity.this, RECORD_AUDIO)
                            == PackageManager.PERMISSION_GRANTED) {
                        RecordPermission = true;
                    }

                    if (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        GpsPermission = true;
                    }

                    if (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        CoPermission = true;
                    }


                    if (StoragePermission && RecordPermission && GpsPermission && CoPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this.getApplicationContext(), RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_COARSE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }

    public void getLocation() {
        if (checkPermission()) {
            try {
                locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(LOCATION_SERVICE);
                checkGPS = locationManager
                        .isProviderEnabled(GPS_PROVIDER);
                checkNetPr = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!(checkGPS || checkNetPr)) {
                    Toast.makeText(getApplicationContext(), "No Service Provider is available", Toast.LENGTH_SHORT).show();
                } else {
                    this.canGetLocation = true;
                    locationManager.requestLocationUpdates(
                            GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        loc = locationManager
                                .getLastKnownLocation(GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }else{
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();}
                        }

                        strlat = String.valueOf(latitude);
                        strlong = String.valueOf(longitude);
                        locationManager.removeUpdates(locationListener);
                        sb1 = new StringBuffer();

                        Date currentTime = Calendar.getInstance().getTime();

                        try {
                            output = new FileOutputStream(LocationSavePathInDevice, append);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();

                        }
                        if (LocationSavePathInDevice.exists()) {


                            if (LocationSavePathInDevice.canWrite()) {
                                try {
                                    output.write(sb1.toString().getBytes());
//
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sb1.delete(0, sb1.length());
                            }

                            sb1.append("\"" + currentTime + "\"");
                            sb1.append(",");
                            sb1.append("\"" + strlat + "\"");
                            sb1.append(",");
                            sb1.append("\"" + strlong + "\"" + '\n');
//

                            if (LocationSavePathInDevice.canWrite()) {
                                try {
                                    output.write(sb1.toString().getBytes());
//
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                sb1.delete(0, sb1.length());
                            }
                        } else {
                            try {
                                LocationSavePathInDevice.createNewFile();


                                if (LocationSavePathInDevice.canWrite()) {
                                    try {
                                        output.write(sb1.toString().getBytes());
//
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    sb1.delete(0, sb1.length());
                                }

                                sb1.append("\"" + currentTime + "\"");
                                sb1.append(",");
                                sb1.append("\"" + strlat + "\"");
                                sb1.append(",");
                                sb1.append("\"" + strlong + "\"" + '\n');
//
                                if (LocationSavePathInDevice.canWrite()) {
                                    try {
                                        output.write(sb1.toString().getBytes());

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    sb1.delete(0, sb1.length());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getAccelerometerValues()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {


            _SensorEventListener=   new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    sensorManager.unregisterListener(_SensorEventListener);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            sensorManager.registerListener(_SensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        } else
        {
            Toast.makeText(getApplicationContext(), "No Accelerometer is available", Toast.LENGTH_SHORT).show();
        }
        strx = String.valueOf(x);
        stry = String.valueOf(y);
        strz = String.valueOf(z);

        sb2 = new StringBuffer();

        Date currentTime = Calendar.getInstance().getTime();

        try {
            output1 = new FileOutputStream(AccelerationSavePathInDevice, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (AccelerationSavePathInDevice.exists()) {


            if (AccelerationSavePathInDevice.canWrite()) {
                try {
                    output1.write(sb2.toString().getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb2.delete(0, sb2.length());
            }

            sb2.append("\"" +currentTime + "\"");
            sb2.append(",");
            sb2.append("\"" + strx + "\"");
            sb2.append(",");
            sb2.append("\"" + stry + "\"");
            sb2.append(",");
            sb2.append("\"" + strz + "\"" + '\n');

            if (AccelerationSavePathInDevice.canWrite()) {
                try {
                    output1.write(sb2.toString().getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb2.delete(0, sb2.length());
            }

        }else {
            try {
                AccelerationSavePathInDevice.createNewFile();


                if (AccelerationSavePathInDevice.canWrite()) {
                    try {
                        output1.write(sb2.toString().getBytes());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb2.delete(0, sb2.length());
                }

                sb2.append("\"" +currentTime + "\"");
                sb2.append(",");
                sb2.append("\"" + strx + "\"");
                sb2.append(",");
                sb2.append("\"" + stry + "\"");
                sb2.append(",");
                sb2.append("\"" + strz + "\"" + '\n');

                if (AccelerationSavePathInDevice.canWrite()) {
                    try {
                        output1.write(sb2.toString().getBytes());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb2.delete(0, sb2.length());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void getSpinnerValues()
    {
        location_value = location_spinner.getItemAtPosition(location_spinner.getSelectedItemPosition()).toString();
        sleep_mode_value = sleep_mode_spinner.getItemAtPosition(sleep_mode_spinner.getSelectedItemPosition()).toString();
        anxiety_level_value = anxiety_level_spinner.getItemAtPosition(anxiety_level_spinner.getSelectedItemPosition()).toString();
        heart_rate_value = heart_rate_spinner.getItemAtPosition(heart_rate_spinner.getSelectedItemPosition()).toString();

        sb3 = new StringBuffer();

        Date currentTime = Calendar.getInstance().getTime();

        try {
            output2 = new FileOutputStream(LabelsSavePathInDevice, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        if (LabelsSavePathInDevice.exists()) {


            if (LabelsSavePathInDevice.canWrite()) {
                try {
                    output2.write(sb3.toString().getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb3.delete(0, sb3.length());
            }

            sb3.append("\"" +currentTime + "\"");
            sb3.append(",");
            sb3.append("\"" + location_value + "\"");
            sb3.append(",");
            sb3.append("\"" + sleep_mode_value + "\"");
            sb3.append(",");
            sb3.append("\"" + heart_rate_value + "\"");
            sb3.append(",");
            sb3.append("\"" + anxiety_level_value + "\"" + '\n');

            if (LabelsSavePathInDevice.canWrite()) {
                try {
                    output2.write(sb3.toString().getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb3.delete(0, sb3.length());
            }
        } else {
            try {
                LabelsSavePathInDevice.createNewFile();


                if (LabelsSavePathInDevice.canWrite()) {
                    try {
                        output2.write(sb3.toString().getBytes());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb3.delete(0, sb3.length());
                }

                sb3.append("\"" +currentTime + "\"");
                sb3.append(",");
                sb3.append("\"" + location_value + "\"");
                sb3.append(",");
                sb3.append("\"" + sleep_mode_value + "\"");
                sb3.append(",");
                sb3.append("\"" + heart_rate_value + "\"");
                sb3.append(",");
                sb3.append("\"" + anxiety_level_value + "\"" + '\n');

                if (LabelsSavePathInDevice.canWrite()) {
                    try {
                        output2.write(sb3.toString().getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb3.delete(0, sb3.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        stopListener();
        timer.cancel();
        _recorder.stop();
        _recorder.release();
        locationListener = null;
        locationManager = null;
        _recorder = null;
        onStop();
        finish();
//        moveTaskToBack (true);
    }

}

