package com.rubicon.platform.authorization.commons.environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;

/**
 */
public class EnvironmentContextInitializer implements ServletContextListener
{

    public static final String ENVIRONMENT_PROPERTY_NAME = "application_environment";

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();

        context.log(String.format("Looking for %s in system environment.",ENVIRONMENT_PROPERTY_NAME));
        String environment = System.getenv().get(ENVIRONMENT_PROPERTY_NAME);
        if(environment != null)
            System.setProperty(ENVIRONMENT_PROPERTY_NAME,environment);

        if(environment == null)
        {
            context.log(String.format("Looking for %s as system property.",ENVIRONMENT_PROPERTY_NAME));
            environment = System.getProperty(ENVIRONMENT_PROPERTY_NAME);
        }

        if(environment == null)
        {
            context.log("Attempting to determine environment from host name.");
            String hostName = null;
            try
            {
                hostName = InetAddress.getLocalHost().getHostName();
            }
            catch (Exception ignore){}

            if(hostName != null)
            {
                context.log(String.format("Detected hostname %s",hostName));

                if(hostName.length() > 4 && hostName.toLowerCase().startsWith("frp"))
                {
                    String env = hostName.substring(3,4);
                    if(env.equals("d"))
                        environment = "dev";
                    else if(env.equals("q"))
                        environment = "qa";
                    else if(env.equals("s"))
                        environment = "stage";
                    else if(env.equals("p"))
                        environment = "prod";
                    else
                        context.log(String.format("Could not determine environment from host name %s.",hostName));

                    if(environment != null)
                        System.setProperty(ENVIRONMENT_PROPERTY_NAME,environment);
                }
            }


            if(environment == null)
            {
                context.log("Could not detect environment. Aborting.");
                throw new RuntimeException("Could not detect environment.");
            }

            context.log(String.format("Detected environment: %s",environment));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

    }
}
