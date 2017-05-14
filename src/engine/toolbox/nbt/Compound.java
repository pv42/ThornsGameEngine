package engine.toolbox.nbt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 26.09.2016.
 */
public class Compound  extends NBTTag{

    public Compound() {
        super("",NBT.DATATYPE_COMPOUND,new ArrayList<>());
    }
    public void addNBTTag(NBTTag tag) {
        if(tag.getDataType() != NBT.DATATYPE_END)getData().add(tag);
    }
    public void addSubCompound(Compound c) {
        getData().add(c);
    }

    @Override
    public String toString() {
        String s = "";
        if(getName()!=null) s += getName() + ":";
        s += "{";
        for (NBTTag c : getData()) {
            s += c.toString();
            s += ";";
        }
        s += "}";
        return s;
    }

    public NBTTag getNBTTagByName(String name) {
        for (NBTTag tag:getData()) {
            if(tag.getName().equals(name)) return tag;
        }
        return null;
    }

    @Override
    public List<NBTTag> getData() {
        return (List<NBTTag>) super.getData();
    }
}
