package com.rubicon.platform.authorization.service.servlet;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubicon.platform.authorization.metrics.HealthCheckRegistryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthMonitorServlet extends HttpServlet
{
    private Logger logger = LoggerFactory.getLogger(HealthMonitorServlet.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String queryString = req.getQueryString() != null
                             ? req.getQueryString()
                             : "";
        logger.debug("Accepting Request: {}?{}", req.getRequestURI(), queryString);

        String monitorName = req.getParameter("healthcheck");
        if (monitorName == null ||
            !HealthCheckRegistryHolder.getInstance().getRegistry().getNames().contains(monitorName))
        {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getOutputStream().write(new String("Invalid or missing monitor name").getBytes());
            return;
        }

        HealthCheck.Result result = HealthCheckRegistryHolder.getInstance().getRegistry().runHealthCheck(monitorName);
        resp.setStatus(result.isHealthy()
                       ? HttpServletResponse.SC_OK
                       : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setHeader("Content-Type", "application/json");
        objectMapper.writeValue(resp.getOutputStream(), result);
    }
}
