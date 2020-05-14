package erik.head.first.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author erik.wang
 * @date 2020-05-13 09:17
 */

public class WeatherData implements Subject {

    private List<Observer> observers = new ArrayList<>();

    private float temperature;
    private float humidity;
    private float pressure;

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(temperature, humidity, pressure);
        }
    }

    private void measurementsChanged() {
        notifyObserver();
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.pressure = pressure;
        this.temperature = temperature;
        this.humidity = humidity;
        measurementsChanged();
    }
}
