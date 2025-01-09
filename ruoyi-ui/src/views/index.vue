<template>
  <div class="app-container home">
    <div class="title-container">
      <h1>润达网站</h1>
    </div>

    <div class="nav-container">
      <router-link to="/runda/report/check" class="nav-link"> 报表</router-link>
      <router-link to="/runda/device" class="nav-link">监测设备管理</router-link>
      <router-link to="/runda/station" class="nav-link">监测站点管理</router-link>
      <router-link to="/runda/query212" class="nav-link">大气数据查询</router-link>
      <router-link to="/runda/infor/calibration" class="nav-link">基础信息管理</router-link>
      <router-link to="/runda/alarm/data" class="nav-link">告警管理</router-link>
    </div>

    <div class="statistics-device-container">
      <!-- 设备统计信息 -->
      <el-card shadow="always" class="stat-card">
        <el-row :gutter="24">
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-s-data stat-icon"></i>
              <el-statistic title="总设备数量" :value="statistics.totalDevices" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-success stat-icon"></i>
              <el-statistic title="正常设备数量" :value="statistics.normalDevices" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-warning-outline stat-icon"></i>
              <el-statistic title="中断设备数量" :value="statistics.interruptedDevices" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-finished stat-icon"></i>
              <el-statistic title="竣工设备数量" :value="statistics.completedDevices" />
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <div class="statistics-station-container">
      <!-- 站点统计信息 -->
      <el-card shadow="always" class="stat-card">
        <el-row :gutter="24">
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-office-building stat-icon"></i>
              <el-statistic title="总站点数量" :value="Station.total" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-check stat-icon"></i>
              <el-statistic title="正常站点数量" :value="Station.normal" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-stopwatch stat-icon"></i>
              <el-statistic title="停用站点数量" :value="Station.stop" />
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <i class="el-icon-delete-solid stat-icon"></i>
              <el-statistic title="删除站点数量" :value="Station.delete" />
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <div class="content-container">
      <div class="text-module">
        <h2>最新公告</h2>
        <ul v-if="latestNotices.length > 0">
          <li v-for="(notice, index) in latestNotices" :key="index">
            <h3>{{ notice.noticeTitle }}</h3>
            <p>{{ notice.noticeContent.slice(0, 200) }}...</p>
          </li>
        </ul>
        <p v-else>暂无公告</p>
      </div>
      <div class="image-module">
        <img src="../assets/images/index-text.png" alt="示例图片" />
      </div>
    </div>
    <div class="footer-container">
      <p>© 2025 润达网站</p>
    </div>
  </div>
</template>

<script>
import { listNotice } from "@/api/system/notice";
import { listDevice } from "@/api/runda/device";
import { listStation } from "@/api/runda/station";

export default {
  name: "Index",
  data() {
    return {
      msg: "欢迎来到润达网站！我们提供专业的服务和技术支持，致力于满足用户的各种需求。",
      latestNotices: [],
      // 统计数据
      statistics: {
        totalDevices: 0,
        normalDevices: 0,
        interruptedDevices: 0,
        completedDevices: 0,
      },
      Station: {
        total: 0,
        normal: 0,
        stop: 0,
        delete: 0,
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      deviceList: [],
      stationlist: [],
      total: 0,
      loading: false,
    };
  },
  created() {
    this.getLatestNotices();
    this.getList();
    this.getStation();
  },
  methods: {
    getLatestNotices() {
      listNotice({ pageNum: 1, pageSize: 3 }).then((response) => {
        if (response.rows && response.rows.length > 0) {
          this.latestNotices = response.rows;
        }
      });
    },

    getList() {
      this.loading = true;
      listDevice(this.queryParams).then(response => {
        this.deviceList = response.rows;
        this.total = response.total;

        // 计算统计数据
        this.statistics.totalDevices = this.deviceList.length;
        this.statistics.normalDevices = this.deviceList.filter(device => device.status === 1).length; // 状态为1表示正常
        this.statistics.interruptedDevices = this.deviceList.filter(device => device.status === 2).length; // 状态为2表示中断
        this.statistics.completedDevices = this.deviceList.filter(device => device.status === 3).length; // 状态为3表示竣工

        this.loading = false;
      });
    },

    getStation() {
      this.loading = true;
      listStation(this.queryParams).then(response => {
        this.stationlist = response.rows;
        this.Station.total = response.total;
        this.Station.normal = this.stationlist.filter(station => station.type === 1).length; // 状态为1表示正常
        this.Station.stop = this.stationlist.filter(station => station.type === 2).length; // 状态为2表示停用
        this.Station.delete = this.stationlist.filter(station => station.type === 3).length; // 状态为3表示删除
        this.loading = false;
      });
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;

  .title-container {
    height: 300px;
    width: 100%;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    text-align: center;
    background-image: url("../assets/logo/sign-bg.png");
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;

    h1 {
      font-size: 48px;
      font-weight: bold;
      color: #333;
      font-family: "楷体", "STKaiti", serif;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    }
  }

  .nav-container {
    display: flex;
    justify-content: center;
    width: 100%;
    height: 60px;
    margin-top: 20px;

    .nav-link {
      width: 50%;
      font-size: 20px;
      color: #ffffff;
      text-align: center;
      text-decoration: none;
      padding: 15px 25px;
      background-color: #66b1ff;
      transition: background-color 0.3s;
      border: 1px solid transparent;
      border-radius: 5px;
      margin: 0 10px;
      transition: background-color 0.3s, border-color 0.3s;

      &:hover {
        background-color: #54dfbe;
        transform: scale(1.05);
      }
    }
  }

  .statistics-device-container,
  .statistics-station-container {
    width: 100%;
    padding: 20px;
    box-sizing: border-box;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    margin-top: 20px;

    .stat-card {
      border: 1px solid #ebeef5;
      border-radius: 10px;
      overflow: hidden;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      background: linear-gradient(to bottom, #ffffff, #f9f9f9);

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
      }

      .stat-item {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        padding: 20px;
        transition: background-color 0.3s;

        &:hover {
          background-color: #f0f9eb;
        }

        .stat-icon {
          font-size: 32px;
          margin-bottom: 10px;
          color: #409eff;
        }

        .el-statistic__title {
          font-size: 16px;
          color: #909399;
        }

        .el-statistic__content {
          font-size: 24px;
          color: #303133;
        }
      }
    }
  }

  .content-container {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin: 20px 0;
    padding: 20px;
    box-sizing: border-box;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

    .text-module,
    .image-module {
      width: 48%;
      box-sizing: border-box;
      padding: 10px;
      display: flex;
      flex-direction: column;
      align-items: flex-start;
    }

    .text-module {
      ul {
        list-style-type: none;
        padding: 0;

        li {
          margin-bottom: 20px;

          h3 {
            font-size: 20px;
            font-weight: bold;
            margin: 0 0 5px 0;
          }

          p {
            font-size: 16px;
            color: #555;
          }
        }
      }
    }

    .image-module img {
      max-width: 100%;
      height: auto;
      display: block;
      margin: 0 auto;
    }
  }

  .footer-container {
    width: 100%;
    height: 50px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    background-color: #333;
    color: #fff;

    .footer-link {
      font-size: 18px;
      color: #fff;
      text-decoration: none;
      transition: color 0.3s;

      &:hover {
        color: #5aac0f;
      }
    }
  }
}
</style>
