package erik.study.book.refacting.chapter01;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public abstract class Price {

    public final static int PRICE_CODE_REGULAR = 1;
    public final static int PRICE_CODE_CHILDREN = 2;
    public final static int PRICE_CODE_NEW_RELEASE = 3;

    abstract public double getCharge(int daysRented);

    public int getFrequentRenterPoints(int daysRented) {
        return 1;
    }
}
