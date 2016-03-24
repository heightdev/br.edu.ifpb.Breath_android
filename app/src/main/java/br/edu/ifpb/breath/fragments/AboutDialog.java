package br.edu.ifpb.breath.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import br.edu.ifpb.breath.R;

/**
 * This class represents the About Dialog.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class AboutDialog {

    private Dialog mDialog;

    /**
     * Constructor method.
     * @param context - The context.
     */
    public AboutDialog(Context context){
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_about);

        TextView version = (TextView) mDialog.findViewById(R.id.about_version);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version.setText(context.getString(R.string.app_name) + " v" + pInfo.versionName);
        }catch(Exception e){}

        TextView description = (TextView) mDialog.findViewById(R.id.about_description);
        description.setText(Html.fromHtml(context.getString(R.string.about_description)));
//        description.setMovementMethod(new LinkMovementMethod());

    }

    /**
     * Shows about dialog.
     */
    public void show(){
        if(mDialog != null && !mDialog.isShowing())
            mDialog.show();
    }


    /**
     * Closes about dialog.
     */
    public void hide(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.hide();
        }
    }
}
