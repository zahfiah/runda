package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警设置对象 alarm_remind
 * 
 * @author runda
 * @date 2025-01-07
 */
public class AlarmRemind extends BaseEntity
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

    /** 国控code */
    @Excel(name = "国控code")
    private String guid;

    /** 阀值 */
    @Excel(name = "阀值")
    private Long standardValue;

    /** 开始时间 */
    private Long beginTime;

    /** 结束时间 */
    private Long endTime;

    /** 时间间隔 */
    private Long timeSpace;

    /** 通知方式 */
    @Excel(name = "通知方式")
    private String notice;

    /** 部门id */
    @Excel(name = "部门id")
    private Long userId;

    /** 部门昵称 */
    private String deptName;

    /** 部门权限 */
    private String deptPath;

    /** 通知方 */
    private Long systemUserId;

    /** 提醒状态 */
    private Long status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 最后更新时间 */
    private Date lastUpdatedDate;

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
    public void setGuid(String guid) 
    {
        this.guid = guid;
    }

    public String getGuid() 
    {
        return guid;
    }
    public void setStandardValue(Long standardValue) 
    {
        this.standardValue = standardValue;
    }

    public Long getStandardValue() 
    {
        return standardValue;
    }
    public void setBeginTime(Long beginTime) 
    {
        this.beginTime = beginTime;
    }

    public Long getBeginTime() 
    {
        return beginTime;
    }
    public void setEndTime(Long endTime) 
    {
        this.endTime = endTime;
    }

    public Long getEndTime() 
    {
        return endTime;
    }
    public void setTimeSpace(Long timeSpace) 
    {
        this.timeSpace = timeSpace;
    }

    public Long getTimeSpace() 
    {
        return timeSpace;
    }
    public void setNotice(String notice) 
    {
        this.notice = notice;
    }

    public String getNotice() 
    {
        return notice;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setDeptPath(String deptPath) 
    {
        this.deptPath = deptPath;
    }

    public String getDeptPath() 
    {
        return deptPath;
    }
    public void setSystemUserId(Long systemUserId) 
    {
        this.systemUserId = systemUserId;
    }

    public Long getSystemUserId() 
    {
        return systemUserId;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setCreateDate(Date createDate) 
    {
        this.createDate = createDate;
    }

    public Date getCreateDate() 
    {
        return createDate;
    }
    public void setLastUpdatedDate(Date lastUpdatedDate) 
    {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastUpdatedDate() 
    {
        return lastUpdatedDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmType", getAlarmType())
            .append("alarmParamType", getAlarmParamType())
            .append("guid", getGuid())
            .append("standardValue", getStandardValue())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("timeSpace", getTimeSpace())
            .append("notice", getNotice())
            .append("userId", getUserId())
            .append("deptName", getDeptName())
            .append("deptPath", getDeptPath())
            .append("systemUserId", getSystemUserId())
            .append("status", getStatus())
            .append("createDate", getCreateDate())
            .append("lastUpdatedDate", getLastUpdatedDate())
            .toString();
    }
}
