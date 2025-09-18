package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.service.cache.CacheReloadController;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Detects when the database has been refreshed. This should only be used in non-production
 * environments, not in production
 */
public class CacheReloadJob extends BaseJob<CacheReloadJobStatus>
{
    private static final String QUERY = "select %s from %s where key_name=?";
    private CacheReloadController cacheReloadController;
    private long lastRefresh = 0;
    private JdbcTemplate jdbcTemplate;
    private String tableName;
    private String valueColumnName;
    private String key;

    public void setCacheReloadController(CacheReloadController cacheReloadController)
    {
        this.cacheReloadController = cacheReloadController;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public void setValueColumnName(String valueColumnName)
    {
        this.valueColumnName = valueColumnName;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    protected void execute()
    {
        boolean needsReload = false;

        long detected = getLastModified();
        if(detected == -1)
        {
            logger.info("No DB available, not refreshing.");
            return;
        }
        if(lastRefresh != detected)
        {
            logger.info("Detected value {}. Focing cache refresh",detected);
            needsReload = true;
        }

        if(needsReload)
        {
            if(cacheReloadController.reloadCaches())
            {
                lastRefresh = detected;
                jobStatus.refreshed();
            }
        }

    }

    private long getLastModified()
    {
        try
        {
            ResultSetExtractor<Date> extractor = new ResultSetExtractor<Date>()
            {
                @Override
                public Date extractData(ResultSet rs) throws SQLException, DataAccessException
                {
                    if(rs.next())
                        return rs.getTimestamp(1);
                    return null;
                }
            };

            Date date = jdbcTemplate.query(
                    String.format(QUERY, valueColumnName, tableName),
                    new Object[]{key}, extractor);
            if(date != null)
            {
                return date.getTime();
            }
        }
        catch (Exception e)
        {
            logger.error("Error reading last modified refresh, ignoring.",e);
            return -1;
        }

        return 0;
    }
}
