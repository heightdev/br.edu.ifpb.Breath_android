package br.edu.ifpb.breath;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * This class is used to generate greenDAO stuff.
 * <a href='http://greendao-orm.com/'>GreenDao ORM</a>
 */
public class MainDao {


    /**
     * Main method.
     * @param args - Some arguments.
     */
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "br.edu.ifpb.breath.database"); // Schema must point to main project package, to know Android project classes.

        schema.enableKeepSectionsByDefault(); // Forces greenDao to generate customizable areas in it models.
        createDatabase(schema);

        DaoGenerator generator = new DaoGenerator();
        generator.generateAll(schema, args[0]); // Args[0] = Parameter defined in dao.gradle (outputDatabase).
    }

    /**
     * Creates SQLite database using greenDAO schema.
     * @param schema - GreenDAO schema.
     */
    private static void createDatabase(Schema schema){

        // PATIENT - BEGIN
        Entity patient = schema.addEntity("Patient");
        patient.implementsSerializable();
        patient.addLongProperty("id").primaryKey();
        patient.addStringProperty("name");
        patient.addIntProperty("age");
        patient.addStringProperty("registrationCode");
        patient.addStringProperty("procedures");
        patient.addStringProperty("surgeries");
        patient.addBooleanProperty("requiredPrecautions");
        patient.addStringProperty("additionalInfo");
        // PATIENT - END

    }
}
