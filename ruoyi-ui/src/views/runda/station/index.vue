<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="监测站" prop="stationName">
        <el-input v-model="queryParams.stationName" placeholder="请输入监测站" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="监测站类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择监测站类型" clearable>
          <el-option v-for="dict in dict.type.stations_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="施工许可证" prop="licensNumber">
        <el-input v-model="queryParams.licensNumber" placeholder="请输入施工许可证" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['runda:station:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:station:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stationList" @selection-change="handleSelectionChange">
      <el-table-column label="监测站" align="center" prop="stationName" />
      <el-table-column label="监测站类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.stations_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="地址" align="center">
        <el-table-column label="市" align="center" prop="cityCn" />
        <el-table-column label="区" align="center" prop="countyCn" />
        <el-table-column label="乡镇" align="center" prop="townCn" />
      </el-table-column>
      <el-table-column label="占地面积" align="center" prop="floorSpace" />
      <el-table-column label="施工许可证" align="center" prop="licensNumber" />
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="stationStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.station_status" :value="scope.row.stationStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-setting" @click="handleViewDevices(scope.row)"
            v-if="hasDevicesForStation(scope.row.id)">设备</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:station:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:station:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改监测站点管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basicInfo">
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="监测站" prop="stationName">
              <el-input v-model="form.stationName" placeholder="请输入监测站" />
            </el-form-item>
            <el-form-item label="监测站类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择监测站类型">
                <el-option v-for="dict in dict.type.stations_type" :key="dict.value" :label="dict.label"
                  :value="parseInt(dict.value)"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="stationStatus">
              <el-select v-model="form.stationStatus" placeholder="请选择状态">
                <el-option v-for="dict in dict.type.station_status" :key="dict.value" :label="dict.label"
                  :value="parseInt(dict.value)"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="经度" prop="longitude">
              <el-input v-model="form.longitude" placeholder="请输入经度" />
            </el-form-item>
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model="form.latitude" placeholder="请输入纬度" />
            </el-form-item>
            <el-form-item label="联系人" prop="linkMan">
              <el-input v-model="form.linkMan" placeholder="请输入联系人" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
            <el-form-item label="施工许可证" prop="licensNumber">
              <el-input v-model="form.licensNumber" placeholder="请输入施工许可证" />
            </el-form-item>
            <el-form-item label="占地面积" prop="floorSpace">
              <el-input v-model="form.floorSpace" placeholder="请输入占地面积" />
            </el-form-item>
            <el-form-item label="省" prop="provinceCn">
              <el-select v-model="form.provinceCn" placeholder="请输入省名称" @change="onProvinceChange">
                <el-option v-for="item in regions.provinces" :key="item.value" :label="item.label"
                  :value="item.label"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="市" prop="cityCn">
              <el-select v-model="form.cityCn" placeholder="请输入市昵称" @change="onCityChange">
                <el-option v-for="item in cities" :key="item.value" :label="item.label" :value="item.label"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="区" prop="countyCn">
              <el-select v-model="form.countyCn" placeholder="请输入区/县昵称" @change="onCountyChange">
                <el-option v-for="item in counties" :key="item.value" :label="item.label"
                  :value="item.label"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="乡镇" prop="townCn">
              <el-select v-model="form.townCn" placeholder="请输入乡/镇昵称">
                <el-option v-for="item in towns" :key="item.value" :label="item.label" :value="item.label"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="部门" name="department">
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="部门名称" prop="userId">
              <el-select v-model="form.userId" placeholder="请选择部门名称" clearable>
                <el-option v-for="user in userOptions" :key="user.id" :label="user.userName" :value="user.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作员" prop="systemUserId">
              <el-radio-group v-model="form.systemUserId">
                <el-radio v-for="dict in dict.type.tell_user" :key="dict.value" :label="parseInt(dict.value)">{{
                  dict.label
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="补充信息" name="additionalInfo">
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="建设单位名称" prop="jsdwmc">
              <el-input v-model="form.jsdwmc" placeholder="请输入建设单位名称" />
            </el-form-item>
            <el-form-item label="建设单位信用代码" prop="jstsyhydm">
              <el-input v-model="form.jstsyhydm" placeholder="请输入建设单位信用代码" />
            </el-form-item>
            <el-form-item label="施工单位名称" prop="sgdwmc">
              <el-input v-model="form.sgdwmc" placeholder="请输入施工单位名称" />
            </el-form-item>
            <el-form-item label="施工单位统一社会信用代码" prop="sgtshyhdym">
              <el-input v-model="form.sgtshyhdym" placeholder="请输入施工单位统一社会信用代码" />
            </el-form-item>
            <el-form-item label="监管部门" prop="jgbm">
              <el-input v-model="form.jgbm" placeholder="请输入监管部门" />
            </el-form-item>
            <el-form-item label="监管责任人" prop="jgzr">
              <el-input v-model="form.jgzr" placeholder="请输入监管责任人" />
            </el-form-item>
            <el-form-item label="监管电话" prop="jgzrdh">
              <el-input v-model="form.jgzrdh" placeholder="请输入监管电话" />
            </el-form-item>
            <el-form-item label="施工阶段" prop="sjjd">
              <el-select v-model="form.sjjd" placeholder="请选择施工阶段">
                <el-option v-for="dict in dict.type.sgjd" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="PM10是否安装" prop="sfazjcsb">
              <el-radio-group v-model="form.sfazjcsb">
                <el-radio v-for="dict in dict.type.pm10_status" :key="dict.value" :label="dict.value">{{ dict.label
                  }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="统计时间" prop="jgrq">
              <el-date-picker clearable v-model="form.jgrq" type="date" value-format="yyyy-MM-dd" placeholder="请选择统计时间">
              </el-date-picker>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设备详情对话框 -->
    <el-dialog title="设备详情" :visible.sync="deviceDialogVisible" width="600px" append-to-body
      custom-class="device-dialog">
      <div v-if="selectedDevices.length > 0">
        <div v-for="(device, index) in selectedDevices" :key="index" class="device-card">
          <div class="device-header">
            <strong>设备名称:</strong> {{ device.deviceName }}
          </div>
          <div class="device-details">
            <p><strong>设备号:</strong> {{ device.deviceNo }}</p>
            <p><strong>状态:</strong>
              <span v-if="device.status === 1">正常</span>
              <span v-else-if="device.status === 2">中断</span>
              <span v-else-if="device.status === 3">竣工</span>
              <span v-else>未知状态</span>
            </p>
            <p><strong>备注:</strong> {{ device.remark }}</p>
            <p><strong>创建时间:</strong> {{ device.createdTime }}</p>
          </div>
        </div>
      </div>
      <div v-else>
        <p>没有找到对应的设备信息。</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="closeDeviceDialog">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStation, getStation, delStation, addStation, sendStation,updateStation } from "@/api/runda/station";
import { listDevice } from "@/api/runda/device";
import { listUser } from "@/api/system/user";
import regions from '@/assets/regions.json'; // 引入地区数据

export default {
  name: "Station",
  dicts: ['monitoring_way', 'from_resource', 'sgjd', 'tell_user', 'pm10_status', 'station_status', 'stations_type'],
  data() {
    return {
      userOptions: [],
      deviceList: [],
      regions: regions,
      provinces: regions.provinces,
      cities: [],
      counties: [],
      towns: [],
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
      // 监测站点管理表格数据
      stationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 当前激活的tab页
      activeTab: 'basicInfo',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        stationName: null,
        type: null,
        licensNumber: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        stationName: [
          { required: true, message: "监测站不能为空", trigger: "blur" }
        ],
        createdTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        longitude: [
          { required: true, message: "经度不能为空", trigger: "blur" }
        ],
        latitude: [
          { required: true, message: "纬度不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "监测站类型不能为空", trigger: "change" }
        ],
        stationStatus: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        floorSpace: [
          { required: true, message: "占地面积不能为空", trigger: "blur" }
        ],
        licensNumber: [
          { required: true, message: "施工许可证不能为空", trigger: "blur" }
        ],
        jsdwmc: [
          { required: true, message: "建设单位名称不能为空", trigger: "blur" }
        ],
        jstsyhydm: [
          { required: true, message: "建设单位信用代码不能为空", trigger: "blur" }
        ],
        sgdwmc: [
          { required: true, message: "施工单位名称不能为空", trigger: "blur" }
        ],
        sgtshyhdym: [
          { required: true, message: "施工单位统一社会信用代码不能为空", trigger: "blur" }
        ],
        jgbm: [
          { required: true, message: "监管部门不能为空", trigger: "blur" }
        ],
        jgzr: [
          { required: true, message: "监管责任人不能为空", trigger: "blur" }
        ],
        jgzrdh: [
          { required: true, message: "监管电话不能为空", trigger: "blur" }
        ],
        sjjd: [
          { required: true, message: "施工阶段不能为空", trigger: "change" }
        ],
        sfazjcsb: [
          { required: true, message: "PM10是否安装不能为空", trigger: "change" }
        ],
      },
      // 设备详情对话框可见性
      deviceDialogVisible: false,
      // 选中的设备信息
      selectedDevices: []
    };
  },
  created() {
    this.getList();
    this.getDevices();
    this.getuserOptions();
  },
  methods: {
    /** 获取部门选项列表 */
    getuserOptions() {
      listUser().then(response => {
        if (response.code === 200 && Array.isArray(response.rows)) {
          this.userOptions = response.rows.map(user => ({
            id: user.userId,
            userName: user.nickName,
          }));
        } else {
          console.error('Invalid users data structure:', response);
        }
      }).catch(error => {
        console.error('Failed to fetch users:', error); // 添加错误处理
      });
    },
    /** 获取设备列表 */
    getDevices() {
      listDevice({
        pageNum: 1, // 设置第一页
        pageSize: 99999, // 设置大页数，确保获取所有数据
      }).then(response => {
        if (response.code === 200 && Array.isArray(response.rows)) {
          this.deviceList = response.rows.map(device => ({
            id: device.id,
            deviceName: device.name,
            deviceNo: device.sn,
            status: device.status,
            remark: device.remark,
            createdTime: device.createdTime,
            stationId: device.stationId // 假设设备对象中有 stationId 字段
          }));
        } else {
          console.error('Invalid devices data structure:', response);
        }
      }).catch(error => {
        console.error('Failed to fetch devices:', error); // 添加错误处理
      });
    },
    /** 查询监测站点管理列表 */
    getList() {
      this.loading = true;
      listStation(this.queryParams).then(response => {
        this.stationList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        stationAddr: null,
        stationName: null,
        riverName: null,
        riverCode: null,
        sectionName: null,
        sectionCode: null,
        riverLevel: null,
        inwardWater: null,
        sectionProperties: null,
        monitoringWay: null,
        sectionControlLevel: null,
        province: null,
        provinceCn: null,
        city: null,
        cityCn: null,
        county: null,
        countyCn: null,
        town: null,
        townCn: null,
        userId: null,
        systemUserId: null,
        createdTime: null,
        lastUpdatedTime: null,
        longitude: null,
        latitude: null,
        type: null,
        remark: null,
        stationStatus: null,
        phone: null,
        linkMan: null,
        countryName: null,
        countryId: null,
        floorSpace: null,
        licensNumber: null,
        fromResource: null,
        jsdwmc: null,
        jstsyhydm: null,
        sgdwmc: null,
        sgtshyhdym: null,
        jgbm: null,
        jgzr: null,
        jgzrdh: null,
        sjjd: null,
        sfazjcsb: null,
        jgrq: null,
        deviceId: null,
        deptId: null, // 确保部门ID也被重置
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
      this.handleQuery();
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
      this.title = "添加监测站点管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getStation(id).then(response => {
        this.form = response.data;

        // 初始化城市、区县和乡镇选项
        this.cities = this.regions.cities.filter(city => city.province === this.regions.provinces.find(p => p.label === this.form.provinceCn)?.value);
        this.counties = this.regions.counties.filter(county => county.city === this.regions.cities.find(c => c.label === this.form.cityCn)?.value);
        this.towns = this.regions.towns.filter(town => town.county === this.regions.counties.find(c => c.label === this.form.countyCn)?.value);

        this.open = true;
        this.title = "修改监测站点管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStation(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();

              sendStation(this.form.stationName).then(response => {
                this.$modal.msgSuccess("发送成功");
                console.log(this.form.stationName)
              });

            });

          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除监测站点管理编号为"' + ids + '"的数据项？').then(function () {
        return delStation(ids);
      }).then(ap => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/station/export', {
        ...this.queryParams
      }, `station_${new Date().getTime()}.xlsx`)
    },
    /** 判断指定站点是否有设备 */
    hasDevicesForStation(stationId) {
      return this.deviceList.some(device => device.stationId === stationId);
    },
    /** 查看设备详情 */
    handleViewDevices(row) {
      const stationId = row.id;
      this.selectedDevices = this.deviceList.filter(device => device.stationId === stationId);
      if (this.selectedDevices.length > 0) {
        this.deviceDialogVisible = true;
      } else {
        this.$modal.msgError("未找到对应的设备信息");
      }
    },
    /** 关闭设备详情对话框 */
    closeDeviceDialog() {
      this.deviceDialogVisible = false;
      this.selectedDevices = [];
    },
    onProvinceChange(province) {
      this.cities = this.regions.cities.filter(city => city.province === this.regions.provinces.find(p => p.label === province)?.value);
      this.counties = [];
      this.towns = [];
      this.form.cityCn = '';
      this.form.countyCn = '';
      this.form.townCn = '';
    },
    onCityChange(city) {
      this.counties = this.regions.counties.filter(county => county.city === this.regions.cities.find(c => c.label === city)?.value);
      this.towns = [];
      this.form.countyCn = '';
      this.form.townCn = '';
    },
    onCountyChange(county) {
      this.towns = this.regions.towns.filter(town => town.county === this.regions.counties.find(c => c.label === county)?.value);
      this.form.townCn = '';
    }
  }
};
</script>

<style scoped>
/* 美化设备详情对话框 */
.device-dialog .el-dialog__body {
  padding: 20px;
}

.device-card {
  background-color: #f9fafc;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  padding: 15px;
  transition: transform 0.2s ease-in-out;
}

.device-card:hover {
  transform: translateY(-5px);
}

.device-header {
  font-size: 1.2em;
  color: #333;
  margin-bottom: 10px;
}

.device-details p {
  margin: 5px 0;
  color: #666;
}
</style>
