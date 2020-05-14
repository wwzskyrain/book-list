package erik.head.first.decorator;

/**
 * @author erik.wang
 * @date 2020-05-13 23:01
 */
public class HouseBlend extends Beverage {

    public HouseBlend() {
        description = "house blend";
    }

    @Override
    public double cost() {
        return 0.89;
    }
}
