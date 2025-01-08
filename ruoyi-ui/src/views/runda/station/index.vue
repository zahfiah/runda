<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="监测站名称" prop="stationName">
        <el-input v-model="queryParams.stationName" placeholder="请输入监测站名称" clearable
          @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="监测站类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择监测站类型" clearable>
          <el-option v-for="dict in dict.type.stations_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="施工许可证编号" prop="licensNumber">
        <el-input v-model="queryParams.licensNumber" placeholder="请输入施工许可证编号" clearable
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
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['runda:station:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['runda:station:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:station:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="监测站名称" align="center" prop="stationName" />
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监测站类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.stations_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="stationStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.station_status" :value="scope.row.stationStatus" />
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="占地面积" align="center" prop="floorSpace" />
      <el-table-column label="施工许可证编号" align="center" prop="licensNumber" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="监测站名称" prop="stationName">
          <el-input v-model="form.stationName" placeholder="请输入监测站名称" />
        </el-form-item>
        <el-form-item label="监测方式" prop="monitoringWay">
          <el-select v-model="form.monitoringWay" placeholder="请选择监测方式">
            <el-option v-for="dict in dict.type.monitoring_way" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="省简称" prop="provinceCn">
          <el-input v-model="form.provinceCn" placeholder="请输入省简称" />
        </el-form-item>
        <el-form-item label="市简称" prop="cityCn">
          <el-input v-model="form.cityCn" placeholder="请输入市简称" />
        </el-form-item>
        <el-form-item label="区/县简称" prop="countyCn">
          <el-input v-model="form.countyCn" placeholder="请输入区/县简称" />
        </el-form-item>
        <el-form-item label="乡/镇简称" prop="townCn">
          <el-input v-model="form.townCn" placeholder="请输入乡/镇简称" />
        </el-form-item>
        <el-form-item label="操作员" prop="systemUserId">
          <el-checkbox-group v-model="form.systemUserId">
            <el-checkbox v-for="dict in dict.type.tell_user" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="创建时间" prop="createdTime">
          <el-date-picker clearable v-model="form.createdTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item label="监测站类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择监测站类型">
            <el-option v-for="dict in dict.type.stations_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态" prop="stationStatus">
          <el-select v-model="form.stationStatus" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.station_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系人" prop="linkMan">
          <el-input v-model="form.linkMan" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="占地面积" prop="floorSpace">
          <el-input v-model="form.floorSpace" placeholder="请输入占地面积" />
        </el-form-item>
        <el-form-item label="施工许可证编号" prop="licensNumber">
          <el-input v-model="form.licensNumber" placeholder="请输入施工许可证编号" />
        </el-form-item>
        <el-form-item label="数据来源" prop="fromResource">
          <el-select v-model="form.fromResource" placeholder="请选择数据来源">
            <el-option v-for="dict in dict.type.from_resource" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建设单位" prop="jsdwmc">
          <el-input v-model="form.jsdwmc" placeholder="请输入建设单位" />
        </el-form-item>
        <el-form-item label="建设单位信用代码" prop="jstsyhydm">
          <el-input v-model="form.jstsyhydm" placeholder="请输入建设单位信用代码" />
        </el-form-item>
        <el-form-item label="施工单位" prop="sgdwmc">
          <el-input v-model="form.sgdwmc" placeholder="请输入施工单位" />
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
        <el-form-item label="PM10设备安装" prop="sfazjcsb">
          <el-checkbox-group v-model="form.sfazjcsb">
            <el-checkbox v-for="dict in dict.type.pm10_status" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="设备id" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备id" />
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
import { listStation, getStation, delStation, addStation, updateStation } from "@/api/runda/station";

export default {
  name: "Station",
  dicts: ['monitoring_way', 'from_resource', 'sgjd', 'tell_user', 'pm10_status', 'station_status', 'stations_type'],
  data() {
    return {
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
          { required: true, message: "监测站名称不能为空", trigger: "blur" }
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
        phone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        floorSpace: [
          { required: true, message: "占地面积不能为空", trigger: "blur" }
        ],
        licensNumber: [
          { required: true, message: "施工许可证编号不能为空", trigger: "blur" }
        ],
        jsdwmc: [
          { required: true, message: "建设单位不能为空", trigger: "blur" }
        ],
        jstsyhydm: [
          { required: true, message: "建设单位信用代码不能为空", trigger: "blur" }
        ],
        sgdwmc: [
          { required: true, message: "施工单位不能为空", trigger: "blur" }
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
          { required: true, message: "PM10设备安装不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
        stationCode: null,
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
        departmentId: null,
        systemUserId: [],
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
        sfazjcsb: [],
        jgrq: null,
        deviceId: null
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
      const id = row.id || this.ids
      getStation(id).then(response => {
        this.form = response.data;
        if (!this.form.systemUserId) {
          this.form.systemUserId = [];
        }
        this.form.sfazjcsb = this.form.sfazjcsb?.split(",") ?? [];
        this.open = true;
        this.title = "修改监测站点管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.systemUserId = this.form.systemUserId.join(",");
          this.form.sfazjcsb = this.form.sfazjcsb.join(",");
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
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/station/export', {
        ...this.queryParams
      }, `station_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
