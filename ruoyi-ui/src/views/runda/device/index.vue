<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备号" prop="sn">
        <el-input v-model="queryParams.sn" placeholder="请输入设备号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="站点id" prop="stationId">
        <el-input v-model="queryParams.stationId" placeholder="请输入站点id" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['runda:device:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['runda:device:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['runda:device:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:device:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="设备名称" align="center" prop="name" />
      <el-table-column label="设备号" align="center" prop="sn" />
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="工地状态" align="center" prop="buildStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.build_status" :value="scope.row.buildStatus" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="SIM卡号" align="center" prop="phoneNumber" />
      <el-table-column label="制造商" align="center" prop="manufacturer" />
      <el-table-column label="标准站" align="center" prop="isStandard">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_standard" :value="scope.row.isStandard" />
        </template>
      </el-table-column>
      <el-table-column label="站点id" align="center" prop="stationId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:device:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:device:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改监测设备管理设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备号" prop="sn">
          <el-input v-model="form.sn" placeholder="请输入设备号" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdTime">
          <el-date-picker clearable v-model="form.createdTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in dict.type.device_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工地状态" prop="buildStatus">
          <el-select v-model="form.buildStatus" placeholder="请选择工地状态">
            <el-option v-for="dict in dict.type.build_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="省昵称" prop="provinceCn">
          <el-input v-model="form.provinceCn" placeholder="请输入省昵称" />
        </el-form-item>
        <el-form-item label="市昵称" prop="cityCn">
          <el-input v-model="form.cityCn" placeholder="请输入市昵称" />
        </el-form-item>
        <el-form-item label="区/县昵称" prop="countyCn">
          <el-input v-model="form.countyCn" placeholder="请输入区/县昵称" />
        </el-form-item>
        <el-form-item label="乡/镇昵称" prop="townCn">
          <el-input v-model="form.townCn" placeholder="请输入乡/镇昵称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="SIM卡号" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入SIM卡号" />
        </el-form-item>
        <el-form-item label="制造商" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
        </el-form-item>
        <el-form-item label="数据来源" prop="fromResource">
          <el-select v-model="form.fromResource" placeholder="请选择数据来源">
            <el-option v-for="dict in dict.type.from_resource" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标准站" prop="isStandard">
          <el-select v-model="form.isStandard" placeholder="请选择标准站">
            <el-option v-for="dict in dict.type.is_standard" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="站点id" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点id" />
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
import { listDevice, getDevice, delDevice, addDevice, updateDevice } from "@/api/runda/device";

export default {
  name: "Device",
  dicts: ['device_status', 'from_resource', 'tell_user', 'device_type', 'is_standard', 'build_status'],
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
      // 监测设备管理设备表格数据
      deviceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        sn: null,
        stationId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "设备名称不能为空", trigger: "blur" }
        ],
        sn: [
          { required: true, message: "设备号不能为空", trigger: "blur" }
        ],
        longitude: [
          { required: true, message: "经度不能为空", trigger: "blur" }
        ],
        latitude: [
          { required: true, message: "纬度不能为空", trigger: "blur" }
        ],
        createdTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ],
        phoneNumber: [
          { required: true, message: "SIM卡号不能为空", trigger: "blur" }
        ],
        manufacturer: [
          { required: true, message: "制造商不能为空", trigger: "blur" }
        ],
        isStandard: [
          { required: true, message: "标准站不能为空", trigger: "change" }
        ],
        stationId: [
          { required: true, message: "站点id不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询监测设备管理设备列表 */
    getList() {
      this.loading = true;
      listDevice(this.queryParams).then(response => {
        this.deviceList = response.rows;
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
        name: null,
        sn: null,
        longitude: null,
        latitude: null,
        createdTime: null,
        lastUpdatedTime: null,
        status: null,
        orgId: null,
        buildStatus: null,
        province: null,
        provinceCn: null,
        city: null,
        cityCn: null,
        county: null,
        countyCn: null,
        town: null,
        townCn: null,
        remark: null,
        phoneNumber: null,
        manufacturer: null,
        departmentId: null,
        systemUserId: [],
        fromResource: null,
        type: null,
        ip: null,
        port: null,
        userName: null,
        password: null,
        isStandard: null,
        guid: null,
        stationId: null,
        addr: null,
        userId: null
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
      this.title = "添加监测设备管理设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDevice(id).then(response => {
        this.form = response.data;
        if (!this.form.systemUserId) {
          this.form.systemUserId = [];
        } else {
          this.form.systemUserId = this.form.systemUserId.split(",");
        }
        this.open = true;
        this.title = "修改监测设备管理设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.systemUserId = this.form.systemUserId.join(",");
          if (this.form.id != null) {
            updateDevice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDevice(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除监测设备管理设备编号为"' + ids + '"的数据项？').then(function () {
        return delDevice(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/device/export', {
        ...this.queryParams
      }, `device_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
