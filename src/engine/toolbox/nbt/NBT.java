package engine.toolbox.nbt;

import engine.toolbox.Log;
import shivt.levels.ShivtLevel;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static engine.toolbox.nbt.Tag.*;

public class NBT {
    private static final String TAG = "NBT";
    private static final boolean COMPRESS = true;


    public static void main(String[] args)  {
        String f1 = "C:\\Users\\pv42\\AppData\\Roaming\\.minecraft\\saves\\Arena_Pvp_1.8\\level.dat";
        String f2 = "C:\\Users\\pv42\\Documents\\IDEA\\GameEngine\\level.gz";
        String f3 = "C:\\Users\\pv42\\Documents\\IDEA\\GameEngine\\test.nbt";
        //FileInputStream in = new FileInputStream(f1);
        //Log.i(read(in).toString());
        ShivtLevel.writeToFile("test", ShivtLevel.demoLevel());
        Log.i("wrote");
        ShivtLevel l = ShivtLevel.readFromFile("test");
        Log.i("read");
        System.out.println(l);
    }
    public static Compound read(String path) throws IOException {
        return read(new FileInputStream(path));
    }
    public static Compound read(InputStream in) throws IOException {
        if(COMPRESS) {
            GZIPInputStream gzipInputStream = new GZIPInputStream(in);
            readType(gzipInputStream);
            return readCompound(gzipInputStream, true);
        } else {
            readType(in);
            return readCompound(in,true);
        }
    }
    public static void write(Compound compound, OutputStream outputStream) {
        try {
            if(COMPRESS) {
                GZIPOutputStream gzout = new GZIPOutputStream(outputStream);
                writeCompound(compound, new DataOutputStream(gzout));
                gzout.close();
            } else {
                writeCompound(compound,new DataOutputStream(outputStream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Compound readCompound(InputStream in, boolean readName) throws IOException {
        Compound compound = new Compound();
        String name = "";
        if(readName) name = readTagName(in);
        compound.setName(name);
        byte type;
        do {
            type = readType(in);
            if(type == DATATYPE_COMPOUND) {
                Compound c = readCompound(in,true);
                compound.addSubCompound(c);
            } else if(type != DATATYPE_END){
                Tag t = readTag(in,type);
                compound.addNBTTag(t);
            }
        } while(type != DATATYPE_END);
        return compound;
    }
    private static Tag readTag(InputStream in, byte type) throws IOException {
        Tag tag = new Tag();
        tag.setDataType(type);
        if(type != DATATYPE_END) {
            tag.setName(readTagName(in));
        }
        Object data = readData(type,new DataInputStream(in));
        if(data != null) tag.setData(data);
        return tag;
    }
    private static byte readType(InputStream in) throws IOException {
        byte b = (byte) in.read();
        return b;
    }

    private static Object readData(byte dataType, DataInputStream in) throws IOException {
        switch(dataType) {
            case DATATYPE_END:
                //Log.d(TAG,"End");
                break;
            case DATATYPE_BYTE:
                //Log.d(TAG,"Byte");
                return in.readByte();
            case DATATYPE_SHORT:
                //Log.d(TAG,"Short");
                return in.readShort();
            case DATATYPE_INT:
                //Log.d(TAG,"Int");
                return in.readInt();
            case DATATYPE_LONG:
                //Log.d(TAG,"Long");
                return in.readLong();
            case DATATYPE_FLOAT:
                //Log.d(TAG,"Float");
                return in.readFloat();
            case DATATYPE_DOUBLE:
                //Log.d(TAG,"Double");
                return in.readDouble();
            case DATATYPE_BYTEARRAY:
                //Log.d(TAG,"Byte-Array");
                return readByteArray(in);
            case DATATYPE_STRING:
                //Log.d(TAG,"String");
                return readString(in);
            case DATATYPE_LIST:
                //Log.d(TAG,"List");
                return readList(in);
            case DATATYPE_COMPOUND:
                //Log.d(TAG,"compound");
                return readCompoundElement(in);
            case DATATYPE_INTARRAY:
                //Log.d(TAG," Int-Array");
                return readIntArray(in);
            default:
                throw new IOException("DatatypId " + dataType + " doesn't exist");
        }
        return null;
    }

    private static String readTagName(InputStream in) throws IOException {
        return readString(new DataInputStream(in));
    }

    private static byte[] readByteArray(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] byteArray = new byte[length];
        for(int i = 0; i < length; i++ ) {
            byteArray[i] = in.readByte();
        }
        return byteArray;
    }

    private static String readString(DataInputStream in) throws IOException {
        short length = in.readShort();
        String str = "";

        for(int i = 0; i < length; ++i) {
            str = str + (char)in.read();
        }

        //Log.d(TAG,"String is " + str);
        return str;
    }

    private static NBTList readList(DataInputStream in) throws IOException {
        byte typ = readType(in);
        NBTList<Object> list = new NBTList<>();
        list.setDataType(typ);
        int lenght = in.readInt();
        for(int i = 0; i < lenght; ++i) {
            list.add(readData(typ, in));
        }
        return list;
    }
    private static Compound readCompoundElement(DataInputStream in) throws IOException {
        Compound compound = new Compound();
        compound.setName("");
        byte type;
        do {
            type = readType(in);
            if(type == DATATYPE_COMPOUND) {
                Compound c = readCompound(in,true);
                compound.addSubCompound(c);
            } else if(type != DATATYPE_END){
                Tag t = readTag(in,type);
                compound.addNBTTag(t);
            }
        } while(type != DATATYPE_END);
        return compound;
    }


    private static int[] readIntArray(DataInputStream in) throws IOException {
        int length = in.readInt();
        int[] intArray = new int[length];
        for(int i = 0; i < length; i++ ) {
            intArray[i] = in.readInt();
        }
        return intArray;
    }
    //out
    private static void writeCompound(Compound compound, DataOutputStream out) throws IOException {
        writeType(DATATYPE_COMPOUND,out);
        writeTagName(compound.getName(),out);
        writeCompoundData(compound,out);
    }
    private static void writeTag(Tag tag, OutputStream out) throws IOException {
        writeType(tag.getDataType(),out);
        if(tag.getDataType() != DATATYPE_END) {
            writeTagName(tag.getName(),out);
        }
        writeData(tag.getDataType(),tag.getData(),new DataOutputStream(out));
    }
    private static void writeType(byte type, OutputStream out) throws IOException {
        out.write(type);
    }
    private static void writeTagName(String name, OutputStream out) throws IOException {
        writeString(name,out);
    }
    private static void writeData(byte dataType, Object data,DataOutputStream out) throws IOException {
        switch (dataType) {
            case DATATYPE_COMPOUND:
                writeCompoundData((Compound) data,out);
            case DATATYPE_END:
                return;
            case DATATYPE_BYTE:
                out.writeByte((Integer) data);
                return;
            case DATATYPE_SHORT:
                out.writeShort((Integer) data);
                return;
            case DATATYPE_INT:
                out.writeInt((Integer) data);
                return;
            case DATATYPE_LONG:
                out.writeLong((Long) data);
                return;
            case DATATYPE_FLOAT:
                out.writeFloat((Float) data);
                return;
            case DATATYPE_DOUBLE:
                out.writeDouble((Double) data);
                return;
            case DATATYPE_BYTEARRAY:
                writeByteArray((byte[]) data,out);
                return;
            case DATATYPE_STRING:
                writeString(String.valueOf(data),out);
                return;
            case DATATYPE_LIST:
                writeList((NBTList) data,out);
                return;
            case DATATYPE_INTARRAY:
                writeIntArray((int[]) data,out);
                return;
            default:
                throw new IOException("DatatypId " + dataType + " doesn't exist");
        }
    }
    private static void writeByteArray(byte[] data,DataOutputStream out) throws IOException {
        out.writeInt(data.length);
        for(byte d : data) {
            out.writeByte(d);
        }
    }
    private static void writeIntArray(int[] data,DataOutputStream out) throws IOException {
        out.writeInt(data.length);
        for(int d : data) {
            out.writeInt(d);
        }
    }
    private static void writeString(String data, OutputStream out) throws IOException {
        short length = (short) data.length();
        out.write((byte)(length/256));
        out.write((byte)(length%256));
        for(short i = 0; i < length; ++i) {
            out.write(data.charAt(i));
        }
    }
    private static void writeList(NBTList data, DataOutputStream out) throws IOException {
        byte dataType = data.getDataType();
        out.writeByte(dataType);
        out.writeInt(data.size());
        for(Object o: data) {
            writeData(dataType,o,out);
        }
    }
    private static void writeCompoundData(Compound compound,DataOutputStream out) throws IOException {
        for (Tag tag: compound.getData()) {
            if(tag instanceof Compound) {
                writeCompound((Compound) tag,out);
            } else {
                writeTag(tag,out);
            }
        }
        out.writeByte(DATATYPE_END);
    }
}
