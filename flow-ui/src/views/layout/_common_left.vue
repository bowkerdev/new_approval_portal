<template>
  <aside class="jp-sidebar" v-show="defaultLayout !== 'dropdown-top'" :style="sidebarLayoutSkin === '5' || sidebarLayoutSkin === '9' ?`background:${defaultTheme}`: ''" :class="'jp-sidebar--' + sidebarLayoutSkin">
    <div class="jp-sidebar__inner">
      <el-menu unique-opened
               :default-active="menuActiveName || 'home'"
               :collapse="sidebarFold"
               :collapseTransition="false"
               class="jp-sidebar__menu">
        <li class="jp-menu-category" v-if="!sidebarFold">{{leftMenuCategory}}</li>
        <sub-menu
          v-for="menu in leftMenuList"
          :key="menu.id"
          :menu="menu"
          :dynamicMenuRoutes="dynamicMenuRoutes">
        </sub-menu>
      </el-menu>
    </div>
  </aside>
</template>
<style lang="scss">
.jp-menu-category {
    padding: 0 20px;
    font-size: 12px;
    line-height: 38px;
    color: #76838f;
    text-transform: uppercase;
    -webkit-transition: all .25s,font .1s .15s,color .1s .15s;
    transition: all .25s,font .1s .15s,color .1s .15s;
}
/* show complete sidebar menu name */
aside {
  .jp-sidebar__menu.el-menu .el-menu-item,
  .jp-sidebar__menu.el-menu .el-submenu > .el-submenu__title {
    height: auto;
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .jp-sidebar__menu .menu-name-text {
    display: inline-block;
  }

  .jp-sidebar__menu.el-menu .menu-name-text {
    white-space: normal;
  }

  .el-submenu__title,
  li.el-menu-item,
  .el-submenu {
    height: auto !important;
    line-height: 1.5 !important;
  }

  .el-menu--collapse .el-menu-item {
    height: 40px !important;
    line-height: 40px !important;
  }
}
.el-menu--vertical .el-menu.el-menu--popup {
  .el-menu-item {
    height: auto !important;
    line-height: 1.5 !important;
    padding-top: 10px;
    padding-bottom: 10px;
  }

  .menu-name-text {
    white-space: normal;
    display: inline-block;
  }
}
</style>

<script>
  import SubMenu from './_common_left_submenu'

  import { currentLangInshowCompName } from '@/utils/i18n'

  export default {
    props: {
      showCompleteName: {
        type: Boolean,
        default: false
      }
    },
    data () {
      return {
        dynamicMenuRoutes: []
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
      },
      sidebarFold: {
        get () {
          return this.$store.state.common.sidebarFold
        }
      },
      isTab: {
        get () {
          return this.$store.state.common.isTab
        }
      },
      leftMenuList: {
        get () {
          return this.$store.state.common.leftMenuList
        },
        set (val) {
          this.$store.commit('common/updateLeftMenuList', val)
        }
      },
      leftMenuCategory: {
        get () {
          return this.$store.state.common.leftMenuCategory
        },
        set (val) {
          this.$store.commit('common/updateLeftMenuCategory', val)
        }
      },
      menuActiveName: {
        get () {
          return this.$store.state.common.menuActiveName
        },
        set (val) {
          this.$store.commit('common/updateMenuActiveName', val)
        }
      },
      mainTabs: {
        get () {
          return this.$store.state.common.mainTabs
        },
        set (val) {
          this.$store.commit('common/updateMainTabs', val)
        }
      },
      defaultTheme () {
        return this.$store.state.config.defaultTheme
      },
      defaultLayout () {
        return this.$store.state.config.defaultLayout
      },
      mainTabsActiveName: {
        get () {
          return this.$store.state.common.mainTabsActiveName
        },
        set (val) {
          this.$store.commit('common/updateMainTabsActiveName', val)
        }
      }
    },
    watch: {
      $route: 'routeHandle'
    },
    created () {
      // this.menuList = JSON.parse(sessionStorage.getItem('menuList') || '[]')
      this.dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
      this.routeHandle(this.$route)
    },
    methods: {
      // ????????????
      routeHandle (route) {
        if (this.isTab) {
          // tab??????, ??????????????????
          let tab = this.mainTabs.filter(item => item.fullPath === route.fullPath)[0]
          let tab2 =this.$store.state.tagsView.visitedViews.filter(item => item.fullPath === route.fullPath.replace("/redirect/","/"))[0]
          if (!tab2&&tab) {
            if(route.matched.length>=2&&route.matched[1].instances!=null){
              var instances =route.matched[1].instances.$refs ==null ?route.matched[1].instances.default:route.matched[1].instances
              if(instances!=null && instances.$refs.searchForm){
                instances.$refs.searchForm.resetFields()
              }
              if(instances!=null&& instances.initOk == true){
                sessionStorage.setItem('instances.initOk', "true")
              }
            }
          }
          if (!tab) {
            if (route.meta.isDynamic) {
              route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0]
              if (!route) {
                return console.error('???????????????????????????!')
              }
            }
            else if(route.component == null&&route.meta!=null&&route.meta.isHome){
              route = this.dynamicMenuRoutes.filter(item => item.meta.affix == true)[0]
              if (!route) {
                return console.error('???????????????????????????!')
              }
              this.$router.push(route.path)
              return
            }
            tab = {
              menuId: route.meta.menuId || route.name,
              name: route.name,
              title: this.$router.currentRoute.query.title || route.meta.title,
              type: route.meta.type,
              iframeUrl: route.meta.iframeUrl || '',
              query: this.$router.currentRoute.query,
              parmas: this.$router.currentRoute.parmas,
              fullPath: this.$router.currentRoute.fullPath
            }
            this.mainTabs = this.mainTabs.concat(tab)
          }
          tab.title = this.$router.currentRoute.query.title || route.meta.title
          this.menuActiveName = tab.menuId + ''
          this.mainTabsActiveName = tab.fullPath
          let topMenuActiveIndex = route.meta.parentIds && route.meta.parentIds.split(',').length > 2 ? route.meta.parentIds.split(',')[2] : '0'
          this.$store.commit('common/updateTopMenuActiveIndex', topMenuActiveIndex)
        }
      },
      isShowComplete () {
        return currentLangInshowCompName()
      }
    }
  }
</script>
