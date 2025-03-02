package com.ruoyi.runda.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.DataQueryCountryMapper;
import com.ruoyi.runda.domain.DataQueryCountry;
import com.ruoyi.runda.service.IDataQueryCountryService;

/**
 * 国控数据Service业务层处理
 *
 * @author ruoyi
 * @date 2025-02-25
 */
@Service
public class DataQueryCountryServiceImpl implements IDataQueryCountryService
{

    @Autowired
    private DataQueryCountryMapper dataQueryCountryMapper;

    // 内部类用于存储站点信息
    private static class StationInfo {
        String id;
        String name;

        StationInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final String BASE_URL = "https://website-api.airvisual.com/v1/";

    // 修改后的httpRequest方法
    @Override
    public void httpRequest() {
        try {
            String city = "zhangjiakou";
            String cityId = getCityId(city);
            List<StationInfo> stations = getStations(cityId);
            processStationData(stations);
        } catch (Exception e) {
            // 建议替换为日志记录，这里保留原始打印方式
            e.printStackTrace();
            throw new RuntimeException("数据请求异常", e);
        }
    }

    private String getCityId(String city) throws Exception {
        String url = BASE_URL + "routes/china/hebei/" + city;
        String response = sendGetRequest(url);
        return JSON.parseObject(response).getString("id");
    }

    private List<StationInfo> getStations(String cityId) throws Exception {
        String url = BASE_URL + "stations/by/cityID/" + cityId + "?language=zh-Hans";
        String response = sendGetRequest(url);
        JSONArray stationsArray = JSON.parseArray(response);

        return stationsArray.stream()
                .map(obj -> {
                    JSONObject station = (JSONObject) obj;
                    return new StationInfo(
                            station.getString("id"),
                            station.getString("name")
                    );
                })
                .collect(Collectors.toList());
    }

    private void processStationData(List<StationInfo> stations) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (StationInfo station : stations) {
            try {
                String url = BASE_URL + "stations/" + station.id + "?AQI=CN&language=zh-Hans";
                String response = sendGetRequest(url);
                JSONObject res = JSON.parseObject(response);
                Map<String, Object> data = new HashMap<>();
                JSONArray pollutants = res.getJSONObject("current").getJSONArray("pollutants");
                for (int i = 0; i < pollutants.size(); i++) {
                    JSONObject pollutant = pollutants.getJSONObject(i);
                    data.put(pollutant.getString("pollutantName"),
                            pollutant.getDouble("concentration"));
                }
                DataQueryCountry  dataQueryCountry = new DataQueryCountry();
                dataQueryCountry.setName(station.name);
                dataQueryCountry.setPm(Double.parseDouble(data.get("pm25").toString()));
                dataQueryCountry.setPm10(Double.parseDouble(data.get("pm10").toString()));
                dataQueryCountry.setSo2Thickness(Double.parseDouble(data.get("so2").toString()));
                dataQueryCountry.setNo2Thickness(Double.parseDouble(data.get("no2").toString()));
                dataQueryCountry.setCoThickness(Double.parseDouble(data.get("co").toString()));
                dataQueryCountry.setCo3Thickness(Double.parseDouble(data.get("o3").toString()));
                dataQueryCountry.setAqi(res.getJSONObject("current").getDoubleValue("aqi"));
                // 使用Java 8时间API处理（推荐方案）
                String ts = res.getJSONObject("current").getString("ts");//"2025-02-28T12:00:00.000Z";
                // 解析为Instant（UTC时间）
                Instant instant = Instant.parse(ts);
                // 转换为北京时间（+8时区）
                ZonedDateTime beijingTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
                // 转换为SQL Timestamp（用于数据库存储）
                //Timestamp sqlTimestamp = Timestamp.from(instant);
                // 格式化为字符串（可选）
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault());
                String formatted = formatter.format(instant);
                dataQueryCountry.setTime(Timestamp.valueOf(formatted));
                dataQueryCountryMapper.insertDataQueryCountry(dataQueryCountry);


//                Map<String, Object> data = new HashMap<>();
//                JSONArray pollutants = res.getJSONObject("current").getJSONArray("pollutants");
//                for (int i = 0; i < pollutants.size(); i++) {
//                    JSONObject pollutant = pollutants.getJSONObject(i);
//                    data.put(pollutant.getString("pollutantName"),
//                            pollutant.getDouble("concentration"));
//                }
//
//                Map<String, Object> stationData = new HashMap<>();
//                stationData.put("name", station.name);
//                stationData.put("data", data);
//                stationData.put("aqi", res.getJSONObject("current").getIntValue("aqi"));
//
//                result.add(stationData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //System.out.println(result);
        //return result;
    }

    private String sendGetRequest(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream is = conn.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining());
        } finally {
            conn.disconnect();
        }
    }

//    @Override
//    public void httpRequest()
//    {
//
////        //调用的api的接口地址
////        String apiPath = "https://devapi.qweather.com/v7/air/now?location=101090301&key=4b6b22d06da749f7bbb4267822dd4691&gzip=n";
////        StringBuilder sb = new StringBuilder();
////        InputStream is = null;
////        BufferedReader br = null;
////        try {
////            URL url = new URL(apiPath);
////            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
////
////            conn.setRequestMethod("GET");
////            conn.setReadTimeout(1000);
////            conn.setConnectTimeout(1000);
////            conn.setRequestProperty("accept" , "*/*");
////            conn.setRequestProperty("contentType", "application/json;charset=utf-8");
////
////
////            is = new GZIPInputStream(conn.getInputStream());
////            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
////
////            String line;
////            while((line = br.readLine()) != null){
////                sb.append(line);
////            }
////            String result2 =  sb.toString(); //返回json字符串
////            System.out.println(result2);
////            //获取数据
////            JSONObject jsonObject = JSON.parseObject(result2);
////            String code = jsonObject.getString("code");
////            System.out.println(apiPath);
////            System.out.println(code);
////            //JSONObject resultJsonObject = jsonObject.getJSONObject("data");
////            //System.out.println(resultJsonObject);
////            // JSONObject bodyJsonObject = resultJsonObject.getJSONObject("showapi_res_body");
////            JSONArray jsonArray = jsonObject.getJSONArray("station");
////            //System.out.println();
////            //遍历json集合，取出数据
////            //System.out.println(jsonArray.size());
////            for (int i = 0; i < jsonArray.size(); i++) {
////                JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
////                //System.out.println(jsonObject2);
////                DataQueryCountry  dataQueryCountry = new DataQueryCountry();
////                //jsonObject2.get("x_x").toString()中的“x_x”要和实际返回的json数据中的字段名一致，否则可能会出现找不到字段的错误提示
////                // news.setId(jsonObject2.get("id").toString());
////                //countryControls.setTime(Long.parseLong(jsonObject2.get("time").toString()));
////                dataQueryCountry.setName(jsonObject2.get("name").toString());
////                String dateStr= jsonObject2.get("pubTime").toString();
////                dateStr = dateStr.replace("T", " ");
////                dateStr = dateStr.substring(0, 15);
////                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
////                try {
////                    Date date = sdf.parse(dateStr);
////                    dataQueryCountry.setTime(date);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////                //countryControls.setTime((Date) jsonObject2.get("pubTime"));
////                dataQueryCountry.setAqi(Double.parseDouble(jsonObject2.get("aqi").toString()));
//                dataQueryCountry.setPm(Double.parseDouble(jsonObject2.get("pm2p5").toString()));
//                dataQueryCountry.setPm10(Double.parseDouble(jsonObject2.get("pm10").toString()));
//                dataQueryCountry.setSo2Thickness(Double.parseDouble(jsonObject2.get("so2").toString()));
//                dataQueryCountry.setNo2Thickness(Double.parseDouble(jsonObject2.get("no2").toString()));
//                dataQueryCountry.setCoThickness(Double.parseDouble(jsonObject2.get("co").toString()));
//                dataQueryCountry.setCo3Thickness(Double.parseDouble(jsonObject2.get("o3").toString()));
////
////                //countryControls.setCid(jsonObject2.get("title").toString());
////
////                //dao层保存数据存入数据库
////                dataQueryCountryMapper.insertDataQueryCountry(dataQueryCountry);
////            }
////        } catch (MalformedURLException e) {
////            e.printStackTrace();
////        } catch (IOException e) {
////            e.printStackTrace();
////        } finally {
////            try {
////                if (is != null) {
////                    is.close();
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////        }
//    }

    /**
     * 查询国控数据
     *
     * @param id 国控数据主键
     * @return 国控数据
     */
    @Override
    public DataQueryCountry selectDataQueryCountryById(Long id)
    {
        return dataQueryCountryMapper.selectDataQueryCountryById(id);
    }

    /**
     * 查询国控数据列表
     *
     * @param dataQueryCountry 国控数据
     * @return 国控数据
     */
    @Override
    public List<DataQueryCountry> selectDataQueryCountryList(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.selectDataQueryCountryList(dataQueryCountry);
    }

    /**
     * 新增国控数据
     *
     * @param dataQueryCountry 国控数据
     * @return 结果
     */
    @Override
    public int insertDataQueryCountry(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.insertDataQueryCountry(dataQueryCountry);
    }

    /**
     * 修改国控数据
     *
     * @param dataQueryCountry 国控数据
     * @return 结果
     */
    @Override
    public int updateDataQueryCountry(DataQueryCountry dataQueryCountry)
    {
        return dataQueryCountryMapper.updateDataQueryCountry(dataQueryCountry);
    }

    /**
     * 批量删除国控数据
     *
     * @param ids 需要删除的国控数据主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCountryByIds(Long[] ids)
    {
        return dataQueryCountryMapper.deleteDataQueryCountryByIds(ids);
    }

    /**
     * 删除国控数据信息
     *
     * @param id 国控数据主键
     * @return 结果
     */
    @Override
    public int deleteDataQueryCountryById(Long id)
    {
        return dataQueryCountryMapper.deleteDataQueryCountryById(id);
    }
}
