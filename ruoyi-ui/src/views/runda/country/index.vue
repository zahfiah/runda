<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="站点id" prop="stationId">
        <el-input
          v-model="queryParams.stationId"
          placeholder="请输入站点id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="站点名称" prop="stationName">
        <el-input
          v-model="queryParams.stationName"
          placeholder="请输入站点名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker clearable
          v-model="queryParams.date"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
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
          v-hasPermi="['runda:country:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['runda:country:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['runda:country:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['runda:country:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="countryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键id" align="center" prop="id" />
      <el-table-column label="站点id" align="center" prop="stationId" />
      <el-table-column label="站点名称" align="center" prop="stationName" />
      <el-table-column label="日期" align="center" prop="date" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.date, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="空气质量指数" align="center" prop="aqi" />
      <el-table-column label="pm2.5浓度" align="center" prop="pm" />
      <el-table-column label="pm10浓度" align="center" prop="pm10" />
      <el-table-column label="so2浓度" align="center" prop="so2Thickness" />
      <el-table-column label="no2浓度" align="center" prop="no2Thickness" />
      <el-table-column label="co浓度" align="center" prop="coThickness" />
      <el-table-column label="o3 浓度" align="center" prop="co3Thickness" />
      <el-table-column label="vocs浓度" align="center" prop="voscThickness" />
      <el-table-column label="设备_id" align="center" prop="deviceId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:country:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['runda:country:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改国控数据查询对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="站点id" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点id" />
        </el-form-item>
        <el-form-item label="站点名称" prop="stationName">
          <el-input v-model="form.stationName" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker clearable
            v-model="form.date"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="空气质量指数" prop="aqi">
          <el-input v-model="form.aqi" placeholder="请输入空气质量指数" />
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
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item label="首要污染物" prop="primaryPollutant">
          <el-input v-model="form.primaryPollutant" placeholder="请输入首要污染物" />
        </el-form-item>
        <el-form-item label="设备_id" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备_id" />
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
import { listCountry, getCountry, delCountry, addCountry, updateCountry } from "@/api/runda/country";

export default {
  name: "Country",
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
      // 国控数据查询表格数据
      countryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        stationId: null,
        stationName: null,
        date: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        stationId: [
          { required: true, message: "站点id不能为空", trigger: "blur" }
        ],
        stationName: [
          { required: true, message: "站点名称不能为空", trigger: "blur" }
        ],
        date: [
          { required: true, message: "日期不能为空", trigger: "blur" }
        ],
        aqi: [
          { required: true, message: "空气质量指数不能为空", trigger: "blur" }
        ],
        pm: [
          { required: true, message: "pm2.5浓度不能为空", trigger: "blur" }
        ],
        pm10: [
          { required: true, message: "pm10浓度不能为空", trigger: "blur" }
        ],
        so2Thickness: [
          { required: true, message: "so2浓度不能为空", trigger: "blur" }
        ],
        no2Thickness: [
          { required: true, message: "no2浓度不能为空", trigger: "blur" }
        ],
        coThickness: [
          { required: true, message: "co浓度不能为空", trigger: "blur" }
        ],
        co3Thickness: [
          { required: true, message: "o3 浓度不能为空", trigger: "blur" }
        ],
        voscThickness: [
          { required: true, message: "vocs浓度不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询国控数据查询列表 */
    getList() {
      this.loading = true;
      listCountry(this.queryParams).then(response => {
        this.countryList = response.rows;
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
        stationId: null,
        stationName: null,
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
        pm10: null,
        pm1Above: null,
        pm25Above: null,
        pm5Above: null,
        pm10Above: null,
        longitude: null,
        latitude: null,
        tsp: null,
        primaryPollutant: null,
        deviceId: null,
        sn: null
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加国控数据查询";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCountry(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改国控数据查询";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCountry(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCountry(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除国控数据查询编号为"' + ids + '"的数据项？').then(function() {
        return delCountry(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/country/export', {
        ...this.queryParams
      }, `country_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
