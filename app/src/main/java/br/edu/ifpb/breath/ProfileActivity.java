package br.edu.ifpb.breath;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ifpb.breath.database.DbHelper;
import br.edu.ifpb.breath.database.Patient;
import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the profile activity.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private TextView mNameTv;
    private TextView mAgeTv;
    private TextView mDescTv;
    private Button mMonitorBt;
    private Button mEditBt;

    private DbHelper mDbHelper;
    private Patient mPatient;

    private boolean mDisableMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNameTv = (TextView) findViewById(R.id.profile_name);
        mAgeTv = (TextView) findViewById(R.id.profile_age);
        mDescTv = (TextView) findViewById(R.id.profile_desc);

        mMonitorBt = (Button) findViewById(R.id.profile_monitor_bt);
        mMonitorBt.setOnClickListener(this);

        mEditBt = (Button) findViewById(R.id.profile_edit);
        mEditBt.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            long id = extras.getLong(Constants.PATIENT_ID_KEY, -1);
            if(id == -1) {
                Toast.makeText(this, getString(R.string.profile_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }

            mDbHelper = new DbHelper(this);
            mPatient = mDbHelper.getPatientById(id);
            if(mPatient == null){
                Toast.makeText(this, getString(R.string.profile_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }

            mNameTv.setText(mPatient.getName());
            int age = mPatient.getAge();
            mAgeTv.setText(age + " " + (age == 1 ? getString(R.string.patient_item_year) : getString(R.string.patient_item_years)));

            String description =
                    "<b>" + getString(R.string.profile_reg_code) + "</b> " + mPatient.getRegistrationCode() + "<br>" +
                    "<b>" + getString(R.string.profile_required_precautions) + "</b> " + (mPatient.getRequiredPrecautions()? getString(R.string.profile_yes) : getString(R.string.profile_no)) + "<br>" +
                    "<b>" + getString(R.string.profile_procedures) + "</b> " + mPatient.getProcedures() + "<br>" +
                    "<b>" + getString(R.string.profile_surgeries) + "</b> " + mPatient.getSurgeries() + "<br>" +
                    "<b>" + getString(R.string.profile_additional_info) + "</b> " + mPatient.getAdditionalInfo();
            mDescTv.setText(Html.fromHtml(description));

            mDisableMonitoring = extras.getBoolean(Constants.DISABLE_MONITORING_OPTION_KEY, false);
            if(mDisableMonitoring)
                mMonitorBt.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!mDisableMonitoring)
            getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_remove:
                new AlertDialog.Builder(this)
                        .setTitle(null)
                        .setMessage(getString(R.string.profile_remove_confirm))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(getString(R.string.profile_remove_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mDbHelper.deletePatientEntry(mPatient);
                                Toast.makeText(ProfileActivity.this, getString(R.string.profile_sucessfully_removed), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.profile_remove_no), null).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == mMonitorBt){
            Intent monitor = new Intent(this, MainActivity.class);
            monitor.putExtra(Constants.PATIENT_ID_KEY, mPatient.getId());
            monitor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(monitor);

            Intent intent = new Intent(Constants.KILL_BACKGROUND_ACTIVITIES);
            intent.setType(Constants.KILL_BACKGROUND_ACTIVITIES_TYPE);
            sendBroadcast(intent);
        }

        if(v == mEditBt){
            Intent edit = new Intent(this, NewEditPatientActivity.class);
            edit.putExtra(Constants.PATIENT_ID_KEY, mPatient.getId());
            startActivity(edit);
        }
    }
}
