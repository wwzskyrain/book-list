package erik.head.first.decorator;

/**
 * @author erik.wang
 * @date 2020-05-13 23:01
 */
public class Espresso extends Beverage {

    public Espresso() {
        description = "Espresso";
    }

    @Override
    public double cost() {
        return 1.99;
    }
}
