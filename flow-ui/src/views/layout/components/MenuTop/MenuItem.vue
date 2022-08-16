<template>
  <div>
    <el-submenu
      v-if="item.children && item.children.length >= 1"
      ref="subMenu"
      :index="item.id + ''"
      popper-append-to-body
      :popper-class="`jp-sidebar--${sidebarLayoutSkin}-popper top-menu-type`"
    >
      <template slot="title">
        <menu-item-name :icon="item.icon" :title="item.name"  />
      </template>
      <menu-item
        v-for="child in item.children"
        :key="child.id"
        :item="child"
        :dynamicMenuRoutes="dynamicMenuRoutes"
        class="nest-menu"
      />
    </el-submenu>

    <template v-else>
      <el-menu-item :index="item.id + ''" @click="gotoRouteHandle(item)">
        <template slot="title">
          <menu-item-name :icon="item.icon" :title="item.name"  />
        </template>
      </el-menu-item>
    </template>
  </div>
</template>

<script>
import { mapState } from 'vuex'
// import { MenuItemName } from './MenuItemName.vue'

export default {
  name: "MenuItem",
  components: { 
    MenuItemName: () => import('./MenuItemName.vue')
  },
  props: {
    item: {
      type: Object,
      default: () => {},
      required: true
    },
    dynamicMenuRoutes: {
      type: Array,
      default: () => [],
      required: true
    }
  },
  data() {
    return {}
  },
  computed: {
    ...mapState({
      sidebarLayoutSkin: state => state.common.sidebarLayoutSkin
    })
  },
  methods: {
    translateRouterPath(menu) {
      return "/" + menu.href.replace(/^\//g, "")
    },
    // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
    gotoRouteHandle(menu) {
      const route = this.dynamicMenuRoutes.filter(
        (item) => item.meta.menuId === menu.id
      )
      if (route.length >= 1) {
        let routePath = this.translateRouterPath(menu)
        this.$router.push({ path: routePath })
      }
    }
  }
}
</script>

<style>
.top-menu-type {
  border-top: 1px solid #FFF;
  background-color: inherit !important;
}
.nest-menu .el-submenu__title i.el-submenu__icon-arrow {
  color: inherit !important;
}
</style>