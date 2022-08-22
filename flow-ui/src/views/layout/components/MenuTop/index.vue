<template>
  <div class="top-menu-pane jp-navbar-height">
    <el-button
      type="text"
      icon="el-icon-caret-left"
      class="move-btn pre"
      :disabled="topMenutranslateX == 0"
      @click="movePre"
    />
    <!-- 菜单 -->
    <div ref="topMenuListWrap" class="menu-scroll-wrap">
      <el-menu mode="horizontal" unique-opened
        :default-active="menuActiveName || 'home'"
        :background-color="defaultTheme"
        text-color="#b5b5b5"
        active-text-color="#ffffff"
        active-text-
        class="top-menu-list"
        :style="`transform: translateX(${topMenutranslateX}px);`"
      >
        <menu-item ref="menuItemRefs"
          v-for="menu in allMenuList" :key="menu['id']"
          :item="menu"
          :dynamicMenuRoutes="dynamicMenuRoutes"
        />

      </el-menu>
    </div>
    <el-button
      type="text"
      icon="el-icon-caret-right"
      class="move-btn next"
      :disabled="(baseMenuItemIndex == menuItemsWidth.length - 2) || leftWidth <= viewWidth"
      @click="moveNext"
    />
  </div>
</template>

<script>
import { mapState } from 'vuex'

import MenuItem from './MenuItem.vue'

export default {
  name: 'MenuTop',
  components: { MenuItem },
  data() {
    return {
      dynamicMenuRoutes: [],
      allMenuList: [],          //菜单
      viewWidth: 300,           //可视区域大小，计算获取，不需修改
      baseMenuItemIndex: -1,    //往前移动遮挡的一级菜单索引
      menuItemsWidth: []        // 一级菜单条目的宽度
    }
  },
  computed: {
    // 系统名字是否折叠状态
    ...mapState({
      sidebarFold: state => state.common.sidebarFold,
      defaultTheme: state => state.config.defaultTheme
    }),
    menuActiveName: {
      get () {
        return this.$store.state.common.menuActiveName
      },
      set (val) {
        this.$store.commit('common/updateMenuActiveName', val)
      }
    },
    // x轴偏移量 ，负数值
    topMenutranslateX() {
      if (this.baseMenuItemIndex < 0) { return 0 }
      let sum = 0
      for (let i = 0; i <= this.baseMenuItemIndex; i++) {
        sum -= this.menuItemsWidth[i]
      }
      return sum
    },
    leftWidth () {
      let sum = 0
      for (let i = this.baseMenuItemIndex + 1; i < this.menuItemsWidth.length; i++) {
        sum += this.menuItemsWidth[i]
      }
      return sum
    }
  },
  watch: {
    sidebarFold() {
      this.handleResize()
    }
  },
  created() {
    this.allMenuList = JSON.parse(sessionStorage.getItem('allMenuList') || '[]')
    this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
  },
  mounted() {
    // 监听窗口大小变化，获取可视区域大小，菜单大小
    window.addEventListener('resize', this.handleResize)
    // 轮询菜单一级条目ref是否渲染好
    this.inquiryMenuItemRefs()
  },
  methods: {
    // 生成ref， 获取宽度
    inquiryMenuItemRefs() {
      if (this.$refs.menuItemRefs) {
        this.handleResize()
      } else {
        setTimeout(() => {
          this.inquiryMenuItemRefs()
        }, 400)
      }
    },
    // 监听窗口大小变化
    handleResize() {
      setTimeout(() => {
        this.menuItemsWidth = this.genMenuItemWidth()
        this.viewWidth = this.$refs.topMenuListWrap.offsetWidth
      }, 200)
    },
    genMenuItemWidth() {
      const refList = this.$refs.menuItemRefs
      return refList.map(el => el.$el.offsetWidth)
    },
    movePre() {
      this.baseMenuItemIndex -= 1
    },
    moveNext() {
      this.baseMenuItemIndex += 1
    },
    destroyed() {
      window.addEventListener('resize', this.handleResize)
    }
  }

}
</script>

<style lang="scss" scoped>
.top-menu-wrap {
  display: flex;
}
.top-menu-pane {
  margin-left: 20px;
  margin-right: 4%;
  width: 50%;
  flex-grow: 2;
  flex-shrink: 1;
  display: flex;
}
.menu-scroll-wrap {
  width: 60%;
  display: flex;
  flex-grow: 1;
  flex-shrink: 1;
  border-right: 0;
  overflow: hidden;
}
.top-menu-list {
  display: flex;
  background-color: transparent;
  transition: all .3s cubic-bezier(.645,.045,.355,1),border 0s,color .1s,font-size 0s;

  & ::v-deep .el-submenu__title .el-submenu__icon-arrow {
    right: 10px;
  }
}
.move-btn {
  padding-left: 10px;
  padding-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.menu-scroll-wrap ::v-deep {
  .el-menu.el-menu--horizontal {
    border-bottom: none;
  }
  .el-menu-item,
  .el-submenu__title {
    padding-left: 10px;
    padding-right: 0;
  }
  .el-submenu,
  .el-menu-item,
  .el-submenu__title {
    height: 100%;
    display: flex;
    line-height: unset;
  }
  .el-menu .el-menu-item i,
  .el-menu .el-submenu__title i {
    color: inherit !important;
  }
  .el-submenu__title {
    align-items: center;
  }
}
</style>
