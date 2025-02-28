<template>
  <div class="app-container home">
    <div class="title-container">
      <h1>润达网站</h1>
      <img :src="backgroundImage" alt="Background Image" class="background-image" />
    </div>

    <div class="nav-container">
      <router-link to="/runda/report/check" class="nav-link"> 报表</router-link>
      <router-link to="/runda/device" class="nav-link">监测设备管理</router-link>
      <router-link to="/runda/station" class="nav-link">监测站点管理</router-link>
      <router-link to="/runda/query212" class="nav-link">大气数据查询</router-link>
      <router-link to="/runda/infor/calibration" class="nav-link">基础信息管理</router-link>
      <router-link to="/runda/alarm/data" class="nav-link">告警管理</router-link>
    </div>

    <div class="announcement-and-carousel-container">
      <div class="announcement-container">
        <h2>公告</h2>
        <ul v-if="notices.length > 0" class="notice-list">
          <li v-for="notice in paginatedNotices" :key="notice.id" class="notice-item">
            <h3>{{ notice.noticeTitle }}</h3>
            <p>{{ notice.noticeContent }}</p>
          </li>
        </ul>
        <div v-else class="no-notices">
          <p>暂无公告。</p>
        </div>
        <div class="pagination-controls">
          <button @click="prevPage" :disabled="currentPage === 1" class="pagination-button">上一页</button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-button">下一页</button>
        </div>
      </div>

      <div class="carousel-container">
        <el-carousel :interval="5000" arrow="always" height="400px" indicator-position="outside">
          <el-carousel-item v-for="(image, index) in images" :key="index">
            <img :src="image.src" :alt="image.alt" class="carousel-image" />
          </el-carousel-item>
        </el-carousel>
      </div>
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

    <div class="weather-container">
      <Weather />
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
import Weather from '@/components/Weather';

export default {
  name: "Index",
  components: {
    Weather,
  },
  data() {
    return {
      msg: "欢迎来到润达网站！我们提供专业的服务和技术支持，致力于满足用户的各种需求。",
      latestNotices: [],
      backgroundImage: require("../assets/logo/sign-bg.png"),
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

      deviceList: [],
      stationlist: [],
      total: 0,
      loading: false,
      totalNotices: 0,

      notices: [],
      currentPage: 1,
      itemsPerPage: 3,
      images: []
    };
  },
  created() {
    this.getList();
    this.getStation();
    this.fetchNotices();
    this.loadImagesFromFolder('images/carousel');
  },
  computed: {
    totalPages() {
      return Math.ceil(this.notices.length / this.itemsPerPage);
    },
    paginatedNotices() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.notices.slice(start, end);
    }
  },
  methods: {
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

    fetchNotices() {
      listNotice().then(response => {
        this.notices = response.rows;
      }).catch(error => {
        console.error("Failed to fetch notices:", error);
      });
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    loadImagesFromFolder(folderPath) {
      const imageExtensions = ['jpg', 'jpeg', 'png', 'gif'];
      const context = require.context(`../assets/images/index`, false, /\.(jpe?g|png|gif)$/i);

      context.keys().forEach(key => {
        const ext = key.split('.').pop().toLowerCase();
        if (imageExtensions.includes(ext)) {
          this.images.push({
            src: context(key),
            alt: `Image ${this.images.length + 1}`
          });
        }
      });
    }
  }
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
    width: 100%;
    position: relative;
    text-align: center;
    height: 400px;
    /* 增加高度 */

    h1 {
      font-size: 48px;
      font-weight: bold;
      color: #fff;
      font-family: "楷体", "STKaiti", serif;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
      position: absolute;
      top: 20px;
      /* 修改为居顶 */
      left: 50%;
      transform: translateX(-50%);
      z-index: 1;
    }

    .background-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      position: absolute;
      top: 0;
      left: 0;
      z-index: 0;
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

  .announcement-and-carousel-container {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 20px;
    background-color: #f9fafc;
    width: 100%;

    .announcement-container {
      width: 43%;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      padding: 20px;
      box-sizing: border-box;

      h2 {
        font-size: 24px;
        margin-bottom: 20px;
        color: #333;
      }

      .notice-list {
        list-style-type: none;
        padding: 0;
        margin: 0;

        .notice-item {
          border-bottom: 1px solid #eaeaea;
          padding-bottom: 15px;
          margin-bottom: 15px;

          &:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 0;
          }

          h3 {
            font-size: 18px;
            margin-bottom: 10px;
            color: #409eff;
          }

          p {
            font-size: 16px;
            color: #606266;
          }
        }
      }

      .no-notices {
        p {
          font-size: 16px;
          color: #909399;
          text-align: center;
        }
      }

      .pagination-controls {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;

        .pagination-button {
          background-color: #66b1ff;
          color: #fff;
          border: none;
          border-radius: 5px;
          padding: 10px 20px;
          cursor: pointer;
          transition: background-color 0.3s;

          &:hover {
            background-color: #54dfbe;
          }

          &[disabled] {
            background-color: #ccc;
            cursor: not-allowed;
          }
        }

        .page-info {
          margin: 0 10px;
          font-size: 16px;
          color: #606266;
        }
      }
    }

    .carousel-container {
      width: 56%;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;

      .carousel-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: opacity 0.5s ease-in-out;
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

  .weather-container {
    width: 100%;
    padding: 20px;
    box-sizing: border-box;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 20px;
      color: #333;
    }

    .loading,
    .error {
      font-size: 16px;
      color: #909399;
      text-align: center;
    }

    .weather-info {
      display: flex;
      flex-direction: column;
      align-items: center;

      .temperature {
        font-size: 48px;
        font-weight: bold;
        color: #409EFF;
        margin-bottom: 10px;
      }

      .description {
        font-size: 20px;
        color: #606266;
        margin-bottom: 20px;
      }

      .details {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 20px;

        .detail-item {
          display: flex;
          align-items: center;
          font-size: 16px;
          color: #909399;

          i {
            margin-right: 5px;
            font-size: 20px;
          }
        }
      }
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
