<template>
  <div class="route-search-wrap">
    <el-autocomplete
      class="inline-input"
      v-model="state"
      :fetch-suggestions="querySearch"
      :placeholder="$i18nMy.t('菜单名/菜单路由')"
      :trigger-on-focus="false"
      @select="handleSelect"
    >
      <template slot-scope="{ item }">
        <div class="match-item">{{ item.item.title.join('>') }}</div>
      </template>
    </el-autocomplete>
  </div>
</template>

<script>
import Fuse from 'fuse.js'
import { fuse_option, generateRoutesInfo, initPinyinTitle } from './index.js'

export default {
  name: 'RouteSearch',
  data() {
    return {
      state: '',
      searchPool: [],
      fuse: undefined
    }
  },
  computed: {
    routes() {
      return this.$store.state.common.leftMenuList
    }
  },
  watch: {
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
    querySearch(queryString, cb) {
      const res = queryString !== ''? this.fuse.search(queryString): []
      cb(res)
    },
    handleSelect(item) {
      if (item.item.path === this.$route.path) {
        return
      }
      this.$router.push(item.item.path)
      this.state = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.route-search-wrap {
  width: 380px;

  .el-autocomplete.inline-input {
    width: 100%;
  }

  .match-item {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
