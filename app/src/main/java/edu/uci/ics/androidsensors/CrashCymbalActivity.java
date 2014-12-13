package edu.uci.ics.androidsensors;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class CrashCymbalActivity extends Activity
{

    private final int SHAKE_THRESHOLD = 25;
    private SensorManager _sensor_manager;
    private SensorEventListener _event_listener;
    private TextView _x;
    private TextView _y;
    private TextView _z;
    private MediaPlayer _player;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private long _elapsed;
    private int _update_value = 0;
    private int _update_speed = 2;

    private Orient currOrient;

    private AssetFileDescriptor _cymbal_1;
    private AssetFileDescriptor _cymbal_2;
    private AssetFileDescriptor _cymbal_3;
    private AssetFileDescriptor _cymbal_4;
    private AssetFileDescriptor _cymbal_5;
    private AssetFileDescriptor _cymbal_6;


    private enum Orient {
        Default,
        BottomDown,
        LeftDown,
        TopDown,
        RightDown,
        BackDown,
        FrontDown
    }


    private Orient getOrientation(float x, float y, float z)
    {
        Orient currOrient;
        // 1 is bottom down
        // 2 is left down
        // 3 is top down
        // 4 is right down
        // 5 is back down
        // 6 is front down
        if (y > x && y > -x && z < y && z > -y) {
            // 1 : Bottom Down
            currOrient = Orient.BottomDown;
        }
        else if (y < x && y > -x && z < x && z > -x) {
            // 2 : Left Down
            currOrient = Orient.LeftDown;
        }
        else if (y < x && y < -x && z > y && z < -y) {
            // 3 : Top Down
            currOrient = Orient.TopDown;
        }
        else if (y > x && y < -x && z > x && z < -x) {
            // 4 : Right Down
            currOrient = Orient.RightDown;
        }
        else if (z > y && z > -y && z > x && z > -x) {
            // 5 : Back Down
            currOrient = Orient.BackDown;
        }
        else if (z < y && z < -y && z < x && z < -x) {
            // 6 : Front Down
            currOrient = Orient.FrontDown;
        }
        else
        {
            currOrient = Orient.Default;
        }
        return currOrient;
    }

    private boolean playSoundIntent(float x, float y, float z){
        switch (currOrient)
        {
            case BottomDown:
            {
                return y < 0;
//                break;
            }
            case LeftDown:
                return x < 0;
            //break;
            case TopDown:
                return y > 0;
            //break;
            case RightDown:
                return x > 0;
            //break;
            case BackDown:
                return z < 0;
//            break;
            case FrontDown:
                return z > 0;
            //break;
            case Default:
                // Should not actually be Default...
                return false;
            //break;
            default:
                // Should not be here.
                return false;
            //break;
        }
    }

    private void updateUI()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                _x.setText("" + last_x);
                _y.setText("" + last_y);
                _z.setText("" + last_z);
            }
        });
    }

    synchronized void playAudio(AssetFileDescriptor audio)
    {
        try
        {
            _player.reset();
            _player.setDataSource(audio.getFileDescriptor(), audio.getStartOffset(), audio.getLength());
            _player.prepare();
            _player.start();
            _elapsed = 0;
        }
        catch (IOException e)
        {
//            Log.e("player", e + "\naudio: " + audio.toString());
        }


    }

    public void detectOrientation(float x, float y, float z)
    {
        Orient ori;
        if (_elapsed > 2000)
        {
            ori = getOrientation(x,y,z);
            if (ori != Orient.Default)
            {
                currOrient = ori;
            }
            _elapsed = 1000;

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        _x = (TextView) findViewById(R.id.x);
        _y = (TextView) findViewById(R.id.y);
        _z = (TextView) findViewById(R.id.z);


        _sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _event_listener = new SensorEventListener()
        {

            @Override
            public void onSensorChanged(SensorEvent event)
            {
                float[] values = event.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];


//                if (_elapsed > 1000)
//                {
//                    detectOrientation();
//                }
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    long curTime = System.currentTimeMillis();
                    if ((curTime - lastUpdate) > 50) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;
                        _elapsed += diffTime;
                        detectOrientation(x,y,z);
                    }
                }
                else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
                {
                    long curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate) > 50)
                    {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;
                        _elapsed += diffTime;

                        if (playSoundIntent(x,y,z))
                        {
//                            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                            double speed = Math.sqrt(x * x + y * y + z * z);

                            if (speed > SHAKE_THRESHOLD)
                            {
                                switch (currOrient)
                                {
                                    case BottomDown:
                                        playAudio(_cymbal_1);
                                        break;
                                    case LeftDown:
                                        playAudio(_cymbal_2);
                                        break;
                                    case TopDown:
                                        playAudio(_cymbal_3);
                                        break;
                                    case RightDown:
                                        playAudio(_cymbal_4);
                                        break;
                                    case BackDown:
                                        playAudio(_cymbal_5);
                                        break;
                                    case FrontDown:
                                        playAudio(_cymbal_6);
                                        break;
                                    case Default:
                                        // Should not actually be Default...


                                    //break;
                                }
//                                playAudio();
                            }
                        }
                        last_x = x;
                        last_y = y;
                        last_z = z;
                        _update_value++;
                        if (_update_value == _update_speed) {
                            updateUI();
                            _update_value = 0;
                        }

                    }

                }

            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {

            }
        };
        
        _player =  new MediaPlayer();
        _cymbal_1 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal1);
        _cymbal_2 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal2);
        _cymbal_3 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal3);
        _cymbal_4 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal4);
        _cymbal_5 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal5);
        _cymbal_6 = getApplicationContext().getResources().openRawResourceFd(R.raw.cymbal6);


    }

    @Override
    public void onResume()
    {
        super.onResume();
        _sensor_manager.registerListener(_event_listener, _sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), _sensor_manager.SENSOR_DELAY_NORMAL);
        _sensor_manager.registerListener(_event_listener, _sensor_manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), _sensor_manager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop()
    {
        _sensor_manager.unregisterListener(_event_listener);
        super.onStop();
    }
    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {

        super.onConfigurationChanged(newConfig);

        if (_elapsed > 1000)
        {
            super.onConfigurationChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                Toast.makeText(this, "landscape", Toast.LENGTH_LONG).show();
            else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                Toast.makeText(this, "portrait", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
