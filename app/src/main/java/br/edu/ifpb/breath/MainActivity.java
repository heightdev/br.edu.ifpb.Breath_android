package br.edu.ifpb.breath;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.felipeporge.bluetoothhelper.BluetoothCloseConnectionException;
import com.felipeporge.bluetoothhelper.BluetoothHelper;
import com.felipeporge.bluetoothhelper.BluetoothIsNotConnectedException;
import com.felipeporge.bluetoothhelper.BluetoothNotFoundException;
import com.felipeporge.bluetoothhelper.OnConnectionListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import br.edu.ifpb.breath.adapters.ViewPagerAdapter;
import br.edu.ifpb.breath.database.DbHelper;
import br.edu.ifpb.breath.database.Patient;
import br.edu.ifpb.breath.entities.SensorData;
import br.edu.ifpb.breath.fragments.AboutDialog;
import br.edu.ifpb.breath.fragments.RefreshHandlerFragment;
import br.edu.ifpb.breath.slidingtabs.SlidingTabLayout;
import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the MainActivity.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class MainActivity extends ActionBarActivity implements OnConnectionListener {

    private Toolbar mToolbar;
    private ViewPager mPager;
    private ViewPagerAdapter mPagerAdapter;
    private SlidingTabLayout mTabsLayout;

    private BluetoothHelper mBtHelper;
    private Queue<String> mDataToSend = new LinkedList<String>();

    private Handler mTimer = new Handler();
    private Runnable mBluetoothCommunicationTask = new Runnable() {
        @Override
        public void run() {

            if(mBtHelper.isConnected()) {
                sendData();
                readData();

                mTimer.postDelayed(this, Constants.BLUETOOTH_SEND_DELAY);
            }
        }
    };

    private DbHelper mDbHelper;
    private Patient mPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(getString(R.string.main_title));
        setSupportActionBar(mToolbar);

        String tabTitles[] = getResources().getStringArray(R.array.main_tabs_titles);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, mTabsTitles fot the Tabs and Number Of Tabs.
        mPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), tabTitles);

        // Assigning ViewPager View and setting the mPagerAdapter
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        // Assiging the Sliding Tab Layout View
        mTabsLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabsLayout.setTextColor(getResources().getColor(R.color.white_regular));
        mTabsLayout.setUseIcons(false);

        mTabsLayout.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the mTabsLayout Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabsLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabsLayout.setViewPager(mPager);

        try {
            mBtHelper = new BluetoothHelper(this, this);
        }catch(BluetoothNotFoundException e){
            Toast.makeText(this,
                    getString(R.string.connect_bt_no_bluetooth_adapter), Toast.LENGTH_LONG).show();
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            long id = extras.getLong(Constants.PATIENT_ID_KEY, -1);
            if(id == -1) {
                Toast.makeText(this, getString(R.string.main_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }

            mDbHelper = new DbHelper(this);
            mPatient = mDbHelper.getPatientById(id);
            if(mPatient == null){
                Toast.makeText(this, getString(R.string.main_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_simulate:
                simulatePatient();
                return true;
            case R.id.action_connect_disconnect:
                if(!mBtHelper.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, BluetoothHelper.ENABLE_BLUETOOTH_REQUEST_CODE);
                } else {
                    bluetoothToogle();
                }
                return true;

            case R.id.action_support:
                sendSupportEmail();
                return true;

            case R.id.action_about:
//                mPager.setCurrentItem(mPagerAdapter.getCount()-1, true);
                new AboutDialog(this).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Connects/Disconnects the Bluetooth.
     */
    private void bluetoothToogle(){
        if(!mBtHelper.isConnected())
            mBtHelper.connect();
        else {
            mTimer.removeCallbacks(mBluetoothCommunicationTask);
            mDataToSend.clear();
            try {
                mBtHelper.disconnect();
            } catch (BluetoothCloseConnectionException e) {
                e.printStackTrace();
            } catch (BluetoothIsNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to.
        if (requestCode == BluetoothHelper.ENABLE_BLUETOOTH_REQUEST_CODE) {
            // This is to make sure the requirements are met.
            if (mBtHelper.isEnabled()) {
                bluetoothToogle();
            }else{
                Toast.makeText(this,
                        getString(R.string.connect_bt_need_to_connect), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Populates example points.
     */
    private void simulatePatient() {

        final Handler handler = new Handler();
        final Runnable task = new Runnable() {
            private Random random = new Random();

            @Override
            public void run() {
                int amplitude = random.nextInt(45);
                int frequency = random.nextInt(50) + 90;
                SensorData rcvObj = new SensorData(amplitude, frequency);
                refreshFragments(rcvObj);

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(task);
    }

    /**
     * Sends the data via Bluetooth.
     */
    public void sendData(){
        if(mDataToSend != null && mDataToSend.size() > 0) {
            try {
                String tmp = mDataToSend.poll();
                mBtHelper.send(tmp);
                Log.w(Constants.DEBUG_TAG, "Sent data: " + tmp);
            } catch (BluetoothIsNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads data available in Bluetooth.
     */
    public void readData(){
        if(mBtHelper.isAvailable()) {
            String rcv = mBtHelper.read();
            Log.w(Constants.DEBUG_TAG, "Received data: " + rcv);

            SensorData rcvObj;
            try{
                rcvObj = new Gson().fromJson(rcv, SensorData.class);
                refreshFragments(rcvObj);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Refreshes all refreshable fragments.
     */
    public void refreshFragments(Object extra){
        HashMap<Integer, Fragment> frags = mPagerAdapter.getExistentFragments();
        Fragment f;
        for(int i : frags.keySet()) {
            f = frags.get(i);
            if (f instanceof RefreshHandlerFragment) {
                ((RefreshHandlerFragment) f).refresh(extra);
            }
        }
    }

    /**
     * This method starts an intent to send a support email.
     */
    private void sendSupportEmail(){
        Intent intent1 = new Intent();
        intent1.setAction(Intent.ACTION_SEND);
        intent1.setType("message/rfc822");
        intent1.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.support_email)});
        intent1.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject));
        intent1.putExtra(Intent.EXTRA_TEXT   , getString(R.string.support_body));
        startActivity(intent1);
    }

    /**
     * Returns if Bluetooth is connected.
     * @return
     */
    public boolean isBluetoothConnected(){
        return mBtHelper.isConnected();
    }

    @Override
    public void onSuccessfullyConnected(BluetoothDevice device) {
        Toast.makeText(this, getString(R.string.connect_bt_connected_with) + " " + device.getName(), Toast.LENGTH_LONG).show();
        mToolbar.getMenu().findItem(R.id.action_connect_disconnect).setTitle(getString(R.string.action_disconnect));
        mTimer.post(mBluetoothCommunicationTask);

        mDataToSend.clear();
//        mDataToSend.add(Integer.toString(mCols) + "#" + Integer.toString(mRows));

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(Constants.AUTONOMOUS_MODE_KEY, true);
        editor.commit();
        refreshFragments(this);
    }

    @Override
    public void onSuccessfullyDisconnected() {
        Toast.makeText(this, getString(R.string.connect_bt_disconnected), Toast.LENGTH_LONG).show();
        mToolbar.getMenu().findItem(R.id.action_connect_disconnect).setTitle(getString(R.string.action_connect));

        refreshFragments(null);
    }

    @Override
    public void onErrorOccurred(int errorCode) {
        switch (errorCode){
            case BluetoothHelper.CONNECTION_ERROR:
                Toast.makeText(this,
                        getString(R.string.connect_bt_connection_error), Toast.LENGTH_SHORT).show();
                break;
            case BluetoothHelper.DEVICE_NOT_FOUND_ERROR:
                Toast.makeText(this,
                        getString(R.string.connect_bt_no_bluetooth_adapter), Toast.LENGTH_SHORT).show();
                break;
            case BluetoothHelper.RECONNECT_AFTER_PAIR_ERROR:
                Toast.makeText(this,
                        getString(R.string.connect_bt_try_again_after_pair), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onStop(){
        super.onStop();

        if(mBtHelper.isConnected()) {
            bluetoothToogle();
        }
    }

    /**
     * Adds a string to send via Bluetooth.
     * @param data - Data to send.
     */
    public void sendViaBT(String data){
        mDataToSend.add(data);
    }

    /**
     * Gets the current patient.
     * @return - Current patient.
     */
    public Patient getPatient(){
        return mPatient;
    }

    /**
     * Gets database helper.
     * @return - DbHelper.
     */
    public DbHelper getDatabaseHelper(){
        return mDbHelper;
    }
}
