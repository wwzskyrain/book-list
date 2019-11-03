package tk.mybatis.simple.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.simple.model.Country;

/**
 * @author erik.wang
 * @date 2019/05/04
 **/
public interface CountryMapper {

    Country selectOneByIdAndCode(@Param("id") Integer id, @Param("countrycode") String code);

}
