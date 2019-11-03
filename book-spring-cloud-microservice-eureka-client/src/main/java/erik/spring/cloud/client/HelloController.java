package erik.spring.cloud.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class HelloController {


    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        ServiceInstance instance = client.getLocalServiceInstance();

        String serviceInfo = String.format("/hello:%s,%s,%s\n", instance.getHost(), instance.getPort(), instance.getServiceId());

        return "Hello World" + serviceInfo;
    }


}
