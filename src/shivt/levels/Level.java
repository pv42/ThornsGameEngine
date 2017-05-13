package shivt.levels;

import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.nbt.Compound;
import engine.toolbox.nbt.NBT;
import engine.toolbox.nbt.NBTList;
import engine.toolbox.nbt.NBTTag;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 15.09.2016.
 */
public class Level {
    private static final String TAG = "LEVEL";
    private static final String LEVELS_SAVE_PATH = "res/shivtLevels/%s.dat";
    private List<Station> stations = new ArrayList<>();
    private List<Route> routes = new ArrayList<>();
    private Level() {
    }
    public static Level demoLevel() {
        Level level = new Level();
        level.addStation(new Station(new Vector3f(10,  0,  0), Station.OWNER_ALLIED,  0));
        level.addStation(new Station(new Vector3f( 0, 10,  0), Station.OWNER_NEUTRAL, 0));
        level.addStation(new Station(new Vector3f( 0, -5, -8), Station.OWNER_NEUTRAL, 0));
        level.addStation(new Station(new Vector3f( 0, -5,  8), Station.OWNER_NEUTRAL, 0));
        level.addRoute(new Route(0, 1, 1));
        level.addRoute(new Route(0, 2, 1));
        level.addRoute(new Route(1, 3, 1));
        level.addRoute(new Route(2, 3, 1));
        return level;
    }
    /*
    -root
     -stations[
        -
      ]
    */

    private void addStation(Station station) {
        stations.add(station);
    }
    private void addRoute(Route route) {
        routes.add(route);
    }
    public List<Station> getStations() {
        return stations;
    }

    public List<Route> getRoutes() {
        return routes;
    }
    public static Level readFromFile(String file) {
        Compound nbt = null;
        try {
            nbt = NBT.read(new FileInputStream(String.format(LEVELS_SAVE_PATH, file)));
            Log.d(TAG,nbt.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Level level = new Level();
        List<Compound> stationsNBT = (List<Compound>) nbt.getNBTTagByName("stations").getData();
        for(Compound stationNBT: stationsNBT) {
            Vector3f pos = new Vector3f();
            pos.x = (float)stationNBT.getNBTTagByName("x").getData();
            pos.y = (float)stationNBT.getNBTTagByName("y").getData();
            pos.z = (float)stationNBT.getNBTTagByName("z").getData();
            int owner = (int)stationNBT.getNBTTagByName("owner").getData();
            int troopStrength = (int)stationNBT.getNBTTagByName("troopStrength").getData();
            level.addStation(new Station(pos,owner,troopStrength));
        }
        List<Compound> routesNBT = (List<Compound>) nbt.getNBTTagByName("routes").getData();
        for(Compound routeNBT: routesNBT) {
            int station1 = (int)routeNBT.getNBTTagByName("station1").getData();
            int station2 = (int)routeNBT.getNBTTagByName("station2").getData();
            float speed = (float)routeNBT.getNBTTagByName("speed").getData();
            level.addRoute(new Route(station1,station2,speed));

        }
        return level;

    }
    public static void writeToFile(String file,Level level) {
        Compound nbt = new Compound();
        nbt.setName("root");
        NBTTag versionTag = new NBTTag("version",NBT.DATATYPE_INT, Settings.LEVEL_FILE_VERSION);
        NBTTag stationsNBT = new NBTTag();
        stationsNBT.setName("stations");
        stationsNBT.setDataType(NBT.DATATYPE_LIST);
        NBTList<Compound> stationsList = new NBTList<>();
        stationsList.setDataType(NBT.DATATYPE_COMPOUND);
        for(Station station: level.getStations()) {
            Compound c = new Compound();
            NBTTag nbtTagX = new NBTTag("x",NBT.DATATYPE_FLOAT,station.getPosition().x);
            NBTTag nbtTagY = new NBTTag("y",NBT.DATATYPE_FLOAT,station.getPosition().y);
            NBTTag nbtTagZ = new NBTTag("z",NBT.DATATYPE_FLOAT,station.getPosition().z);
            NBTTag nbtTagO = new NBTTag("owner",NBT.DATATYPE_INT,station.getOwner());
            NBTTag nbtTagT = new NBTTag("troopStrength",NBT.DATATYPE_INT,station.getTroopsStrength());
            c.addNBTTag(nbtTagX);
            c.addNBTTag(nbtTagY);
            c.addNBTTag(nbtTagZ);
            c.addNBTTag(nbtTagO);
            c.addNBTTag(nbtTagT);
            stationsList.add(c);
        }
        stationsNBT.setData(stationsList);
        nbt.addNBTTag(stationsNBT);
        NBTTag routesNBT = new NBTTag();
        routesNBT.setName("routes");
        routesNBT.setDataType(NBT.DATATYPE_LIST);
        NBTList<Compound> routesList = new NBTList<>();
        routesList.setDataType(NBT.DATATYPE_COMPOUND);
        for(Route route: level.getRoutes()) {
            Compound c = new Compound();
            NBTTag nbtTag1 = new NBTTag("station1",NBT.DATATYPE_INT,route.getStations()[0]);
            NBTTag nbtTag2 = new NBTTag("station2",NBT.DATATYPE_INT,route.getStations()[1]);
            NBTTag nbtTagS = new NBTTag("speed",NBT.DATATYPE_FLOAT,route.getSpeed());
            c.addNBTTag(nbtTag1);
            c.addNBTTag(nbtTag2);
            c.addNBTTag(nbtTagS);
            routesList.add(c);
        }
        routesNBT.setData(routesList);
        nbt.addNBTTag(routesNBT);
        Log.d(nbt.toString());
        try {
            File f = new File(String.format(LEVELS_SAVE_PATH,file));
            NBT.write(nbt,new FileOutputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
