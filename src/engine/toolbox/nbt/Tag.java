package engine.toolbox.nbt;


import java.util.List;

/***
 * Created by pv42 on 26.09.2016.
 */
public class Tag<T> {
    public static final byte DATATYPE_END = 0;
    public static final byte DATATYPE_BYTE = 1;
    public static final byte DATATYPE_SHORT = 2;
    public static final byte DATATYPE_INT = 3;
    public static final byte DATATYPE_LONG = 4;
    public static final byte DATATYPE_FLOAT = 5;
    public static final byte DATATYPE_DOUBLE = 6;
    public static final byte DATATYPE_BYTEARRAY = 7;
    public static final byte DATATYPE_STRING = 8;
    public static final byte DATATYPE_LIST = 9;
    public static final byte DATATYPE_COMPOUND = 10;
    public static final byte DATATYPE_INTARRAY = 11;

    private String name;
    private byte dataType;
    private T data;

    public Tag() {
    }

    public Tag(String name, T data) {
        this(name, data, dataTypeFromClass(data.getClass()));
    }

    public Tag(String name, T data, byte dataType) {
        this.name = name;
        this.dataType = dataType;
        this.data = data;
    }

    public static byte dataTypeFromClass(Class c) {
        if (c == Byte.class) {
            return DATATYPE_BYTE;
        } else if (c == Short.class) {
            return DATATYPE_SHORT;
        } else if (c == Integer.class) {
            return DATATYPE_INT;
        } else if (c == Long.class) {
            return DATATYPE_LONG;
        } else if (c == Float.class) {
            return DATATYPE_FLOAT;
        } else if (c == Double.class) {
            return DATATYPE_DOUBLE;
        } else if (c == List.class) {
            return DATATYPE_BYTEARRAY;
        } else if (c == List.class) {
            return DATATYPE_LIST;
        } else if (c == List.class) {
            return DATATYPE_COMPOUND;
        } else if (c == List.class) {
            return DATATYPE_INTARRAY;
        }
        return 0;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String ds;
        ds = data.toString();
        if (dataType == DATATYPE_STRING) ds = "\"" + ds + "\"";
        return name + ":" + ds;
    }
}
