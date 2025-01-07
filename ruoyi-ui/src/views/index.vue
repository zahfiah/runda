<template>
  <div class="app-container home">
    <div class="title-container">
      <h1>润达网站</h1>
    </div>
    <div class="nav-container">
      <router-link to="/runda/device" class="nav-link"
        >检测设备管理</router-link
      >
      <router-link to="/runda/query" class="nav-link">数据查询</router-link>
      <router-link to="/runda/info/calibration" class="nav-link"
        >基础信息管理</router-link
      >
      <router-link to="/runda/alarm/remind" class="nav-link"
        >告警管理</router-link
      >
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
import { listNotice } from "@/api/system/notice"; // 假设这是公告管理系统的API

export default {
  name: "Index",
  data() {
    return {
      msg: "欢迎来到润达网站！我们提供专业的服务和技术支持，致力于满足用户的各种需求。",
      latestNotices: [],
    };
  },
  created() {
    this.getLatestNotices();
  },
  methods: {
    getLatestNotices() {
      listNotice({ pageNum: 1, pageSize: 3 }).then((response) => {
        if (response.rows && response.rows.length > 0) {
          this.latestNotices = response.rows;
        }
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
    background-image: url("../assets/images/index-background.jpg");
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
      width: 40%;
      font-size: 24px;
      color: #ffffff;
      text-align: center;
      text-decoration: none;
      padding: 15px 25px;
      background-color: #54dfbe;
      transition: background-color 0.3s;
      border: 1px solid transparent;
      border-radius: 5px;
      margin: 0 10px;
      transition: background-color 0.3s, border-color 0.3s;

      &:hover {
        background-color: #66b1ff;
        transform: scale(1.05);
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
