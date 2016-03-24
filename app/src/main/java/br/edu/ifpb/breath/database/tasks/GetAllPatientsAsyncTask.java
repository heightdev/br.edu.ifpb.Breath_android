package br.edu.ifpb.breath.database.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import br.edu.ifpb.breath.database.DbHelper;
import br.edu.ifpb.breath.database.Patient;

/**
 * Created by felipepx on 3/24/16.
 */
public class GetAllPatientsAsyncTask extends AsyncTask<Void, Void, Object> {

    private DbHelper mDbHelper;
    private OnTaskStatusChanged mListener;
    private ProgressBar mProgressBar;

    /**
     * Constructor method.
     * @param context - The context.
     * @param listener - On task status changed listener instance.
     * @param progressBar - A progress bar.
     */
    public GetAllPatientsAsyncTask(Context context, OnTaskStatusChanged listener, ProgressBar progressBar){
        mListener = listener;
        mProgressBar = progressBar;

        mDbHelper = new DbHelper(context);
    }

    @Override
    protected void onPreExecute() {
        if(mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Object doInBackground(Void... params) {
        try{
            return mDbHelper.loadAllPatients();
        }catch(Exception e){
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        mProgressBar.setVisibility(View.GONE);

        if(result instanceof Exception)
            mListener.onTaskFailed((Exception) result);
        else
            mListener.onTaskCompleted(result);
    }
}
