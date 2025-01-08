<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
          v-hasPermi="['runda:info:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['runda:info:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['runda:info:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:info:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="站点id" align="center" prop="stationId" />
      <el-table-column label="报警类型" align="center" prop="alarmType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_type" :value="scope.row.alarmType" />
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="phoneNumber" />
      <el-table-column label="短信模板" align="center" prop="smsTem" />
      <el-table-column label="发送内容" align="center" prop="smsMessage" />
      <el-table-column label="失败原因" align="center" prop="smsFail" />
      <el-table-column label="创建时间" align="center" prop="createDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:info:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:info:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改短信记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="站点id" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点id" />
        </el-form-item>
        <el-form-item label="报警类型" prop="alarmType">
          <el-select v-model="form.alarmType" placeholder="请选择报警类型">
            <el-option v-for="dict in dict.type.alarm_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="短信模板" prop="smsTem">
          <el-input v-model="form.smsTem" placeholder="请输入短信模板" />
        </el-form-item>
        <el-form-item label="发送内容" prop="smsMessage">
          <el-input v-model="form.smsMessage" placeholder="请输入发送内容" />
        </el-form-item>
        <el-form-item label="失败原因" prop="smsFail">
          <el-input v-model="form.smsFail" placeholder="请输入失败原因" />
        </el-form-item>
        <el-form-item label="操作员" prop="systemUserId">
          <el-checkbox-group v-model="form.systemUserId">
            <el-checkbox v-for="dict in dict.type.tell_user" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="创建时间" prop="createDate">
          <el-date-picker clearable v-model="form.createDate" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
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
import { listInfo, getInfo, delInfo, addInfo, updateInfo } from "@/api/runda/info";

export default {
  name: "Info",
  dicts: ['tell_user', 'info_status', 'alarm_type'],
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
      // 短信记录表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        stationId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        stationId: [
          { required: true, message: "站点id不能为空", trigger: "blur" }
        ],
        alarmType: [
          { required: true, message: "报警类型不能为空", trigger: "change" }
        ],
        phoneNumber: [
          { required: true, message: "手机号不能为空", trigger: "blur" }
        ],
        smsTem: [
          { required: true, message: "短信模板不能为空", trigger: "blur" }
        ],
        smsMessage: [
          { required: true, message: "发送内容不能为空", trigger: "blur" }
        ],
        createDate: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询短信记录列表 */
    getList() {
      this.loading = true;
      listInfo(this.queryParams).then(response => {
        this.infoList = response.rows;
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
        userId: null,
        userName: null,
        enterpriseId: null,
        stationId: null,
        alarmType: null,
        beginTime: null,
        endTime: null,
        timeSpace: null,
        phoneNumber: null,
        smsTem: null,
        smsMessage: null,
        smsFail: null,
        deptId: null,
        deptName: null,
        deptPath: null,
        systemUserId: [],
        infoStatus: null,
        createDate: null,
        lastUpdatedDate: null
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
      this.title = "添加短信记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data;
        // this.form.systemUserId = this.form.systemUserId?.split(",") ?? [];
        this.form.sfazjcsb = this.form.sfazjcsb?.split(",") ?? [];
        this.open = true;
        this.title = "修改短信记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.systemUserId = this.form.systemUserId.join(",");
          if (this.form.id != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除短信记录编号为"' + ids + '"的数据项？').then(function () {
        return delInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
