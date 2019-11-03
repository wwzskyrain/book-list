package tk.mybatis.simple.model;

/**
 * @author: erik.wang
 * @Date: 2019/5/5
 */
public class Country {

    private Long id;
    private String countryName;
    private String countryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {

        return new StringBuffer()
                .append("[id=").append(id).append(",")
                .append("countryName=").append(countryName).append(",")
                .append("countryCode=").append(countryCode).append("]")
                .toString();

    }

}
