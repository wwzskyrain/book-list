package tk.mybatis.simple.mapper;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import tk.mybatis.simple.model.Country;

public class CountryMapperTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void init() {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config-for-country-test.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } catch (IOException ignore) {
            ignore.printStackTrace();
        }
    }

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Country> countryList = sqlSession.selectList("selectAll");
            printCountryList(countryList);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_selectOneWithoutParam() {
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {

            Country country = sqlSession.selectOne("selectOneWithoutParam");
            printCountryList(Arrays.asList(country));
        } finally {
            sqlSession.close();
        }

    }

    @Test
    public void test_selectOneById() { //一个参数，基本类型
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Country country = sqlSession.selectOne("selectOneById", 2);
            printCountryList(Arrays.asList(country));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_selectOneByIdAndCodeWithList() { //一个参数，基本类型--报错的。这个会被当做inMode来解释执行的。
        //很纳闷，接口调用时，是如何实现按照#{0},#{1},#{2}来传递参数的。
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Country country = sqlSession.selectOne("selectOneByIdAndCodeWithList", Arrays.asList(3, "RU"));
            printCountryList(Arrays.asList(country));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_selectOneByIdAndCodeWithMap() { //多个参数，用map传参，用key来获取参数
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", 4);
            paramMap.put("countrycode", "GB");
            Country country = sqlSession.selectOne("selectOneByIdAndCodeWithMap", paramMap);
            printCountryList(Arrays.asList(country));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_selectOneByIdAndCodeThroughInterfaceWithParamAnnotation() { //多个参数，用map传参，用key来获取参数
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {

            CountryMapper countryMapper  = sqlSession.getMapper(CountryMapper.class);
            Country country=countryMapper.selectOneByIdAndCode(2,"US");

            printCountryList(Arrays.asList(country));
        } finally {
            sqlSession.close();
        }
    }




    private void printCountryList(List<Country> countryList) {
        for (Country country : countryList) {
            System.out.printf("%-4d%4s%4s\n", country.getId(), country.getCountryName(), country.getCountryCode());
        }
    }
}
