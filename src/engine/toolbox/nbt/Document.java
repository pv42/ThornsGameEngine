package engine.toolbox.nbt;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by pv42 on 26.09.2016.
 */
public class Document extends Compound {


    public Document(List<Compound> subCompounds) {

    }

    public static Document createFromFile(String path) throws FileNotFoundException,IOException{
        FileInputStream in = new FileInputStream(path);
        return null; //todo
    }
}
