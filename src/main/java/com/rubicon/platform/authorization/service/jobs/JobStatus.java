package com.rubicon.platform.authorization.service.jobs;

import org.jgroups.annotations.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * User: mhellkamp
 * Date: 1/31/13
 */
@ManagedResource
public class JobStatus
{
	private boolean running;
	private long lastStartTime;
	private long lastDuration;
    private long lastErrorTime;
    private String lastErrorStackTrace;

	public void start()
	{
		running = true;
		lastStartTime = System.currentTimeMillis();
	}

	public void stop()
	{
		if(!running)
			return;

		running = false;
		lastDuration = System.currentTimeMillis() - lastStartTime;
	}

    public void error(Throwable throwable)
    {
        lastErrorTime = System.currentTimeMillis();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        throwable.printStackTrace(ps);
        ps.flush();
        lastErrorStackTrace = os.toString();
    }

	@ManagedAttribute
	public boolean isRunning()
	{
		return running;
	}

	@ManagedAttribute
	public String getLastStartTime()
	{
		if (lastStartTime == 0)
			return "";

		return new Date(lastStartTime).toString();
	}

	@ManagedAttribute
	public long getLastDuration()
	{
		return lastDuration;
	}

    @ManagedAttribute
    public String getLastErrorTime()
    {
        if (lastErrorTime == 0)
            return "";

        return new Date(lastErrorTime).toString();
    }

    @ManagedAttribute
    public String getLastErrorStackTrace()
    {
        return lastErrorStackTrace;
    }
}
