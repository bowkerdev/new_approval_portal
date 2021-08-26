<template>
    <el-card class="box-card"  shadow="hover" style="margin-top:5px">
      <div slot="header" class="clearfix">
        <span>{{$i18nMy.t('流转记录')}}</span>
      </div>
      <el-steps :active="historicTaskStepNodeList.length">
        <el-step :key="index" v-for="(node, index) in historicTaskStepNodeList"
        :title="node.activityName" finish-status="success"
         :description="node.assigneeName + node.time"></el-step>
      </el-steps>
      <el-table
      size="small"
      :data="historicTaskList"
      style="width: 100%">
      <el-table-column
        prop="histIns.activityName"
        :label="$i18nMy.t('任务')"
        width="180">
        <template slot-scope="scope">
          {{scope.row.histIns.activityName || scope.row.comment.status}}
        </template>
      </el-table-column>
      <el-table-column
        prop="assigneeName"
        :label="$i18nMy.t('执行人')">
      </el-table-column>
      <el-table-column
        prop="histIns.startTime"
        :label="$i18nMy.t('开始时间')"
        width="150">
        <template slot-scope="scope">
          {{scope.row.histIns.startTime | formatDate}}
        </template>
      </el-table-column>
       <el-table-column
        prop="histIns.endTime"
        :label="$i18nMy.t('结束时间')"
        width="150">
        <template slot-scope="scope">
          {{scope.row.histIns.endTime | formatDate}}
        </template>
      </el-table-column>
       <el-table-column
        prop="comment.status"
        :label="$i18nMy.t('办理状态')"
        width="150">
        <template slot-scope="scope">
          {{$i18nMy.t(scope.row.comment.status)}}
        </template>
      </el-table-column>
       <el-table-column
        prop="comment.message"
        :label="$i18nMy.t('审批意见')">
      </el-table-column>
       <el-table-column
        prop="durationTime"
        :label="$i18nMy.t('任务历时')"
        width="150">
        <template slot-scope="scope">
          {{scope.row.durationTime || '0s'}}
        </template>
      </el-table-column>
    </el-table>
    </el-card>
</template>
<script>
export default {
  props: {
    historicTaskList: {
      type: Array,
      default: []
    }
  },
  computed: {
    historicTaskStepNodeList () {
      let nodeArra = []
      this.historicTaskMap.forEach((arra) => {
        let obj = {activityName: '', assigneeName: '', time: ''}
        arra.forEach((node) => {
          debugger
          obj.activityName = node.histIns.activityName
          if (node.histIns.activityType == "endEvent") {
            obj.activityName = 'End'
          }
          obj.assigneeName += (node.assigneeName || '') + ','
          obj.time = (!node.histIns.startTime ? '--' : this.moment(node.histIns.startTime).format('YYYY-MM-DD HH:mm:ss'))
        })
        nodeArra.push(obj)
      })

      return nodeArra
    },
    historicTaskMap () {
      let map = new Map()
      this.historicTaskList.forEach((act) => {
        let key = act.histIns.activityId + parseInt(act.histIns.startTime / 1000)
        let val = map.get(key)
        if (val) {
          val.push(act)
        } else {
          var array = []
          array.push(act)
          map.set(key, array)
        }
      })
      return map
    }
  }
}
</script>
