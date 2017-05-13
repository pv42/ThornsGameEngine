package engine.toolbox.collada;

/***
 * Created by pv42 on 02.08.16.
 */
public class Param {
    private String Name;
    private String dataType;

    public Param(String name, String dataType) {
        Name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return Name;
    }

    public String getDataType() {
        return dataType;
    }
}
