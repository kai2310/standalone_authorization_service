package com.rubicon.platform.authorization.service.v1.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rubicon.platform.authorization.data.model.CompoundId;

import java.util.List;

/**
 */
public class ServiceSubjectQueryResponse
{
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<CompoundId> subjects;

    public List<CompoundId> getSubjects()
    {
        return subjects;
    }

    public void setSubjects(List<CompoundId> subjects)
    {
        this.subjects = subjects;
    }
}
