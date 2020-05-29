package erik.head.first.decorator;

/**
 * @author erik.wang
 * @date 2020-05-13 23:07
 */
public class Main {



    public static void main(String[] args) {

        Beverage beverage = new Espresso();

        Beverage beverage2 = new Espresso();
        //得到了一个加了摩卡的'espresso'
        beverage2 = new Mocha(beverage2);
    }

}
