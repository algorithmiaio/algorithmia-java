import com.algorithmia.algo.AlgoInheritable;

/**
 * Created by james on 06/02/16.
 * example input class for AlgorithmTest -> algorithmPipeClass() test spec
 */
public class Weather extends AlgoInheritable {

    public float temperature;
    public float airPressure;
    public float humidity;

    public Weather(float newTemp, float newPressure, float newHumidity){
        temperature = newTemp;
        airPressure = newPressure;
        humidity = newHumidity;
    }

    public static Weather Weather(float newTemp, float newPressure, float newHumidity) {
        return new Weather(newTemp, newPressure, newHumidity);
    }
}