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

export default {
  name: "Index",
  components: {
    Weather,
  },
  data() {
    return {
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
      titleImages: []
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
    }
  },
  methods: {
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
        this.Station.normal = this.stationlist.filter(station => station.type === 1).length; // 状态为1表示正常
        this.Station.stop = this.stationlist.filter(station => station.type === 2).length; // 状态为2表示停用
        this.Station.delete = this.stationlist.filter(station => station.type === 3).length; // 状态为3表示删除
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
  padding: 20px;

  .title-container {
    width: 100%;
    position: relative;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }

    .el-carousel {
      border-radius: inherit;

      // 轮播指示器美化
      ::v-deep .el-carousel__indicator {
        padding: 8px;

        .el-carousel__button {
          width: 20px;
          border-radius: 4px;
          background: rgba(255, 255, 255, 0.6);
          transition: all 0.3s;
        }

        &:hover .el-carousel__button {
          background: #fff;
          width: 30px;
        }
      }
    }

    .title-overlay {
      position: absolute;
      top: 10%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 2;
      text-align: center;


      h1 {
        font-size: 2.8rem;
        font-weight: 500;
        color: #fff;
        font-family: system-ui, -apple-system, sans-serif;
        text-shadow: 1px 2px 3px rgba(0, 0, 0, 0.2);
        letter-spacing: normal;
        padding: 12px 24px;
        background: rgba(15, 42, 67, 0.85);
        border-radius: 8px;

        &::after {
          content: none;
        }
      }
    }

    .background-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 8s ease;
    }

    // 轮播切换动画
    .el-carousel__item {
      &.is-active {
        .background-image {
          transform: scale(1.05);
        }
      }
    }
  }

  @keyframes titleGlow {
    from {
      text-shadow: 0 0 10px rgba(255, 255, 255, 0.5),
        0 0 20px rgba(255, 255, 255, 0.3),
        0 0 30px rgba(255, 255, 255, 0.2);
    }

    to {
      text-shadow: 0 0 20px rgba(255, 255, 255, 0.8),
        0 0 30px rgba(255, 255, 255, 0.6),
        0 0 40px rgba(255, 255, 255, 0.4);
    }
  }

  @media (max-width: 768px) {
    .title-container {
      .title-overlay {
        padding: 15px 25px;

        h1 {
          font-size: 2.2rem;

          &::after {
            bottom: -6px;
            height: 2px;
          }
        }
      }

      .el-carousel {
        height: 300px !important;
      }
    }
  }

  .nav-container {
    display: flex;
    justify-content: center;
    width: 110%;
    height: 60px;
    margin-top: 20px;

    .nav-link {
      width: auto; // 修改宽度为自动
      font-size: 20px;
      color: #ffffff;
      text-align: center;
      text-decoration: none;
      padding: 15px 25px;
      background-color: #66b1ff;
      transition: background-color 0.3s, transform 0.3s;
      border: 1px solid transparent;
      border-radius: 5px;
      margin: 0 10px;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1));
        transform: scaleX(0);
        transform-origin: left;
        transition: transform 0.3s;
        z-index: 0;
      }

      &:hover {
        background-color: #54dfbe;
        transform: scale(1.05);

        &::before {
          transform: scaleX(1);
        }
      }

      span {
        position: relative;
        z-index: 1;
      }
    }
  }

  .announcement-and-carousel-container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    padding: 20px;
    background-color: #f9fafc;
    width: 100%;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .announcement-container {
      width: 100%;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      padding: 20px;
      box-sizing: border-box;
      max-height: 450px; // 设置固定高度
      overflow-y: auto; // 内容超出时可滚动

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
          position: relative;
          padding-bottom: 35px;

          &:last-child {
            border-bottom: none;
            margin-bottom: 0;
            padding-bottom: 35px !important;
          }

          h3 {
            font-size: 18px;
            margin-bottom: 10px;
            color: #409eff;
            animation: fadeIn 0.5s ease-in-out;
          }

          p {
            font-size: 16px;
            color: #606266;
            animation: fadeIn 0.5s ease-in-out 0.2s;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            overflow: hidden;
            position: relative;
            max-height: 48px;
          }

          .notice-time {
            position: absolute;
            bottom: 10px;
            left: 0;
            font-size: 14px;
            color: #909399;
            bottom: 10px !important;
          }

          .expand-button {
            position: absolute;
            bottom: 0;
            right: 0;
            background: none;
            border: none;
            color: #409eff;
            cursor: pointer;
            font-size: 14px;
          }

          &:hover {
            background-color: #f0f9eb;
            border-radius: 8px;

            p {
              margin-bottom: 10px;
              line-height: 1.6;
              max-height: unset;
            }
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

      .loading-state {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;

        .loading-spinner {
          border: 4px solid #f3f3f3;
          border-top: 4px solid #3498db;
          border-radius: 50%;
          width: 30px;
          height: 30px;
          animation: spin 1s linear infinite;
        }

        @keyframes spin {
          0% {
            transform: rotate(0deg);
          }

          100% {
            transform: rotate(360deg);
          }
        }
      }
    }

    .carousel-container {
      width: 100%;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      backdrop-filter: blur(10px); // 添加玻璃磨砂效果

      .carousel-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: opacity 0.5s ease-in-out;
      }
    }
  }

  .carousel-container {
    position: relative;

    .stats-overlay {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 80%;
      z-index: 2;

      h3 {
        font-size: 24px;
        font-weight: 600;
        color: #333;
        text-align: center;
        margin-bottom: 15px;
        text-shadow: 1px 2px 4px rgba(0, 0, 0, 0.2);
      }

      .stat-item {
        text-align: center;
        padding: 15px;
        background: rgba(245, 245, 245, 0.8);
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-3px);
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .stat-icon {
          font-size: 32px;
          color: #409eff;
          margin-bottom: 10px;
        }

        .el-statistic__title {
          font-size: 14px;
          color: #666;
        }

        .el-statistic__content {
          font-size: 22px;
          color: #333;
          font-weight: bold;
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
    background-color: darken(#333, 5%);
    color: #fff;
    padding: 10px 0;
    margin-top: 10px;
    position: relative;
    overflow: hidden;

    // 新增渐变背景
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 3px;
      background: linear-gradient(90deg, #66b1ff, #54dfbe);
    }

    // 网格布局容器
    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 30px;
      padding: 0 10px;

      @media (max-width: 992px) {
        grid-template-columns: repeat(2, 1fr);
      }

      @media (max-width: 576px) {
        grid-template-columns: 1fr;
      }
    }

    // 区块标题样式
    .footer-section {
      h4 {
        font-size: 18px;
        margin-bottom: 15px;
        color: #66b1ff;
        position: relative;
        padding-bottom: 8px;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          width: 40px;
          height: 2px;
          background: #54dfbe;
        }
      }

      ul {
        list-style: none;
        padding: 0;
        margin: 0;

        li {
          margin-bottom: 8px;

          a {
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            transition: all 0.3s;
            display: flex;
            align-items: center;

            &:hover {
              color: #54dfbe;
              transform: translateX(5px);
            }

            i {
              margin-right: 8px;
              font-size: 14px;
            }
          }
        }
      }

      &.contact {
        .contact-item {
          display: flex;
          align-items: center;
          margin-bottom: 10px;

          i {
            width: 24px;
            font-size: 16px;
            color: #66b1ff;
          }
        }
      }
    }

    // 版权信息
    .copyright {
      text-align: center;
      margin-top: 10px;
      padding-top: 10px;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      font-size: 14px;
      color: rgba(255, 255, 255, 0.6);

      a {
        color: #66b1ff;
        margin: 0 5px;
      }
    }
  }
}
</style>
