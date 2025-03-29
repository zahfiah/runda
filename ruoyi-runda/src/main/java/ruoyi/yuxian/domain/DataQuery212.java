package ruoyi.yuxian.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
@Data
public class DataQuery212 {

    /** 主键id */
    @Id
    private String id;
    /** 设备型号 */
    @Field("sn")
    private String sn;

    /** 日期 */
    @Field("date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /** 温度 */
    @Field("temperature")
    private Double temperature;

    /** 湿度 */
    @Field("humidity")
    private Double humidity;

    /** 风速 */
    @Field("windSpeed")
    private Double windSpeed;

    /** 风向 */
    @Field("windDirection")
    private Double windDirection;//风向

    /** 风向 */
    @Field("windDirectionString")
    private String windDirectionString;


    /** 压力 */
    @Field("pressure")
    private Double pressure;


    /** 噪音 */
    @Field("noise")
    private Double noise;

    /** 粉尘pm2.5 */
    @Field("dust")
    private Double pm2_5;//粉尘pm2.5

    /** pm10浓度 */
    @Field("pm10")
    private Double pm10;


    /** co浓度 */
    @Field("coThickness")
    private Double coThickness;


    /** 空气质量指数 */
    @Field("aqi")
    private Double aqi;

    /** 部门_id */
    @Field("deptId")
    private Long deptId;

    /** 设备_id */
    @Field("deviceId")
    private String deviceId;

    /** 设备名称 */
    private String deviceName;

}
