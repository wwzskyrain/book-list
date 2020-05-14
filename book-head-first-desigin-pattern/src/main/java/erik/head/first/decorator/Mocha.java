package erik.head.first.decorator;

/**
 * @author erik.wang
 * @date 2020-05-13 23:04
 */
public class Mocha extends CondimentDecorator {

    private static final double PRICE_MOCHA = 0.2;

    public Mocha(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public String getDescription() {
        return "Mocha";
    }

    @Override
    public double cost() {
        return beverage.cost() + PRICE_MOCHA;
    }
}
