<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备名称(测站名称)" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入设备名称(测站名称)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备号" prop="sn">
        <el-input
          v-model="queryParams.sn"
          placeholder="请输入设备号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input
          v-model="queryParams.longitude"
          placeholder="请输入经度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="纬度" prop="latitude">
        <el-input
          v-model="queryParams.latitude"
          placeholder="请输入纬度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createdTime">
        <el-date-picker clearable
          v-model="queryParams.createdTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="最后修改时间" prop="lastUpdatedTime">
        <el-date-picker clearable
          v-model="queryParams.lastUpdatedTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后修改时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="机构ID" prop="orgId">
        <el-input
          v-model="queryParams.orgId"
          placeholder="请输入机构ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省" prop="province">
        <el-input
          v-model="queryParams.province"
          placeholder="请输入省"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省昵称" prop="provinceCn">
        <el-input
          v-model="queryParams.provinceCn"
          placeholder="请输入省昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="市" prop="city">
        <el-input
          v-model="queryParams.city"
          placeholder="请输入市"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="市昵称" prop="cityCn">
        <el-input
          v-model="queryParams.cityCn"
          placeholder="请输入市昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区/县" prop="county">
        <el-input
          v-model="queryParams.county"
          placeholder="请输入区/县"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区/县昵称" prop="countyCn">
        <el-input
          v-model="queryParams.countyCn"
          placeholder="请输入区/县昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乡/镇" prop="town">
        <el-input
          v-model="queryParams.town"
          placeholder="请输入乡/镇"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="乡/镇昵称" prop="townCn">
        <el-input
          v-model="queryParams.townCn"
          placeholder="请输入乡/镇昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="SIM卡号" prop="phoneNumber">
        <el-input
          v-model="queryParams.phoneNumber"
          placeholder="请输入SIM卡号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="制造商" prop="manufacturer">
        <el-input
          v-model="queryParams.manufacturer"
          placeholder="请输入制造商"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="departmentId">
        <el-input
          v-model="queryParams.departmentId"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作员" prop="systemUserId">
        <el-input
          v-model="queryParams.systemUserId"
          placeholder="请输入操作员"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="fromResource">
        <el-input
          v-model="queryParams.fromResource"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="port">
        <el-input
          v-model="queryParams.port"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="password">
        <el-input
          v-model="queryParams.password"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否是标准站0.不是、1.是、" prop="isStandard">
        <el-input
          v-model="queryParams.isStandard"
          placeholder="请输入是否是标准站0.不是、1.是、"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="国控战站点code" prop="guid">
        <el-input
          v-model="queryParams.guid"
          placeholder="请输入国控战站点code"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="站点id" prop="stationId">
        <el-input
          v-model="queryParams.stationId"
          placeholder="请输入站点id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="addr">
        <el-input
          v-model="queryParams.addr"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['runda:jianceshebei:add']"
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
          v-hasPermi="['runda:jianceshebei:edit']"
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
          v-hasPermi="['runda:jianceshebei:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['runda:jianceshebei:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jianceshebeiList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="设备名称(测站名称)" align="center" prop="name" />
      <el-table-column label="设备号" align="center" prop="sn" />
      <el-table-column label="经度" align="center" prop="longitude" />
      <el-table-column label="纬度" align="center" prop="latitude" />
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后修改时间" align="center" prop="lastUpdatedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastUpdatedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态(1：正常，2：停用，3：删除)" align="center" prop="status" />
      <el-table-column label="机构ID" align="center" prop="orgId" />
      <el-table-column label="运行状态(1：运行正常，2：异常)" align="center" prop="runStatus" />
      <el-table-column label="省" align="center" prop="province" />
      <el-table-column label="省昵称" align="center" prop="provinceCn" />
      <el-table-column label="市" align="center" prop="city" />
      <el-table-column label="市昵称" align="center" prop="cityCn" />
      <el-table-column label="区/县" align="center" prop="county" />
      <el-table-column label="区/县昵称" align="center" prop="countyCn" />
      <el-table-column label="乡/镇" align="center" prop="town" />
      <el-table-column label="乡/镇昵称" align="center" prop="townCn" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="SIM卡号" align="center" prop="phoneNumber" />
      <el-table-column label="制造商" align="center" prop="manufacturer" />
      <el-table-column label="${comment}" align="center" prop="departmentId" />
      <el-table-column label="操作员" align="center" prop="systemUserId" />
      <el-table-column label="${comment}" align="center" prop="fromResource" />
      <el-table-column label="设备类型(1：空气检测设备，2：水质检测设备)" align="center" prop="type" />
      <el-table-column label="${comment}" align="center" prop="ip" />
      <el-table-column label="${comment}" align="center" prop="port" />
      <el-table-column label="${comment}" align="center" prop="userName" />
      <el-table-column label="${comment}" align="center" prop="password" />
      <el-table-column label="是否是标准站0.不是、1.是、" align="center" prop="isStandard" />
      <el-table-column label="国控战站点code" align="center" prop="guid" />
      <el-table-column label="站点id" align="center" prop="stationId" />
      <el-table-column label="${comment}" align="center" prop="addr" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:jianceshebei:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['runda:jianceshebei:remove']"
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

    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备名称(测站名称)" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称(测站名称)" />
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
          <el-date-picker clearable
            v-model="form.createdTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="最后修改时间" prop="lastUpdatedTime">
          <el-date-picker clearable
            v-model="form.lastUpdatedTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择最后修改时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="机构ID" prop="orgId">
          <el-input v-model="form.orgId" placeholder="请输入机构ID" />
        </el-form-item>
        <el-form-item label="省" prop="province">
          <el-input v-model="form.province" placeholder="请输入省" />
        </el-form-item>
        <el-form-item label="省昵称" prop="provinceCn">
          <el-input v-model="form.provinceCn" placeholder="请输入省昵称" />
        </el-form-item>
        <el-form-item label="市" prop="city">
          <el-input v-model="form.city" placeholder="请输入市" />
        </el-form-item>
        <el-form-item label="市昵称" prop="cityCn">
          <el-input v-model="form.cityCn" placeholder="请输入市昵称" />
        </el-form-item>
        <el-form-item label="区/县" prop="county">
          <el-input v-model="form.county" placeholder="请输入区/县" />
        </el-form-item>
        <el-form-item label="区/县昵称" prop="countyCn">
          <el-input v-model="form.countyCn" placeholder="请输入区/县昵称" />
        </el-form-item>
        <el-form-item label="乡/镇" prop="town">
          <el-input v-model="form.town" placeholder="请输入乡/镇" />
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
        <el-form-item label="${comment}" prop="departmentId">
          <el-input v-model="form.departmentId" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="操作员" prop="systemUserId">
          <el-input v-model="form.systemUserId" placeholder="请输入操作员" />
        </el-form-item>
        <el-form-item label="${comment}" prop="fromResource">
          <el-input v-model="form.fromResource" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="port">
          <el-input v-model="form.port" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="password">
          <el-input v-model="form.password" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="是否是标准站0.不是、1.是、" prop="isStandard">
          <el-input v-model="form.isStandard" placeholder="请输入是否是标准站0.不是、1.是、" />
        </el-form-item>
        <el-form-item label="国控战站点code" prop="guid">
          <el-input v-model="form.guid" placeholder="请输入国控战站点code" />
        </el-form-item>
        <el-form-item label="站点id" prop="stationId">
          <el-input v-model="form.stationId" placeholder="请输入站点id" />
        </el-form-item>
        <el-form-item label="${comment}" prop="addr">
          <el-input v-model="form.addr" placeholder="请输入${comment}" />
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
import { listJianceshebei, getJianceshebei, delJianceshebei, addJianceshebei, updateJianceshebei } from "@/api/runda/jianceshebei";

export default {
  name: "Jianceshebei",
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
      // 设备表格数据
      jianceshebeiList: [],
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
        longitude: null,
        latitude: null,
        createdTime: null,
        lastUpdatedTime: null,
        status: null,
        orgId: null,
        runStatus: null,
        province: null,
        provinceCn: null,
        city: null,
        cityCn: null,
        county: null,
        countyCn: null,
        town: null,
        townCn: null,
        phoneNumber: null,
        manufacturer: null,
        departmentId: null,
        systemUserId: null,
        fromResource: null,
        type: null,
        ip: null,
        port: null,
        userName: null,
        password: null,
        isStandard: null,
        guid: null,
        stationId: null,
        addr: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        createdTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        lastUpdatedTime: [
          { required: true, message: "最后修改时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备列表 */
    getList() {
      this.loading = true;
      listJianceshebei(this.queryParams).then(response => {
        this.jianceshebeiList = response.rows;
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
        runStatus: null,
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
        systemUserId: null,
        fromResource: null,
        type: null,
        ip: null,
        port: null,
        userName: null,
        password: null,
        isStandard: null,
        guid: null,
        stationId: null,
        addr: null
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
      this.title = "添加设备";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getJianceshebei(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateJianceshebei(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addJianceshebei(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除设备编号为"' + ids + '"的数据项？').then(function() {
        return delJianceshebei(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('runda/jianceshebei/export', {
        ...this.queryParams
      }, `jianceshebei_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
