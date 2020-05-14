package erik.head.first.decorator;

/**
 * @author erik.wang
 * @date 2020-05-13 23:00
 */
public abstract class CondimentDecorator extends Beverage {

    Beverage beverage;

    public abstract String getDescription();

}
