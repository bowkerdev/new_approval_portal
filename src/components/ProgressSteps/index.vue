<template>
  <div class="zm-progress-steps" :class="{ 'gradient': gradient }">
    <template v-for="(item, index) in steps">
      <div
        :key="index"
        class="steps-item"
        :class="getItemBarClass(index)"
        @click="clickStepFn(index)"
      >
        <div class="steps-item-text" :style="itemTextStyle()">
          <span v-if="showIndex">{{ index + 1 }}<span>. </span></span>
          <i v-if="showIcon && item.icon" :class="item.icon" />
          {{ item.title }}
        </div>
        <div class="attach pre-attach" />
        <div v-if="index" class="attach back-attach" />
      </div>
    </template>
  </div>
</template>

<script>
const gradientTotal = 6

export default {
  name: 'ProgressSteps',
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    steps: {
      type: Array,
      required: true
    },
    // clickFn: {
    //   type: Function,
    //   default: null
    // },
    value: {
      type: Number
    },
    finishStatus: {
      type: String,
      default: 'finish'
    },
    showIcon: {
      type: Boolean,
      default: false
    },
    showIndex: {
      type: Boolean,
      default: false
    },
    gradient: {
      type: Boolean,
      default: false
    },
    textAlign: {
      type: String,
      validator: (val) => ['center', 'left', 'right'].indexOf(val) !== -1
    }
  },
  data() {
    return {
      activeIndex: this.value || 0
    }
  },
  watch: {
    value: {
      handler(val, oldVal) {
        this.activeIndex = val
      }
    }
  },
  methods: {
    itemTextStyle() {
      if(!this.textAlign) { return '' }
      return `text-align: ${this.textAlign};`
    },
    isFinishStatus(index) {
      // index  <= activeIndex
      return this.activeIndex >= index
    },
    getItemBarClass(index) {
      //渐变模式
      if(this.gradient) { 
        if(index === this.activeIndex) {
          return 'gradient-current'
        }
        const classi = (index + 1) % gradientTotal
        return 'gradient-' + (classi || gradientTotal)
      } else {
        // 非渐变模式
        if (!this.isFinishStatus(index)) { return 'wait' }
        return this.steps[index].finishStatus || this.finishStatus
      }
    },
    clickStepFn(index) {
      if (index === this.activeIndex) { return }
      // v-model有绑定值则emit触发更新， 没有则手动赋值
      if (this.value || this.value === 0) {
        this.$emit('change', index)
      } else {
        this.activeIndex = index
      }
      this.$emit('click-fn', this.steps[index])
    }
  }
}
</script>
