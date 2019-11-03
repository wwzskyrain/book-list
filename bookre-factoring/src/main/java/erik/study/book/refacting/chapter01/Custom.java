package erik.study.book.refacting.chapter01;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author erik.wang
 * @Date 2019-11-03
 */
public class Custom {

    private String name;
    private List<Rental> rentals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public String statement() {
        String result = "Rental Record for " + getName() + "\n";
        for (Rental rental : rentals) {
            result += "\t" + rental.getMovie().getName() + "\t" + rental.getCharge() + "\n";
        }

        result += "Amount owed is " + getTotalCharge() + "\n";
        result += "You earned " + getTotalFrequentRenterPoints() + " frequent renter points";

        return result;
    }

    public String htmlStatement() {
        String result = "<H1>Rental Record for <EM>" + getName() + "</EM></H1><P>\n";
        for (Rental rental : rentals) {
            result += rental.getMovie().getName() + ":" + rental.getCharge() + "<BR>";
        }
        result += "Amount owed is " + getTotalCharge() + "<BR>";
        result += "<P> You earned <EM>" + getTotalFrequentRenterPoints() + "</EM> frequent renter points<P>";

        return result;
    }

    public double getTotalCharge() {
        return rentals.stream().collect(Collectors.summingDouble(Rental::getCharge));
    }

    public int getTotalFrequentRenterPoints() {
        return rentals.stream().collect(Collectors.summingInt(Rental::getDaysRented));
    }

}
