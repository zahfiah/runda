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
      <el-form-item label="设备" prop="deviceId">
        <!-- 改为 deviceId -->
        <!-- <el-select
          v-model="queryParams.deviceId"
          placeholder="请选择设备"
          clearable
          @keyup.enter.native="handleQuery"
        >
          <el-option
            v-for="device in deviceOptions"
            :key="device.deviceId"
            :label="device.deviceName"
            :value="device.deviceId"
          />
        </el-select>
      </el-form-item> -->
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
      <el-form-item label="时间类型" prop="timeType">
        <el-select
          v-model="queryParams.timeType"
          placeholder="请选择时间类型"
          @change="handleTimeTypeChange"
        >
          <el-option label="起止时间（日期）" value="date" />
          <el-option label="起止时间（小时）" value="hour" />
        </el-select>
      </el-form-item>

      <!-- 日期类型的选择器 -->
      <!-- 日期类型的选择器 -->
      <template v-if="queryParams.timeType === 'date'">
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="queryParams.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="queryParams.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
          />
        </el-form-item>
      </template>

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
          v-hasPermi="['runda:query:add']"
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
          v-hasPermi="['runda:query:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="query212List"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="35" align="center" />
      <!-- <el-table-column label="主键id" align="center" prop="id" /> -->
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column
        label="日期"
        align="center"
        prop="createDate"
        width="100"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="空气质量指数" align="center" prop="aqi" />
      <!-- <el-table-column label="pm2.5(μg/m)" align="center" prop="pm" /> -->
      <el-table-column label="PM2.5(μg/m³)" align="center" prop="dust">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >PM<sub style="font-size: 12px; margin-left: 1px">2.5</sub></span
            >
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="pm10浓度" align="center" prop="pm10" /> -->
      <el-table-column label="PM10(μg/m³)" align="center" prop="pm10">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >PM<sub style="font-size: 12px; margin-left: 1px">10</sub></span
            >
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="SO2浓度" align="center" prop="so2Thickness">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >SO<sub style="font-size: 12px; margin-left: 1px">2</sub
              >浓度</span
            >
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="NO2浓度" align="center" prop="no2Thickness">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >NO<sub style="font-size: 12px; margin-left: 1px">2</sub
              >浓度</span
            >
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="co浓度" align="center" prop="coThickness">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span>CO浓度</span>
            <span style="margin-top: 2px">(mg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="O3浓度" align="center" prop="co3Thickness">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >O<sub style="font-size: 12px; margin-left: 1px">3</sub>浓度</span
            >
            <span style="margin-top: 2px">(μg/m³)</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="vocs浓度" align="center" prop="voc">
        <template slot="header">
          <div
            style="display: flex; flex-direction: column; align-items: center"
          >
            <span
              >VOC<sub style="font-size: 12px; margin-left: 1px">s</sub></span
            >
            <span style="margin-top: 2px">(mg/m³)</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="噪音" align="center" prop="noise" />
      <el-table-column label="风速(m/s)" align="scenter" prop="windSpeed" />
      <el-table-column label="风向" align="center" prop="windDirectionString" />
      <el-table-column label="温度" align="center" prop="temperature" />

      <el-table-column label="湿度" align="center" prop="humidity" />
      <el-table-column label="压力" align="center" prop="pressure" />

      <!-- <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:query212:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['runda:query212:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column> -->
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改大气数据查询212对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备id" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备id" />
        </el-form-item>
        <el-form-item label="空气质量指数" prop="aqi">
          <el-input v-model="form.aqi" placeholder="请输入空气质量指数" />
        </el-form-item>
        <el-form-item label="温度" prop="temperature">
          <el-input v-model="form.temperature" placeholder="请输入温度" />
        </el-form-item>
        <el-form-item label="风速" prop="windSpeed">
          <el-input v-model="form.windSpeed" placeholder="请输入风速" />
        </el-form-item>
        <el-form-item label="风向" prop="windDirection">
          <el-input v-model="form.windDirection" placeholder="请输入风向" />
        </el-form-item>
        <el-form-item label="湿度" prop="humidity">
          <el-input v-model="form.humidity" placeholder="请输入湿度" />
        </el-form-item>
        <el-form-item label="压力" prop="pressure">
          <el-input v-model="form.pressure" placeholder="请输入压力" />
        </el-form-item>
        <el-form-item label="噪音" prop="noise">
          <el-input v-model="form.noise" placeholder="请输入噪音" />
        </el-form-item>
        <el-form-item label="pm2.5浓度" prop="pm">
          <el-input v-model="form.pm" placeholder="请输入pm2.5浓度" />
        </el-form-item>
        <el-form-item label="pm10浓度" prop="pm10">
          <el-input v-model="form.pm10" placeholder="请输入pm10浓度" />
        </el-form-item>
        <el-form-item label="so2浓度" prop="so2Thickness">
          <el-input v-model="form.so2Thickness" placeholder="请输入so2浓度" />
        </el-form-item>
        <el-form-item label="no2浓度" prop="no2Thickness">
          <el-input v-model="form.no2Thickness" placeholder="请输入no2浓度" />
        </el-form-item>
        <el-form-item label="co浓度" prop="coThickness">
          <el-input v-model="form.coThickness" placeholder="请输入co浓度" />
        </el-form-item>
        <el-form-item label="o3 浓度" prop="co3Thickness">
          <el-input v-model="form.co3Thickness" placeholder="请输入o3 浓度" />
        </el-form-item>
        <el-form-item label="vocs浓度" prop="voscThickness">
          <el-input v-model="form.voscThickness" placeholder="请输入vocs浓度" />
        </el-form-item>
        <!-- <el-form-item label="开始时间" prop="beginTime">
          <el-date-picker
            clearable
            v-model="form.beginTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始时间"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            clearable
            v-model="form.endTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结束时间"
          >
          </el-date-picker>
        </el-form-item> -->
        <el-form-item label="日期" prop="date">
          <el-date-picker
            clearable
            v-model="form.date"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择日期和时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="首要污染物" prop="primaryPollutant">
          <el-input
            v-model="form.primaryPollutant"
            placeholder="请输入首要污染物"
          />
        </el-form-item>
        <el-form-item label="站点_id" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点_id" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listQuery212,
  getQuery212,
  delQuery212,
  addQuery212,
  updateQuery212,
  listByDeviceId,
} from "@/api/runda/query212";
import request from "@/utils/request"; // 添加这行导入
import { start } from "nprogress";
export default {
  name: "Query212",
  data() {
    return {
      // 遮罩层
      deviceOptions: [], // 用于存储设备列表
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
      // 大气数据查询212表格数据
      query212List: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      // queryParams: {
      //   pageNum: 1,
      //   pageSize: 10,
      //   deviceId: null,
      //   beginTime: null,
      //   endTime: null,
      // },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: null, // 改为 deviceId
        timeType: "date", // 默认选择日期类型
        startDate: null,
        endDate: null,
        selectedDate: null,
        startHour: null,
        endHour: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceId: [
          { required: true, message: "设备id不能为空", trigger: "blur" },
        ],
        aqi: [
          { required: true, message: "空气质量指数不能为空", trigger: "blur" },
        ],
        temperature: [
          { required: true, message: "温度不能为空", trigger: "blur" },
        ],
        windSpeed: [
          { required: true, message: "风速不能为空", trigger: "blur" },
        ],
        windDirection: [
          { required: true, message: "风向不能为空", trigger: "blur" },
        ],
        humidity: [
          { required: true, message: "湿度不能为空", trigger: "blur" },
        ],
        pressure: [
          { required: true, message: "压力不能为空", trigger: "blur" },
        ],
        noise: [{ required: true, message: "噪音不能为空", trigger: "blur" }],
        pm: [{ required: true, message: "pm2.5浓度不能为空", trigger: "blur" }],
        pm10: [
          { required: true, message: "pm10浓度不能为空", trigger: "blur" },
        ],
        so2Thickness: [
          { required: true, message: "so2浓度不能为空", trigger: "blur" },
        ],
        no2Thickness: [
          { required: true, message: "no2浓度不能为空", trigger: "blur" },
        ],
        coThickness: [
          { required: true, message: "co浓度不能为空", trigger: "blur" },
        ],
        co3Thickness: [
          { required: true, message: "o3 浓度不能为空", trigger: "blur" },
        ],
        voscThickness: [
          { required: true, message: "vocs浓度不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
    this.getDeviceList(); // 在组件创建时获取设备列表
  },
  methods: {
    handleTimeTypeChange(value) {
      // 切换时间类型时重置相关字段
      if (value === "date") {
        this.queryParams.selectedDate = null;
        this.queryParams.startHour = null;
        this.queryParams.endHour = null;
      } else {
        this.queryParams.startDate = null;
        this.queryParams.endDate = null;
      }
    },

    async getDeviceList() {
      try {
        // 从 localStorage 获取设备列表
        const cachedDeviceList = localStorage.getItem("deviceList");

        // 如果缓存中有设备数据，直接使用缓存数据
        if (cachedDeviceList) {
          this.deviceOptions = JSON.parse(cachedDeviceList);
          console.log("从缓存中获取设备列表：", this.deviceOptions);

          if (this.deviceOptions.length > 0) {
            this.$message.success(
              `成功从缓存获取到 ${this.deviceOptions.length} 个设备`
            );
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

    handleQuery() {
      this.queryParams.pageNum = 1; // 重置为第一页
      console.log("查询参数：", this.queryParams);
      this.getList(); // 调用获取列表的方法
    },

    getList(params = this.queryParams) {
      // this.loading = true;
      this.loading = false;

      // 处理设备ID查询
      if (
        this.queryParams.deviceId &&
        !this.queryParams.startDate &&
        !this.queryParams.endDate &&
        !this.queryParams.selectedDate &&
        !this.queryParams.startHour &&
        !this.queryParams.endHour
      ) {
        request({
          url: "/runda/query212/listByDeviceId",
          method: "get",
          params: {
            deviceId: this.queryParams.deviceId,
            page: this.queryParams.pageNum,
            size: this.queryParams.pageSize,
          },
        })
          .then((response) => {
            this.handleResponse(response);
          })
          .catch((error) => {
            console.error("查询设备数据失败：", error);
            this.$message.error("查询设备数据失败");
          })
          .finally(() => {
            this.loading = false;
          });
        return;
      }

      // 处理日期查询
      if (
        this.queryParams.timeType === "date" &&
        this.queryParams.startDate &&
        this.queryParams.deviceId == null
      ) {
        const date = `${this.queryParams.startDate}`;
        const endDate = this.queryParams.endDate
          ? `${this.queryParams.endDate} 23:59:59`
          : `${this.queryParams.startDate} 23:59:59`;

        request({
          // url: "http://192.168.124.9:81/dev-api/runda/query212/listByDate",
          url: "/runda/query212/listByDate",
          method: "get",
          params: {
            date: date,
            endDateTime: endDate,
            deviceId: this.queryParams.deviceId,
            page: this.queryParams.pageNum,
            size: this.queryParams.pageSize,
          },
        })
          .then((response) => {
            this.handleResponse(response);
          })
          .catch((error) => {
            console.error("查询日期数据失败：", error);
            this.$message.error("查询失败");
          })
          .finally(() => {
            this.loading = false;
          });
        return;
      }
      // 处理设备ID和日期共同查询的情况
      if (this.queryParams.deviceId && this.queryParams.startDate) {
        const date = `${this.queryParams.startDate}`;

        request({
          url: "/runda/query212/listByDateAndDeviceId",
          method: "get",
          params: {
            deviceId: this.queryParams.deviceId,
            date, // 确保传递 date

            page: this.queryParams.pageNum,
            size: this.queryParams.pageSize,
          },
        })
          .then((response) => {
            console.log(response); // 打印返回的数据
            this.handleResponse(response);
          })
          .catch((error) => {
            console.error("查询设备和日期数据失败：", error);
            this.$message.error("查询失败");
          })
          .finally(() => {
            this.loading = false;
          });
        return;
      }
      // 处理设备ID和小时共同查询的情况
      if (
        this.queryParams.deviceId &&
        this.queryParams.selectedDate &&
        this.queryParams.startHour &&
        this.queryParams.endHour
      ) {
        // 确保 selectedDate 格式正确，并提取出日期部分
        const date = this.queryParams.selectedDate
          ? `${this.queryParams.selectedDate}`
          : null;

        // 打印 selectedDate 以确认
        console.log("Selected Date:", date);

        // 确保 date 为有效的日期格式
        if (!date) {
          console.error("缺少日期参数 (date)");
          this.$message.error("日期参数不能为空");
          return; // 如果没有日期，终止请求
        }

        // 处理 startDateTime 和 endDateTime
        const startDateTime = `${this.queryParams.selectedDate} ${this.queryParams.startHour}`;
        const endDateTime = `${this.queryParams.selectedDate} ${this.queryParams.endHour}`;

        // 打印所有参数确认
        console.log("Start DateTime:", startDateTime);
        console.log("End DateTime:", endDateTime);

        // 确保传递了所有必要的参数
        if (!startDateTime || !endDateTime) {
          console.error("缺少开始时间或结束时间");
          return; // 如果缺少任何时间参数，不发送请求
        }

        // 发起请求
        request({
          // url: "http://localhost:81/dev-api/runda/query212/listByDateAndDeviceId",
          url: "/runda/query212/listByDateTimeRangeAndDeviceId",
          method: "get",
          params: {
            deviceId: this.queryParams.deviceId,
            date: date, // 确保传递了 date 参数
            startDateTime: startDateTime,
            endDateTime: endDateTime,
            page: this.queryParams.pageNum,
            size: this.queryParams.pageSize,
          },
        })
          .then((response) => {
            this.handleResponse(response);
          })
          .catch((error) => {
            console.error("查询设备和小时数据失败：", error);
            this.$message.error("查询失败");
          })
          .finally(() => {
            this.loading = false;
          });

        return;
      }

      // 处理小时查询
      if (
        this.queryParams.timeType === "hour" &&
        this.queryParams.selectedDate &&
        this.queryParams.startHour &&
        this.queryParams.endHour
      ) {
        const startDateTime = `${this.queryParams.selectedDate} ${this.queryParams.startHour}`;
        const endDateTime = `${this.queryParams.selectedDate} ${this.queryParams.endHour}`;

        request({
          url: "/runda/query212/listByDateTimeRange",
          method: "get",
          params: {
            startDateTime: startDateTime,
            endDateTime: endDateTime,
            deviceId: this.queryParams.deviceId,
            page: this.queryParams.pageNum,
            size: this.queryParams.pageSize,
          },
        })
          .then((response) => {
            this.handleResponse(response);
          })
          .catch((error) => {
            console.error("查询小时数据失败：", error);
            this.$message.error("查询失败");
          })
          .finally(() => {
            this.loading = false;
          });
        return;
      }
    },

    // 处理查询结果响应
    handleResponse(response) {
      if (response && response.code === 0) {
        this.query212List = response.rows || [];
        this.total = response.total || 0;

        if (this.query212List.length === 0) {
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
        date: null,
        aqi: null,
        temperature: null,
        windSpeed: null,
        windDirection: null,
        humidity: null,
        pressure: null,
        noise: null,
        pm: null,
        pm10: null,
        so2Thickness: null,
        no2Thickness: null,
        coThickness: null,
        co3Thickness: null,
        voscThickness: null,
        pm03Above: null,
        pm05Above: null,
        pm1: null,
        pm25: null,

        pm1Above: null,
        pm25Above: null,
        pm5Above: null,
        pm10Above: null,
        beginTime: null,
        endTime: null,
        longitude: null,
        latitude: null,
        tsp: null,
        primaryPollutant: null,
        stationId: null,
        sn: null,
      };
      this.resetForm("form");
    },
    // /** 搜索按钮操作 */
    // handleQuery() {
    //   this.queryParams.pageNum = 1;
    //   this.getList();
    // },
    /** 重置按钮操作 */
    // resetQuery() {
    //   this.resetForm("queryForm");
    //   this.handleQuery();
    // },
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        deviceId: null,
        timeType: "date",
        startDate: null,
        endDate: null,
        selectedDate: null,
        startHour: null,
        endHour: null,
      };
      this.getList();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加大气数据查询212";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getQuery212(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改大气数据查询212";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateQuery212(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addQuery212(this.form).then((response) => {
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
      this.$modal
        .confirm('是否确认删除大气数据查询212编号为"' + ids + '"的数据项？')
        .then(function () {
          return delQuery212(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    // /** 导出按钮操作 */
    // handleExport() {
    //   console.log("导出参数:", this.queryParams); // 打印 queryParams，检查其内容

    //   this.download(
    //     "runda/query212/export",
    //     {
    //       ...this.queryParams,
    //     },
    //     `query212_${new Date().getTime()}.xlsx`
    //   );
    // },
    //   handleExport() {
    //     console.log("导出参数:", this.queryParams); // 打印 queryParams，检查其内容

    //     const startDateTime = `${this.queryParams.selectedDate} ${this.queryParams.startHour}`;
    //     const endDateTime = `${this.queryParams.selectedDate} ${this.queryParams.endHour}`;

    //     // 调试：输出构造的参数
    //     console.log("构造的请求参数:", {
    //       deviceId: this.queryParams.deviceId,
    //       startDateTime,
    //       endDateTime,
    //       page: this.queryParams.pageNum,
    //       size: this.queryParams.pageSize,
    //     });

    //     // 构造请求参数
    //     const params = {
    //       deviceId: this.queryParams.deviceId, // 设备ID
    //       startDateTime, // 起始时间
    //       endDateTime, // 结束时间
    //       page: this.queryParams.pageNum, // 当前页码
    //       size: this.queryParams.pageSize, // 每页条数
    //     };

    //     // // 发起下载请求
    //     // this.load(
    //     //   "runda/query212/export2", // 后端接口路径
    //     //   params, // 请求参数----很重要
    //     //   `query212_${new Date().getTime()}.xlsx` // 导出的文件名，附加时间戳避免重复
    //     // );
    //     // 判断条件，决定是调用 /export2 还是 /export
    // let url = "";
    // if (this.queryParams.selectedDate && this.queryParams.startHour && this.queryParams.endHour) {
    //   // 如果 selectedDate, startHour 和 endHour 存在，使用 /export2
    //   url = "runda/query212/export2";
    // } else {
    //   // 否则，使用 /export
    //   url = "runda/query212/export";
    // }

    // // 发起下载请求
    // this.load(
    //   url, // 根据条件选择的后端接口路径
    //   params, // 请求参数
    //   `query212_${new Date().getTime()}.xlsx` // 导出的文件名，附加时间戳避免重复
    // );
    //   },
    handleExport() {
      console.log("导出参数:", this.queryParams); // 打印 queryParams，检查其内容

      // 确保startDate和endDate都有值，作为日期范围
      const startDateTime = `${this.queryParams.selectedDate} ${this.queryParams.startHour}`;
      const endDateTime = `${this.queryParams.selectedDate} ${this.queryParams.endHour}`;

      // 只有当设备ID存在时，才需要带上设备ID参数
      const deviceId = this.queryParams.deviceId;

      // 判断是否是导出基于日期的报告
      let url = "";
      let params = {};

      // 如果有设备ID，并且是按设备与日期时间范围导出的
      if (deviceId && startDateTime && endDateTime) {
        console.log("调用 /export2 接口导出数据");

        url = "runda/query212/export2"; // 按设备ID和日期范围导出
        params = {
          deviceId: deviceId,
          startDateTime: startDateTime,
          endDateTime: endDateTime,
          page: this.queryParams.pageNum,
          size: this.queryParams.pageSize,
        };
      } else if (startDateTime && endDateTime) {
        // 如果没有设备ID，只按日期范围导出
        console.log("调用 /export 接口导出数据");

        url = "runda/query212/export"; // 按日期范围导出
        params = {
          startDateTime: startDateTime,
          endDateTime: endDateTime,
          page: this.queryParams.pageNum,
          size: this.queryParams.pageSize,
        };
      }

      // 调试：输出构造的请求参数
      console.log("构造的请求参数:", params);

      // 发起下载请求
      this.load(
        url, // 后端接口路径
        params, // 请求参数
        `query212_${new Date().getTime()}.xlsx` // 导出的文件名，附加时间戳避免重复
      );
    },
  },
};
</script>
