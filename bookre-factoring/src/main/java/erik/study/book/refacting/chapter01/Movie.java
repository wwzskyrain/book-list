package erik.study.book.refacting.chapter01;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public class Movie {

    private Price price;
    private String name;

    public void setPrice(int priceCode) {
        switch (priceCode) {
            case Price.PRICE_CODE_REGULAR:
                price = new RegularPrice();
                break;
            case Price.PRICE_CODE_CHILDREN:
                price = new ChildrenPrice();
                break;
            case Price.PRICE_CODE_NEW_RELEASE:
                price = new NewReleasePrice();
                break;
            default:
                throw new IllegalArgumentException("Incorrect Price Code.");    //首字母大写起强调作用
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCharge(int daysRented) {
        return price.getCharge(daysRented);
    }
    public int getFrequentRenterPoints(int daysRented){
        return price.getFrequentRenterPoints(daysRented);
    }
}
