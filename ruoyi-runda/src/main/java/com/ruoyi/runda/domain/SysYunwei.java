package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 运维日志对象 sys_yunwei
 * 
 * @author runda
 * @date 2025-01-11
 */
public class SysYunwei extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 工地名称 */
    @Excel(name = "工地名称")
    private String siteName;
    /**设备编号 */
    @Excel(name = "设备号")
    private String sn;
    /** 运维时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "运维时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date maintenanceTime;

    /** 是否完成 */
    @Excel(name = "是否完成")
    private Long isFinsh;

    /** 图片 */
    @Excel(name = "图片")
    private String img;

    /** 日志信息 */
    @Excel(name = "日志信息")
    private String log;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getSiteName() 
    {
        return siteName;
    }
    public void setSn(String sn)
    {
        this.sn = sn;
    }
    public String getSn()
    {
        return sn;
    }
    public void setMaintenanceTime(Date maintenanceTime) 
    {
        this.maintenanceTime = maintenanceTime;
    }

    public Date getMaintenanceTime() 
    {
        return maintenanceTime;
    }
    public void setIsFinsh(Long isFinsh) 
    {
        this.isFinsh = isFinsh;
    }

    public Long getIsFinsh() 
    {
        return isFinsh;
    }
    public void setImg(String img) 
    {
        this.img = img;
    }

    public String getImg() 
    {
        return img;
    }
    public void setLog(String log) 
    {
        this.log = log;
    }

    public String getLog() 
    {
        return log;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteName", getSiteName())
            .append("maintenanceTime", getMaintenanceTime())
            .append("isFinsh", getIsFinsh())
            .append("img", getImg())
            .append("log", getLog())
            .toString();
    }
}
