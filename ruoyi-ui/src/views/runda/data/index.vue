<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="px"
    >
      <el-form-item label="报警类型" prop="alarmType">
        <el-select
          v-model="queryParams.alarmType"
          placeholder="请选择报警类型"
          clearable
        >
          <el-option
            v-for="dict in dict.type.alarm_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="报警参数类型" prop="alarmParamType">
        <el-select
          v-model="queryParams.alarmParamType"
          placeholder="请选择报警参数类型"
          clearable
        >
          <el-option
            v-for="dict in dict.type.alarm_param_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker
          clearable
          v-model="queryParams.beginTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择开始时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          clearable
          v-model="queryParams.endTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择结束时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="站点昵称" prop="staName">
        <el-input
          v-model="queryParams.staName"
          placeholder="请输入站点昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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

    <el-table
      v-loading="loading"
      :data="dataList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报警类型" align="center" prop="alarmType">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.alarm_type"
            :value="scope.row.alarmType"
          />
        </template>
      </el-table-column>
      <el-table-column label="设备报警值" align="center" prop="alarmValue" />
      <el-table-column
        label="设备报警阀值"
        align="center"
        prop="standardValue"
      />
      <el-table-column
        label="开始时间"
        align="center"
        prop="beginTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.beginTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="结束时间"
        align="center"
        prop="endTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报警信息状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.alarm_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column label="站点昵称" align="center" prop="staName" />
      <el-table-column label="设备昵称" align="center" prop="deName" />
      <el-table-column label="部门昵称" align="center" prop="deptName" />
      <el-table-column
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
            v-hasPermi="['runda:data:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['runda:data:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改告警日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="报警类型" prop="alarmType">
          <el-select v-model="form.alarmType" placeholder="请选择报警类型">
            <el-option
              v-for="dict in dict.type.alarm_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报警参数类型" prop="alarmParamType">
          <el-select
            v-model="form.alarmParamType"
            placeholder="请选择报警参数类型"
          >
            <el-option
              v-for="dict in dict.type.alarm_param_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备报警值" prop="alarmValue">
          <el-input v-model="form.alarmValue" placeholder="请输入设备报警值" />
        </el-form-item>
        <el-form-item label="设备报警阀值" prop="standardValue">
          <el-input
            v-model="form.standardValue"
            placeholder="请输入设备报警阀值"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="beginTime">
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
        </el-form-item>
        <el-form-item label="报警信息状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择报警信息状态">
            <el-option
              v-for="dict in dict.type.alarm_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="站点id" prop="staId">
          <el-input v-model="form.staId" placeholder="请输入站点id" />
        </el-form-item>
        <el-form-item label="站点昵称" prop="staName">
          <el-input v-model="form.staName" placeholder="请输入站点昵称" />
        </el-form-item>
        <el-form-item label="设备id" prop="deId">
          <el-input v-model="form.deId" placeholder="请输入设备id" />
        </el-form-item>
        <el-form-item label="设备昵称" prop="deName">
          <el-input
            v-model="form.deName"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="部门id" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门id" />
        </el-form-item>
        <el-form-item label="部门昵称" prop="deptName">
          <el-input
            v-model="form.deptName"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="部门权限" prop="deptPath">
          <el-input v-model="form.deptPath" placeholder="请输入部门权限" />
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
  listData,
  getData,
  delData,
  addData,
  updateData,
} from "@/api/runda/data";

export default {
  name: "Data",
  dicts: ["alarm_status", "alarm_type", "alarm_param_type"],
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
      // 告警日志表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        alarmType: null,
        alarmParamType: null,
        beginTime: null,
        endTime: null,
        staName: null,
        deName: null,
        deptName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        alarmType: [
          { required: true, message: "报警类型不能为空", trigger: "change" },
        ],
        alarmParamType: [
          {
            required: true,
            message: "报警参数类型不能为空",
            trigger: "change",
          },
        ],
        standardValue: [
          { required: true, message: "设备报警阀值不能为空", trigger: "blur" },
        ],
        beginTime: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        endTime: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
        status: [
          {
            required: true,
            message: "报警信息状态不能为空",
            trigger: "change",
          },
        ],
        staId: [{ required: true, message: "站点id不能为空", trigger: "blur" }],
        staName: [
          { required: true, message: "站点昵称不能为空", trigger: "blur" },
        ],
        deId: [{ required: true, message: "设备id不能为空", trigger: "blur" }],
        deName: [
          { required: true, message: "设备昵称不能为空", trigger: "blur" },
        ],
        deptId: [
          { required: true, message: "部门id不能为空", trigger: "blur" },
        ],
        deptName: [
          { required: true, message: "部门昵称不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询告警日志列表 */
    getList() {
      this.loading = true;
      listData(this.queryParams).then((response) => {
        this.dataList = response.rows;
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
        alarmType: null,
        alarmParamType: null,
        alarmValue: null,
        standardValue: null,
        beginTime: null,
        endTime: null,
        status: null,
        staId: null,
        staName: null,
        deId: null,
        deName: null,
        deptId: null,
        deptName: null,
        deptPath: null,
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
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加告警日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getData(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改告警日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateData(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addData(this.form).then((response) => {
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
        .confirm('是否确认删除告警日志编号为"' + ids + '"的数据项？')
        .then(function () {
          return delData(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "runda/data/export",
        {
          ...this.queryParams,
        },
        `data_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
