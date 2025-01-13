<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工地名称" prop="siteName">
        <el-input v-model="queryParams.siteName" placeholder="请输入工地名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="运维时间" prop="maintenanceTime">
        <el-date-picker clearable v-model="queryParams.maintenanceTime" type="date" value-format="yyyy-MM-dd"
          placeholder="请选择运维时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['runda:yunwei:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['runda:yunwei:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['runda:yunwei:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:yunwei:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="yunweiList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="工地名称" align="center" prop="siteName" />
      <el-table-column label="设备号" align="center" prop="sn" />
      <el-table-column label="运维时间" align="center" prop="maintenanceTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.maintenanceTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否完成" align="center" prop="isFinsh">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.is_complete" :value="scope.row.isFinsh" />
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center" prop="img" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.img" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="日志信息" align="center" prop="log" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:yunwei:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:yunwei:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改运维日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工地名称" prop="siteName">
          <el-input v-model="form.siteName" placeholder="请输入工地名称" />
        </el-form-item>
        <el-form-item label="设备号" prop="sn">
          <el-input v-model="form.sn" placeholder="请输入设备号" />
        </el-form-item>
        <el-form-item label="运维时间" prop="maintenanceTime">
          <el-date-picker clearable v-model="form.maintenanceTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择运维时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否完成" prop="isFinsh">
          <el-radio-group v-model="form.isFinsh">
            <el-radio v-for="dict in dict.type.is_complete" :key="dict.value" :label="parseInt(dict.value)">{{
              dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片" prop="img">
          <image-upload v-model="form.img" />
        </el-form-item>
        <el-form-item label="日志信息" prop="log">
          <el-input v-model="form.log" placeholder="请输入日志信息" />
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
import { listYunwei, getYunwei, delYunwei, addYunwei, updateYunwei } from "@/api/runda/yunwei";
import { listDevice, getDevice, delDevice, addDevice, updateDevice } from "@/api/runda/device";
export default {
  name: "Yunwei",
  dicts: ['is_complete'],
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
      // 运维日志表格数据
      yunweiList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        siteName: null,
        maintenanceTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        siteName: [
          { required: true, message: "工地名称不能为空", trigger: "blur" }
        ],
        sn: [
          { required: true, message: "设备号不能为空", trigger: "blur" }
        ],
        maintenanceTime: [
          { required: true, message: "运维时间不能为空", trigger: "blur" }
        ],
        isFinsh: [
          { required: true, message: "是否完成不能为空", trigger: "change" }
        ],
        log: [
          { required: true, message: "日志信息不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询运维日志列表 */
    getList() {
      this.loading = true;
      listYunwei(this.queryParams).then(response => {
        this.yunweiList = response.rows;
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
        siteName: null,
        sn: null,
        maintenanceTime: null,
        isFinsh: null,
        img: null,
        log: null
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
      this.title = "添加运维日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getYunwei(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改运维日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateYunwei(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addYunwei(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除运维日志编号为"' + ids + '"的数据项？').then(function () {
        return delYunwei(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/yunwei/export', {
        ...this.queryParams
      }, `yunwei_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
