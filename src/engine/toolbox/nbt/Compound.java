package engine.toolbox.nbt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 26.09.2016.
 */
public class Compound  {
    private List<Compound> subCompounds;
    private List<NBTTag> tags;
    private String name;

    public Compound() {
        subCompounds = new ArrayList<>();
        tags = new ArrayList<>();
    }
    public void addNBTTag(NBTTag tag) {
        if(tag.getDataType() != NBT.DATATYPE_END)tags.add(tag);
    }
    public void addSubCompound(Compound c) {
        subCompounds.add(c);
    }

    public List<Compound> getSubCompounds() {
        return subCompounds;
    }

    @Override
    public String toString() {
        String s = "";
        if(name!=null) s += name + ":";
        s += "{";
        for (Compound c : subCompounds) {
            s += c.toString();
            s += ";";
        }
        for (NBTTag t : tags) {
            s += t.toString();
            s += ";";
        }
        s += "}";
        return s;
    }

    public List<NBTTag> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NBTTag> getNBTTAGs() {
        return tags;
    }
    public NBTTag getNBTTagByName(String name) {
        for (NBTTag tag:tags) {
            if(tag.getName().equals(name)) return tag;
        }
        return null;
    }
}
