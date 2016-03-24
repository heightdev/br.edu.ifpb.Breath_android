package br.edu.ifpb.breath.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpb.breath.R;
import br.edu.ifpb.breath.database.Patient;

/**
 * This class represents the patient list adapter.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class PatientListAdapter extends ArrayAdapter<Patient> {

    private List<Patient> mData = new ArrayList<Patient>();
    private Context mContext;

    /**
     * Constructor method.
     * @param context - The context.
     * @param data - The list of patients.
     */
    public PatientListAdapter(Context context, List<Patient> data) {
        super(context, 0, data);

        this.mContext = context;
        this.mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.patient_item, null);

        Patient patient = mData.get(position);

        TextView name = (TextView) rootView.findViewById(R.id.patient_item_name);
        name.setText(patient.getName());

        TextView ageTv = (TextView) rootView.findViewById(R.id.patient_item_age);
        int age = patient.getAge();
        ageTv.setText(age + " " + (age == 1? mContext.getString(R.string.patient_item_year) : mContext.getString(R.string.patient_item_years)));

        return rootView;
    }

}
