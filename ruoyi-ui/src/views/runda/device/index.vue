<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="设备名称" prop="name">
        <el-select v-model="queryParams.name" placeholder="请选择设备">
          <el-option
            v-for="device in deviceList"
            :key="device.id"
            :label="device.name"
            :value="device.name"
          ></el-option>
        </el-select>
      </el-form-item> -->
      <!-- <el-form-item label="设备名称" prop="name">
        <el-select
          v-model="queryParams.name"
          placeholder="请选择设备"
          clearable
          @keyup.enter.native="handleQuery"
        >
        
          <el-option
            v-for="station in deviceList"
            :key="station.id"
            :label="station.name"
            :value="station.id"
          ></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="设备号" prop="sn">
        <el-input v-model="queryParams.sn" placeholder="请输入设备号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="站点名称" prop="stationId">
        <el-select v-model="queryParams.stationId" placeholder="请选择站点">
          <!-- <el-select v-model="form.stationId" placeholder="请选择站点"> -->
          <el-option v-for="station in stationList" :key="station.id" :label="station.stationName"
            :value="station.id"></el-option>
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
          v-hasPermi="['runda:device:add']">新增</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['runda:device:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />

      <el-table-column label="设备号" align="center" prop="sn" />
      <el-table-column label="设备名称" align="center" prop="name" />
      <!-- <el-table-column label="站点" align="center" prop="stationId" /> -->
      <el-table-column label="站点名称" align="center" prop="stationName">
        <template slot-scope="scope">
          <span v-if="scope.row.stationName">{{ scope.row.stationName }}</span>
          <span v-else>未命名站点</span>
        </template>

        <template slot-scope="scope">
          <span>{{ scope.row.stationName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_status" :value="scope.row.status" v-if="scope.row.status !== 7" />
          <span v-else>未知</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="是否运维" align="center" prop="isYunwei">
        <template slot-scope="scope">
          <dict-tag
            :options="dict.type.is_yunwei"
            :value="scope.row.isYunwei"
          />
        </template>
      </el-table-column> -->
      <el-table-column label="是否运维" align="center" prop="isYunwei">
        <template slot-scope="scope">
          <span v-if="scope.row.isYunwei === 0">否</span>
          <span v-else>是</span>
        </template>
      </el-table-column>
      <el-table-column label="SIM卡号" align="center" prop="phoneNumber" />

      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createdTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['runda:device:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['runda:device:remove']">删除</el-button>
          <el-button v-if="scope.row.isYunwei === 1" size="mini" type="text" style="color: #f56c6c"
            icon="el-icon-setting" @click="handleMaintenance(scope.row)"
            v-hasPermi="['runda:device:maintenance']">运维</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 添加或修改监测设备管理设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="deviceForm" :model="deviceForm" :rules="rules" label-width="80px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="deviceForm.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备号" prop="sn">
          <el-input v-model="deviceForm.sn" placeholder="请输入设备号" />
        </el-form-item>
        <el-form-item label="制造商" prop="manufacturer">
          <el-input v-model="deviceForm.manufacturer" placeholder="请输入制造商" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="deviceForm.longitude" placeholder="请输入经度" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="deviceForm.latitude" placeholder="请输入纬度" />
        </el-form-item>

        <el-form-item label="站点名称" prop="stationId">
          <!-- <el-select v-model="queryParams.stationId" placeholder="请选择站点"> -->
          <el-select v-model="deviceForm.stationId" placeholder="请选择站点">
            <el-option v-for="station in stationList" :key="station.id" :label="station.stationName"
              :value="station.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="deviceForm.status" placeholder="请选择设备状态">
            <el-option v-for="dict in dict.type.device_status" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="省" prop="provinceCn">
          <el-input v-model="deviceForm.provinceCn" placeholder="河北省" disabled />
        </el-form-item>
        <el-form-item label="市" prop="cityCn">
          <el-input v-model="deviceForm.cityCn" placeholder="张家口市" disabled />
        </el-form-item>
        <el-form-item label="区" prop="countyCn">
          <el-select v-model="deviceForm.countyCn" placeholder="请输入区/县昵称" @change="handleCountyChange">
            <el-option v-for="item in countys" :key="item.value" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="乡镇" prop="townCn">
          <el-select v-model="deviceForm.townCn" placeholder="请选择乡/镇" :disabled="!deviceForm.countyCn">
            <el-option v-for="item in currentTowns" :key="item.value" :label="item.label" :value="item.label" />
          </el-select>
        </el-form-item>
        <el-form-item label="SIM卡号" prop="phoneNumber">
          <el-input v-model="deviceForm.phoneNumber" placeholder="请输入SIM卡号" />
        </el-form-item>

        <el-form-item label="设备类型" prop="type">
          <el-select v-model="deviceForm.type" placeholder="请选择设备类型">
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="是否运维" prop="isYunwei">
          <el-radio-group v-model="deviceForm.isYunwei">
            <el-radio v-for="dict in dict.type.is_yunwei" :key="dict.value" :label="parseInt(dict.value)">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="deviceForm.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 添加运维日志 -->
    <el-dialog :title="title" :visible.sync="openYunwei" width="500px" append-to-body>
      <el-form ref="yunweiForm" :model="yunweiForm" :rules="rules" label-width="80px">
        <el-form-item label="设备名称" prop="siteName">
          <el-input v-model="yunweiForm.siteName" placeholder="请输入设备名称" />
        </el-form-item>

        <el-form-item label="设备号" prop="sn">
          <el-input v-model="yunweiForm.sn" placeholder="请输入设备号" />
        </el-form-item>
        <el-form-item label="运维时间" prop="maintenanceTime">
          <el-date-picker clearable v-model="yunweiForm.maintenanceTime" type="date" value-format="yyyy-MM-dd"
            placeholder="请选择运维时间">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="是否完成" prop="isFinsh">
          <el-radio-group v-model="yunweiForm.isFinsh">
            <el-radio v-for="dict in dict.type.is_complete" :key="dict.value" :label="parseInt(dict.value)">{{
              dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图片" prop="img">
          <image-upload v-model="yunweiForm.img" />
        </el-form-item>
        <el-form-item label="日志信息" prop="log">
          <el-input v-model="yunweiForm.log" placeholder="请输入日志信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitYunweiForm">确 定</el-button>
        <el-button @click="cancelYunwei">取 消</el-button>
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
import { addYunwei, updateYunwei } from "@/api/runda/yunwei";
import { listStation } from "@/api/runda/station";
export default {
  name: "Device",
  dicts: [
    "device_status",
    "is_yunwei",
    "from_resource",
    "tell_user",
    "device_type",
    "build_status",
    "is_complete",
    "standard",
  ],
  data() {
    return {
      // 遮罩层
      isMaintenanceFinished: false, // 新增
      selectedCounty: null, // 新增
      selectedTown: null, // 新增
      openYunwei: false,
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
        provinceCn: null,
        countyCn: null,
        townCn: null,
      },
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
        // ... 其他区县
      ],
      // 将乡镇数据按区县ID分组
      townMap: {
        1: [
          // 红旗楼街道
          { value: "1", label: "红旗楼街道" },
          { value: "2", label: "胜利北路街道" },
          { value: "3", label: "五一路街道" },
          { value: "4", label: "花园街街道" },
          { value: "5", label: "工业路街道" },
          { value: "6", label: "南站街道" },
          { value: "7", label: "马路东街道" },
          { value: "8", label: "老鸦庄镇" },
          { value: "9", label: "姚家庄镇" },
        ],
        2: [
          // 新华街街道
          { value: "10", label: "新华街街道" },
          { value: "11", label: "大境门街道" },
          { value: "12", label: "明德北街街道" },
          { value: "13", label: "明德南街街道" },
          { value: "14", label: "堡子里街道" },
          { value: "15", label: "南营坊街道" },
          { value: "16", label: "工人新村街道" },
          { value: "17", label: "东窑子镇" },
          { value: "18", label: "沈家屯镇" },
        ],
        3: [
          // 天泰寺街道
          { value: "19", label: "天泰寺街道" },
          { value: "20", label: "皇城街道" },
          { value: "21", label: "南关街道" },
          { value: "22", label: "南大街街道" },
          { value: "23", label: "大北街街道" },
          { value: "24", label: "工业街街道" },
          { value: "25", label: "建国街街道" },
          { value: "26", label: "庞家堡镇" },
          { value: "27", label: "河子西乡" },
          { value: "28", label: "春光乡" },
          { value: "29", label: "侯家庙乡" },
          { value: "30", label: "洋河南镇" },
          { value: "31", label: "深井镇" },
          { value: "32", label: "崞村镇" },
          { value: "33", label: "沙岭子镇" },
          { value: "34", label: "姚家房镇" },
          { value: "35", label: "大仓盖镇" },
          { value: "36", label: "贾家营镇" },
          { value: "37", label: "顾家营镇" },
          { value: "38", label: "赵川镇" },
          { value: "39", label: "王家湾乡" },
          { value: "40", label: "塔儿村乡" },
          { value: "41", label: "江家屯乡" },
          { value: "42", label: "东望山乡" },
          { value: "43", label: "李家堡乡" },
        ],
        4: [
          // 城镇街道
          { value: "44", label: "城镇街道" },
          { value: "45", label: "煤矿街道" },
          { value: "46", label: "花园乡" },
          { value: "47", label: "辛庄子乡" },
          { value: "48", label: "定方水乡" },
          { value: "49", label: "段家堡乡" },
        ],
        5: [
          // 孔家庄镇
          { value: "50", label: "孔家庄镇" },
          { value: "51", label: "万全镇" },
          { value: "52", label: "洗马林镇" },
          { value: "53", label: "郭磊庄镇" },
          { value: "54", label: "膳房堡乡" },
          { value: "55", label: "北新屯乡" },
          { value: "56", label: "宣平堡乡" },
          { value: "57", label: "高庙堡乡" },
          { value: "58", label: "旧堡乡" },
          { value: "59", label: "安家堡乡" },
          { value: "60", label: "北沙城乡" },
        ],
        6: [
          // 西湾子街道
          { value: "61", label: "西湾子街道" },
          { value: "62", label: "西湾子镇" },
          { value: "63", label: "高家营镇" },
          { value: "64", label: "四台嘴乡" },
          { value: "65", label: "红旗营乡" },
          { value: "66", label: "石窑子乡" },
          { value: "67", label: "驿马图乡" },
          { value: "68", label: "石嘴子乡" },
          { value: "69", label: "狮子沟乡" },
          { value: "70", label: "清三营乡" },
          { value: "71", label: "白旗乡" },
        ],
        7: [
          // 张北县的乡镇
          { value: "72", label: "张北镇" },
          { value: "73", label: "公会镇" },
          { value: "74", label: "二台镇" },
          { value: "75", label: "大囫囵镇" },
          { value: "76", label: "小二台镇" },
          { value: "77", label: "沙沟镇" },
          { value: "78", label: "台路沟乡" },
          { value: "79", label: "油篓沟乡" },
          { value: "80", label: "馒头营乡" },
          { value: "81", label: "二泉井乡" },
          { value: "82", label: "单晶河乡" },
          { value: "83", label: "大河乡" },
          { value: "84", label: "海流图乡" },
          { value: "85", label: "两面井乡" },
          { value: "86", label: "大西湾乡" },
          { value: "87", label: "郝家营乡" },
          { value: "88", label: "白庙滩乡" },
          { value: "89", label: "战海乡" },
          { value: "90", label: "三号乡" },
          { value: "91", label: "宇宙营乡" },
          { value: "92", label: "察北" },
        ],
        8: [
          // 康保县的乡镇
          { value: "93", label: "康保镇" },
          { value: "94", label: "张纪镇" },
          { value: "95", label: "士城子镇" },
          { value: "96", label: "邓油坊镇" },
          { value: "97", label: "李家地镇" },
          { value: "98", label: "照阳河镇" },
          { value: "99", label: "屯垦镇" },
          { value: "100", label: "闫油坊乡" },
          { value: "101", label: "丹清河乡" },
          { value: "102", label: "哈咇嘎乡" },
          { value: "103", label: "二号卜乡" },
          { value: "104", label: "芦家营乡" },
          { value: "105", label: "忠义乡" },
          { value: "106", label: "处长地乡" },
          { value: "107", label: "满德堂乡" },
          { value: "108", label: "康保牧场" },
          { value: "109", label: "屯垦林场" },
        ],
        9: [
          // 沽源县的乡镇
          { value: "110", label: "平定堡镇" },
          { value: "111", label: "小厂镇" },
          { value: "112", label: "黄盖淖镇" },
          { value: "113", label: "九连城镇" },
          { value: "114", label: "高山堡乡" },
          { value: "115", label: "小河子乡" },
          { value: "116", label: "二道渠乡" },
          { value: "117", label: "大二号回族乡" },
          { value: "118", label: "闪电河乡" },
          { value: "119", label: "长梁乡" },
          { value: "120", label: "丰源店乡" },
          { value: "121", label: "西辛营乡" },
          { value: "122", label: "莲花滩乡" },
          { value: "123", label: "白土窑乡" },
          { value: "124", label: "榆树沟" },
          { value: "125", label: "沙梁子" },
          { value: "126", label: "小城子" },
          { value: "127", label: "东大门" },
        ],
        10: [
          // 尚义县的乡镇
          { value: "128", label: "南壕堑镇" },
          { value: "129", label: "大青沟镇" },
          { value: "130", label: "八道沟镇" },
          { value: "131", label: "红土梁镇" },
          { value: "132", label: "小蒜沟镇" },
          { value: "133", label: "三工地镇" },
          { value: "134", label: "满井镇" },
          { value: "135", label: "大营盘乡" },
          { value: "136", label: "大苏计乡" },
          { value: "137", label: "石井乡" },
          { value: "138", label: "七甲乡" },
          { value: "139", label: "套里庄乡" },
          { value: "140", label: "甲石河乡" },
          { value: "141", label: "下马圈乡" },
        ],
        11: [
          // 蔚县的乡镇
          { value: "142", label: "蔚州镇" },
          { value: "143", label: "代王城镇" },
          { value: "144", label: "西合营镇" },
          { value: "145", label: "吉家庄镇" },
          { value: "146", label: "白乐镇" },
          { value: "147", label: "暖泉镇" },
          { value: "148", label: "南留庄镇" },
          { value: "149", label: "北水泉镇" },
          { value: "150", label: "桃花镇" },
          { value: "151", label: "阳眷镇" },
          { value: "152", label: "宋家庄镇" },
          { value: "153", label: "下宫村乡" },
          { value: "154", label: "南杨庄乡" },
          { value: "155", label: "柏树乡" },
          { value: "156", label: "常宁乡" },
          { value: "157", label: "涌泉庄乡" },
          { value: "158", label: "杨庄窠乡" },
          { value: "159", label: "南岭庄乡" },
          { value: "160", label: "陈家洼乡" },
          { value: "161", label: "黄梅乡" },
          { value: "162", label: "白草村乡" },
          { value: "163", label: "草沟堡乡" },
        ],
        12: [
          // 阳原县的乡镇
          { value: "164", label: "西城镇" },
          { value: "165", label: "东城镇" },
          { value: "166", label: "化稍营镇" },
          { value: "167", label: "揣骨疃镇" },
          { value: "168", label: "东井集镇" },
          { value: "169", label: "要家庄乡" },
          { value: "170", label: "东坊城堡乡" },
          { value: "171", label: "井儿沟乡" },
          { value: "172", label: "三马坊乡" },
          { value: "173", label: "高墙乡" },
          { value: "174", label: "大田洼乡" },
          { value: "175", label: "辛堡乡" },
          { value: "176", label: "马圈堡乡" },
          { value: "177", label: "浮图讲乡" },
        ],
        13: [
          // 怀安县的乡镇
          { value: "178", label: "柴沟堡镇" },
          { value: "179", label: "左卫镇" },
          { value: "180", label: "头百户镇" },
          { value: "181", label: "怀安城镇" },
          { value: "182", label: "渡口堡乡" },
          { value: "183", label: "第六屯乡" },
          { value: "184", label: "西湾堡乡" },
          { value: "185", label: "西沙城乡" },
          { value: "186", label: "太平庄乡" },
          { value: "187", label: "王虎屯乡" },
          { value: "188", label: "第三堡乡" },
        ],
        14: [
          // 怀来县的乡镇
          { value: "93", label: "沙城镇" },
          { value: "94", label: "北辛堡镇" },
          { value: "95", label: "新保安镇" },
          { value: "96", label: "东花园镇" },
          { value: "97", label: "官厅镇" },
          { value: "98", label: "桑园镇" },
          { value: "99", label: "存瑞镇" },
          { value: "100", label: "土木镇" },
          { value: "101", label: "大黄庄镇" },
          { value: "102", label: "西八里镇" },
          { value: "103", label: "小南辛堡镇" },
          { value: "104", label: "狼山乡" },
          { value: "105", label: "鸡鸣驿乡" },
          { value: "106", label: "东八里乡" },
          { value: "107", label: "瑞云观乡" },
          { value: "108", label: "孙庄子乡" },
          { value: "109", label: "王家楼回族乡" },
        ],
        15: [
          // 涿鹿县的乡镇
          { value: "110", label: "涿鹿镇" },
          { value: "111", label: "张家堡镇" },
          { value: "112", label: "武家沟镇" },
          { value: "113", label: "五堡镇" },
          { value: "114", label: "保岱镇" },
          { value: "115", label: "矾山镇" },
          { value: "116", label: "大堡镇" },
          { value: "117", label: "河东镇" },
          { value: "118", label: "东小庄镇" },
          { value: "119", label: "辉耀镇" },
          { value: "120", label: "大河南镇" },
          { value: "121", label: "温泉屯镇" },
          { value: "122", label: "蟒石口镇" },
          { value: "123", label: "栾庄乡" },
          { value: "124", label: "黑山寺乡" },
          { value: "125", label: "卧佛寺乡" },
          { value: "126", label: "谢家堡乡" },
        ],
        16: [
          // 赤城县的乡镇
          { value: "127", label: "赤城镇" },
          { value: "128", label: "田家窑镇" },
          { value: "129", label: "龙关镇" },
          { value: "130", label: "雕鄂镇" },
          { value: "131", label: "独石口镇" },
          { value: "132", label: "白草镇" },
          { value: "133", label: "龙门所镇" },
          { value: "134", label: "后城镇" },
          { value: "135", label: "东卯镇" },
          { value: "136", label: "炮梁乡" },
          { value: "137", label: "大海陀乡" },
          { value: "138", label: "镇宁堡乡" },
          { value: "139", label: "马营乡" },
          { value: "140", label: "云州乡" },
          { value: "141", label: "三道川乡" },
          { value: "142", label: "东万口乡" },
          { value: "143", label: "茨营子乡" },
          { value: "144", label: "样田乡" },
        ],
        17: [], // 经开区无乡镇
        18: [], // 察北管理区无乡镇
        19: [], // 塞北管理无乡镇
        // ... 其他区县的乡镇
      },
      currentTowns: [], // 当前选中区县的乡镇列表
      // 设备表单参数
      deviceForm: {},
      // 确保stationList存在
      stationList: [],
      // 运维日志表单参数
      yunweiForm: {},
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
        manufacturer: [
          { required: true, message: "制造商不能为空", trigger: "blur" },
        ],
        isYunwei: [
          { required: true, message: "是否运维不能为空", trigger: "change" },
        ],
        stationId: [
          { required: true, message: "站点id不能为空", trigger: "blur" },
        ],
        // provinceCn: [
        //   { required: true, message: "省昵称不能为空", trigger: "blur" },
        // ],
        // cityCn: [
        //   { required: true, message: "市昵称不能为空", trigger: "blur" },
        // ],
        countyCn: [
          { required: true, message: "区/县昵称不能为空", trigger: "blur" },
        ],
        // townCn: [
        //   { required: true, message: "乡/镇昵称不能为空", trigger: "blur" },
        // ],
        siteName: [
          { required: true, message: "工地名称不能为空", trigger: "blur" },
        ],

        maintenanceTime: [
          { required: true, message: "运维时间不能为空", trigger: "blur" },
        ],
        isFinsh: [
          { required: true, message: "是否完成不能为空", trigger: "change" },
        ],
        log: [{ required: true, message: "日志信息不能为空", trigger: "blur" }],
      },
    };
  },
  created() {
    // this.getList();
    this.getStations().then(() => {
      this.getList();
    });
  },
  methods: {
    handleCountyChange(value) {
      // 重置乡镇选择
      this.deviceForm.townCn = null;

      // 根据选中的区县更新乡镇列表
      const countyValue = this.countys.find(
        (item) => item.label === value
      )?.value;
      if (countyValue && this.townMap[countyValue]) {
        this.currentTowns = this.townMap[countyValue];
      } else {
        this.currentTowns = [];
      }
    },
    /** 查询监测设备管理设备列表 */
    // getList() {
    //   this.loading = true;
    //   listDevice(this.queryParams).then((response) => {
    //     this.deviceList = response.rows;
    //     this.total = response.total;
    //     this.loading = false;
    //   });
    // },
    getList() {
      this.loading = true;
      listDevice(this.queryParams)
        .then((response) => {
          if (response.code === 200 && Array.isArray(response.rows)) {
            // console.log("Current stationList:", this.stationList); // 调试输出
            const devicesWithStationNames = response.rows.map((device) => {
              const stationIdString = String(device.stationId);
              const station = this.stationList.find(
                (s) => String(s.id) === stationIdString
              );
              console.log(
                `Looking for station with ID ${stationIdString}:`,
                station
              ); // 调试输出
              return {
                ...device,
                stationName: station ? station.stationName : "未命名站点",
              };
            });
            this.deviceList = devicesWithStationNames;
            console.log("Current deviceList:", this.deviceList); // 调试输出
            this.total = response.total;
          } else {
            console.error("Invalid response:", response);
          }
        })
        .catch((error) => {
          console.error("Failed to fetch devices:", error);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.resetDeviceForm();
    },
    getStations() {
      return listStation({
        pageNum: 1, // 设置第一页
        pageSize: 99999, // 设置大页数，确保获取所有数据
      })
        .then((response) => {
          if (response.code === 200 && Array.isArray(response.rows)) {
            this.stationList = response.rows;
            console.log("Current stationList:", this.stationList); // 调试输出
          } else {
            console.error("Invalid stations data structure:", response);
          }
        })
        .catch((error) => {
          console.error("Failed to fetch stations:", error); // 添加错误处理
        });
    },

    // 表单重置
    resetDeviceForm() {
      this.deviceForm = {
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
        provinceCn: "河北省",
        city: null,
        cityCn: "张家口市",
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
        isYunwei: null,
        guid: null,
        stationId: null,
        addr: null,
        userId: null,
      };
      this.resetForm("deviceForm");
    },
    resetYunweiForm() {
      this.yunweiForm = {
        siteName: null,
        sn: null,
        maintenanceTime: null,
        isFinsh: null,
        img: null,
        log: null,
      };
      this.resetForm("yunweiForm");
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
      this.resetDeviceForm();
      this.open = true;
      this.title = "添加监测设备管理设备";
    },
    handleMaintenance(row) {
      this.resetYunweiForm();
      this.openYunwei = true;
      this.title = "运维日志";
      this.yunweiForm = {
        deviceId: row.id,
        sn: row.sn,
        // siteName: "",
        siteName: row.name, // 初始化设备名称
        maintenanceTime: "",
        isFinsh: 2,
        img: "",
        log: "",
      };
      console.log("运维操作:", row);
      // TODO: 实现运维相关功能
    },
    cancelYunwei() {
      this.openYunwei = false;
      this.resetYunweiForm();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.resetDeviceForm();
      const id = row.id || this.ids;
      getDevice(id).then((response) => {
        this.deviceForm = response.data;
        this.open = true;
        this.title = "修改监测设备管理设备";
      });
    },
    /** 提交设备表单 */
    submitForm() {
      this.$refs["deviceForm"].validate((valid) => {
        if (valid) {
          if (this.deviceForm.id != null) {
            updateDevice(this.deviceForm).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDevice(this.deviceForm).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
              // 发送设备信息到消息通知
              sendDevice(this.deviceForm).then(response => {
                this.$modal.msgSuccess("发送成功");

              });
            });
          }
        }
      });
    },
    /** 提交运维日志表单 */
    submitYunweiForm() {
      this.$refs["yunweiForm"].validate((valid) => {
        if (valid) {
          addYunwei(this.yunweiForm)
            .then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.openYunwei = false;
              // 如果运维完成，更新设备运维状态为2
              if (this.yunweiForm.isFinsh === 1) {
                const updateData = {
                  id: this.yunweiForm.deviceId,
                  isYunwei: 2, // 运维完成后设置为2
                };

                updateDevice(updateData)
                  .then(() => {
                    this.getList(); // 刷新列表
                  })
                  .catch((error) => {
                    console.error("更新设备运维状态失败:", error);
                  });
              }

              this.resetYunweiForm();
            })
            .catch(() => {
              this.$modal.msgError("新增失败");
            });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除监测设备管理设备编号为"' + ids + '"的数据项？')
        .then(function () {
          return delDevice(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => { });
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
