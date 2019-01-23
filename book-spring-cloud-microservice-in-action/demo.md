# 1.actuate 常用链接
1.  应用配置类
    ```
    1.  curl http://localhost:8080/autoconfig | jq .
    2.  curl http://localhost:8080/beans | jq .
    3.  curl http://localhost:8080/configprops | jq .
    4.  curl http://localhost:8080/env | jq .
    5.  curl http://localhost:8080/mappings | jq .
    ```
2.  度量指标类
    ```
    1.  curl http://localhost:8080/metrics | jq .
    2.  curl http://localhost:8080/metrics/threads.peak | jq .
    3.  curl http://localhost:8080/metrics/specific.property.name | jq .
    4.  curl http://localhost:8080/health | jq .
    5.  curl http://localhost:8080/dump | jq .
    6.  curl http://localhost:8080/trace | jq .
    ```
3.  操作控制类
    ```
    1.  
    ```

# 2.程序说明
1.  主要功能：启动bootstrap的ApplicationBootStrap，就可以看到，除了自定义链接`/help`
    之外，还有很多链接，比如actuate提供的很多访问链接，具体参加[1.actuate 常用链接]().

2.  actuate的功能是通过`spring-boot-starter-actuator`来加载到classpath中，然后由
    spring-boot对应的bean到系统中的。