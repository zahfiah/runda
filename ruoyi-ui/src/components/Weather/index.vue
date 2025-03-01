<template>
  <div class="weather-container" :style="weatherBackground">
    <h2>实时天气</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="weather-info">
      <div class="temperature">
        <span>{{ temperature }}°C</span>
      </div>
      <div class="description">
        <span>{{ description }}</span>
      </div>
      <div class="details">
        <div class="detail-item">
          <i class="el-icon-wind-power"></i>
          <span>{{ windSpeed }} m/s 风速</span>
        </div>
        <div class="detail-item">
          <i class="el-icon-cloudy-and-sunny"></i>
          <span>{{ humidity }}% 湿度</span>
        </div>
        <div class="detail-item">
          <i class="el-icon-coordinate"></i>
          <span>{{ city }}, {{ province }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "Weather",
  data() {
    return {
      loading: true,
      error: null,
      temperature: null,
      description: null,
      windSpeed: null,
      humidity: null,
      city: null,
      province: null,
    };
  },
  computed: {
  },
  mounted() {
    this.fetchWeather();
  },
  methods: {
    async fetchWeather() {
      try {
        const response = await axios.get(
          `https://restapi.amap.com/v3/weather/weatherInfo?city=130700&key=3104b8bf3f96a8f4c80c1a41cfb19711`
        );
        const weatherData = response.data.lives[0];
        this.temperature = Math.floor(weatherData.temperature);
        this.description = weatherData.weather;
        this.windSpeed = weatherData.windpower;
        this.humidity = weatherData.humidity;
        this.city = weatherData.city;
        this.province = weatherData.province;
      } catch (error) {
        console.error('Error fetching weather data:', error);
        this.error = '无法获取天气数据';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.weather-container {
  width: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  background-size: cover;
  background-position: center;

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
</style>