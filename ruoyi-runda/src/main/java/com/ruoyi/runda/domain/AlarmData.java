package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警日志对象 alarm_data
 * 
 * @author runda
 * @date 2025-01-07
 */
public class AlarmData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 报警类型 */
    @Excel(name = "报警类型")
    private Long alarmType;

    /** 报警参数类型 */
    @Excel(name = "报警参数类型")
    private Long alarmParamType;

    /** 报警值 */
    @Excel(name = "报警值")
    private Long alarmValue;

    /** 阀值 */
    @Excel(name = "阀值")
    private Long standardValue;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date beginTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 报警信息状态 */
    @Excel(name = "报警信息状态")
    private Long status;

    /** 站点id */
    @Excel(name = "站点id")
    private Long stationId;

    /** 站点昵称 */
    private String staName;

    /** 设备id */
    @Excel(name = "设备id")
    private Long deviceId;

    /** 设备昵称 */
    private String deviceName;

    /** 部门id */
    @Excel(name = "部门id")
    private Long userId;

    /** 部门昵称 */
    private String userName;

    /** 部门权限 */
    private String userPath;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAlarmType(Long alarmType) 
    {
        this.alarmType = alarmType;
    }

    public Long getAlarmType() 
    {
        return alarmType;
    }
    public void setAlarmParamType(Long alarmParamType) 
    {
        this.alarmParamType = alarmParamType;
    }

    public Long getAlarmParamType() 
    {
        return alarmParamType;
    }
    public void setAlarmValue(Long alarmValue) 
    {
        this.alarmValue = alarmValue;
    }

    public Long getAlarmValue() 
    {
        return alarmValue;
    }
    public void setStandardValue(Long standardValue) 
    {
        this.standardValue = standardValue;
    }

    public Long getStandardValue() 
    {
        return standardValue;
    }
    public void setBeginTime(Date beginTime) 
    {
        this.beginTime = beginTime;
    }

    public Date getBeginTime() 
    {
        return beginTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setStationId(Long stationId) 
    {
        this.stationId = stationId;
    }

    public Long getStationId() 
    {
        return stationId;
    }
    public void setStaName(String staName) 
    {
        this.staName = staName;
    }

    public String getStaName() 
    {
        return staName;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setUserPath(String userPath) 
    {
        this.userPath = userPath;
    }

    public String getUserPath() 
    {
        return userPath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmType", getAlarmType())
            .append("alarmParamType", getAlarmParamType())
            .append("alarmValue", getAlarmValue())
            .append("standardValue", getStandardValue())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("status", getStatus())
            .append("stationId", getStationId())
            .append("staName", getStaName())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("userPath", getUserPath())
            .toString();
    }
}
