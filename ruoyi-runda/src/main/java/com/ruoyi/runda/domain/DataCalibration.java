package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据校准管理对象 data_calibration
 * 
 * @author runda
 * @date 2025-01-07
 */
public class DataCalibration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 县级id */
    @Excel(name = "县级id")
    private Long userId;

    /** 部门昵称 */
    private String deptName;

    /** 状态 */
    private Long state;

    /** 创建人 */
    private String createdUser;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdDate;

    /** 算法 */
    private String algorithm;

    /** 参数 */
    private String parameter;

    /** 参数数值 */
    private String pValue;

    /** 设备id */
    private Long deviceId;

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
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setState(Long state) 
    {
        this.state = state;
    }

    public Long getState() 
    {
        return state;
    }
    public void setCreatedUser(String createdUser) 
    {
        this.createdUser = createdUser;
    }

    public String getCreatedUser() 
    {
        return createdUser;
    }
    public void setCreatedDate(Date createdDate) 
    {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() 
    {
        return createdDate;
    }
    public void setAlgorithm(String algorithm) 
    {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() 
    {
        return algorithm;
    }
    public void setParameter(String parameter) 
    {
        this.parameter = parameter;
    }

    public String getParameter() 
    {
        return parameter;
    }
    public void setpValue(String pValue) 
    {
        this.pValue = pValue;
    }

    public String getpValue() 
    {
        return pValue;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("deptName", getDeptName())
            .append("state", getState())
            .append("createdUser", getCreatedUser())
            .append("createdDate", getCreatedDate())
            .append("remark", getRemark())
            .append("algorithm", getAlgorithm())
            .append("parameter", getParameter())
            .append("pValue", getpValue())
            .append("deviceId", getDeviceId())
            .toString();
    }
}
