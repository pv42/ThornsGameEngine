package engine.toolbox.nbt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 26.09.2016.
 */
public class Compound  extends Tag<List<Tag>> {

    public Compound() {
        super("", new ArrayList<>(), DATATYPE_COMPOUND);
    }
    public void addNBTTag(Tag tag) {
        if(tag.getDataType() == DATATYPE_END) throw new IllegalArgumentException("Can't add end tag to Compound");
            getData().add(tag);
    }
    public void addSubCompound(Compound c) {
        getData().add(c);
    }



    public Tag getSubTagByName(String name) {
        for (Tag tag:getData()) {
            if(tag.getName().equals(name)) return tag;
        }
        return null;
    }

    @Override
    public List<Tag> getData() {
        return super.getData();
    }

    @Override
    public String toString() {
        String s = "";
        if(getName()!=null) s += getName() + ":";
        s += "{";
        for (Tag c : getData()) {
            s += c.toString();
            s += ";";
        }
        s += "}";
        return s;
    }
}
