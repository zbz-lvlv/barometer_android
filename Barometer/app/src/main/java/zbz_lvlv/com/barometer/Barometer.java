package zbz_lvlv.com.barometer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

@TargetApi(21)
public class Barometer extends AppCompatActivity implements SensorEventListener{

    TextView airPressureWholeTV;
    TextView airPressureDecimalTV;
    TextView altitudeWholeTV;
    TextView altitudeDecimalTV;

    EditText refPressureET;
    EditText refTemperatureET;

    private SensorManager mSensorManager;
    private Sensor mPressure;

    int altitude = 0;
    int pressure = 101325;
    int referencePressure = 101325;
    int referenceTemperature = 293; //in kelvin

    final int CALIBRATION_VALUE = 235;

    boolean settingRefTempPres = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barometer);

        this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        this.getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

        airPressureWholeTV = (TextView)this.findViewById(R.id.airPressureWholeTV);
        airPressureDecimalTV = (TextView)this.findViewById(R.id.airPressureDecimalTV);
        altitudeWholeTV = (TextView)this.findViewById(R.id.altitudeWholeTV);
        altitudeDecimalTV = (TextView)this.findViewById(R.id.altitudeDecimalTV);

        refPressureET = (EditText)this.findViewById(R.id.refPressureET);
        refTemperatureET = (EditText)this.findViewById(R.id.refTemperatureET);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        pressure = (int)(event.values[0] * 100) - CALIBRATION_VALUE;
        updatePressureUI();
        updateAltitudeUI();
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void updatePressureUI(){
        airPressureWholeTV.setText(String.valueOf((int)(pressure / 100)));
        int decimalPressure = pressure - (int)(pressure / 100) * 100;
        airPressureDecimalTV.setText("." + String.format("%02d", decimalPressure) + " hPa");
    }

    public void updateAltitudeUI(){
        double a = referenceTemperature / Math.pow((double)pressure / (double)referencePressure, -1 / 5.263886027);
        altitude = (int)((a - referenceTemperature) / (-0.00649) * 10);

        altitudeWholeTV.setText(String.valueOf((int)(altitude / 10)));
        int decimalAltitude = altitude - (int)(altitude / 10) * 10;
        altitudeDecimalTV.setText("." + String.format("%01d", decimalAltitude) + " m");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuBar:

                if(!settingRefTempPres) {

                    settingRefTempPres = true;

                    refPressureET.setVisibility(View.VISIBLE);
                    refTemperatureET.setVisibility(View.VISIBLE);
                    
                    refPressureET.setText(referencePressure + "");
                    refTemperatureET.setText(referenceTemperature + "");

                }
                else{

                    settingRefTempPres = false;

                    refPressureET.setVisibility(View.GONE);
                    refTemperatureET.setVisibility(View.GONE);

                    referencePressure = Integer.parseInt(refPressureET.getText().toString());
                    referenceTemperature = Integer.parseInt(refTemperatureET.getText().toString());

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
