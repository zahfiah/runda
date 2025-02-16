package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 短信记录对象 alarm_info
 * 
 * @author runda
 * @date 2025-02-14
 */
public class AlarmInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 县级id */
    private Long userId;

    /** 联系人 */
    @Excel(name = "联系人")
    private String userName;

    /** 单位id */
    private Long enterpriseId;

    /** 站点id */
    private Long stationId;

    /** 报警类型 */
    private Long alarmType;

    /** 开始时间 */
    private Long beginTime;

    /** 结束时间 */
    private Long endTime;

    /** 时间间隔 */
    private Long timeSpace;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phoneNumber;

    /** 短信模板 */
    @Excel(name = "短信模板")
    private String smsTem;

    /** 发送内容 */
    @Excel(name = "发送内容")
    private String smsMessage;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String smsFail;

    /** 部门id */
    private Long deptId;

    /** 部门昵称 */
    private String deptName;

    /** 部门权限 */
    private String deptPath;

    /** 操作员 */
    private Long systemUserId;

    /** 信息发送状态 */
    private Long infoStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdatedDate;

    /** 设备状态 */
    @Excel(name = "设备状态")
    private String status;

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 站点名称 */
    private String stationName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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
    public void setEnterpriseId(Long enterpriseId) 
    {
        this.enterpriseId = enterpriseId;
    }

    public Long getEnterpriseId() 
    {
        return enterpriseId;
    }
    public void setStationId(Long stationId) 
    {
        this.stationId = stationId;
    }

    public Long getStationId() 
    {
        return stationId;
    }
    public void setAlarmType(Long alarmType) 
    {
        this.alarmType = alarmType;
    }

    public Long getAlarmType() 
    {
        return alarmType;
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
    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }
    public void setSmsTem(String smsTem) 
    {
        this.smsTem = smsTem;
    }

    public String getSmsTem() 
    {
        return smsTem;
    }
    public void setSmsMessage(String smsMessage) 
    {
        this.smsMessage = smsMessage;
    }

    public String getSmsMessage() 
    {
        return smsMessage;
    }
    public void setSmsFail(String smsFail) 
    {
        this.smsFail = smsFail;
    }

    public String getSmsFail() 
    {
        return smsFail;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
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
    public void setInfoStatus(Long infoStatus) 
    {
        this.infoStatus = infoStatus;
    }

    public Long getInfoStatus() 
    {
        return infoStatus;
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
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
    public void setStationName(String stationName) 
    {
        this.stationName = stationName;
    }

    public String getStationName() 
    {
        return stationName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("enterpriseId", getEnterpriseId())
            .append("stationId", getStationId())
            .append("alarmType", getAlarmType())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .append("timeSpace", getTimeSpace())
            .append("phoneNumber", getPhoneNumber())
            .append("smsTem", getSmsTem())
            .append("smsMessage", getSmsMessage())
            .append("smsFail", getSmsFail())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("deptPath", getDeptPath())
            .append("systemUserId", getSystemUserId())
            .append("infoStatus", getInfoStatus())
            .append("createDate", getCreateDate())
            .append("lastUpdatedDate", getLastUpdatedDate())
            .append("status", getStatus())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("stationName", getStationName())
            .toString();
    }
}
