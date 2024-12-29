<template>
  <div class="app-container home">
    <div class="title-container">
      <h1>润达网站</h1>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index"
};
</script>

<style scoped lang="scss">
.home {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .title-container {
    text-align: center;
    
    h1 {
      font-size: 48px;
      font-weight: bold;
      color: #333;
    }
  }
}
</style>
