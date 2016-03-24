package br.edu.ifpb.breath;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * This class represents the base application.
 */
@ReportsCrashes(formKey = "", // will not be used
        mailTo = "felipefpx@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.acra_text)
public class CustomApplication extends Application {

    public static final boolean USE_ACRA = true;

    @Override
    public void onCreate() {
        super.onCreate();

        if(USE_ACRA)
            ACRA.init(this);
    }
}
