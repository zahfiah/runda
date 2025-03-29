<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="设备" prop="deviceId" label-width="45px">
        <el-select v-model="queryParams.deviceId" placeholder="请选择设备" clearable @change="handleDeviceChange" filterable>
          <el-option v-for="device in deviceOptions" :key="device.deviceId" :label="device.deviceName"
            :value="device.deviceId" />
        </el-select>
      </el-form-item>

      <!-- 小时类型的选择器 -->
      <el-form-item label="选择日期" prop="selectedDate">
        <el-date-picker v-model="queryParams.selectedDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd"
          format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item label="开始时间" prop="startHour">
        <el-time-select v-model="queryParams.startHour" :picker-options="{
          start: '00:00',
          step: '01:00',
          end: '23:00',
        }" placeholder="选择开始时间" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endHour">
        <el-time-select v-model="queryParams.endHour" :picker-options="{
          start: '00:00',
          step: '01:00',
          end: '23:59',
        }" placeholder="选择结束时间" :min-time="queryParams.startHour" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['runda:data:add']">新增</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:data:export']">导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="站点名称" align="center" prop="stationName" />
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column label="日期" align="center" prop="dateTimeStr" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dateTimeStr) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="so2浓度(μg/m³)" align="center" prop="averageSo2">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>so2浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="no2浓度(μg/m³)" align="center" prop="averageNo2">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>no2浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="o3浓度(μg/m³)" align="center" prop="averageO3">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>o3浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="小时pm25浓度(μg/m³)" align="center" prop="averagePm2_5">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>颗粒物
              （粒径小于等于2.5μm）
              1小时平均</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="日pm2.5浓度(μg/m³)" align="center" prop="averagePm2_5_24h">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>颗粒物
              （粒径小于等于2.5μm）
              24小时滑动平均</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="小时pm10浓度(μg/m³)" align="center" prop="averagePm10">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>颗粒物
              （粒径小于等于10μm）
              1小时平均</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="日pm10浓度(μg/m³)" align="center" prop="averagePm10_24h">
        <template slot="header">
          <div style="display: flex; flex-direction: column; align-items: center">
            <span>颗粒物
              （粒径小于等于10μm）
              24小时滑动平均</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="空气质量指数(AQI)" align="center" prop="averageAqi" />
      <el-table-column label="级别" align="center" prop="level" />
      <el-table-column label="质量" align="center" prop="quality" />
      <el-table-column label="颜色" align="center" prop="color" />
      <el-table-column label="主要污染物" align="center" prop="primaryPollutant" width="150px" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="handlePagination" />

    <!-- 添加或修改监测小时报表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listData, getData, delData, addData, updateData } from "@/api/runda/data";
import request from "@/utils/request";

export default {
  name: "Data",
  data() {
    return {
      cachedAllData: [], // 新增：缓存全量数据
      // 新增设备选项
      deviceOptions: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 监测小时报表表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: null,
        selectedDate: new Date().toISOString().split('T')[0],
        startHour: null,
        endHour: null,
      },
      // 表单数据
      form: {
        id: null,
        deviceId: null,
        queryTime: null,
        averageAqi: null,
        averageSo2: null,
        averageNo2: null,
        averageO3: null,
        averagePm25: null,
        averagePm10: null,
        aqiLevel: null,
        aqiQuality: null,
        aqiColor: null,
        primaryPollutant: null,
        createdAt: null,
        updatedAt: null
      },
      // 表单校验
      rules: {
        deviceId: [
          { required: true, message: '请选择设备', trigger: 'blur' }
        ],
        selectedDate: [
          { required: true, message: '请选择日期', trigger: 'blur' }
        ],
        startHour: [
          { required: true, message: '请选择开始时间', trigger: 'blur' }
        ],
        endHour: [
          { required: true, message: '请选择结束时间', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    this.getDeviceList();
    // 新增：页面加载时获取当前时间的前一个整点小时数据
    this.fetchLatestHourData();
  },
  methods: {
    handlePagination(pagination) {
      if (!this.queryParams.deviceId) {
        // 无设备选择时使用前端分页
        this.queryParams.pageNum = pagination.page;
        this.queryParams.pageSize = pagination.limit;
        this.dataList = this.getPaginatedData(this.cachedAllData);
      }
    },
    // 新增设备相关方法
    async getDeviceList() {
      try {
        // 请求新的接口获取设备列表
        const response = await request({
          url: "/runda/query212/listDeviceIdAndName",
          method: "get",
        });

        // 打印完整的响应对象，以便调试
        console.log("接口响应:", response);

        if (Array.isArray(response) && response.length > 0) {
          // 处理响应数据
          this.deviceOptions = response.map((row) => ({
            deviceId: row.id,
            deviceName: row.name,
          }));

          console.log("找到的所有设备：", this.deviceOptions);

          if (this.deviceOptions.length > 0) {
            this.$message.success(
              `成功获取到 ${this.deviceOptions.length} 个设备`
            );
          } else {
            this.$message.warning("未找到任何可用设备");
          }
        } else {
          this.$message.warning("未找到任何可用设备");
        }
      } catch (error) {
        console.error("获取设备列表失败：", error);
        this.$message.error("获取设备列表失败");
      }
    },

    handleDeviceChange(deviceId) {
      // 仅更新选中的设备ID
      this.queryParams.deviceId = deviceId;
      console.log("已选择设备ID：", deviceId);
    },
    /** 查询监测小时报表列表 */
    generateHourRange(start, end) {
      if (!start) return [];
      const startHour = parseInt(start.split(':')[0]);
      const endHour = end ? parseInt(end.split(':')[0]) : startHour;
      return Array.from({ length: endHour - startHour + 1 }, (_, i) =>
        `${String(startHour + i).padStart(2, '0')}:00`
      );
    },

    validateDateParams() {
      if (!this.queryParams.deviceId || !this.queryParams.startDate) {
        this.loading = false;
        return false;
      }
      return true;
    },

    generateDateRange(startDate, endDate) {
      const dateRange = [];
      const currentDate = new Date(startDate);
      const stopDate = new Date(endDate || startDate);

      while (currentDate <= stopDate) {
        dateRange.push(currentDate.toISOString().split('T')[0]);
        currentDate.setDate(currentDate.getDate() + 1);
      }

      return dateRange;
    },


    async getList() {
      this.loading = true; // 显示加载圈

      if (!this.validateHourParams()) {
        this.loading = false;
        return;
      }

      const hours = this.generateHourRange(
        this.queryParams.startHour,
        this.queryParams.endHour
      );

      try {
        const responses = await Promise.all(
          hours.map(hour => this.fetchHourData(hour))
        );
        this.processData(responses);
      } catch (error) {
        this.handleDataError(error);
      } finally {
        this.loading = false; // 确保加载圈关闭
      }
    },

    validateHourParams() {
      if (!this.queryParams.selectedDate) {
        this.$message.warning("请先选择日期");
        return false;
      }
      if (!this.queryParams.startHour || !this.queryParams.endHour) {
        this.$message.warning("请同时选择开始时间和结束时间");
        return false;
      }
      return true;
    },

    async fetchHourDataWithoutDeviceId() {
      try {
        const response = await request({
          url: "/runda/air/list-hour-data",
          params: {
            beginTime: `${this.queryParams.selectedDate} ${this.queryParams.startHour}`,
            endTime: `${this.queryParams.selectedDate} ${this.queryParams.endHour}`
          }
        });

        return {
          code: response.code === 200 ? 0 : -1,
          rows: (response.data || []).map(item => ({
            stationName: item.stationName,
            deviceName: item.deviceName,
            dateTimeStr: item.createdAt,
            averageSo2: item.so2,
            averageNo2: item.no2,
            averageO3: item.o3,
            averagePm2_5: item.pm25,
            averagePm2_5_24h: item.pm25_24h,
            averagePm10: item.pm10,
            averagePm10_24h: item.pm10_24h,
            averageAqi: item.aqi,
            level: item.aqiLevel,
            quality: item.aqiQuality,
            color: item.aqiColor,
            primaryPollutant: item.primaryPollutant
          }))
        };
      } catch (error) {
        console.error('查询失败:', error);
        return { code: -1, rows: [] };
      }
    },

    async fetchHourDataWithoutDeviceId(hour) {
      try {
        const response = await request({
          url: "/runda/air/list-hour-data",
          params: {
            beginTime: `${this.queryParams.selectedDate} ${this.queryParams.startHour}`,
            endTime: `${this.queryParams.selectedDate} ${this.queryParams.endHour}`
          }
        });
        // 字段转换逻辑
        return {
          code: response.code === 200 ? 0 : -1,
          rows: (response.data || []).map(item => ({
            ...item,
            // 字段映射
            averagePm2_5: item.averagePm25,
            averagePm10: item.averagePm10,
            averagePm2_5_24h: item.averagePm25_24,
            averagePm10_24h: item.averagePm10_24,
            level: item.aqiLevel,
            quality: item.aqiQuality,
            color: item.aqiColor,
            // 保持原始字段
            primaryPollutant: item.primaryPollutant,
            dateTimeStr: item.createdAt // 假设使用createdAt作为时间字段
          }))
        };
      } catch (error) {
        console.error(`查询${hour}数据失败:`, error);
        return { code: -1, rows: [] };
      }
    },

    async fetchHourData(hour) {
      if (this.queryParams.deviceId) {
        return await this.fetchHourDataWithDeviceId(hour);
      } else {
        return await this.fetchHourDataWithoutDeviceId(hour);
      }
    },

    async fetchHourDataWithDeviceId(hour) {
      try {
        const response = await request({
          url: "/runda/air/hourly-average-for-specific-time",
          params: {
            dateTime: `${this.queryParams.selectedDate} ${hour}:00`,
            deviceId: this.queryParams.deviceId
          }
        });

        if (response.code === 0 && Array.isArray(response.rows)) {
          return {
            code: 0,
            rows: response.rows.map(item => ({
              stationName: item.stationName,
              deviceName: item.deviceName,
              dateTimeStr: item.dateTimeStr,
              averageSo2: item.averageSo2,
              averageNo2: item.averageNo2,
              averageO3: item.averageO3,
              averagePm2_5: item.averagePm2_5,
              averagePm2_5_24h: item.averagePm2_5_24h,
              averagePm10: item.averagePm10,
              averagePm10_24h: item.averagePm10_24h,
              averageAqi: item.averageAqi,
              level: item.level,
              quality: item.quality,
              color: item.color,
              primaryPollutant: item.primaryPollutant
            }))
          };
        } else {
          console.error(`查询${hour}数据失败:`, response.msg);
          return { code: -1, rows: [] };
        }
      } catch (error) {
        console.error(`查询${hour}数据失败:`, error);
        return { code: -1, rows: [] };
      }
    },

    processData(responses) {
      const allData = responses.reduce((acc, res) => {
        if (res.code === 0) {
          return acc.concat(res.rows || []);
        }
        return acc;
      }, []);

      // 缓存全量数据（新增）
      this.cachedAllData = allData;

      // 分页逻辑（修改）
      this.total = allData.length;
      this.dataList = this.getPaginatedData(allData);
    },
    getPaginatedData(data) {
      return data.slice(
        (this.queryParams.pageNum - 1) * this.queryParams.pageSize,
        this.queryParams.pageNum * this.queryParams.pageSize
      );
    },
    // 处理查询结果响应
    handleResponse(response) {
      if (response && response.code === 0) {
        this.dataList = response.rows || [];
        this.total = response.total || 0;

        if (this.dataList.length === 0) {
          this.$message.warning("未找到符合条件的数据");
        } else {
          // this.$message.success(`查询成功，共找到 ${this.total} 条记录`);
        }
      } else {
        this.$message.error(response.msg || "查询失败");
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        deviceId: null,
        queryTime: null,
        averageAqi: null,
        averageSo2: null,
        averageNo2: null,
        averageO3: null,
        averagePm25: null,
        averagePm10: null,
        aqiLevel: null,
        aqiQuality: null,
        aqiColor: null,
        primaryPollutant: null,
        createdAt: null,
        updatedAt: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        deviceId: null,
        selectedDate: null,
        startHour: null,
        endHour: null
      };
      this.getList();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加监测小时报表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getData(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改监测小时报表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateData(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addData(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除监测小时报表编号为"' + ids + '"的数据项？').then(function () {
        return delData(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/air/export', {
        ...this.queryParams
      }, `data_${new Date().getTime()}.xlsx`)
    },

    // 新增方法：获取当前时间的前一个整点小时数据
    async fetchLatestHourData() {
      try {
        // 计算当前时间的前一个整点小时
        const now = new Date();
        const previousHour = new Date(now.getTime() - 60 * 60 * 1000);
        const formattedDate = previousHour.toISOString().split('T')[0];
        const beginHour = previousHour.getHours().toString().padStart(2, '0') + ":00";
        const endHour = previousHour.getHours().toString().padStart(2, '0') + ":59";

        // 调用接口获取数据
        const response = await request({
          url: "/runda/air/list-hour-data",
          params: {
            beginTime: `${formattedDate} ${beginHour}`,
            endTime: `${formattedDate} ${endHour}`
          }
        });

        // 更新数据列表并进行分页处理
        if (response.code === 200 && Array.isArray(response.data)) {
          this.cachedAllData = response.data.map(item => ({
            ...item,
            averagePm2_5_24h: item.averagePm25_24,
            averagePm10_24h: item.averagePm10_24,
            averagePm2_5: item.averagePm25,
            averagePm10: item.averagePm10,
            level: item.aqiLevel,
            quality: item.aqiQuality,
            color: item.aqiColor,
            dateTimeStr: item.createdAt
          }));

          // 分页逻辑
          this.total = this.cachedAllData.length;
          this.dataList = this.getPaginatedData(this.cachedAllData); // 改为从缓存池获取
          // 确保分页参数正确传递
          console.log('分页参数:', this.queryParams);
          console.log('总数据量:', this.total);
          console.log('当前页数据:', this.dataList);
        } else {
          this.$message.warning("未找到符合条件的数据");
        }
      } catch (error) {
        console.error("获取最新小时数据失败:", error);
        this.$message.error("获取最新小时数据失败");
      } finally {
        this.loading = false; // 确保加载圈关闭
      }
    },

  }
};
</script>
