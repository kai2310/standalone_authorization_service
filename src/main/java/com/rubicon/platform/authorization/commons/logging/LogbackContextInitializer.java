package com.rubicon.platform.authorization.commons.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;
import org.springframework.util.SystemPropertyUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * User: mhellkamp
 * Date: 11/2/12
 */
public class LogbackContextInitializer implements ServletContextListener
{
    private static final String CONFIG_FILE_LOCATION = "loggingConfigurationFile";

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        String path = sce.getServletContext().getInitParameter(CONFIG_FILE_LOCATION);
        if(path == null || path.length() == 0)
            throw new RuntimeException(
                    String.format("Could not file context initialization parameter %s",CONFIG_FILE_LOCATION));


        path = SystemPropertyUtils.resolvePlaceholders(path);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(new File(path));
        }
        catch (JoranException e)
        {
            throw new RuntimeException("Error initializing Logback",e);
        }

        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
    }
}
