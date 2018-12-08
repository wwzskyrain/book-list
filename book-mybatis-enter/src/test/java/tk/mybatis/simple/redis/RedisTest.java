package tk.mybatis.simple.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {

    @Test
    public void test_redis_connection(){

        Jedis jedis = new Jedis("localhost", 6379);


        jedis.set("key-1","value-1");
        jedis.set("key-2","value-2");

        System.out.println(jedis.get("key-1"));
        System.out.println(jedis.get("key-2"));
    }

}
