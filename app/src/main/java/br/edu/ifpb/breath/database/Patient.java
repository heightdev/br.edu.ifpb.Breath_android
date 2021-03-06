package br.edu.ifpb.breath.database;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "PATIENT".
 */
public class Patient implements java.io.Serializable {

    private Long id;
    private String name;
    private Integer age;
    private String registrationCode;
    private String procedures;
    private String surgeries;
    private Boolean requiredPrecautions;
    private String additionalInfo;

    // KEEP FIELDS - put your custom fields here

    public Patient(String name, Integer age, String regCode, String procedures, String surgeries, String additionalInfo, Boolean requiredPrecautions) {
        this.name = name;
        this.age = age;
        this.registrationCode = regCode;
        this.procedures = procedures;
        this.surgeries = surgeries;
        this.additionalInfo = additionalInfo;
        this.requiredPrecautions = requiredPrecautions;
    }

    // KEEP FIELDS END

    public Patient() {
    }

    public Patient(Long id) {
        this.id = id;
    }

    public Patient(Long id, String name, Integer age, String registrationCode, String procedures, String surgeries, Boolean requiredPrecautions, String additionalInfo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.registrationCode = registrationCode;
        this.procedures = procedures;
        this.surgeries = surgeries;
        this.requiredPrecautions = requiredPrecautions;
        this.additionalInfo = additionalInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getProcedures() {
        return procedures;
    }

    public void setProcedures(String procedures) {
        this.procedures = procedures;
    }

    public String getSurgeries() {
        return surgeries;
    }

    public void setSurgeries(String surgeries) {
        this.surgeries = surgeries;
    }

    public Boolean getRequiredPrecautions() {
        return requiredPrecautions;
    }

    public void setRequiredPrecautions(Boolean requiredPrecautions) {
        this.requiredPrecautions = requiredPrecautions;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
