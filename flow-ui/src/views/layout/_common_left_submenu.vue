<template>
  <el-submenu
    v-if="menu.children && menu.children.length >= 1"
    :index="menu.id + ''"
    :popper-class="'jp-sidebar--' + sidebarLayoutSkin + '-popper'">
    <template slot="title">
      <i :class="`${menu.icon} jp-sidebar__menu-icon`"></i>
      <div class="menu-name-text" slot="title">
        <span>{{ menu.name }}</span>
      </div>
    </template>
    <sub-menu
      v-for="item in menu.children"
      :key="item.id"
      :menu="item"
      :showCompleteName="showCompleteName"
      :dynamicMenuRoutes="dynamicMenuRoutes">
    </sub-menu>
  </el-submenu>
  <el-menu-item v-else :index="menu.id + ''" @click="gotoRouteHandle(menu)">
    <i :class="`${menu.icon} jp-sidebar__menu-icon`"></i>
    <div class="menu-name-text" slot="title">
      <span>{{ menu.name }}</span>
    </div>
  </el-menu-item>
</template>

<script>
  import SubMenu from './_common_left_submenu'

  export default {
    name: 'sub-menu',
    props: {
      menu: {
        type: Object,
        required: true
      },
      dynamicMenuRoutes: {
        type: Array,
        required: true
      },
      showCompleteName: {
        type: Boolean,
        default: false
      }
    },
    components: {
      SubMenu
    },
    computed: {
      sidebarLayoutSkin: {
        get () {
          return this.$store.state.common.sidebarLayoutSkin
        }
      }
    },
    methods: {
      translateRouterPath (menu) {
        return '/' + menu.href.replace(/^\//g, '')
      },
      // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
      gotoRouteHandle (menu) {
        const route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.id)
        if (route.length >= 1) {
          let routePath = this.translateRouterPath(menu)
          this.$router.push({path: routePath})
        }
      }
    }
  }
</script>
