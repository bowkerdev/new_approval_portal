<template>
  <div class="ep-list">
    <template v-if="$slots.default">
      <slot></slot>
    </template>
    <template v-else>
      <div class="ep-list-empty-text">{{$i18nMy.t('暂无数据')}}</div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'EpList'
}
</script>

<style lang="scss" src="./style/index.scss"></style>
