package br.edu.ifpb.breath;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents a base activity.
 */
public class BaseActivity extends AppCompatActivity {

    private KillReceiver mKillReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKillReceiver = new KillReceiver();
        registerReceiver(mKillReceiver,
                IntentFilter.create(Constants.KILL_BACKGROUND_ACTIVITIES, Constants.KILL_BACKGROUND_ACTIVITIES_TYPE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mKillReceiver);
    }

    /**
     * Broadcast receiver to kill the activity.
     */
    private final class KillReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}