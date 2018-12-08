package tk.mybatis.simple.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tk.mybatis.simple.model.SysUser;

import java.io.IOException;
import java.io.Reader;

public class CacheTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void setup() {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    @Test
    public void test_one_level_cache() {

        SqlSession sqlSession = sqlSessionFactory.openSession();
        SysUser sysUser1 = null;
        try {
            SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);

            sysUser1 = sysUserMapper.selectUserById(1l);

            SysUser sysUser2 = sysUserMapper.selectUserById(1l);

            Assert.assertEquals(sysUser1, sysUser2);

        } finally {
            sqlSession.close();
        }
        System.out.println("open a new sqlSession.");

        sqlSession = sqlSessionFactory.openSession();
        try {
            SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
            SysUser sysUser3 = sysUserMapper.selectUserById(1l);

            Assert.assertNotEquals(sysUser1, sysUser3);
        }finally {
            sqlSession.close();
        }


    }


}
