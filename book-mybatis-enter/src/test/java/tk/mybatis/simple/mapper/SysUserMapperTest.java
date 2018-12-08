package tk.mybatis.simple.mapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import tk.mybatis.simple.model.SysUser;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;


public class SysUserMapperTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void init() {

        try {

            Reader mybatisConfigReader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(mybatisConfigReader);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_select_user_and_role_by_id() {

        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.selectUserAndRoleById(1001l);
            Assert.assertEquals(1001, (long) sysUser.getId());
            Assert.assertNotNull(sysUser.getRole());

        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_select_user_and_role_by_id_2() {

        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.selectUserAndRoleById2(1l);
            Assert.assertEquals(1, (long) sysUser.getId());
            Assert.assertNotNull(sysUser.getRole());

        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_select_user_and_roles_by_id() {

        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            SysUserMapper sysUserMapper = sqlSession.getMapper(SysUserMapper.class);
            SysUser sysUser = sysUserMapper.selectUserAndRolesById(1l);
            Assert.assertEquals(1, (long) sysUser.getId());
            Assert.assertNotNull(sysUser.getRoles());
            Assert.assertEquals(2, sysUser.getRoles());

        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void test_insert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);

            SysUser user = new SysUser();
            user.setId(100l);
            user.setName("testName");
            user.setPassword("testPassword");
            user.setEmail("testEmail@test.com");
            user.setInfo("testInfo");
            user.setHeadImg("testHeadImg");
            user.setCreateTime(new Date());

            userMapper.insert(user);

            SysUser user1 = userMapper.selectUserById(100l);
            Assert.assertEquals(user.getName(), user1.getName());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void test_update() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            SysUserMapper userMapper = sqlSession.getMapper(SysUserMapper.class);

            SysUser user = userMapper.selectUserById(1l);
            String newPassword = "newPassword";
            user.setPassword(newPassword);
            userMapper.update(user);

            SysUser user1 = userMapper.selectUserById(1l);
            Assert.assertEquals(newPassword, user1.getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }


    @Test
    public void test_one_level_cache() {

        SqlSession sqlSession1 = sqlSessionFactory.openSession();

        try {
            SysUserMapper sysUserMapper1 = sqlSession1.getMapper(SysUserMapper.class);
            SysUser user1 = sysUserMapper1.selectUserById(1l);
            String newEmail = "new-email@test.com";
            user1.setEmail(newEmail);
            SysUser user2 = sysUserMapper1.selectUserById(1l);
            Assert.assertEquals(user1, user2);
//          因为有一级缓存，所以实际上只查询了一次DB，而且user1就是user2；
        } finally {
            sqlSession1.close();
        }

    }

    @Test
    public void test_two_level_cache_read_only1() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        try {

            SysUserMapper sysUserMapper1 = sqlSession1.getMapper(SysUserMapper.class);
            SysUser user1 = sysUserMapper1.selectUserById(1l);

//  不同的SqlSession，开启二级缓存的话，而且是readOnly，当sqlSession关闭之后，才会将查询数据写到cache中
            SysUserMapper sysUserMapper2 = sqlSession2.getMapper(SysUserMapper.class);  //所以，这里不会命中cache的。
            SysUser user2 = sysUserMapper2.selectUserById(1l);

            Assert.assertNotEquals(user1, user2);   //所以，user1 != user2.
            // 当配置了redis缓存之后，这个UT也是跑的通的；
            // 因为两个sqlSession的mapper获取的entity都需要序列化，所以是两个内容相同的不同entity。
            Assert.assertEquals(user1.getId(), user2.getId());

            Assert.assertEquals(user1.getPassword(), user2.getPassword());

        } finally {
            sqlSession1.close();
            sqlSession2.close();
        }

    }

    @Test
    public void test_two_level_cache_read_only2() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();

        SysUser user1 = null;
        try {

            SysUserMapper sysUserMapper1 = sqlSession1.getMapper(SysUserMapper.class);
            user1 = sysUserMapper1.selectUserById(1l);

        } finally {
            sqlSession1.close();
        }

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        try {
//  不同的SqlSession，开启二级缓存，而且是readOnly，而且之前也因close而将查询到的数据缓存到了cache，
            SysUserMapper sysUserMapper2 = sqlSession2.getMapper(SysUserMapper.class);
            SysUser user2 = sysUserMapper2.selectUserById(1l);  // 所以这里直接查缓存，而没有查db，而且命中了缓存
            Assert.assertEquals(user1, user2);  // 所以user1=user2的。
            Assert.assertEquals(user1.getId(), user2.getId());

            Assert.assertEquals(user1.getPassword(), user2.getPassword());
        } finally {
            sqlSession2.close();
        }
    }

    @Test
    public void test_two_level_cache_read_only3() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();

        SysUser user1 = null;
        try {

            SysUserMapper sysUserMapper1 = sqlSession1.getMapper(SysUserMapper.class);
            user1 = sysUserMapper1.selectUserById(1001l);

        } finally {
            sqlSession1.close();
        }

        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        try {
//  不同的SqlSession，开启二级缓存，而且之前也因close而将查询到的数据缓存到了cache，
            SysUserMapper sysUserMapper2 = sqlSession2.getMapper(SysUserMapper.class);
            SysUser user2 = sysUserMapper2.selectUserById(1001l);  // 所以这里直接查缓存，而没有查db，而且命中了缓存
            Assert.assertNotEquals(user1, user2);  // 但是，cache模式是"可读写"，所以user1 != user2的。
            Assert.assertEquals(user1.getId(), user2.getId());
            Assert.assertEquals(user1.getPassword(), user2.getPassword());
        } finally {
            sqlSession2.close();
        }
    }

}
