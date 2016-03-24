package br.edu.ifpb.breath;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the splash screen.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNextScreen();
            }
        }, Constants.SPLASH_SCREEN_DELAY);
    }

    /**
     * This method closes the splash screen and starts the correct next screen.
     */
    private void goToNextScreen(){
        Intent nextIntent = new Intent(this, PatientListActivity.class);
        startActivity(nextIntent);
        finish();
    }

}
