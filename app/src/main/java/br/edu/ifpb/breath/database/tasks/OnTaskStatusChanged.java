package br.edu.ifpb.breath.database.tasks;

/**
 * This class handles when a task changed its status.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public interface OnTaskStatusChanged {

    /**
     * Called when the task completed.
     * @param result - Task result.
     */
    void onTaskCompleted(Object result);

    /**
     * Called when the task failed.
     * @param exception - Task exception.
     */
    void onTaskFailed(Exception exception);
}
