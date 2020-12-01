package vaulttool

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import se.su.it.tomcat.valves.HeaderEncodingValve

class Application extends GrailsAutoConfiguration implements EnvironmentAware{
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        println "### Start setting up Tomcat AJP on port 8009 and encoding attributes to UTF-8 ###"
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory()
        tomcat.addAdditionalTomcatConnectors(createSslConnector())
        //HeaderEncodingValve headerEncodingValve = new HeaderEncodingValve()
        //headerEncodingValve.setPattern('^.*$')
        //tomcat.addContextValves(headerEncodingValve)
        println "### Finished setting up Tomcat AJP on port 8009 and encoding attributes to UTF-8 ###"
        return tomcat
    }

    private Connector createSslConnector() {
        Connector ajpConnector = new Connector("AJP/1.3");
        try {
            ajpConnector.port = 8009
            ajpConnector.redirectPort = 8443
            ajpConnector.enableLookups = false
            ajpConnector.setProperty("redirectPort", "8443")
            ajpConnector.setProperty("protocol", "AJP/1.3")
            ajpConnector.setProperty("address", "127.0.0.1")
            ajpConnector.setProperty("enableLookups", "false")
            ajpConnector.URIEncoding = "UTF-8"
            return ajpConnector
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed setting up AJP Connector", ex)
        }
    }

    @Override
    void setEnvironment(Environment environment) {
        Properties systemProps = System.properties
        String local = "/local"
        if(systemProps.get("vaulttool.localfilepath") && !systemProps.get("vaulttool.localfilepath").isEmpty()) {
            local = systemProps.get("vaulttool.localfilepath")
        }
        File externalConfigFile = new File("${local}/vaulttool/conf/application.yml")
        if (externalConfigFile.exists()) {
            Resource resourceConfig = new FileSystemResource(externalConfigFile.absolutePath)
            YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()
            ypfb.setResources([resourceConfig] as Resource[])
            ypfb.afterPropertiesSet()
            Properties properties = ypfb.getObject()
            if (properties.size() > 0) {
                environment.propertySources.addFirst(new PropertiesPropertySource("${externalConfigFile.absolutePath}", properties))
            }
            println("### Added external configuration from file ${externalConfigFile.absolutePath} ###")
        } else {
            println("### No external configuration file found with filename ${externalConfigFile.absolutePath} ###")
        }
    }
}