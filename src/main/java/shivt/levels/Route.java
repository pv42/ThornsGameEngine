package shivt.levels;

/***
 * Created by pv42 on 15.09.2016.
 */
public class Route {
    private int[] stations = new int[2];
    private float speed;

    public Route(int station1, int station2, float speed) {
        this.stations[0] = station1;
        this.stations[1] = station2;
        this.speed = speed;
    }

    public int[] getStations() {
        return stations;
    }

    public float getSpeed() {
        return speed;
    }
}
