package com.ruoyi.runda.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;
@Document(collection = "deviceairdata")
@CompoundIndexes({
        @CompoundIndex(name = "dataTime_deviceId_path", def = "{'deviceId': 1, 'dataTime': -1}")
})
public class AirDataResult extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /** 主键id */
    @Id
    public String id;
    /** 设备ID */
    @Indexed
    public String deviceId;
    /** 设备名称*/
    public String deviceName;
    /** 状态(1：正常，2：停用，3：删除，4.中断)*/
    public Integer status;
    /** 指令时间  */
    public Date createDate;
    /** 数据时间*/

    @Indexed
    public Date dataTime;
    /** 经度*/
    public Double longitude;
    /** 纬度*/
    public Double latitude;
    /** 部门id*/
    public Long deptId;
    /** 部门路径*/
    public String path;
    /** 站点_id */
    public Long stationId;
    /** 站点昵称*/
    public String stationName;

    /** 请求编码QN*/
    public String qn;
    /** 系统编码ST*/
    public String st;
    /** 命令编码CN*/
    public String cn;
    /** 访问密码PW*/
    public String pw;
    /** 站点唯一标识MN*/
    public String mn;
    /** 应答标识Flag*/
    public Integer flag;
    /** 指令参数CP*/
    public String cp;
    /** 接收到数据包字符串*/
    public String messageData;

    //生成个字段对应的get set方法
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getDataTime() {
        return dataTime;
    }
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Long getDeptId() {
        return deptId;
    }
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Long getStationId() {
        return stationId;
    }
    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getQn() {
        return qn;
    }
    public void setQn(String qn) {
        this.qn = qn;
    }
    public String getSt() {
        return st;
    }
    public void setSt(String st) {
        this.st = st;
    }
    public String getCn() {
        return cn;
    }
    public void setCn(String cn) {
        this.cn = cn;
    }
    public String getPw() {
        return pw;
    }
    public void setPw(String pw) {
        this.pw = pw;
    }
    public String getMn() {
        return mn;
    }
    public void setMn(String mn) {
        this.mn = mn;
    }
    public Integer getFlag() {
        return flag;
    }
    public void setFlag(Integer flag) {
        this.flag = flag;
    }
    public String getCp() {
        return cp;
    }
    public void setCp(String cp) {
        this.cp = cp;
    }
    public String getMessageData() {
        return messageData;
    }
    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

}
