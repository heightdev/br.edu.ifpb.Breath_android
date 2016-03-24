package br.edu.ifpb.breath.entities;

/**
 * This class represents the data received from sensor.
 * @author Felipe Porge Xavier
 */
public class SensorData {

    private int amplitude;
    private int frequency;

    public SensorData(){}

    /**
     * Constructor method.
     * @param amplitude - Sensor amplitude.
     * @param frequency - Respiratory frequency.
     */
    public SensorData(int amplitude, int frequency){
        this.amplitude = amplitude;
        this.frequency = frequency;
    }


    /* Getters and Setters - BEGIN */

    public float getAmplitude() {
        return amplitude/10;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = (int) amplitude*10;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /* Getters and Setters - END */
}