package erik.study.book.refacting.chapter01;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public class NewReleasePrice extends Price {

    @Override
    public double getCharge(int daysRented) {
        return daysRented * 3;
    }

    @Override
    public int getFrequentRenterPoints(int daysRented) {

        return (daysRented > 1) ? 2 : 1;

    }
}
