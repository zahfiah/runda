<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="单位昵称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入单位昵称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="监控类别" prop="monitorCategory">
        <el-select v-model="queryParams.monitorCategory" placeholder="请选择监控类别" clearable>
          <el-option v-for="dict in dict.type.monitoring_way" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['runda:list:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['runda:list:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['runda:list:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:list:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="listList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="单位昵称" align="center" prop="name" />
      <el-table-column label="单位地址" align="center" prop="addr" />
      <el-table-column label="单位电话" align="center" prop="phone" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改时间" align="center" prop="lastUpdatedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastUpdatedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="诱发事故物质" align="center" prop="polluteGoods" />
      <el-table-column label="热源" align="center" prop="heatSource" />
      <el-table-column label="水污染" align="center" prop="polluteWater" />
      <el-table-column label="空气污染" align="center" prop="polluteAir" />
      <el-table-column label="固废" align="center" prop="polluteSolid" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:list:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:list:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改单位名录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="单位昵称" prop="name">
          <el-input v-model="form.name" placeholder="请输入单位昵称" />
        </el-form-item>
        <el-form-item label="监控类别" prop="monitorCategory">
          <el-select v-model="form.monitorCategory" placeholder="请选择监控类别">
            <el-option v-for="dict in dict.type.monitoring_way" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位地址" prop="addr">
          <el-input v-model="form.addr" placeholder="请输入单位地址" />
        </el-form-item>
        <el-form-item label="单位电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入单位电话" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="修改时间" prop="lastUpdatedTime">
          <el-date-picker clearable v-model="form.lastUpdatedTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择修改时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="操作员id" prop="systemUserId">
          <el-checkbox-group v-model="form.systemUserId">
            <el-checkbox v-for="dict in dict.type.tell_user" :key="dict.value" :label="dict.value">
              {{ dict.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="站点编号" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点编号" />
        </el-form-item>
        <el-form-item label="诱发事故物质" prop="polluteGoods">
          <el-input v-model="form.polluteGoods" placeholder="请输入诱发事故物质" />
        </el-form-item>
        <el-form-item label="热源" prop="heatSource">
          <el-input v-model="form.heatSource" placeholder="请输入热源" />
        </el-form-item>
        <el-form-item label="水污染" prop="polluteWater">
          <el-input v-model="form.polluteWater" placeholder="请输入水污染" />
        </el-form-item>
        <el-form-item label="空气污染" prop="polluteAir">
          <el-input v-model="form.polluteAir" placeholder="请输入空气污染" />
        </el-form-item>
        <el-form-item label="固废" prop="polluteSolid">
          <el-input v-model="form.polluteSolid" placeholder="请输入固废" />
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
import { listList, getList, delList, addList, updateList } from "@/api/runda/list";

export default {
  name: "List",
  dicts: ['monitoring_way', 'tell_user', 'log_status'],
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
      // 单位名录表格数据
      listList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        monitorCategory: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "单位昵称不能为空", trigger: "blur" }
        ],
        addr: [
          { required: true, message: "单位地址不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "单位电话不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        lastUpdatedTime: [
          { required: true, message: "修改时间不能为空", trigger: "blur" }
        ],
        stationId: [
          { required: true, message: "站点编号不能为空", trigger: "blur" }
        ],
        polluteGoods: [
          { required: true, message: "诱发事故物质不能为空", trigger: "blur" }
        ],
        heatSource: [
          { required: true, message: "热源不能为空", trigger: "blur" }
        ],
        polluteWater: [
          { required: true, message: "水污染不能为空", trigger: "blur" }
        ],
        polluteAir: [
          { required: true, message: "空气污染不能为空", trigger: "blur" }
        ],
        polluteSolid: [
          { required: true, message: "固废不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询单位名录列表 */
    getList() {
      this.loading = true;
      listList(this.queryParams).then(response => {
        this.listList = response.rows;
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
        polluteUnitCategory: null,
        polluteCategory: null,
        monitorCategory: null,
        addr: null,
        phone: null,
        remark: null,
        province: null,
        city: null,
        county: null,
        createTime: null,
        lastUpdatedTime: null,
        departmentId: null,
        createUser: null,
        systemUserId: [],
        status: null,
        stationId: null,
        polluteGoods: null,
        heatSource: null,
        polluteWater: null,
        polluteAir: null,
        polluteSolid: null
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
      this.title = "添加单位名录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getList(id).then(response => {
        this.form = response.data;
        this.form.sfazjcsb = this.form.sfazjcsb?.split(",") ?? [];
        this.open = true;
        this.title = "修改单位名录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.systemUserId = this.form.systemUserId.join(",");
          if (this.form.id != null) {
            updateList(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addList(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除单位名录编号为"' + ids + '"的数据项？').then(function () {
        return delList(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/list/export', {
        ...this.queryParams
      }, `list_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
