package cn.m1yellow.mypages.config;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.util.Set;

/**
 * 让外部 tomcat 中的程序也能向 nacos 注册
 */
@Configuration
public class NacosConfig implements ApplicationRunner {

    @Autowired(required = false)
    private NacosAutoServiceRegistration registration;

    @Value("${server.port}")
    Integer port;


    @Override
    public void run(ApplicationArguments args) {
        if (registration != null && port != null) {
            registration.setPort(port);
            registration.start();
        }
    }


    /*
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (registration != null && port != null) {
            Integer tomcatPort = port;
            try {
                tomcatPort = new Integer(getTomcatPort());
            } catch (Exception e) {
                e.printStackTrace();
            }

            registration.setPort(tomcatPort);
            registration.start();
        }
    }
    */


    /**
     * 获取外部tomcat端口
     */
    public String getTomcatPort() throws Exception {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("org.apache.coyote.http11.Http11AprProtocol")));
        String port = objectNames.iterator().next().getKeyProperty("port");
        return port;
    }

}
