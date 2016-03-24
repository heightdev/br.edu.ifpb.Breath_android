package br.edu.ifpb.breath.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import br.edu.ifpb.breath.MainActivity;
import br.edu.ifpb.breath.ProfileActivity;
import br.edu.ifpb.breath.R;
import br.edu.ifpb.breath.database.Patient;
import br.edu.ifpb.breath.entities.Point;
import br.edu.ifpb.breath.entities.SensorData;
import br.edu.ifpb.breath.utils.Constants;

/**
 * This class represents the monitor fragment.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class MonitorFragment extends Fragment implements RefreshHandlerFragment {

    private MainActivity mMain;
    private LineChart chart;
    private Patient patient;
    private boolean isContinuousEnabled = true;
    private int lastX = 0;

    private TextView frequency;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_monitor, container, false);

        mMain = ((MainActivity) getActivity());
        frequency = (TextView) rootView.findViewById(R.id.monitor_frequency);

        mMain.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        patient = mMain.getPatient();
        rootView.findViewById(R.id.patient_item_ly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMain, ProfileActivity.class);
                intent.putExtra(Constants.PATIENT_ID_KEY, patient.getId());
                intent.putExtra(Constants.DISABLE_MONITORING_OPTION_KEY, true);
                startActivity(intent);
            }
        });

        chart = (LineChart) rootView.findViewById(R.id.monitor_chart);
        rootView.findViewById(R.id.monitor_continuous_ly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView icon = (ImageView) rootView.findViewById(R.id.monitor_continuous_icon);
                TextView desc = (TextView) rootView.findViewById(R.id.monitor_continuous_desc);

                if(isContinuousEnabled){
                    icon.setImageResource(R.drawable.ic_visibility_off_white_18dp);
                    desc.setText(getString(R.string.monitor_continuous_disabled));
                    desc.setTextColor(getResources().getColor(R.color.red_regular));
                    isContinuousEnabled = false;

                    mMain.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }else{
                    icon.setImageResource(R.drawable.ic_visibility_white_18dp);
                    desc.setText(getString(R.string.monitor_continuous_enabled));
                    desc.setTextColor(getResources().getColor(R.color.green_pool));
                    isContinuousEnabled = true;

                    mMain.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });

        TextView name = (TextView) rootView.findViewById(R.id.patient_item_name);
        name.setText(patient.getName());

        TextView ageTv = (TextView) rootView.findViewById(R.id.patient_item_age);
        int age = patient.getAge();
        ageTv.setText(age + " " + (age == 1? mMain.getString(R.string.patient_item_year) : mMain.getString(R.string.patient_item_years)));

        initChart();

        return rootView;
    }

    @Override
    public void refresh(Object extra) {

        if(!(extra instanceof SensorData))
            return;

        SensorData data = (SensorData) extra;
        addPoint(new Point(++lastX, data.getAmplitude()));
        frequency.setText(Integer.toString(data.getFrequency()));
    }

    /**
     * Initializes the chart.
     */
    private void initChart(){
        chart.setDescription("");
        chart.setNoDataTextDescription(getString(R.string.monitor_no_data));

        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setClickable(false);

        LineData data = new LineData();

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
        l.setForm(Legend.LegendForm.LINE);

        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.TRANSPARENT);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(5);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.TRANSPARENT);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    /**
     * Adds a new point.
     * @param point - Point to add.
     */
    private void addPoint(Point point) {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }else if(set.getEntryCount() == 60){
                set.removeFirst();
            }

            Log.w("Monitor", "(" + point.getX() + ", " + point.getY() + ")");
            // add a new x-value first
            data.addXValue(point.getX() + "");
            data.addEntry(new Entry(point.getY(), point.getX()), 0);


            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setAutoScaleMinMaxEnabled(true);
            chart.setVisibleXRangeMaximum(60);
//            chart.invalidate();
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
//            chart.moveViewToX(data.getXValCount() - 121);

            // this automatically refreshes the chart (calls invalidate())
            chart.moveViewTo(data.getXValCount() - 7, 55f, YAxis.AxisDependency.LEFT);
        }
    }

    /**
     * Creates the data set.
     * @return - Line data set.
     */
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.green_pool));
        set.setLineWidth(2f);
        set.setCircleRadius(0f);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.TRANSPARENT);
        set.setValueTextSize(0f);
        set.setDrawValues(false);
        return set;
    }
}
