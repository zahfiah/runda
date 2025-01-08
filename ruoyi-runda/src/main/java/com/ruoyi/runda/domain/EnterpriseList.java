package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 单位名录对象 enterprise_list
 * 
 * @author runda
 * @date 2025-01-07
 */
public class EnterpriseList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 单位昵称 */
    @Excel(name = "单位昵称")
    private String name;

    /** 重点排污单位类别 */
    private String polluteUnitCategory;

    /** 污染类别:1废烟、2废气、3废水、4废渣、5噪音、6脏水、7垃圾、8电磁辐射、9二氧化碳、10二氧化硫、11氮氧化物、12粒子状污染物、13酸雨、14化学物质 */
    private Long polluteCategory;

    /** 监控类别 */
    private Long monitorCategory;

    /** 单位地址 */
    @Excel(name = "单位地址")
    private String addr;

    /** 单位电话 */
    @Excel(name = "单位电话")
    private String phone;

    /** 省 */
    private Long province;

    /** 市 */
    private Long city;

    /** 区县 */
    private Long county;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdatedTime;

    /** 部门id */
    private Long departmentId;

    /** 创建人 */
    private String createUser;

    /** 操作员id */
    private Long systemUserId;

    /** 状态 */
    private Long status;

    /** 站点编号 */
    private Long stationId;

    /** 诱发事故物质 */
    @Excel(name = "诱发事故物质")
    private String polluteGoods;

    /** 热源 */
    @Excel(name = "热源")
    private String heatSource;

    /** 水污染 */
    @Excel(name = "水污染")
    private String polluteWater;

    /** 空气污染 */
    @Excel(name = "空气污染")
    private String polluteAir;

    /** 固废 */
    @Excel(name = "固废")
    private String polluteSolid;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setPolluteUnitCategory(String polluteUnitCategory) 
    {
        this.polluteUnitCategory = polluteUnitCategory;
    }

    public String getPolluteUnitCategory() 
    {
        return polluteUnitCategory;
    }
    public void setPolluteCategory(Long polluteCategory) 
    {
        this.polluteCategory = polluteCategory;
    }

    public Long getPolluteCategory() 
    {
        return polluteCategory;
    }
    public void setMonitorCategory(Long monitorCategory) 
    {
        this.monitorCategory = monitorCategory;
    }

    public Long getMonitorCategory() 
    {
        return monitorCategory;
    }
    public void setAddr(String addr) 
    {
        this.addr = addr;
    }

    public String getAddr() 
    {
        return addr;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setProvince(Long province) 
    {
        this.province = province;
    }

    public Long getProvince() 
    {
        return province;
    }
    public void setCity(Long city) 
    {
        this.city = city;
    }

    public Long getCity() 
    {
        return city;
    }
    public void setCounty(Long county) 
    {
        this.county = county;
    }

    public Long getCounty() 
    {
        return county;
    }
    public void setLastUpdatedTime(Date lastUpdatedTime) 
    {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getLastUpdatedTime() 
    {
        return lastUpdatedTime;
    }
    public void setDepartmentId(Long departmentId) 
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId() 
    {
        return departmentId;
    }
    public void setCreateUser(String createUser) 
    {
        this.createUser = createUser;
    }

    public String getCreateUser() 
    {
        return createUser;
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
    public void setStationId(Long stationId) 
    {
        this.stationId = stationId;
    }

    public Long getStationId() 
    {
        return stationId;
    }
    public void setPolluteGoods(String polluteGoods) 
    {
        this.polluteGoods = polluteGoods;
    }

    public String getPolluteGoods() 
    {
        return polluteGoods;
    }
    public void setHeatSource(String heatSource) 
    {
        this.heatSource = heatSource;
    }

    public String getHeatSource() 
    {
        return heatSource;
    }
    public void setPolluteWater(String polluteWater) 
    {
        this.polluteWater = polluteWater;
    }

    public String getPolluteWater() 
    {
        return polluteWater;
    }
    public void setPolluteAir(String polluteAir) 
    {
        this.polluteAir = polluteAir;
    }

    public String getPolluteAir() 
    {
        return polluteAir;
    }
    public void setPolluteSolid(String polluteSolid) 
    {
        this.polluteSolid = polluteSolid;
    }

    public String getPolluteSolid() 
    {
        return polluteSolid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("polluteUnitCategory", getPolluteUnitCategory())
            .append("polluteCategory", getPolluteCategory())
            .append("monitorCategory", getMonitorCategory())
            .append("addr", getAddr())
            .append("phone", getPhone())
            .append("remark", getRemark())
            .append("province", getProvince())
            .append("city", getCity())
            .append("county", getCounty())
            .append("createTime", getCreateTime())
            .append("lastUpdatedTime", getLastUpdatedTime())
            .append("departmentId", getDepartmentId())
            .append("createUser", getCreateUser())
            .append("systemUserId", getSystemUserId())
            .append("status", getStatus())
            .append("stationId", getStationId())
            .append("polluteGoods", getPolluteGoods())
            .append("heatSource", getHeatSource())
            .append("polluteWater", getPolluteWater())
            .append("polluteAir", getPolluteAir())
            .append("polluteSolid", getPolluteSolid())
            .toString();
    }
}
