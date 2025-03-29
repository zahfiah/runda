package ruoyi.yuxian.domain;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Device {

    /** 主键 */
    private Long id;

    /** 设备名称 */
    private String name;

    /** 设备号 */
    private String sn;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:FFss")
    private Date createdTime;

    /** 状态 */
    private Long status;

    /** 工地状态 */
    private Long buildStatus;

    /** 区/县 */
    private Long county;

    /** 县名称 */
    private String countyCn;

    /** 制造商 */
    private String manufacturer;

    /** 部门ID */
    private Long departmentId;

    /** 设备类型 */
    private Long type;


}
