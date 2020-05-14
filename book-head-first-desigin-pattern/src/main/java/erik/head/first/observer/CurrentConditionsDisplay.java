package erik.head.first.observer;

/**
 * @author erik.wang
 * @date 2020-05-13 09:32
 */
public class CurrentConditionsDisplay implements Observer, DisplayElement {

    /**
     * 貌似没太多必要持有'主题'的引用
     */
    private Subject weatherData;
    private float temperature;
    private float humidity;

    public CurrentConditionsDisplay(Subject weatherData) {
        this.weatherData = weatherData;
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }

    void display() {
        toString();
    }

    @Override
    public String toString() {
        return "CurrentConditionsDisplay{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
