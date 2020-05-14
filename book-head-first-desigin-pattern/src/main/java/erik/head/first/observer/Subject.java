package erik.head.first.observer;

/**
 * @author erik.wang
 * @date 2020-05-13 09:16
 * 主题，也就是可被观察的对象，所以有时候也可以叫'Observable'
 */
public interface Subject {

    void register(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserver();

}
