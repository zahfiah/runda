<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="设备名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入设备名称"
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
      <!--      <el-form-item label="省昵称" prop="provinceCn">-->
      <!--        <el-input-->
      <!--          v-model="queryParams.provinceCn"-->
      <!--          placeholder="请输入省昵称"-->
      <!--          clearable-->
      <!--          @keyup.enter.native="handleQuery"-->
      <!--        />-->
      <!--      </el-form-item>-->
      <el-form-item label="县名称" prop="countyCn">
        <el-input
          v-model="queryParams.countyCn"
          placeholder="请输入县名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!--      <el-form-item label="乡/镇昵称" prop="townCn">-->
      <!--        <el-input-->
      <!--          v-model="queryParams.townCn"-->
      <!--          placeholder="请输入乡/镇昵称"-->
      <!--          clearable-->
      <!--          @keyup.enter.native="handleQuery"-->
      <!--        />-->
      <!--      </el-form-item>-->
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
          v-hasPermi="['runda:device:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['runda:device:edit']"
          >编辑</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['runda:device:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['runda:device:export']"
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
      :data="deviceList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键id" align="center" prop="id" /> -->
      <el-table-column label="设备名称" align="center" prop="name" />
      <el-table-column label="设备号" align="center" prop="sn" />
      <el-table-column label="站点" align="center" prop="stationId" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="createdTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.device_status"
            :value="scope.row.status"
          />
        </template>
      </el-table-column>
      <el-table-column label="工地状态" align="center" prop="buildStatus">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.build_status"
            :value="scope.row.buildStatus"
          />
        </template>
      </el-table-column>
      <el-table-column label="SIM卡号" align="center" prop="phoneNumber" />
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
            v-hasPermi="['runda:device:edit']"
            >编辑</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['runda:device:remove']"
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

    <!-- 添加或修改监测设备管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备号" prop="sn">
          <el-input v-model="form.sn" placeholder="请输入设备号" />
        </el-form-item>
        <el-form-item label="所属站点" prop="stationId">
          <!-- <el-input v-model="form.stationId" placeholder="请输入站点id" /> -->
          <el-select v-model="form.stationId" placeholder="请输入站点id">
            <el-option
              v-for="item in stationOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择设备状态">
            <el-option
              v-for="dict in dict.type.device_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工地状态" prop="buildStatus">
          <el-select v-model="form.buildStatus" placeholder="请选择工地状态">
            <el-option
              v-for="dict in dict.type.build_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="省" prop="provinceCn">
          <el-select v-model="form.provinceCn" placeholder="请输入省名称">
            <el-option
              v-for="item in provinces"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="市" prop="cityCn">
          <el-select v-model="form.cityCn" placeholder="请输入市昵称">
            <el-option
              v-for="item in citys"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区" prop="countyCn">
          <el-select v-model="form.countyCn" placeholder="请输入区/县昵称">
            <el-option
              v-for="item in countys"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="乡镇" prop="townCn">
          <el-select v-model="form.townCn" placeholder="请输入乡/镇昵称">
            <el-option
              v-for="item in towns"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="SIM卡号" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="请输入SIM卡号" />
        </el-form-item>
        <el-form-item label="制造商" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option
              v-for="dict in dict.type.device_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标准站" prop="isStandard">
          <el-select v-model="form.isStandard" placeholder="请选择标准站">
            <el-option
              v-for="dict in dict.type.standard"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
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
  listDevice,
  getDevice,
  delDevice,
  addDevice,
  updateDevice,
} from "@/api/runda/device";

export default {
  name: "Device",
  dicts: [
    "device_status",
    "from_resource",
    "standard",
    "device_type",
    "build_status",
    "province",
    "city",
    "county",
    "town",
  ],
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
      // 监测设备管理表格数据
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
        provinceCn: null,
        countyCn: null,
        townCn: null,
      },
      provinces: [
        { value: 1, label: "北京市" },
        { value: 2, label: "天津市" },
        { value: 3, label: "河北省" },
        { value: 4, label: "山西省" },
        { value: 5, label: "内蒙古自治区" },
        { value: 6, label: "辽宁省" },
        { value: 7, label: "吉林省" },
        { value: 8, label: "黑龙江省" },
        { value: 9, label: "上海市" },
        { value: 10, label: "江苏省" },
        { value: 11, label: "浙江省" },
        { value: 12, label: "安徽省" },
        { value: 13, label: "福建省" },
        { value: 14, label: "江西省" },
        { value: 15, label: "河南省" },
        { value: 16, label: "湖北省" },
        { value: 17, label: "湖南省" },
        { value: 18, label: "广东省" },
        { value: 19, label: "广西壮族自治区" },
        { value: 20, label: "海南省" },
        { value: 21, label: "重庆市" },
        { value: 22, label: "四川省" },
        { value: 23, label: "贵州省" },
        { value: 24, label: "云南省" },
        { value: 25, label: "西藏自治区" },
        { value: 26, label: "陕西省" },
        { value: 27, label: "甘肃省" },
        { value: 28, label: "青海省" },
        { value: 29, label: "宁夏回族自治区" },
        { value: 30, label: "新疆维吾尔自治区" },
        { value: 31, label: "台湾省" },
        { value: 32, label: "香港特别行政区" },
        { value: 33, label: "澳门特别行政区" },
        // 根据需要添加更多站点
      ],
      citys: [
        { value: 1, label: "石家庄市" },
        { value: 2, label: "唐山市" },
        { value: 3, label: "秦皇岛市" },
        { value: 4, label: "邯郸市" },
        { value: 5, label: "邢台市" },
        { value: 6, label: "保定市" },
        { value: 7, label: "张家口市" },
        { value: 8, label: "承德市" },
        { value: 9, label: "沧州市" },
        { value: 10, label: "廊坊市" },
        { value: 11, label: "衡水市" },
      ],
      countys: [
        { value: 1, label: "桥东区" },
        { value: 2, label: "桥西区" },
        { value: 3, label: "宣化区" },
        { value: 4, label: "下花园区" },
        { value: 5, label: "万全区" },
        { value: 6, label: "崇礼县" },
        { value: 7, label: "张北县" },
        { value: 8, label: "康保县" },
        { value: 9, label: "沽源县" },
        { value: 10, label: "尚义县" },
        { value: 11, label: "蔚县" },
        { value: 12, label: "阳原县" },
        { value: 13, label: "怀安县" },
        { value: 14, label: "怀来县" },
        { value: 15, label: "涿鹿县" },
        { value: 16, label: "赤城县" },
        { value: 17, label: "经开区" },
        { value: 18, label: "察北管理区" },
        { value: 19, label: "塞北管理" },
      ],
      towns: [
        { value: 1, label: "张北镇" },
        { value: 2, label: "公会镇" },
        { value: 3, label: "二台镇" },
        { value: 4, label: "大囫囵镇" },
        { value: 5, label: "小二台镇" },
        { value: 6, label: "沙沟镇" },
        { value: 7, label: "台路沟乡" },
        { value: 8, label: "油篓沟乡" },
        { value: 9, label: "馒头营乡" },
        { value: 10, label: "二泉井乡" },
        { value: 11, label: "单晶河乡" },
        { value: 12, label: "大河乡" },
        { value: 13, label: "海流图乡" },
        { value: 14, label: "两面井乡" },
        { value: 15, label: "大西湾乡" },
        { value: 16, label: "郝家营乡" },
        { value: 17, label: "白庙滩乡" },
        { value: 18, label: "战海乡" },
        { value: 19, label: "三号乡" },
        { value: 20, label: "宇宙营乡" },
        { value: 21, label: "察北" },
      ],

      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "设备名称不能为空", trigger: "blur" },
        ],
        sn: [{ required: true, message: "设备号不能为空", trigger: "blur" }],
        longitude: [
          { required: true, message: "经度不能为空", trigger: "blur" },
        ],
        latitude: [
          { required: true, message: "纬度不能为空", trigger: "blur" },
        ],
        lastUpdatedTime: [
          { required: true, message: "最后修改时间不能为空", trigger: "blur" },
        ],
        status: [
          { required: true, message: "设备状态不能为空", trigger: "change" },
        ],
        buildStatus: [
          { required: true, message: "工地状态不能为空", trigger: "change" },
        ],
        provinceCn: [
          { required: true, message: "省昵称不能为空", trigger: "blur" },
        ],
        cityCn: [
          { required: true, message: "市昵称不能为空", trigger: "blur" },
        ],
        countyCn: [
          { required: true, message: "区/县昵称不能为空", trigger: "blur" },
        ],
        townCn: [
          { required: true, message: "乡/镇昵称不能为空", trigger: "blur" },
        ],
        remark: [{ required: true, message: "备注不能为空", trigger: "blur" }],
        phoneNumber: [
          { required: true, message: "SIM卡号不能为空", trigger: "blur" },
        ],
        manufacturer: [
          { required: true, message: "制造商不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "设备类型不能为空", trigger: "change" },
        ],
        isStandard: [
          { required: true, message: "标准站不能为空", trigger: "change" },
        ],
        stationId: [
          { required: true, message: "站点id不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询监测设备管理列表 */
    getList() {
      this.loading = true;
      listDevice(this.queryParams).then((response) => {
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
        addr: null,
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
      this.title = "添加监测设备管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getDevice(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改监测设备管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateDevice(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDevice(this.form).then((response) => {
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
        .confirm('是否确认删除监测设备管理编号为"' + ids + '"的数据项？')
        .then(function () {
          return delDevice(ids);
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
        "runda/device/export",
        {
          ...this.queryParams,
        },
        `device_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>
