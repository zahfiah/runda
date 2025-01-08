package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 接口日志对象 interface_log
 * 
 * @author runda
 * @date 2025-01-07
 */
public class InterfaceLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 数据条数 */
    @Excel(name = "数据条数")
    private Long dataCount;

    /** 设备添加数 */
    @Excel(name = "设备添加数")
    private Long deviceAddcount;

    /** 日志信息 */
    @Excel(name = "日志信息")
    private String msg;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date modifyDate;

    /** 设备修改数 */
    @Excel(name = "设备修改数")
    private Long deviceUpdatecount;

    /** 类型 */
    @Excel(name = "类型")
    private Long type;

    /** 部门名称 */
    private String deptName;

    /** 部门id */
    @Excel(name = "部门id")
    private Long userId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDataCount(Long dataCount) 
    {
        this.dataCount = dataCount;
    }

    public Long getDataCount() 
    {
        return dataCount;
    }
    public void setDeviceAddcount(Long deviceAddcount) 
    {
        this.deviceAddcount = deviceAddcount;
    }

    public Long getDeviceAddcount() 
    {
        return deviceAddcount;
    }
    public void setMsg(String msg) 
    {
        this.msg = msg;
    }

    public String getMsg() 
    {
        return msg;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setModifyDate(Date modifyDate) 
    {
        this.modifyDate = modifyDate;
    }

    public Date getModifyDate() 
    {
        return modifyDate;
    }
    public void setDeviceUpdatecount(Long deviceUpdatecount) 
    {
        this.deviceUpdatecount = deviceUpdatecount;
    }

    public Long getDeviceUpdatecount() 
    {
        return deviceUpdatecount;
    }
    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }
    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("dataCount", getDataCount())
            .append("deviceAddcount", getDeviceAddcount())
            .append("msg", getMsg())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("modifyDate", getModifyDate())
            .append("deviceUpdatecount", getDeviceUpdatecount())
            .append("type", getType())
            .append("deptName", getDeptName())
            .append("userId", getUserId())
            .toString();
    }
}
