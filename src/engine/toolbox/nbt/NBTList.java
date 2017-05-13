package engine.toolbox.nbt;

import java.util.ArrayList;

/**
 * Created by pv42 on 27.09.2016.
 */
public class NBTList<E> extends ArrayList<E> {
    private byte dataType;

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }
}
