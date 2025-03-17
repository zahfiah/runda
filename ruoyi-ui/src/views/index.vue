<template>
  <div class="app-container home">
    <div class="title-container">
      <el-carousel :interval="5000" arrow="always" height="400px" indicator-position="outside">
        <el-carousel-item v-for="(image, index) in titleImages" :key="index">
          <img :src="image.src" :alt="image.alt" class="background-image" />
        </el-carousel-item>
      </el-carousel>
      <div class="title-overlay">
        <h1>塞林智慧环保</h1>
      </div>
    </div>

    <div class="nav-container">
      <router-link to="/runda/data" class="nav-link">监测小时报表</router-link>
      <router-link to="/runda/device" class="nav-link">监测设备管理</router-link>
      <router-link to="/runda/station" class="nav-link">监测站点管理</router-link>
      <router-link to="/runda/query212" class="nav-link">大气数据查询</router-link>
      <router-link to="/runda/yunwei" class="nav-link">运维日志</router-link>
      <router-link to="/runda/infor/calibration" class="nav-link">基础信息管理</router-link>
      <router-link to="/runda/alarm/remind" class="nav-link">告警管理</router-link>
    </div>

    <!-- 区县统计模块 -->
    <div class="region-stats-container">
      <div class="region-header" @click="toggleStats">
        <h2 class="region-title">张家口监测详情</h2>
        <i class="el-icon-arrow-down" :class="{ 'rotate-180': isStatsVisible }"></i>
      </div>
      <transition name="el-zoom-in-top">
        <div v-show="isStatsVisible" class="stats-content">
          <el-row :gutter="20">
            <el-col 
              v-for="(region, index) in zjkRegions" 
              :key="index"
              :xs="24" :sm="12" :md="8" :lg="6"
            >
              <div class="region-card">
                <h3>{{ region.label }}</h3>
                <div class="stats-grid">
                  <div class="stat-item">
                    <i class="el-icon-monitor"></i>
                    <div class="stat-content">
                      <span class="count">{{ deviceCount(region.label) }}</span>
                      <span class="label">监测设备</span>
                    </div>
                  </div>
                  <div class="stat-item">
                    <i class="el-icon-office-building"></i>
                    <div class="stat-content">
                      <span class="count">{{ stationCount(region.label) }}</span>
                      <span class="label">监测站点</span>
                    </div>
                  </div>
                </div>
                <!-- 新增按钮 -->
                <div class="region-buttons">
                  <el-button size="mini" @click="toggleRegionStats(region.label, 'device')">设备状态统计</el-button>
                  <el-button size="mini" @click="toggleRegionStats(region.label, 'station')">站点状态统计</el-button>
                </div>
                <!-- 设备状态统计内容 -->
                <div v-if="showDeviceStats[region.label]" class="region-status-stats">
                  <h4>设备状态统计</h4>
                  <el-row :gutter="12">
                    <el-col v-for="(stat, index) in deviceStatusStats(region.label)" :key="index" :span="12">
                      <div class="status-item">
                        <i :class="stat.icon"></i>
                        <span>{{ stat.title }}: {{ stat.value }}</span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <!-- 站点状态统计内容 -->
                <div v-if="showStationStats[region.label]" class="region-status-stats">
                  <h4>站点状态统计</h4>
                  <el-row :gutter="12">
                    <el-col v-for="(stat, index) in stationStatusStats(region.label)" :key="index" :span="12">
                      <div class="status-item">
                        <i :class="stat.icon"></i>
                        <span>{{ stat.title }}: {{ stat.value }}</span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </transition>
    </div>

    <div class="announcement-and-carousel-container">
      <div class="announcement-container">
        <div class="announcement-header" style="display: flex; justify-content: space-between;">
          <h2>公告</h2>
          <el-button type="primary" icon="el-icon-refresh" @click="refreshNotices" :loading="loadingNotices"
            class="refresh-button">
            刷新公告
          </el-button>
        </div>
        <ul v-if="notices.length > 0" class="notice-list">
          <li v-for="notice in sortedNotices" :key="notice.id" class="notice-item">
            <h3>{{ notice.noticeTitle }}</h3>
            <p>{{ notice.noticeContent }}</p>
            <span class="notice-time">{{ parseTime(notice.createTime) }}</span>
          </li>
        </ul>
        <div v-else class="no-notices">
          <p>暂无公告。</p>
        </div>
        <div class="refresh-button-container">
        </div>
      </div>

      <div class="carousel-container">
        <el-carousel :interval="5000" arrow="always" height="400px" indicator-position="outside">
          <!-- 第一个轮播项 - 设备统计 -->
          <el-carousel-item>
            <img src="../assets/images/information.jpg" alt="设备统计" class="carousel-image" />
            <div class="stats-overlay">
              <h3>设备统计</h3>
              <el-row :gutter="24" class="stats-content">
                <el-col v-for="(stat, index) in deviceStats" :key="index" :span="6">
                  <div class="stat-item">
                    <i :class="stat.icon" class="stat-icon"></i>
                    <el-statistic :title="stat.title" :value="stat.value" />
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-carousel-item>

          <!-- 第二个轮播项 - 站点统计 -->
          <el-carousel-item>
            <img src="../assets/images/information.jpg" alt="站点统计" class="carousel-image" />
            <div class="stats-overlay">
              <h3>站点统计</h3>
              <el-row :gutter="24" class="stats-content">
                <el-col v-for="(stat, index) in stationStats" :key="index" :span="6">
                  <div class="stat-item">
                    <i :class="stat.icon" class="stat-icon"></i>
                    <el-statistic :title="stat.title" :value="stat.value" />
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>

    <div class="weather-container">
      <Weather />
    </div>

    <div class="footer-container">
      <div class="footer-content">
        <!-- 公司信息 -->
        <div class="footer-section">
          <h4>塞林环保</h4>
          <ul>
            <li><a href="#"><i class="el-icon-info"></i>关于我们</a></li>
            <li><a href="#"><i class="el-icon-office-building"></i>加入我们</a></li>
          </ul>
        </div>

        <!-- 产品服务 -->
        <div class="footer-section">
          <h4>产品服务</h4>
          <ul>
            <li><a href="#"><i class="el-icon-monitor"></i>监测系统</a></li>
            <li><a href="#"><i class="el-icon-data-analysis"></i>数据分析</a></li>
            <li><a href="#"><i class="el-icon-cloudy"></i>环保云平台</a></li>
          </ul>
        </div>

        <!-- 联系方式 -->
        <div class="footer-section contact">
          <h4>联系我们</h4>
          <div class="contact-item">
            <i class="el-icon-phone"></i>
            <span>400-123-4567</span>
          </div>
          <div class="contact-item">
            <i class="el-icon-message"></i>
            <span>contact@example.com</span>
          </div>
          <div class="contact-item">
            <i class="el-icon-location"></i>
            <span>河北省张家口市桥东区</span>
          </div>
        </div>

        <!-- 社交媒体 -->
        <div class="footer-section">
          <h4>关注我们</h4>
          <div class="social-links">
            <a href="#" class="social-icon"><i class="el-icon-weixin"></i></a>
            <a href="#" class="social-icon"><i class="el-icon-weibo"></i></a>
            <a href="#" class="social-icon"><i class="el-icon-link"></i></a>
          </div>
        </div>
      </div>

      <div class="copyright">
        <p>© 2025 塞林智慧环保
          <a href="#">隐私政策</a> |
          <a href="#">服务条款</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { listNotice } from "@/api/system/notice";
import { listDevice } from "@/api/runda/device";
import { listStation } from "@/api/runda/station";
import Weather from '@/components/Weather';
import regions from '@/assets/regions.json'

export default {
  name: "Index",
  components: {
    Weather,
  },
  data() {
    return {
      isStatsVisible: false,
      zjkRegions: regions.counties
        .filter(county => county.city === '130700')
        .map(item => ({ 
          value: item.value, 
          label: item.label.replace('区', '') // 去除"区"字尾
        })),
      loadingNotices: false,
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

      deviceList: [],
      stationlist: [],
      total: 0,
      loading: false,
      totalNotices: 0,


      notices: [],
      currentPage: 1,
      itemsPerPage: 3,
      images: [],
      titleImages: [],
      showDeviceStats: {}, // 控制设备状态统计的显示
      showStationStats: {}, // 控制站点状态统计的显示
    };
  },
  created() {
    this.getList();
    this.getStation();
    this.fetchNotices();
    this.loadTitleImagesFromFolder('images/title');
  },
  computed: {
    deviceStats() {
      return [
        { icon: 'el-icon-s-data', title: '总设备数量', value: this.statistics.totalDevices },
        { icon: 'el-icon-success', title: '正常设备数量', value: this.statistics.normalDevices },
        { icon: 'el-icon-warning-outline', title: '中断设备数量', value: this.statistics.interruptedDevices },
        { icon: 'el-icon-finished', title: '竣工设备数量', value: this.statistics.completedDevices }
      ]
    },
    stationStats() {
      return [
        { icon: 'el-icon-office-building', title: '总站点数量', value: this.Station.total },
        { icon: 'el-icon-check', title: '正常站点数量', value: this.Station.normal },
        { icon: 'el-icon-stopwatch', title: '停用站点数量', value: this.Station.stop },
        { icon: 'el-icon-delete-solid', title: '删除站点数量', value: this.Station.delete }
      ]
    },
    sortedNotices() {
      return [...this.notices].reverse(); // 倒序显示
    },
    paginatedNotices() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.notices.slice(start, end);
    },
    deviceStatusStats() {
      return (regionName) => [
        { icon: 'el-icon-success', title: '正常设备', value: this.deviceList.filter(d => d.countyCn?.includes(regionName) && d.status === 1).length },
        { icon: 'el-icon-warning-outline', title: '中断设备', value: this.deviceList.filter(d => d.countyCn?.includes(regionName) && d.status === 2).length },
        { icon: 'el-icon-finished', title: '竣工设备', value: this.deviceList.filter(d => d.countyCn?.includes(regionName) && d.status === 3).length },
        { icon: 'el-icon-question', title: '未知设备', value: this.deviceList.filter(d => d.countyCn?.includes(regionName) && ![1, 2, 3].includes(d.status)).length },
      ];
    },
    stationStatusStats() {
      return (regionName) => [
        { icon: 'el-icon-check', title: '正常站点', value: this.stationlist.filter(s => s.countyCn?.includes(regionName) && s.stationStatus === 1).length },
        { icon: 'el-icon-stopwatch', title: '停用站点', value: this.stationlist.filter(s => s.countyCn?.includes(regionName) && s.stationStatus === 2).length },
        { icon: 'el-icon-delete-solid', title: '删除站点', value: this.stationlist.filter(s => s.countyCn?.includes(regionName) && s.stationStatus === 3).length },
        { icon: 'el-icon-question', title: '未知站点', value: this.stationlist.filter(s => s.countyCn?.includes(regionName) && ![1, 2, 3].includes(s.stationStatus)).length },
      ];
    },
  },
  methods: {
    toggleStats() {
    this.isStatsVisible = !this.isStatsVisible;
  },
    deviceCount(regionName) {
      return this.deviceList.filter(d => 
        d.countyCn && d.countyCn.includes(regionName)
      ).length
    },
    // 站点数量统计方法
    stationCount(regionName) {
      return this.stationlist.filter(s => 
        s.countyCn && s.countyCn.includes(regionName)
      ).length
    },
    getList() {
      this.loading = true;
      listDevice({
        pageNum: 1, // 设置第一页
        pageSize: 99999, // 设置大页数，确保获取所有数据
      }).then(response => {
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
      listStation({
        pageNum: 1,
        pageSize: 99999
      }).then(response => {
        this.stationlist = response.rows;
        console.log(response.rows);

        this.Station.total = response.total;
        //当stationtype为1时计算站点数量
        this.Station.normal = this.stationlist.filter(station => station.stationStatus === 1).length; // 状态为1表示正常
        this.Station.stop = this.stationlist.filter(station => station.stationStatus === 2).length; // 状态为2表示停用
        this.Station.delete = this.stationlist.filter(station => station.stationStatus === 3).length; // 状态为3表示删除
        this.loading = false;
      });
    },

    async refreshNotices() {
      try {
        this.loadingNotices = true;
        await this.fetchNotices();
        this.$message.success('公告已刷新');
      } catch (error) {
        console.error("刷新公告失败:", error);
        this.$message.error('刷新公告失败');
      } finally {
        this.loadingNotices = false;
      }
    },
    // 修改原来的获取公告方法
    fetchNotices() {
      return listNotice().then(response => {
        this.notices = response.rows;
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

    loadTitleImagesFromFolder(folderPath) {
      const imageExtensions = ['jpg', 'jpeg', 'png', 'gif'];
      const context = require.context(`../assets/images/index`, false, /\.(jpe?g|png|gif)$/i);

      context.keys().forEach(key => {
        const ext = key.split('.').pop().toLowerCase();
        if (imageExtensions.includes(ext)) {
          this.titleImages.push({
            src: context(key),
            alt: `Title Image ${this.titleImages.length + 1}`
          });
        }
      });
    },
    toggleRegionStats(regionName, type) {
      if (type === 'device') {
        this.$set(this.showDeviceStats, regionName, !this.showDeviceStats[regionName]);
        this.$set(this.showStationStats, regionName, false); // 隐藏站点统计
      } else if (type === 'station') {
        this.$set(this.showStationStats, regionName, !this.showStationStats[regionName]);
        this.$set(this.showDeviceStats, regionName, false); // 隐藏设备统计
      }
    },
  }
};
</script>

<style scoped lang="scss">

@import "@/assets/styles/seeindex.scss";

.region-card {
  .region-buttons {
    margin-top: 10px;
    display: flex;
    gap: 10px;
  }
  .region-status-stats {
    margin-top: 10px;
    padding: 10px;
    background-color: #f5f7fa;
    border-radius: 4px;
    h4 {
      margin-bottom: 10px;
      font-size: 14px;
      color: #303133;
    }
    .status-item {
      display: flex;
      align-items: center;
      margin-bottom: 5px;
      i {
        margin-right: 5px;
        color: #409eff;
      }
      span {
        font-size: 12px;
        color: #606266;
      }
    }
  }
}
</style>