package erik.study.book.refacting.chapter01;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public class RegularPrice extends Price {

    @Override
    public double getCharge(int daysRented) {
        int result = 2;
        if (daysRented > 2) {
            result += (daysRented - 2) * 1.5;
        }
        return result;
    }
}
