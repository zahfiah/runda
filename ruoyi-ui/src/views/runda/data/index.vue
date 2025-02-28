<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="85px"
    >
      <el-form-item label="设备" prop="deviceId" label-width="45px">
        <el-select
          v-model="queryParams.deviceId"
          placeholder="请选择设备"
          clearable
          @change="handleDeviceChange"
        >
          <el-option
            v-for="device in deviceOptions"
            :key="device.deviceId"
            :label="device.deviceName"
            :value="device.deviceId"
          />
        </el-select>
      </el-form-item>

      <!-- 删除: <el-form-item label="时间类型" prop="timeType">
        <el-select
          v-model="queryParams.timeType"
          placeholder="请选择时间类型"
          @change="handleTimeTypeChange"
        >
          <el-option label="起止时间（小时）" value="hour" />
        </el-select>
      </el-form-item> -->

      <!-- 小时类型的选择器 -->
      <template v-if="queryParams.timeType === 'hour'">
        <el-form-item label="选择日期" prop="selectedDate">
          <el-date-picker
            v-model="queryParams.selectedDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startHour">
          <el-time-select
            v-model="queryParams.startHour"
            :picker-options="{
              start: '00:00',
              step: '01:00',
              end: '23:00',
            }"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endHour">
          <el-time-select
            v-model="queryParams.endHour"
            :picker-options="{
              start: '00:00',
              step: '01:00',
              end: '23:59',
            }"
            placeholder="选择结束时间"
            :min-time="queryParams.startHour"
          />
        </el-form-item>
      </template>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['runda:data:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['runda:data:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="城市名称" align="center" prop="deptId" /> -->
      <el-table-column label="站点名称" align="center" prop="stationName" />
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column
        label="日期"
        align="center"
        :prop="queryParams.timeType === 'date' ? 'date' : 'dateTimeStr'"
        width="100"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row[queryParams.timeType === 'date' ? 'date' : 'dateTimeStr']) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="so2浓度(μg/m³)" align="center" prop="averageSo2" >
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>so2浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="no2浓度(μg/m³)" align="center" prop="averageNo2" >
      <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>no2浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="o3浓度(μg/m³)" align="center" prop="averageO3" >
      <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>o3浓度</span>
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="小时pm25浓度(μg/m³)" align="center" prop="averagePm2_5" >
      <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>小时pm25</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="小时pm10浓度(μg/m³)" align="center" prop="averagePm10" >
      <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>小时pm10</span>
            <span style="margin-top: 2px">浓度(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="平均每天pm25" align="center" prop="averagePm2_5_24" />
      <el-table-column label="平均每天pm10" align="center" prop="averagePm10_24" /> -->
      <el-table-column label="空气质量指数(AQI)" align="center" prop="averageAqi" />
      <el-table-column label="级别" align="center" prop="level" />
      <el-table-column label="质量" align="center" prop="quality" />
      <el-table-column label="颜色" align="center" prop="color" />
      <el-table-column label="主要污染物" align="center" prop="primaryPollutant" />
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

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
        timeType: "hour", // 默认选择小时类型
        selectedDate: null,
        startHour: null,
        endHour: null,
      },

      // 表单校验
      rules: {
        deviceId: [
          { required: true, message: '请选择设备', trigger: 'blur' }
        ],
        // ],
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
    this.getList();
    this.getDeviceList(); // 新增设备列表获取
  },
  methods: {
    // 新增设备相关方法
    async getDeviceList() {
      try {
        // 从 localStorage 获取设备列表
        const cachedDeviceList = localStorage.getItem("deviceList");

        // 如果缓存中有设备数据，直接使用缓存数据
        if (cachedDeviceList) {
          this.deviceOptions = JSON.parse(cachedDeviceList);
          console.log("从缓存中获取设备列表：", this.deviceOptions);

          if (this.deviceOptions.length > 0) {
          } else {
            this.$message.warning("缓存中没有找到设备");
          }
          return;
        }
        // 创建一个数组存储所有成功的请求结果
        const uniqueDevices = new Map();
        const batchSize = 100;

        // 使用 for 循环分批发送请求
        for (let deviceId = 1; deviceId <= 1000; deviceId += batchSize) {
          const batchPromises = [];

          // 创建这一批的请求
          for (let i = 0; i < batchSize && deviceId + i <= 1000; i++) {
            batchPromises.push(
              request({
                url: "/runda/query212/listByDeviceId",
                method: "get",
                params: { deviceId: deviceId + i },
              }).catch((error) => {
                // 忽略单个请求的错误，返回 null
                return null;
              })
            );
          }

          // 等待这一批请求完成
          const responses = await Promise.all(batchPromises);

          // 处理响应
          responses.forEach((response) => {
            if (
              response &&
              response.code === 0 &&
              response.rows &&
              response.rows.length > 0
            ) {
              const deviceData = response.rows[0];
              if (!uniqueDevices.has(deviceData.deviceId)) {
                uniqueDevices.set(deviceData.deviceId, {
                  deviceId: deviceData.deviceId,
                  deviceName: deviceData.deviceName,
                });
              }
            }
          });
        }

        // 转换为数组并更新设备选项
        this.deviceOptions = Array.from(uniqueDevices.values());
        console.log("找到的所有设备：", this.deviceOptions);

        if (this.deviceOptions.length > 0) {
          localStorage.setItem(
            "deviceList",
            JSON.stringify(this.deviceOptions)
          );

          this.$message.success(
            `成功获取到 ${this.deviceOptions.length} 个设备`
          );
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

    async fetchDailyData(date) {
      try {
        return await request({
          url: "http://localhost:8080/runda/air/daily-hourly-average",
          params: {
            
            date: date,
            deviceId: this.queryParams.deviceId,
          }
        });
      } catch (error) {
        console.error(`查询${date}数据失败:`, error);
        return { code: -1, rows: [] };
      }
    },

    async getList() {
      this.loading = true;

      if (this.queryParams.timeType === 'hour') {
        if (!this.validateHourParams()) return;

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
        }
      }

      this.loading = false;
    },

    validateHourParams() {
      if (this.queryParams.timeType === 'hour' && !this.queryParams.deviceId) {
        if (!this.queryParams.selectedDate || !this.queryParams.startHour) {
          this.loading = false;
          return false;
        }
        return true;
      }
      if (!this.queryParams.deviceId || !this.queryParams.selectedDate || !this.queryParams.startHour) {
        this.loading = false;
        return false;
      }
      return true;
    },

    async fetchHourData(hour) {
      if (this.queryParams.deviceId) {
        try {
          return await request({
            url: "/runda/air/hourly-average-for-specific-time",
            params: {
              deviceId: this.queryParams.deviceId,
              dateTime: `${this.queryParams.selectedDate} ${hour}`,
            }
          });
        } catch (error) {
          console.error(`查询${hour}数据失败:`, error);
          return { code: -1, rows: [] };
        }
      } else {
        try {
          return await request({
            url: "http://localhost:8080/runda/air/average-by-hour",
            params: {
              dateTime: `${this.queryParams.selectedDate} ${hour}`,
            }
          });
        } catch (error) {
          console.error(`查询${hour}数据失败:`, error);
          return { code: -1, rows: [] };
        }
      }
    },

    processData(responses) {
      const allData = responses.reduce((acc, res) => 
        res.code === 0 ? acc.concat(res.rows) : acc, []);
      console.log(responses);
      
      this.total = allData.length;
      this.dataList = allData.slice(
        (this.queryParams.pageNum - 1) * this.queryParams.pageSize,
        this.queryParams.pageNum * this.queryParams.pageSize
      );

      if (!allData.length) this.$message.warning("未找到数据");
    },

    handleDataError(error) {
      console.error("查询失败:", error);
      this.$message.error("查询失败");
      this.loading = false;
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
        timeType: "hour", // 固定为小时
        selectedDate: null,
        startHour: null,
        endHour: null
      };
      this.getList();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
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
      this.$modal.confirm('是否确认删除监测小时报表编号为"' + ids + '"的数据项？').then(function() {
        return delData(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/data/export', {
        ...this.queryParams
      }, `data_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>



