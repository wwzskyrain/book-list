package erik.study.book.refacting.chapter01;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public class ChildrenPrice extends Price {

    @Override
    public double getCharge(int daysRented) {
        double result = 1.5;
        if (daysRented > 3) {
            result += (daysRented - 3) * 1.5;
        }
        return result;
    }
}
