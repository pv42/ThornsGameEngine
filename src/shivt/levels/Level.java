package shivt.levels;

import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.nbt.Compound;
import engine.toolbox.nbt.NBT;
import engine.toolbox.nbt.NBTList;
import engine.toolbox.nbt.Tag;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.nbt.Tag.*;

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
        List<Compound> stationsNBT = (List<Compound>) nbt.getSubTagByName("stations").getData();
        for(Compound stationNBT: stationsNBT) {
            Vector3f pos = new Vector3f();
            pos.x = (float)stationNBT.getSubTagByName("x").getData();
            pos.y = (float)stationNBT.getSubTagByName("y").getData();
            pos.z = (float)stationNBT.getSubTagByName("z").getData();
            int owner = (int)stationNBT.getSubTagByName("owner").getData();
            int troopStrength = (int)stationNBT.getSubTagByName("troopStrength").getData();
            level.addStation(new Station(pos,owner,troopStrength));
        }
        List<Compound> routesNBT = (List<Compound>) nbt.getSubTagByName("routes").getData();
        for(Compound routeNBT: routesNBT) {
            int station1 = (int)routeNBT.getSubTagByName("station1").getData();
            int station2 = (int)routeNBT.getSubTagByName("station2").getData();
            float speed = (float)routeNBT.getSubTagByName("speed").getData();
            level.addRoute(new Route(station1,station2,speed));

        }
        return level;

    }
    public static void writeToFile(String file,Level level) {
        Compound nbt = new Compound();
        nbt.setName("root");
        Tag versionTag = new Tag("version",DATATYPE_INT, Settings.LEVEL_FILE_VERSION);
        Tag stationsNBT = new Tag();
        stationsNBT.setName("stations");
        stationsNBT.setDataType(DATATYPE_LIST);
        NBTList<Compound> stationsList = new NBTList<>();
        stationsList.setDataType(DATATYPE_COMPOUND);
        for(Station station: level.getStations()) {
            Compound c = new Compound();
            Tag nbtTagX = new Tag("x",DATATYPE_FLOAT,station.getPosition().x);
            Tag nbtTagY = new Tag("y",DATATYPE_FLOAT,station.getPosition().y);
            Tag nbtTagZ = new Tag("z",DATATYPE_FLOAT,station.getPosition().z);
            Tag nbtTagO = new Tag("owner",DATATYPE_INT,station.getOwner());
            Tag nbtTagT = new Tag("troopStrength",DATATYPE_INT,station.getTroopsStrength());
            c.addNBTTag(nbtTagX);
            c.addNBTTag(nbtTagY);
            c.addNBTTag(nbtTagZ);
            c.addNBTTag(nbtTagO);
            c.addNBTTag(nbtTagT);
            stationsList.add(c);
        }
        stationsNBT.setData(stationsList);
        nbt.addNBTTag(stationsNBT);
        Tag routesNBT = new Tag();
        routesNBT.setName("routes");
        routesNBT.setDataType(DATATYPE_LIST);
        NBTList<Compound> routesList = new NBTList<>();
        routesList.setDataType(DATATYPE_COMPOUND);
        for(Route route: level.getRoutes()) {
            Compound c = new Compound();
            Tag nbtTag1 = new Tag("station1", DATATYPE_INT,route.getStations()[0]);
            Tag nbtTag2 = new Tag("station2", DATATYPE_INT,route.getStations()[1]);
            Tag nbtTagS = new Tag("speed", DATATYPE_FLOAT,route.getSpeed());
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

    @Override
    public String toString() {
        return "Level{" +
                "stations=" + stations +
                ", routes=" + routes +
                '}';
    }
}
