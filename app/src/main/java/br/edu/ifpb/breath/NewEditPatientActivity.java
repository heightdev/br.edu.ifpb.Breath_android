package br.edu.ifpb.breath;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import br.edu.ifpb.breath.database.DbHelper;
import br.edu.ifpb.breath.database.Patient;
import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the register and change patient information activity.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class NewEditPatientActivity extends BaseActivity implements View.OnClickListener {

    private EditText mNameEt;
    private EditText mAgeEt;
    private EditText mRegEt;
    private EditText mProceduresEt;
    private EditText mSurgeriesEt;
    private EditText mAdditionalInfoEt;
    private CheckBox mRequiredPrecautionsCb;
    private Button mAddPatientBt;

    private DbHelper mDbHelper;
    private Patient mPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        mNameEt = (EditText) findViewById(R.id.new_patient_name);
        mAgeEt = (EditText) findViewById(R.id.new_patient_age);
        mRegEt = (EditText) findViewById(R.id.new_patient_reg_code);
        mProceduresEt = (EditText) findViewById(R.id.new_patient_procedures);
        mSurgeriesEt = (EditText) findViewById(R.id.new_patient_surgeries);
        mAdditionalInfoEt = (EditText) findViewById(R.id.new_patient_additional_info);

        mRequiredPrecautionsCb = (CheckBox) findViewById(R.id.new_patient_required_precautions);
        mAddPatientBt = (Button) findViewById(R.id.new_patient_add);
        mAddPatientBt.setOnClickListener(this);

        mDbHelper = new DbHelper(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            getSupportActionBar().setTitle(getString(R.string.new_patient_edit_title));

            long id = extras.getLong(Constants.PATIENT_ID_KEY, -1);
            if (id == -1) {
                Toast.makeText(this, getString(R.string.new_patient_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }

            mPatient = mDbHelper.getPatientById(id);
            if (mPatient == null) {
                Toast.makeText(this, getString(R.string.new_patient_invalid_user), Toast.LENGTH_SHORT).show();
                finish();
            }

            mNameEt.setText(mPatient.getName());
            mAgeEt.setText(Integer.toString(mPatient.getAge()));
            mRegEt.setText(mPatient.getRegistrationCode());
            mProceduresEt.setText(mPatient.getProcedures());
            mSurgeriesEt.setText(mPatient.getSurgeries());
            mAdditionalInfoEt.setText(mPatient.getAdditionalInfo());
            mRequiredPrecautionsCb.setChecked(mPatient.getRequiredPrecautions());

            mAddPatientBt.setText(getString(R.string.new_patient_save));
        }

    }

    @Override
    public void onClick(View v) {
        if(v == mAddPatientBt) {
            String name = mNameEt.getText().toString();
            if (name == null || name.length() == 0) {
                Toast.makeText(this, getString(R.string.new_patient_invalid_name), Toast.LENGTH_SHORT).show();
                return;
            }

            String age = mAgeEt.getText().toString();
            if (age == null || age.length() == 0) {
                Toast.makeText(this, getString(R.string.new_patient_invalid_age), Toast.LENGTH_SHORT).show();
                return;
            }

            if(mPatient != null) {
                mDbHelper.deletePatientEntry(mPatient);
            }

            mDbHelper.insertPatient(new Patient(name,
                    Integer.parseInt(age),
                    mRegEt.getText().toString(),
                    mProceduresEt.getText().toString(),
                    mSurgeriesEt.getText().toString(),
                    mAdditionalInfoEt.getText().toString(),
                    mRequiredPrecautionsCb.isChecked()));

            if(mPatient == null) {
                Toast.makeText(this, getString(R.string.new_patient_successfully_created), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, getString(R.string.new_patient_successfully_saved), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Constants.KILL_BACKGROUND_ACTIVITIES);
                intent.setType(Constants.KILL_BACKGROUND_ACTIVITIES_TYPE);
                sendBroadcast(intent);

                Intent patientList = new Intent(this, PatientListActivity.class);
                startActivity(patientList);
            }

            finish();
        }
    }
}
