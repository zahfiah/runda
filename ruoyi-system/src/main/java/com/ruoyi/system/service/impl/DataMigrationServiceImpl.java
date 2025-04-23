package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.DataMigrationService;
import org.locationtech.jts.math.DD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    @Autowired
    private JdbcTemplate jdbcTemplateA; // 数据库 runda 的 JdbcTemplate

    @Autowired
    private JdbcTemplate jdbcTemplateB; // 数据库 province_test 的 JdbcTemplate

    // 定义station字段映射关系
    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();
    static {
        FIELD_MAPPING.put("station_name", "XMMC");
        FIELD_MAPPING.put("link_man", "JZGDFZR");
        FIELD_MAPPING.put("phone", "JZGDFZRDH");
        FIELD_MAPPING.put("city", "SSSDM");
        FIELD_MAPPING.put("county", "QXDM");
        FIELD_MAPPING.put("licens_number", "SGXKZBH");
        FIELD_MAPPING.put("latitude", "ZXWD");
        FIELD_MAPPING.put("longitude", "ZXJD");
        FIELD_MAPPING.put("jsdwmc", "JSDWMC");
        FIELD_MAPPING.put("jstyshxydm", "JSTYSHXYDM");
        FIELD_MAPPING.put("sgdwmc", "SGDWMC");
        FIELD_MAPPING.put("sgtyshxydm", "SGTYSHXYDM");
        FIELD_MAPPING.put("created_time", "XGRQSJ");
        FIELD_MAPPING.put("last_updated_time", "REPORT_TIME");
        FIELD_MAPPING.put("floor_space", "ZDMJ");
        FIELD_MAPPING.put("fromResource", "EEMP_FLAG");
    }

    // 合并 FIELD_MAPPING_device1 和 FIELD_MAPPING_device2 的映射关系
    private static final Map<String, String> FIELD_MAPPING_DEVICE = new HashMap<>();
    static {
        FIELD_MAPPING_DEVICE.put("name", "JCDMC");
        FIELD_MAPPING_DEVICE.put("sn", "JCZDBH");
        FIELD_MAPPING_DEVICE.put("status", "YXZT");
        FIELD_MAPPING_DEVICE.put("fromResource", "EEMP_FLAG");
        FIELD_MAPPING_DEVICE.put("created_time", "XGRQSJ");
        FIELD_MAPPING_DEVICE.put("last_updated_time", "REPORT_TIME");
        FIELD_MAPPING_DEVICE.put("latitude", "ZXWD");
        FIELD_MAPPING_DEVICE.put("longitude", "ZXJD");
        FIELD_MAPPING_DEVICE.put("city", "SSSDM");
    }





    @Override
    @Transactional
    public String migrateData() {
        // 1. 定义需要迁移的表和目标表
        String sourceTable = "station"; // 源表
        String targetTable = "t_b_site_info";
        // 2. 迁移数据
        migrateTableData(sourceTable, targetTable);
        return "数据迁移完成！";
    }

    @Override
    public String migrateStateData() {
        // 1. 定义需要迁移的表和目标表
        String sourceTable = "station"; // 源表
        String targetTable = "t_d_site_state"; // 目标表

        // 2. 迁移数据
        migrateStateData(sourceTable, targetTable);
        return "数据迁移完成！";
    }

    @Override
    public String migrateMonitorData() {
        // 1. 定义需要迁移的表和目标表
        String sourceTable = "device"; // 源表
        String targetTable = "t_d_monitor_state";//目标表
        // 2. 迁移数据
        migrateMonitorData(sourceTable, targetTable);
        return "数据迁移完成！";
    }

    @Override
    public String migrateMonitorInfoData() {
        // 1. 定义需要迁移的表和目标表
        String sourceTable = "device"; // 源表
        String targetTable = "t_b_monitor_info";//目标表
        // 2. 迁移数据
        migrateMonitorInfoData(sourceTable, targetTable);
        return "数据迁移完成！";
    }


    @Override
    public String migrateMonitorHourData() {
        // 1. 查询源表数据
        List<Map<String, Object>> dataList = querySourceData();

        // 3. 将新数据插入到目标表
        insertData("t_d_monitor_hdata", dataList);

        return "数据迁移完成！";
    }

    /**
     * 迁移t_b_site_info表的数据
     *
     * @param sourceTable 源表名
     * @param targetTable 目标表名
     */
    private void migrateTableData(String sourceTable, String targetTable) {
        // 1. 从源表查询数据
        String selectSql = "SELECT * FROM " + sourceTable;

        List<Map<String, Object>> dataList = jdbcTemplateA.queryForList(selectSql);

        // 2. 过滤掉已经存在的数据
        List<Map<String, Object>> newDataList = filterExistingData(targetTable, dataList);

        // 3. 将新数据插入到目标表
        if (!newDataList.isEmpty()) {
            for (Map<String, Object> row : newDataList) {
                try {
                    // 构建插入 SQL 和参数
                    Object[] params = buildInsertParams(targetTable, row);
                    if (params != null) {
                        // 执行插入
                        jdbcTemplateB.update(params[0].toString(), (Object[]) params[1]);
                    }
                } catch (Exception e) {
                    // 记录错误并继续迁移
                    System.err.println("Error migrating row: " + row + ", Error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 迁移 t_d_site_state 表的数据
     *
     * @param sourceTable 源表名
     * @param targetTable 目标表名
     */
    private void migrateStateData(String sourceTable, String targetTable) {
        // 1. 从源表查询数据
        String selectSql = "SELECT station_name, licens_number, created_time, last_updated_time, status, from_resource FROM " + sourceTable;
        List<Map<String, Object>> sourceDataList = jdbcTemplateA.queryForList(selectSql);

        // 2. 遍历源表数据
        for (Map<String, Object> sourceRow : sourceDataList) {
            // 获取唯一标识（假设 station_name 是唯一标识）
            String stationName = (String) sourceRow.get("station_name");

            // 3. 查询目标表中是否存在对应的记录
            String selectTargetSql = "SELECT SGZT FROM " + targetTable + " WHERE GDMC = ?";
            List<Map<String, Object>> targetDataList = jdbcTemplateB.queryForList(selectTargetSql, stationName);

            // 4. 如果目标表中存在对应的记录
            if (!targetDataList.isEmpty()) {
                Map<String, Object> targetRow = targetDataList.get(0);
                String targetStatus = (String) targetRow.get("SGZT");

                // 5. 比较源表和目标表的 status 字段
                String sourceStatus = mapStatusValue(sourceRow.get("status"));
                if (!sourceStatus.equals(targetStatus)) {
                    // 如果 status 不一致，则更新目标表中的 SGZT 字段
                    updateTargetStatus(targetTable, stationName, sourceStatus);
                }
            } else {
                // 6. 如果目标表中不存在对应的记录，则插入新数据
                insertNewData(targetTable, sourceRow);
            }
        }
    }
    /**
     * 构建插入 SQL 和参数
     *
     * @param targetTable 目标表名
     * @param row        源数据行
     * @return 包含 SQL 和参数的数组，第一个元素是 SQL，第二个元素是参数数组
     */
    private Object[] buildInsertStateParams(String targetTable, Map<String, Object> row) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();

        addColumn(columns, values, paramsList, "GDMC", row.get("station_name"), "");
        addColumn(columns, values, paramsList, "SGXKZBH", row.get("licens_number"), null);
        addColumn(columns, values, paramsList, "XGRQSJ", row.get("created_time"), null);
        addColumn(columns, values, paramsList, "REPORT_TIME", row.get("last_updated_time"), null);

        String statusValue = mapStatusValue(row.get("status"));
        addColumn(columns, values, paramsList, "SGZT", statusValue, "长期停工");

        addColumn(columns, values, paramsList, "EEMP_FLAG", row.get("fromResource"), "");

        String sql = "INSERT INTO " + targetTable + " (" + columns + ") VALUES (" + values + ")";
        return new Object[]{sql, paramsList.toArray()};
    }

    /**
     * 添加列和值到 SQL 中
     *
     * @param columns     SQL 列名
     * @param values      SQL 值
     * @param paramsList  参数列表
     * @param columnName  列名
     * @param value       列值
     * @param defaultValue 默认值
     */
    private void addColumn(StringBuilder columns, StringBuilder values, List<Object> paramsList, String columnName, Object value, Object defaultValue) {
        if (value != null || defaultValue != null) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(columnName);
            values.append("?");
            paramsList.add(value != null ? value : defaultValue);
        }
    }

    /**
     * 迁移 t_d_monitor_state 表的数据
     *
     * @param sourceTable 源表名
     * @param targetTable 目标表名
     */
    private void migrateMonitorData(String sourceTable, String targetTable) {
        // 1. 从源表查询数据
        String selectSql = "SELECT d.name, s.licens_number, d.created_time, d.last_updated_time, d.fromResource, d.status, d.sn " +
                "FROM " + sourceTable + " d " +
                "LEFT JOIN station s ON d.name LIKE CONCAT(s.station_name, '%')";
        List<Map<String, Object>> sourceDataList = jdbcTemplateA.queryForList(selectSql);
        //打印查询到的sourceDataList
        // 2. 遍历源表数据
        for (Map<String, Object> sourceRow : sourceDataList) {
            // 获取唯一标识（假设 name 是唯一标识）
            String nameValue = (String) sourceRow.get("name");

            // 3. 查询目标表中是否存在对应的记录
            String selectTargetSql = "SELECT YXZT FROM " + targetTable + " WHERE JCDMC = ?";
            List<Map<String, Object>> targetDataList = jdbcTemplateB.queryForList(selectTargetSql, nameValue);

            // 4. 如果目标表中存在对应的记录
            if (!targetDataList.isEmpty()) {
                Map<String, Object> targetRow = targetDataList.get(0);
                String targetStatus = (String) targetRow.get("YXZT");

                // 5. 比较源表和目标表的 status 字段
                String sourceStatus = mapStatusToYXZT(sourceRow.get("status"));
                if (!sourceStatus.equals(targetStatus)) {
                    // 如果 status 不一致，则更新目标表中的 YXZT 字段
                    updateTargetMonitorStatus(targetTable, nameValue, sourceStatus);
                }
            } else {
                // 6. 如果目标表中不存在对应的记录，则插入新数据
                InsertNewParams(targetTable, sourceRow);
            }
        }
    }

    private void InsertNewParams(String targetTable, Map<String, Object> sourceRow) {

        Object[] params = buildInsertMonitorParams(targetTable, sourceRow);
        if (params != null) {
            try {
                jdbcTemplateB.update(params[0].toString(), (Object[]) params[1]);
                System.out.println("插入成功：设备 " + sourceRow.get("name") + " 的数据已插入");
            } catch (Exception e) {
                System.err.println("插入失败：设备 " + sourceRow.get("name") + "，错误信息：" + e.getMessage());
            }
        }
    }

    /**
     * 迁移 t_b_monitor_info表的数据
     *
     * @param sourceTable 源表名
     * @param targetTable 目标表名
     */
    private void migrateMonitorInfoData(String sourceTable, String targetTable) {

        String selectSql = "SELECT d.name, s.licens_number, d.created_time, d.last_updated_time, d.fromResource,   d.status, d.sn,d.latitude,d.longitude " +
                "FROM " + sourceTable + " d " +
                "LEFT JOIN station s   ON d.name LIKE CONCAT(s.station_name, '%') AND LENGTH(s.station_name) > 5\n" +
                "  ";

        List<Map<String, Object>> dataList = jdbcTemplateA.queryForList(selectSql);

        // 2. 过滤掉已经存在的数据
        List<Map<String, Object>> newDataList = filterExistingMonitorInfoData(targetTable, dataList);

        // 3. 将新数据插入到目标表
        if (!dataList.isEmpty()) {
            for (Map<String, Object> row : newDataList) {
                try {
                    // 检查 SGXKZBH 是否为空
                    String licensNumberValue = (String) row.get("licens_number");
                    if (licensNumberValue == null || licensNumberValue.isEmpty()) {
                        continue;
                    }

                    // 构建插入 SQL 和参数
                    Object[] params = buildInsertMonitorInfoParams(targetTable, row);
                    if (params != null) {
                        // 执行插入
                        jdbcTemplateB.update(params[0].toString(), (Object[]) params[1]);
                    }
                } catch (Exception e) {
                    // 记录错误并继续迁移
                    System.err.println("Error migrating row: " + row + ", Error: " + e.getMessage());
                }
            }
        }
    }


    /**
     * 查询需要迁移的数据
     *
     * @return 返回查询到的数据列表
     */
    private List<Map<String, Object>> querySourceData() {
        // 定义SQL查询语句（使用参数化查询）
        String selectSql = "SELECT " +
                "d.sn, " +
                "d.fromResource, " +
                "s.licens_number, " +
                "h.average_pm2_5, " +
                "h.average_pm10, " +
                "h.average_so2, " +
                "h.wd, " +
                "h.sd, " +
                "h.created_at, " +
                "h.updated_at, " +
                "h.dept_id " +
                "FROM device d " +
                "LEFT JOIN station s ON d.station_id =s.id " +
                "LEFT JOIN hourly_average_air_data h ON h.device_id =d.id " +
                "WHERE h.created_at >= ? " +
                "AND h.created_at < ?";

        try {
            // 计算时间范围（前一个完整小时）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentHourStart = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime previousHourStart = currentHourStart.minusHours(1);

            // 格式化时间参数
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String startTime = previousHourStart.format(formatter);
            String endTime = currentHourStart.format(formatter);

            // 执行查询
            return jdbcTemplateA.queryForList(selectSql, startTime, endTime);

        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }
    /**
     * 将数据插入到目标表
     *
     * @param targetTable 目标表名
     * @param dataList    需要插入的数据列表
     */
    private void insertData(String targetTable, List<Map<String, Object>> dataList) {
        if (dataList.isEmpty()) {
            return;
        }
        // 定义需要过滤的搅拌站dept_id列表
        Set<String> excludedDeptIds = new HashSet<>(Arrays.asList(
                "18011","18012","18013","18069","18070","18071","18072","18074",
                "18075","18076","18077","18078","18079","18080","18081","18082",
                "18083","18084","18085","18086","18087","18088","18111","18112"
        ));
        //对dataList根据deptId进行过滤
        dataList.removeIf(row -> excludedDeptIds.contains(row.get("dept_id")));

        // 定义插入 SQL 模板
        String insertSql = "INSERT INTO " + targetTable + " (JCZDBH, XGRQSJ, REPORT_TIME, EEMP_FLAG, SGXKZBH, PM2_5, PM10,SO2, WD, SD) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 遍历数据列表，逐条插入
        for (Map<String, Object> row : dataList) {
            try {
                // 检查 licens_number 是否为空
                String licensNumber = (String) row.get("licens_number");
                if (licensNumber == null || licensNumber.isEmpty()) {
                    continue;  // 跳过该条记录
                }
                jdbcTemplateB.update(insertSql,
                        row.get("sn"),
                        row.get("created_at"),
                        row.get("updated_at"),
                        row.get("fromResource"),
                        row.get("licens_number"),
                        row.get("average_pm2_5"),
                        row.get("average_pm10"),
                        row.get("average_so2"),
                        row.get("wd"),
                        row.get("sd"));
            } catch (Exception e) {
                // 记录错误日志
                System.err.println("Error inserting row: " + row + ", Error: " + e.getMessage());
            }
        }
    }

    /**
     * 过滤掉t_b_site_info表中已经存在的数据
     *
     * @param targetTable 目标表名
     * @param dataList    源数据列表
     * @return 需要插入的新数据列表
     */
    private List<Map<String, Object>> filterExistingData(String targetTable, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> newDataList = new ArrayList<>();

        // 查询目标表中已经存在的唯一字段值
        String checkSql = "SELECT GDMC, SGXKZBH FROM " + targetTable;
        List<Map<String, Object>> existingValues = jdbcTemplateB.queryForList(checkSql);

        // 过滤掉已经存在的数据
        for (Map<String, Object> row : dataList) {
            String gdmcValue = (String) row.get("station_name");
            String sgxkzbhValue = (String) row.get("licens_number");

            boolean exists = false;
            for (Map<String, Object> existingRow : existingValues) {
                if (gdmcValue.equals(existingRow.get("GDMC")) && sgxkzbhValue.equals(existingRow.get("SGXKZBH"))) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                newDataList.add(row);
            }
        }

        return newDataList;
    }


    /**
     * 过滤掉t_d_monitor_state表中已经存在的数据
     *
     * @param targetTable 目标表名
     * @param dataList    源数据列表
     * @return 需要插入的新数据列表
     */
    private List<Map<String, Object>> filterExistingMonitorData(String targetTable, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> newDataList = new ArrayList<>();

        // 查询目标表中已经存在的唯一字段值
        String checkSql = "SELECT JCDMC FROM " + targetTable;
        Set<String> existingValues = new HashSet<>(jdbcTemplateB.queryForList(checkSql, String.class));

        // 过滤掉已经存在的数据
        for (Map<String, Object> row : dataList) {
            String uniqueValue = (String) row.get("name");
            if (!existingValues.contains(uniqueValue)) {
                newDataList.add(row);
            }
        }

        return newDataList;
    }

    /**
     * 过滤掉t_b_monitor_info表中已经存在的数据
     *
     * @param targetTable 目标表名
     * @param dataList    源数据列表
     * @return 需要插入的新数据列表
     */
    private List<Map<String, Object>> filterExistingMonitorInfoData(String targetTable, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> newDataList = new ArrayList<>();

        // 查询目标表中已经存在的唯一字段值
        String checkSql = "SELECT JCZDMC FROM " + targetTable;
        Set<String> existingValues = new HashSet<>(jdbcTemplateB.queryForList(checkSql, String.class));

        // 过滤掉已经存在的数据
        for (Map<String, Object> row : dataList) {
            String uniqueValue = (String) row.get("name");
            if (!existingValues.contains(uniqueValue)) {
                newDataList.add(row);
            }
        }

        return newDataList;
    }
    /**
     * 构建t_b_site_info插入 SQL 和参数
     *
     * @param targetTable 目标表名
     * @param row         源数据行
     * @return 包含 SQL 和参数的数组，第一个元素是 SQL，第二个元素是参数数组
     */
    private Object[] buildInsertParams(String targetTable, Map<String, Object> row) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();

        // 处理 station_name 映射到 XMMC 和 GDMC
        String stationNameValue = (String) row.get("station_name");
        if (stationNameValue == null) {
            stationNameValue = ""; // 如果 station_name 为空，使用空字符串作为默认值
        }

        // 添加 XMMC 字段
        columns.append("XMMC");
        values.append("?");
        paramsList.add(stationNameValue);

        // 添加 GDMC 字段
        columns.append(", GDMC");
        values.append(", ?");
        paramsList.add(stationNameValue);

        // 处理其他字段
        for (Map.Entry<String, String> entry : FIELD_MAPPING.entrySet()) {
            String sourceField = entry.getKey();
            String targetField = entry.getValue();

            // 跳过已经处理的字段
            if (targetField.equals("XMMC") || targetField.equals("GDMC")) {
                continue;
            }

            Object value = row.get(sourceField);
            if (value == null) {
                // 如果目标字段不允许为空，跳过该字段
                if (isFieldRequired(targetTable, targetField)) {
                    continue;
                }
                value = ""; // 如果值为空，使用空字符串作为默认值
            }

            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(targetField);
            values.append("?");
            paramsList.add(value);
        }
        // 确保 ZDMJ 字段被包含
        if (!columns.toString().contains("ZDMJ")) {
            columns.append(", ZDMJ");
            values.append(", ?");
            paramsList.add(0.0);
        }
        if (columns.length() == 0) {
            return null; // 没有可插入的字段
        }

        String sql = "INSERT INTO " + targetTable + " (" + columns + ") VALUES (" + values + ")";
        return new Object[]{sql, paramsList.toArray()};
    }

    /**
     * 更新目标表中的 SGZT 字段
     *
     * @param targetTable  目标表名
     * @param stationName  站点名称（唯一标识）
     * @param sourceStatus 源表中的状态值
     */
    private void updateTargetStatus(String targetTable, String stationName, String sourceStatus) {
        String updateSql = "UPDATE " + targetTable + " SET SGZT = ? WHERE GDMC = ?";
        try {
            jdbcTemplateB.update(updateSql, sourceStatus, stationName);
//            System.out.println("更新成功：站点 " + stationName + " 的状态已更新为 " + sourceStatus);
        } catch (Exception e) {
//            System.err.println("更新失败：站点 " + stationName + "，错误信息：" + e.getMessage());
        }
    }

    /**
     * 更新目标表中的 SGZT 字段
     *
     * @param targetTable  目标表名
     * @param stationName  站点名称（唯一标识）
     * @param sourceStatus 源表中的状态值
     */
    private void updateTargetMonitorStatus(String targetTable, String stationName, String sourceStatus) {
        String updateSql = "UPDATE " + targetTable + " SET YXZT = ? WHERE JCDMC = ?";
        try {
            jdbcTemplateB.update(updateSql, sourceStatus, stationName);
//            System.out.println("更新成功：站点 " + stationName + " 的状态已更新为 " + sourceStatus);
        } catch (Exception e) {
//            System.err.println("更新失败：站点 " + stationName + "，错误信息：" + e.getMessage());
        }
    }
    /**
     * 插入新数据到目标表
     *
     * @param targetTable 目标表名
     * @param sourceRow   源表数据行
     */
    private void insertNewData(String targetTable, Map<String, Object> sourceRow) {
        Object[] params = buildInsertStateParams(targetTable, sourceRow);
        if (params != null) {
            try {
                jdbcTemplateB.update(params[0].toString(), (Object[]) params[1]);
                System.out.println("插入成功：站点 " + sourceRow.get("station_name") + " 的数据已插入");
            } catch (Exception e) {
                System.err.println("插入失败：站点 " + sourceRow.get("station_name") + "，错误信息：" + e.getMessage());
            }
        }
    }


    /**
     * 映射源表中的 status 字段值到目标表的 SGZT 字段值
     *
     * @param statusObj 源表中的 status 字段值
     * @return 映射后的状态值
     */
    private String mapStatusValue(Object statusObj) {
        if (statusObj == null) {
            return "长期停工";
        }

        String statusValue = statusObj instanceof Integer ? String.valueOf(statusObj) : (String) statusObj;

        switch (statusValue) {
            case "1": return "施工";
            case "2": return "停工";
            case "3":
            case "4": return "竣工";
            default: return "长期停工";
        }
    }


    /**
     * 将 status 映射为 SGZT 字段的值
     *
     * @param status 源数据中的 status 值
     * @return 映射后的 YXZT 值
     */
    private String mapStatusToYXZT(Object status) {
        if (status == null) {
            return "在用"; // 默认值
        }

        if (status instanceof Integer) {
            int statusInt = (Integer) status;
            switch (statusInt) {
                case 1:
                case 4:
                case 7:
                    return "在用";
                case 2:
                    return "暂停";
                case 3:
                    return "拆除";
                default:
                    return "在用"; // 默认值
            }
        } else if (status instanceof String) {
            String statusStr = (String) status;
            switch (statusStr) {
                case "1":
                case "4":
                case "7":
                    return "在用";
                case "2":
                    return "暂停";
                case "3":
                    return "拆除";
                default:
                    return "在用"; // 默认值
            }
        }

        return "在用"; // 默认值
    }


    /**
     * 构建 t_b_monitor_info 插入 SQL 和参数
     *
     * @param targetTable 目标表名
     * @param row         源数据行
     * @return 包含 SQL 和参数的数组，第一个元素是 SQL，第二个元素是参数数组
     */
    private Object[] buildInsertMonitorInfoParams(String targetTable, Map<String, Object> row) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();
        Set<String> addedColumns = new HashSet<>(); // 用于跟踪已添加的字段

        // 1. 处理 JCZDMC（name）
        String nameValue = (String) row.get("name");
        if (nameValue == null) {
            nameValue = ""; // 如果 name 为空，使用空字符串作为默认值
        }
        columns.append("JCZDMC");
        values.append("?");
        paramsList.add(nameValue);
        addedColumns.add("JCZDMC"); // 标记 JCZDMC 已添加

        // 处理其他字段
        for (Map.Entry<String, String> entry : FIELD_MAPPING_DEVICE.entrySet()) {
            String sourceField = entry.getKey();
            String targetField = entry.getValue();

            // 跳过已经处理的字段
            if (addedColumns.contains(targetField)) {
                continue;
            }
            //如果字段名为JCDMC则跳过这个字段插入其他字段
            if (targetField.equals("JCDMC")||targetField.equals("YXZT") ) {
                continue;
            }
            Object value = row.get(sourceField);
            if (value == null) {
                // 如果目标字段不允许为空，跳过该字段
                if (isFieldRequired(targetTable, targetField)) {
                    continue;
                }
                value = ""; // 如果值为空，使用空字符串作为默认值
            }

            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(targetField);
            values.append("?");
            paramsList.add(value);
            addedColumns.add(targetField); // 标记字段已添加
        }

        // 2. 处理 SGXKZBH（licens_number）
        String licensNumberValue = (String) row.get("licens_number");
        if (licensNumberValue == null) {
            licensNumberValue = ""; // 提供一个默认值
        }
        if (!addedColumns.contains("SGXKZBH")) {
            columns.append(", SGXKZBH");
            values.append(", ?");
            paramsList.add(licensNumberValue);
            addedColumns.add("SGXKZBH");
        }

        // 3. 处理 XGRQSJ（created_time）
        Object createdTimeValue = row.get("created_time");
        if (createdTimeValue != null && !addedColumns.contains("XGRQSJ")) {
            columns.append(", XGRQSJ");
            values.append(", ?");
            paramsList.add(createdTimeValue);
            addedColumns.add("XGRQSJ");
        }

        // 4. 将 GXSJ 的值设置为跟 XGRQSJ 一样
        if (createdTimeValue != null && !addedColumns.contains("GXSJ")) {
            columns.append(", GXSJ");
            values.append(", ?");
            paramsList.add(createdTimeValue);
            addedColumns.add("GXSJ");
        }

        // 5. 处理 REPORT_TIME（last_updated_time）
        Object lastUpdatedTimeValue = row.get("last_updated_time");
        if (lastUpdatedTimeValue != null && !addedColumns.contains("REPORT_TIME")) {
            columns.append(", REPORT_TIME");
            values.append(", ?");
            paramsList.add(lastUpdatedTimeValue);
            addedColumns.add("REPORT_TIME");
        }

        // 6. 处理 EEMP_FLAG（fromResource）
        Object fromResourceObj = row.get("fromResource");
        if (fromResourceObj != null && !addedColumns.contains("EEMP_FLAG")) {
            String fromResourceValue;
            if (fromResourceObj instanceof Integer) {
                // 如果 fromResource 是 Integer 类型，转换为 String
                fromResourceValue = String.valueOf((Integer) fromResourceObj);
            } else if (fromResourceObj instanceof String) {
                // 如果 fromResource 是 String 类型，直接使用
                fromResourceValue = (String) fromResourceObj;
            } else {
                // 其他类型，设置为空字符串
                fromResourceValue = "";
            }
            columns.append(", EEMP_FLAG");
            values.append(", ?");
            paramsList.add(fromResourceValue);
            addedColumns.add("EEMP_FLAG");
        }

        // 7. 处理 JCZDBH（sn）
        String snValue = (String) row.get("sn");
        if (snValue != null && !addedColumns.contains("JCZDBH")) {
            columns.append(", JCZDBH");
            values.append(", ?");
            paramsList.add(snValue);
            addedColumns.add("JCZDBH");
        } else if (!addedColumns.contains("JCZDBH")) {
            // 如果 sn 为空，提供一个默认值
            columns.append(", JCZDBH");
            values.append(", ?");
            paramsList.add("bh"); // 提供一个默认值
            addedColumns.add("JCZDBH");
        }

        // 8. 处理 ZXJD（longitude）
        Double zxjdValue = (Double) row.get("longitude");
        if (zxjdValue != null && !addedColumns.contains("ZXJD")) {
            columns.append(", ZXJD");
            values.append(", ?");
            paramsList.add(zxjdValue);
            addedColumns.add("ZXJD");
        } else if (!addedColumns.contains("ZXJD")) {
            columns.append(", ZXJD");
            values.append(", ?");
            paramsList.add(0.0); // 提供一个默认值
            addedColumns.add("ZXJD");
        }

        // 9. 处理 ZXWD（latitude）
        Double zxwdValue = (Double) row.get("latitude");
        if (zxwdValue != null && !addedColumns.contains("ZXWD")) {
            columns.append(", ZXWD");
            values.append(", ?");
            paramsList.add(zxwdValue);
            addedColumns.add("ZXWD");
        } else if (!addedColumns.contains("ZXWD")) {
            columns.append(", ZXWD");
            values.append(", ?");
            paramsList.add(0.0); // 提供一个默认值
            addedColumns.add("ZXWD");
        }

        String sql = "INSERT INTO " + targetTable + " (" + columns + ") VALUES (" + values + ")";
        return new Object[]{sql, paramsList.toArray()};
    }

    /**
     * 构建 t_d_monitor_state 插入 SQL 和参数
     *
     * @param targetTable 目标表名
     * @param row        源数据行
     * @return 包含 SQL 和参数的数组，第一个元素是 SQL，第二个元素是参数数组
     */
    private Object[] buildInsertMonitorParams(String targetTable, Map<String, Object> row) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> paramsList = new ArrayList<>();
        Set<String> addedColumns = new HashSet<>(); // 用于跟踪已添加的字段

        // 1. 处理 JCDMC（name）
        String nameValue = (String) row.get("name");
        if (nameValue == null) {
            nameValue = ""; // 如果 name 为空，使用空字符串作为默认值
        }
        columns.append("JCDMC"); // 目标表字段名是 JCDMC
        values.append("?");
        paramsList.add(nameValue);
        addedColumns.add("JCDMC");

        // 2. 处理 YXZT（status）
        String statusValue = mapStatusToYXZT(row.get("status"));
        columns.append(", YXZT");
        values.append(", ?");
        paramsList.add(statusValue);
        addedColumns.add("YXZT");

        // 3. 处理其他字段
        for (Map.Entry<String, String> entry : FIELD_MAPPING_DEVICE.entrySet()) {
            String sourceField = entry.getKey();
            String targetField = entry.getValue();

            // 跳过已经处理的字段
            if (addedColumns.contains(targetField)) {
                continue;
            }

            Object value = row.get(sourceField);
            if (value == null) {
                // 如果目标字段不允许为空，跳过该字段
                if (isFieldRequired(targetTable, targetField)) {
                    continue;
                }
                value = ""; // 如果值为空，使用空字符串作为默认值
            }

            // 截断过长的数据
            if (targetField.equals("YXZT") && value instanceof String) {
                String stringValue = (String) value;
                int maxLength = 10; // 假设最大长度为10，根据实际情况调整
                if (stringValue.length() > maxLength) {
                    value = stringValue.substring(0, maxLength);
                }
            }

            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(targetField);
            values.append("?");
            paramsList.add(value);
            addedColumns.add(targetField);
        }

        // 4. 处理 SGXKZBH（licens_number）
        String licensNumberValue = (String) row.get("licens_number");
        if (licensNumberValue == null) {
            licensNumberValue = ""; // 提供一个默认值
        }
        if (!addedColumns.contains("SGXKZBH")) {
            columns.append(", SGXKZBH");
            values.append(", ?");
            paramsList.add(licensNumberValue);
            addedColumns.add("SGXKZBH");
        }

        // 5. 处理 XGRQSJ（created_time）
        Object createdTimeValue = row.get("created_time");
        if (createdTimeValue != null && !addedColumns.contains("XGRQSJ")) {
            columns.append(", XGRQSJ");
            values.append(", ?");
            paramsList.add(createdTimeValue);
            addedColumns.add("XGRQSJ");
        }


        String sql = "INSERT INTO " + targetTable + " (" + columns + ") VALUES (" + values + ")";
        System.out.println("生成的插入SQL: " + sql);
        System.out.println("参数值: " + paramsList);
        return new Object[]{sql, paramsList.toArray()};
    }
    /**
     * 检查目标字段是否允许为空
     */
    private boolean isFieldRequired(String tableName, String fieldName) {

        return true; // 所有字段都不允许为空
    }
}