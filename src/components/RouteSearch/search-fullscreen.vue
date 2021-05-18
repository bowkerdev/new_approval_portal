<template>
  <el-dialog 
    :title="$i18nMy.t('搜索菜单')"
    :visible.sync="visible"
    :append-to-body="true"
    custom-class="full-route-search-wrap"
    :before-close="beforeCloseFn"
  >
    <el-input
      :placeholder="$i18nMy.t('菜单名/菜单路由')"
      v-model="state"
      clearable
      style="margin-bottom: 20px;"
    />
    <el-scrollbar class="match-item-pane">
      <ul class="match-item-list">
        <template v-for="item in options">
          <li
            :key="item.item.path"
            class="match-item"
            @click="selectItem(item)"
          >
            {{ item.item.title.join('>') }}
          </li>
        </template>
      </ul>
    </el-scrollbar>
  </el-dialog>
</template>

<script>
import Fuse from 'fuse.js'
import { fuse_option, generateRoutesInfo, initPinyinTitle } from './index.js'

export default {
  name: 'FullRouteSearch',
  data() {
    return {
      visible: false,
      state: '',
      searchPool: [],
      options: [],
      fuse: undefined
    }
  },
  computed: {
    routes() {
      return this.$store.state.common.leftMenuList
    }
  },
  watch: {
    state(val) {
      if(val) {
        this.options = this.fuse.search(val)
      } else {
        this.options = []
      }
    },
    searchPool(val) {
      this.initFuse(val)
    }
  },
  mounted() {
    this.searchPool = initPinyinTitle(this.generateRoutesInfo(this.routes))
  },
  methods: {
    generateRoutesInfo(routes, prefixTitle) {
      return generateRoutesInfo(routes, prefixTitle)
    },
    initFuse(list) {
      this.fuse = new Fuse(list, fuse_option)
    },
    selectItem(item) {
      if (item.item.path === this.$route.path) {
        return
      }
      this.state = ''
      this.visible = false
      this.$router.push(item.item.path)
    },
    beforeCloseFn() {
      this.visible = false
      this.state = ''
    }
  }
}
</script>

<style lang="scss">
.full-route-search-wrap.el-dialog {
  top: 30px;
  transform: translate(-50%);

  .match-item-pane .el-scrollbar__wrap {
    max-height: 30vh;

    .match-item-list {
      margin: 0;
      padding: 0;
    }

    .match-item {
      padding: 0 20px;
      margin: 0;
      line-height: 34px;
      cursor: pointer;
      color: #606266;
      font-size: 13px;
      list-style: none;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      &:hover {
        background-color: #F5F7FA;
      }
    }
    
  }
}
</style>
