<template>
  <nav ref="navbar"  :style="{ '--defaultTheme': defaultTheme}" :class="'jp-navbar  jp-navbar--' + navbarLayoutType ">
    <div class="jp-navbar__header">
      <!-- <h1 class="jp-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="jp-navbar__brand-lg" href="javascript:;">
          <img height="50px" src='../../assets/img/logo.png'/>
        </a>
        <a class="jp-navbar__brand-mini" href="javascript:;">
          <img :src="logo" height="40px" width="40px"/>
        </a>
      </h1> -->
      <h1 class="jp-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="jp-navbar__brand-lg" href="javascript:;"> <img :src="logo" height="40px" width="40px"/>{{$i18nMy.t(productName)}}</a>
        <a class="jp-navbar__brand-mini" href="javascript:;">
          <img :src="logo" height="40px" width="40px"/>
        </a>
      </h1>
    </div>
    <div class="jp-navbar__body clearfix"
      :class="{'top-menu-wrap': defaultLayout == 'dropdown-top'}"
      style="overflow:hidden"
    >

      <el-menu
        class="jp-navbar__menu"
        mode="horizontal">
        <el-menu-item class="jp-navbar__switch"  @click="sidebarFold = !sidebarFold">
             <i  :class="sidebarFold ? 'fa fa-indent':'fa fa-outdent'"></i>
        </el-menu-item>
      </el-menu>

      <el-menu v-if="defaultLayout !== 'dropdown-top'" class="jp-navbar__menu " :default-active="topMenuActiveIndex" ref="topMenu"  mode="horizontal">
        <el-menu-item  class="el_menu_item" v-for="menu in topMenuList"
          :index="menu.id"
          :key="menu.id"
          @click="showLeftMenu(menu)"
          :ref="menu.id"
          :menu="menu">
           <i :class="`${menu.icon} jp-sidebar__menu-icon`" style="display: inline-block!important;"></i>
          {{menu.name}}
          </el-menu-item>

        <el-submenu index="2" v-if="topHideMenuList.length != 0">
          <template slot="title">{{$i18nMy.t('更多')}}</template>
          <el-menu-item  v-for="menu in topHideMenuList"
          :index="menu.id"
          :key="menu.id"
          :ref="menu.id"
          @click="showLeftMenu(menu)"
          :menu="menu">
           <i :class="`${menu.icon} jp-sidebar__menu-icon`" style="display: inline-block!important;"></i>
          {{menu.name}}
          </el-menu-item>
        </el-submenu>
      </el-menu>
      <!-- 菜单放顶部 -->
      <menu-top v-if="defaultLayout === 'dropdown-top'" />

      <el-menu
        class="jp-navbar__menu jp-navbar__menu--right"
        mode="horizontal">
<!--        <el-menu-item class="hide-sm">
          <template slot="title">
                <color-picker></color-picker>
          </template>
        </el-menu-item>
        <el-menu-item class="hide-sm">
          <template slot="title">
                <notice-icon
                    class="action notice"
                    :tabs="noticeTabs" >
              </notice-icon>
          </template>
        </el-menu-item> -->

        <el-menu-item v-if="defaultLayout !== 'top' && defaultLayout !== 'dropdown-top'" class="jp-navbar__avatar">
          <route-search />
        </el-menu-item>

        <el-menu-item v-if="defaultLayout === 'top' && defaultLayout !== 'dropdown-top'" @click="openSearchMenu">
          <i class="el-icon-search" />
          <full-screen-route-search ref="fullRouteSearch" />
        </el-menu-item>

        <el-menu-item class="jp-navbar__avatar">
          <el-dropdown :show-timeout="0" placement="bottom">
            <span class="el-dropdown-link">
              <img :src="languageIcon" style="border-radius: 0px;width: 24px;">
              <span class="less-md-hide">{{ language }}</span>
            </span>
            <el-dropdown-menu slot="dropdown" style="margin-top: -10px;">
              <el-dropdown-item v-for="(item,i) in languageList" :key="i" @click.native="changeLanguage" :lang='item.lang'>
                <img :src="item.src" style="border-radius: 0px;width: 24px;"> {{item.text}}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>

        <el-menu-item class="jp-navbar__avatar">
          <el-dropdown :show-timeout="0" placement="bottom" trigger="click" >
            <span class="el-dropdown-link" style="display: flex; align-items: center; justify-content: center;" >
              <i class="el-icon-user-solid"></i>
              <el-tooltip class="item" effect="dark" :content="userName" placement="bottom-end">
                <div class="less-md-hide user-name">{{ userName }}</div>
              </el-tooltip>
            </span>
            <el-dropdown-menu slot="dropdown" style="margin-top: -10px;">
              <!-- <el-dropdown-item @click.native="updatePasswordHandle()">{{$i18nMy.t('修改密码')}}</el-dropdown-item> -->
              <el-dropdown-item @click.native="logoutHandle()">{{$i18nMy.t('退出')}}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
<!--        <el-menu-item class="hide-sm"  @click="showRight">
          <template slot="title">
               <i class="el-icon-more rotate-90" @click="showRight"></i>
          </template>
        </el-menu-item> -->
      </el-menu>
    </div>
    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
  </nav>
</template>

<script>
  import UpdatePassword from './UpdatePassword'
  import {clearLoginInfo} from '@/utils'
  import NoticeIcon from '@/components/NoticeIcon'
  import ColorPicker from '@/components/colors/ColorPicker'
  import RouteSearch from '@/components/RouteSearch/search'
  import FullScreenRouteSearch from '@/components/RouteSearch/search-fullscreen'

  import MenuTop from './components/MenuTop/index.vue'

  export default {
    data () {
      return {
        updatePassowrdVisible: false,
        activeIndex: '',
        topMenuList: [],
        topHideMenuList: [],
        allMenuList: [],
        languageList:[],
        language: "中文",
        languageIcon: "/static/images/china.png",
        screenWidth: document.body.clientWidth,
        noticeTabs: [
        ]
      }
    },
    components: {
      UpdatePassword,
      ColorPicker,
      NoticeIcon,
      RouteSearch,
      FullScreenRouteSearch,
      MenuTop
    },
    computed: {
      navbarLayoutType () {
        return this.$store.state.common.navbarLayoutType
      },
      topMenuActiveIndex: {
        get () {
          return this.$store.state.common.topMenuActiveIndex
        },
        set (val) {
          this.$store.commit('common/updateTopMenuActiveIndex', val)
        }
      },
      sidebarFold: {
        get () {
          return this.$store.state.common.sidebarFold
        },
        set (val) {
          this.$store.commit('common/updateSidebarFold', val)
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
      userName: {
        get () {
          return this.$store.state.user.loginName + ' - ' + this.$store.state.user.name
        }
      },
      photo: {
        get () {
          return this.$store.state.user.photo
        }
      },
      logo () {
        return this.$store.state.config.logo
      },
      defaultTheme () {
        return this.$store.state.config.defaultTheme
      },
      productName () {
        return this.$store.state.config.productName
      },
      defaultLayout () {
        return this.$store.state.config.defaultLayout
      }
    },
    created () {
      //document.body.style.zoom = 0.9
      this.allMenuList = JSON.parse(sessionStorage.getItem('allMenuList') || '[]')
      if (this.defaultLayout === 'top') {
        this.topMenuActiveIndex = this.allMenuList[0].id
        this.showLeftMenu(this.allMenuList[0])
      } else {
        this.$store.commit('common/updateLeftMenuList', this.allMenuList)
      }
      var tmp = window.langList.find(item => { return item.lang == (localStorage.getItem('lang') || 'en-US')})
      this.languageList=window.langList
      this.language = tmp.text
      this.languageIcon = tmp.src
      /* this.$http({
        url: '/notify/oaNotify/self/data?pageNo=1&pageSize=10&isSelf=true&readFlag=0',
        method: 'get'
      }).then(({data}) => {
        this.noticeTabs[0].count = data.page.count
        this.noticeTabs[0].url = '/notify/MyNotifyList'
        this.noticeTabs[0].list = data.page.list.map((item) => {
          return {
            id: item.id,
            avatar: item.createBy.photo,
            title: item.title,
            description: item.content,
            datetime: item.createDate,
            type: '通知'
          }
        })
      })
      this.$http({
        url: '/mailBox/list?pageNo=1&pageSize=10&mail.title=&readstatus=0',
        method: 'get'
      }).then(({data}) => {
        this.noticeTabs[1].count = data.page.count
        this.noticeTabs[1].url = '/mailbox/index'
        this.noticeTabs[1].list = data.page.list.map((item) => {
          return {
            id: item.id,
            avatar: item.sender.photo,
            title: item.mail.title,
            description: item.mail.content,
            datetime: item.sendtime,
            type: '站内信'
          }
        })
      }) */
    },
    mounted () {
      if (this.defaultLayout === 'top') {
        this.fixTopMenu()
      }
    },
    watch: {
      topMenuActiveIndex (val) {
        this.topMenuList.forEach((menu) => {
          if (menu.id === val) {
            this.showLeftMenu(menu)
          }
        })
        this.topHideMenuList.forEach((menu) => {
          if (menu.id === val) {
            this.showLeftMenu(menu)
          }
        })
      },
      defaultLayout (val) {
        if (this.defaultLayout === 'top') {
          let needSetLeft = true
          this.allMenuList.forEach((item) => {
            if (item.id === this.topMenuActiveIndex) {
              this.showLeftMenu(item)
              needSetLeft = false
            }
          })
          if (needSetLeft) {
            this.topMenuActiveIndex = this.allMenuList[0].id
            this.showLeftMenu(this.allMenuList[0])
          }
          this.fixTopMenu()
        } else {
          this.topMenuList = []
          this.topHideMenuList = []
          this.$store.commit('common/updateLeftMenuCategory', '')
          this.$store.commit('common/updateLeftMenuList', this.allMenuList)
        }
      }
    },
    methods: {
      topTranslateRouterPath (menu) {
        return '/' + menu.href.replace(/^\//g, '')
      },
      // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
      topGotoRouteHandle (menu) {
        console.log("跳转至指定路由")
        let routePath = this.topTranslateRouterPath(menu)
        this.$router.push({path: routePath})
      },
      openSearchMenu() {
        this.$refs.fullRouteSearch.visible = true
      },
      fixTopMenu () {
        let width = window.getComputedStyle(this.$refs.navbar).width
        let size = (parseInt(width) - 800) / 124
        this.topMenuList = []
        this.topHideMenuList = []
        this.allMenuList.forEach((item, index) => {
          if (index < size - 1) {
            this.topMenuList.push(item)
          } else {
            this.topHideMenuList.push(item)
          }
        })
      },
      showRight () {
        this.$emit('showRight', true)
      },
      changeLanguage(e){
        var lang=e.currentTarget.lang
        this.language = e.currentTarget.textContent
        this.languageIcon = e.currentTarget.getElementsByTagName("img")[0].src
        localStorage.setItem('lang',lang)
        location.reload()
      },
      showLeftMenu (menu) {
        this.$store.commit('common/updateLeftMenuList', menu.children)
        this.$store.commit('common/updateLeftMenuCategory', menu.name)
      },
      // 修改密码
      updatePasswordHandle () {
        this.updatePassowrdVisible = true
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init()
        })
      },
      // 退出
      logoutHandle () {
        this.$confirm($i18nMy.t(`确定进行[退出]操作?`), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.$http({
            url: '/sys/logout',
            method: 'get'
          }).then(({data}) => {
            if (data && data.success) {
              clearLoginInfo()
              if (process.env.VUE_APP_SSO_LOGIN == 'true') {
                window.location.href = process.env.VUE_APP_SSO_SERVER
              } else {
                this.$router.replace({name: 'login'})
              }
            }
          })
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
.top-menu-wrap {
  display: flex;
}
@media (max-width: 992px) {
  .less-md-hide {
    display: none;
  }
}

.user-name {
  width: 130px;
  text-overflow: ellipsis;
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

</style>
