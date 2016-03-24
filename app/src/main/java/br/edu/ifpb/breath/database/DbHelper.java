package br.edu.ifpb.breath.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * This class represents a database helper.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class DbHelper {

    private PatientDao mPatientDao;
    private DaoSession mDaoSession;
    private DaoMaster mDaoMaster;

    /**
     * Constructor method.
     * @param context - The context.
     */
    public DbHelper(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "tc-db.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        startNewSession();

    }

    /**
     * Gets dao session.
     * @return - Dao session.
     */
    public DaoSession getSession(){
        return this.mDaoSession;
    }

    /**
     * Gets patient data access operator.
     * @return - DosageDAO.
     */
    public PatientDao getDosageDao(){
        return this.mPatientDao;
    }

    /**
     * Clean database.
     */
    public void cleanDatabase(){
        mPatientDao.deleteAll();
    }

    /**
     * Starts a new database session.
     */
    public void startNewSession(){
        if(mDaoSession != null) {
            mDaoSession.clear();
        }

        mDaoSession = mDaoMaster.newSession();

        mPatientDao = mDaoSession.getPatientDao();
    }

    /**
     * Inserts all patients from List in local database.
     * @param patients - List of patients.
     */
    public void insertAllPatients(List<Patient> patients){
        if(patients == null)
            return;

        for(Patient pat : patients){
            if(pat == null)
                continue;

            insertPatient(pat);
        }
    }

    /**
     * Inserts a patient in local database.
     * @param patient - Patient to insert.
     */
    public void insertPatient(Patient patient){

        if(patient == null)
            return;

        mPatientDao.insertInTx(patient);
    }


    /**
     * Updates a patient entry.
     * @param patient - Updated patient.
     */
    public void updatePatient(Patient patient){
        if(patient == null)
            return;

        deletePatientEntry(patient);
        insertPatient(patient);
    }

    /**
     * Deletes a patient entry.
     * @param patient - Patient to delete.
     */
    public void deletePatientEntry(Patient patient){
        if(patient == null)
            return;


        mPatientDao.deleteByKey(patient.getId());
    }


    /**
     * Loads all patients.
     * @return - List of patients.
     */
    public List<Patient> loadAllPatients(){
        return mPatientDao.loadAll();
    }


    /**
     * Gets a patient using a specific id.
     * @param id - Patient id.
     * @return - Patient.
     */
    public Patient getPatientById(long id){
        return mPatientDao.loadByRowId(id);
    }
}
