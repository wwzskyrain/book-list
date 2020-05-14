package erik.head.first.observer;

/**
 * @author erik.wang
 * @date 2020-05-13 09:16
 * 观察者，
 */
public interface Observer {

    void update(float temperature, float humidity, float pressure);

}
