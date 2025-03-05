package com.ruoyi.runda.utils;

import com.github.pagehelper.util.StringUtil;
import com.ruoyi.runda.domain.AirDataResult;
import com.ruoyi.runda.domain.DataQuery212;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class Convert {
    public DataQuery212 convertAirDataResultToDataQuery212(AirDataResult airDataResult) {
        DataQuery212 result = new DataQuery212();

        String id = airDataResult.id;
        result.setId(id);

        String sn = airDataResult.mn;
        result.setSn(sn);

        Date data = airDataResult.dataTime;
        result.setDate(data);

        String fullData = ""; // 弃用
        result.setFullData(fullData);

        Double deviceTemperature = 0.0d; // 弃用
        result.setDeviceTemperature(deviceTemperature);

        Long depId = airDataResult.deptId;
        result.setDeptId(depId);

        Long stationId = airDataResult.stationId;
        result.setStationId(stationId);

        String deviceId = airDataResult.deviceId;
        result.setDeviceId(deviceId);

        String deviceName = airDataResult.deviceName;
        result.setDeviceName(deviceName);

        String stationName = airDataResult.stationName;
        result.setStationName(stationName);


//        result = ProcessCp(airDataResult.cp, result);
        return result;
    }
    private DataQuery212 ProcessCp(Map<String,String> cp, DataQuery212 result) {
        String rtd = "-Rtd";
        String tem = AirFactorCodingEnum.A01001.getCode() + rtd;
        String hum = AirFactorCodingEnum.A01002.getCode() + rtd;
        String pre = AirFactorCodingEnum.A01006.getCode() + rtd;
        String windSpeed = AirFactorCodingEnum.A01007.getCode() + rtd;
        String windDirection = AirFactorCodingEnum.A01008.getCode() + rtd;

        String pm10 = AirFactorCodingEnum.A34002.getCode() + rtd;
        String pm25 = AirFactorCodingEnum.A34004.getCode() + rtd;
        String tsp = AirFactorCodingEnum.A34001.getCode() + rtd;
        String no = AirFactorCodingEnum.A21003.getCode() + rtd;
        String no2 = AirFactorCodingEnum.A21004.getCode() + rtd;
        String so2 = AirFactorCodingEnum.A21026.getCode() + rtd;
        String co = AirFactorCodingEnum.A21005.getCode() + rtd;
        String c6h6 = AirFactorCodingEnum.A25002.getCode() + rtd;
        String noiseLa = NoiseFactorCodingEnum.LA.getCode() + rtd;
        String o3 = AirFactorCodingEnum.O3.getCode() + rtd;
        String o3Szzt = AirFactorCodingEnum.A05024.getCode() + rtd;

        String dataTimeTag = "DataTime";
        String dateTimeSet= cp.get(dataTimeTag);
        long dataTime = Long.valueOf( dateTimeSet);
        if (StringUtil.isEmpty(dateTimeSet)) {
            result.setCreateDate(new Date());
        } else {
            result.setCreateDate(new Date(dataTime));
        }
        Double temValue = cp.get(tem) == null ? 0 : Double.valueOf(cp.get(tem));
        Double humValue = cp.get(hum) == null ? 0 : Double.valueOf(cp.get(hum));
        Double preValue = cp.get(pre) == null ? 0 : Double.valueOf(cp.get(pre));
        Double wsValue = cp.get(windSpeed) == null ? 0.0d : Double.valueOf(cp.get(windSpeed));
        Double wdValue = cp.get(windDirection) == null ? 0.0d : Double.valueOf(cp.get(windDirection));
        Double pm10Value = cp.get(pm10) == null ? 0 : Double.valueOf(cp.get(pm10));
        String pm25Value = cp.get(pm25) == null ? "0" :  cp.get(pm25) ;
        Double tspValue = cp.get(tsp) == null ? 0 : Double.valueOf(cp.get(tsp));
        Double noValue = cp.get(no) == null ? 0 : Double.valueOf(cp.get(no));
        Double no2Value = cp.get(no2) == null ? 0 : Double.valueOf(cp.get(no2));
        Double so2Value = cp.get(so2) == null ? 0 : Double.valueOf(cp.get(so2));
        Double coValue = cp.get(co) == null ? 0.0d : Double.valueOf(cp.get(co));
        Double c6h6Value = cp.get(c6h6) == null ? 0.0d : Double.valueOf(cp.get(c6h6));
        Double o3Value = cp.get(o3) == null ? 0 : Double.valueOf(cp.get(o3));
        if (cp.get(o3) == null) {
            o3Value = cp.get(o3Szzt) == null ? 0 : Double.valueOf(cp.get(o3Szzt));
        }
        Double noiseValue = cp.get(noiseLa) == null ? 0 : Double.valueOf(cp.get(noiseLa));

        result.setPm25above(pm25Value);
        result.setPm10(pm10Value);
        result.setTsp(tspValue);
        result.setNoThickness(noValue);
        result.setNo2Thickness(no2Value);
        result.setSo2Thickness(so2Value);
        result.setCoThickness(coValue);
        result.setCo3Thickness(o3Value);

        result.setTemperature(temValue);
        result.setHumidity(humValue);
        result.setWindSpeed(wsValue);
        result.setWindDirection(wdValue);
        result.setPressure(preValue);
        result.setNoise(noiseValue);

        return result;
    }
}

