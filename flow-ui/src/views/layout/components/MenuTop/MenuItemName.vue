<template>
  <div class="wrap">
    <i v-if="icon" :class="`${icon} jp-sidebar__menu-icon`"></i>
    <div class="menu-name-text">
      <span>{{ title }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "MenuItemName",
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  }
}
</script>

<style scoped>
.wrap {
  padding-right: 20px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.el-submenu__title .menu-name-text > span {
  white-space: nowrap !important;
}
.el-menu-item [class^=el-icon-] {
  margin-right: 0;
}
.menu-name-text {
  padding-right: 10px;
}
</style>
