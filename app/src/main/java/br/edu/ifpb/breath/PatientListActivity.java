package br.edu.ifpb.breath;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpb.breath.adapters.PatientListAdapter;
import br.edu.ifpb.breath.database.DbHelper;
import br.edu.ifpb.breath.database.Patient;
import br.edu.ifpb.breath.database.tasks.GetAllPatientsAsyncTask;
import br.edu.ifpb.breath.database.tasks.OnTaskStatusChanged;
import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the patient list.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class PatientListActivity extends BaseActivity implements View.OnClickListener, OnTaskStatusChanged {

    private FloatingActionButton mAddBt;
    private ListView mPatientList;
    private List<Patient> mPatients = new ArrayList<Patient>();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        mAddBt = (FloatingActionButton) findViewById(R.id.patient_add);
        mAddBt.setOnClickListener(this);

        mPatientList = (ListView) findViewById(R.id.patient_list);
        mProgressBar = (ProgressBar) findViewById(R.id.patient_list_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetAllPatientsAsyncTask(this, this, mProgressBar).execute();
    }

    @Override
    public void onClick(View v) {
        if(v == mAddBt){
            Intent intent = new Intent(this, NewEditPatientActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onTaskCompleted(Object result) {

        try {
            mPatients = (List<Patient>) result;
            mPatientList.setAdapter(new PatientListAdapter(this, mPatients));
            mPatientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PatientListActivity.this, ProfileActivity.class);
                    intent.putExtra(Constants.PATIENT_ID_KEY, ((PatientListAdapter) mPatientList.getAdapter()).getItem(position).getId());
                    startActivity(intent);
                }
            });

            if (mPatients == null || mPatients.size() == 0)
                findViewById(R.id.patient_list_no_patients).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.patient_list_no_patients).setVisibility(View.GONE);
        }catch(Exception e){
            onTaskFailed(e);
        }
    }

    @Override
    public void onTaskFailed(Exception exception) {
        Toast.makeText(this, getString(R.string.patient_list_load_failed), Toast.LENGTH_SHORT).show();
        finish();
    }
}
