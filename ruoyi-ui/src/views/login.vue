<template>
  <div class="login-container">
    <div class="i-nav-box">
      <ul class="logo-pic-box">
        <li>
          <router-link to="/" style="color: #333">
            <img src="../assets/logo/sign-logo.png" alt="Logo" />
          </router-link>
        </li>
        <span></span>
        <li>登录中心</li>
      </ul>
      <div class="sign-top-phone">
        <img src="../assets/logo/sign-top-phone.png" />
        010-81188066
      </div>
    </div>
    <div class="login">
      <div class="sign-center-box">
        <img src="../assets/logo/sign-left.png" />
        <div class="sign-form-box">
          <el-form
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <img src="../assets/logo/sign-logo.png" alt="Logo" class="logo" />
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                type="text"
                auto-complete="off"
                placeholder="账号"
              >
                <svg-icon
                  slot="prefix"
                  icon-class="user"
                  class="el-input__icon input-icon"
                />
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                auto-complete="off"
                placeholder="密码"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon
                  slot="prefix"
                  icon-class="password"
                  class="el-input__icon input-icon"
                />
              </el-input>
            </el-form-item>
            <el-form-item prop="code" v-if="captchaEnabled">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="验证码"
                style="width: 63%"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon
                  slot="prefix"
                  icon-class="validCode"
                  class="el-input__icon input-icon"
                />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" />
              </div>
            </el-form-item>
            <el-checkbox
              v-model="loginForm.rememberMe"
              style="margin: 0px 0px 25px 0px"
              >记住密码</el-checkbox
            >
            <el-form-item style="width: 100%">
              <el-button
                :loading="loading"
                size="medium"
                type="primary"
                style="width: 100%"
                @click.native.prevent="handleLogin"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登 录 中...</span>
              </el-button>
              <div style="float: right" v-if="register">
                <router-link class="link-type" :to="'/register'"
                  >立即注册</router-link
                >
              </div>
            </el-form-item>
            <p style="padding-bottom: 10px; text-align: center">
              张家口建设投资集团有限公司
            </p>
            <div class="centered-link-container">
              <a
                href="https://beian.miit.gov.cn/"
                target="_blank"
                class="centered-link"
                >冀ICP备17012227号-2</a
              >
            </div>
          </el-form>
          <!--  底部  -->
        </div>
        <div class="sign-form-reflect"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: true,
        code: "",
        uuid: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }],
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled =
          res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password:
          password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), {
              expires: 30,
            });
            Cookies.set("rememberMe", this.loginForm.rememberMe, {
              expires: 30,
            });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove("rememberMe");
          }
          this.$store
            .dispatch("Login", this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || "/" }).catch(() => {});
            })
            .catch(() => {
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
        }
      });
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  //background-image: url("../assets/images/login-background.jpg");
  //background-image: url("../assets/logo/ouzbgw2dfaw.webp");
  background-size: cover;
  width: 100%;
  position: relative;
}
.sign-center-box {
  width: 880px;
  height: 480px;
  background-size: 100% 100%;
  position: absolute;
  left: 50%;
  margin-left: -390px;
  top: 50%;
  margin-top: -197px;
  box-shadow: 0 10px 15px 5px #7097b8;
}

.sign-center-box > img {
  height: 480px;
}

.sign-center-box .sign-form-box {
  width: 400px;
  height: 480px;
  background: #fff;
  float: right;
  padding: 20px 0;
}

.sign-center-box .sign-form-box .sign-form-img {
  width: 100%;
  height: auto;
  text-align: center;
}

.sign-center-box .sign-form-box .sign-form-info {
  width: 100%;
  height: auto;
  padding-top: 40px;
  text-align: center;
}

.sign-center-box .sign-form-box .sign-form-info > h5 {
  font-size: 12px;
  color: #000;
  height: 45px;
  line-height: 45px;
}

.sign-center-box .sign-form-box .sign-form-info > h5 a {
  color: #3aaead;
}

.sign-center-box .sign-form-box .sign-form-info > div > input {
  width: 220px;
  height: 40px;
  line-height: 40px;
  border-radius: 5px;
  border: 1px solid #a0a0a0;
  margin-bottom: 20px;
  font-size: 12px;
}

.sign-center-box .sign-form-box .sign-form-info > div .sign-btn {
  background: #00a09e;
  border: 0;
  color: #fff;
  font-size: 14px;
}

.sign-center-box .sign-form-box .sign-form-info .forget-pas {
  font-size: 12px;
  margin-top: 38px;
}

.sign-center-box .sign-form-box .sign-form-info .forget-pas a {
  color: #00a09e;
}
.sign-form-reflect {
  width: 100%;
  height: 108px;
  position: absolute;
  left: 0;
  bottom: -109px;
  margin-top: 2px;
  background: url(../assets/logo/sign-form-reflect.png) no-repeat left bottom;
  background-size: 100% 100%;
}
//.sign-center-box .sign-form-box .sign-form-info .sign-email>input {
//  background: url(../img/sign-email.jpg) no-repeat left center !important;
//  padding-left: 43px;
//  color: #666
//}
//
//.sign-center-box .sign-form-box .sign-form-info .sign-pas>input {
//  background: url(../img/sign-pas.png) no-repeat left center !important;
//  padding-left: 43px;
//  color: #666
//}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.login-code-img {
  height: 38px;
}
ul,
li {
  padding: 0;
  margin: 0;
  list-style: none;
}
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100vh; /* 使用视口高度 */
  background-image: url("../assets/logo/sign-bg.png");
  background-size: cover;
}

.i-nav-box {
  width: 100%;
  min-width: 1200px;
  border-bottom: 1px solid #e9ebec;
  padding: 0 20px;
  position: relative; /* 改为相对定位 */
  z-index: 2;
  background-color: white; /* 可选：为了与背景对比，使导航更显眼 */
  line-height: 100px;
}
//.i-nav-box {
//  width: 100%;
//  height: 100px;
//  line-height: 100px;
//  min-width: 1200px;
//  border-bottom: 1px solid #e9ebec;
//  position: absolute;
//  top: 0;
//  left: 0;
//  z-index: 2;
//  padding: 0 20px;
//  overflow: hidden
//}
.i-nav-box .logo-pic-box {
  width: auto;
  height: 100%;
  line-height: 100px;
  float: left;
  overflow: hidden;
}

.i-nav-box .logo-pic-box li {
  float: left;
  padding-left: 0;
  font-size: 21px;
}

.i-nav-box .logo-pic-box li img {
  height: 37px;
  width: auto;
  vertical-align: middle;
}

.i-nav-box .logo-pic-box span {
  width: 1px;
  height: 50px !important;
  background: #a6a7a7;
  display: inline-block;
  height: 100%;
  float: left;
  margin: 25px 20px;
}
.sign-top-phone {
  width: auto;
  height: 100%;
  float: right;
  font-size: 18px;
  color: #01a09c;
}
.centered-link-container {
  text-align: center; /* 使容器内的内容居中 */
  margin-top: 20px; /* 可选：增加顶部间距以改善视觉效果 */
  margin-bottom: 20px; /* 可选：增加底部间距以改善视觉效果 */
}
.centered-link {
  color: #1890ff; /* 设置链接文本颜色为蓝色 */
  text-decoration: none; /* 可选：去除链接下划线 */
}
.logo {
  padding-left: 50px;
}
</style>
