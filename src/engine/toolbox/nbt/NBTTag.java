package engine.toolbox.nbt;


/***
 * Created by pv42 on 26.09.2016.
 */
public class NBTTag {
    private String name;
    private byte dataType;
    private Object data;
    public NBTTag(){}
    public NBTTag(String name, byte dataType, Object data) {
        this.name = name;
        this.dataType = dataType;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String ds;
        ds = data.toString();
        if(dataType == NBT.DATATYPE_STRING) ds = "\"" + ds + "\"";
            return name + ":" + ds;
    }
}
