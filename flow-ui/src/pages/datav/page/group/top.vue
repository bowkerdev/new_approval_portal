<template>

  <div class="top-nav">

    <el-row  class="row-bg" >
    <el-col :span="6"><div class="datav-title">在线大屏设计</div></el-col>
    <el-col :span="12">
      <el-menu class="nav"
             mode="horizontal"
             background-color="#212528"
             text-color="#fff"
             active-text-color="#409EFF"
             @mousedown="contain.handleMouseDown">
      <el-submenu :index="index+''"
                  v-for="(item,index) in baseList"
                  :key="index">
        <template slot="title">
          <el-tooltip effect="dark"
                      :content="item.label"
                      placement="top">
            <i :class="'nav__icon iconfont '+item.icon"></i>
          </el-tooltip>
        </template>
        <div style="width:390px">
          <el-menu-item v-for="(citem,cindex) in item.children"
                        @click="handleAdd(citem.option,true)"
                        :key="cindex"
                        class="menu-inline"
                        :index="`${index}-${cindex}`">
            <div class="usehove">
              <img :src="citem.option.img"
                   class="inside-img">
              <div class="bottom-text">{{citem.label}}</div>
            </div>

          </el-menu-item>
        </div>
      </el-submenu>

      
    </el-menu>
    </el-col>
    <el-col :span="6" style="float:right">
        <el-menu class="nav"
             mode="horizontal"
             background-color="#212528"
             text-color="#fff"
             active-text-color="#409EFF"
             @mousedown="contain.handleMouseDown">
          <el-menu-item index="9"
                        :disabled='!contain.canUndo'
                        @click="contain.editorUndo">
            <el-tooltip effect="dark"
                        content="撤销"
                        placement="top">
              <i class="nav__icon el-icon-arrow-left"></i>
            </el-tooltip>
          </el-menu-item>
          <el-menu-item index="10"
                        :disabled='!contain.canRedo'
                        @click="contain.editorRedo">
            <el-tooltip effect="dark"
                        content="重做"
                        placement="top">
              <i class="nav__icon el-icon-arrow-right"></i>
            </el-tooltip>
          </el-menu-item>
          <el-menu-item index="12"
                        @click="$refs.result.show=true">
            <el-tooltip effect="dark"
                        content="大屏结构"
                        placement="top">
              <i class="nav__icon el-icon-info"></i>
            </el-tooltip>
          </el-menu-item>
          <el-menu-item index="6"
                      @click="handleReset"
                      v-show="!contain.menuFlag">
          <el-tooltip effect="dark"
                      content="还原"
                      placement="top">
            <i class="nav__icon iconfont icon-reset"></i>
          </el-tooltip>
        </el-menu-item>

      <el-menu-item index="7"
              @click="handleView"
              v-show="contain.menuFlag">
        <el-tooltip effect="dark"
                    content="预览"
                    placement="top">
          <i class="nav__icon iconfont icon-view"></i>
        </el-tooltip>
      </el-menu-item>
      <el-menu-item index="11"
                    @click="handleImg">
        <el-tooltip effect="dark"
                    content="导出图片"
                    placement="top">
          <i class="nav__icon el-icon-camera"
             style="font-size:18px"></i>
        </el-tooltip>
      </el-menu-item>
      <el-menu-item index="8"
                    @click="handleBuild">
        <el-tooltip effect="dark"
                    content="保存"
                    placement="top">
          <i class="nav__icon iconfont icon-build"></i>
        </el-tooltip>
      </el-menu-item>
      </el-menu>
    </el-col>
  </el-row>
    
    <result ref="result"></result>
  </div>

</template>

<script>
import { uuid } from '../../utils/utils'
import baseList from '../../option/base'
import { updateComponent } from '../../api/visual'
import result from './result';
import request from '@/utils/httpRequest'
export default {
  inject: ["contain"],
  provide () {
    return {
      contain: this.contain
    };
  },
  components: {
    result
  },
  data () {
    return {
      baseList: baseList
    }
  },
  methods: {
    vaildData (id) {
      const list = [];
      for (var i = 0; i < 20; i++) {
        list.push(i + '')
      }
      return list.includes(id)
    },
    handleImg () {
      this.$confirm('是否导出大屏图片？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$Screenshot(document.querySelector("#wrapper")).then(canvas => {
          this.downFile(canvas.toDataURL("image/jpeg", 0.8), new Date().getTime());
          this.$message.success('图片导出成功')
        });
      }).catch(() => {

      });

    },
    handleView () {
      this.contain.menuFlag = false;
      this.contain.handleInitActive();
      this.contain.setScale(document.body.clientWidth);
    },
    handleReset () {
      this.contain.menuFlag = true;
      this.contain.setScale(this.contain.contentWidth);
    },
    handleBuild () {
      // if (this.$route.query.id <= 100) {
      //   this.$message.error('模版例子不能修改')
      //   return
      // }
      this.contain.handleInitActive();
      const loading = this.$loading({
        lock: true,
        text: '正在保存配置，请稍后',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      this.$nextTick(() => {
        html2canvas(document.getElementById('content'), {
          useCORS: true,
          backgroundColor: null,
          allowTaint: true
        }).then(canvas => {
          function dataURLtoFile (dataurl, filename) {
            var arr = dataurl.split(','),
              mime = arr[0].match(/:(.*?);/)[1],
              bstr = atob(arr[1]),
              n = bstr.length,
              u8arr = new Uint8Array(n);
            while (n--) {
              u8arr[n] = bstr.charCodeAt(n);
            }
            return new File([u8arr], filename, { type: mime });
          }

          var file = dataURLtoFile(canvas.toDataURL('image/jpeg', 0.1), new Date().getTime() + '.jpg');
          var formdata = new FormData();
          formdata.append('file', file)
          request({
            url: '/sys/file/webupload/upload?uploadPath=/datascreen',
            method: "POST",
            data: formdata,
            headers: { "Content-Type": "multipart/form-data" },
          })
          .then(res => {
            const url = res.data.url;
            const formdata = {
                screenShot: url,
                detail: JSON.stringify(this.contain.config),
                component: JSON.stringify(this.contain.nav)
            }
            if(this.$route.query.id){
              formdata.id = this.$route.query.id
            }
            return updateComponent(formdata)
          }).then(() => {
            loading.close();
            this.$confirm('保存成功大屏配置, 是否打开预览?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              let routeUrl = this.$router.resolve({
                path: '/view?id=' + this.contain.id
              })
              window.open(routeUrl.href, '_blank');
            }).catch(() => {

            });
          }).catch(() => {
            this.$message.error('保存失败')
            loading.close();
          })
        })
      })
    },
    handleAdd (option, first = false) {
      let obj = this.deepClone(option);
      obj.left = 0;
      obj.top = 0
      obj.index = uuid();
      if (first) {
        this.contain.nav.unshift(obj);
      } else {
        this.contain.nav.push(obj);
      }

    },
    handleUndo () {
      this.contain.editorUndo()
    },
    handleRedo () {
      this.contain.editorRedo()
    }
  }
}
</script>

<style lang='scss'>

.top-nav {
  .datav-title{
    line-height: 45px;
    padding-left: 100px;
    color: #909399;
    background-color: rgb(33, 37, 40);
    font-size: 20px;
  }
  .nav {
    border-bottom: 0 !important;
    height: 45px;
    line-height: 45px;
    overflow: hidden;
  }
  .nav__icon {
    margin-right: 5px;
  }
  .nav .el-submenu .el-submenu__title,
  .nav .el-menu-item {
    height: 45px;
    line-height: 45px;
    font-size: 12px;
  }

  .el-menu-item {
    height: 100px !important;
  }
}
.el-menu--horizontal {
  .menu-inline {
    text-align: center;
    display: inline-block !important;
  }
  .bottom-text {
    color: #b1b1b1;
  }
  .inside-img {
    width: 110px;
    height: 70px;
    border: 2px solid transparent;
    box-sizing: border-box;
  }
  .inside-img:hover {
    border-color: #006eff;
  }
  .usehove:hover {
    .bottom-text {
      color: #fff;
    }
  }
}
</style>