import Vue from 'vue'

// 千分位
Vue.directive('thousands', {
  bind (el, binding, vnode) {
    const key = Object.keys(binding.modifiers)[0]
    // 处理千分位展示
    const generatingThousandthPer = (num) => {
      // eslint-disable-next-line one-var
      let num1 = num.split('.')[0], num2 = num.split('.')[1]
      let c = num1.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
      return num.toString().indexOf('.') !== -1 ? c + '.' + num2 : c
    }
    // 赋值
    const assignment = (event, value, value2) => {
      binding.value[key] = value
      // eslint-disable-next-line no-return-assign
      vnode.context.$nextTick(() => event.target.value = generatingThousandthPer(value2 || value))
    }
    // 处理最后一位非法字符
    const handlerIllegalStr = (str) => {
      while (!(/^[0-9]+.?[0-9]*/.test(str.charAt(str.length - 1)))) {
        str = str.substr(0, str.length - 1)
      }
      return str
    }

    el.addEventListener('input', (event) => {
      let inp = event.target.value = event.target.value.replace(/,/g, '')
      assignment(event, inp)
    })

    // element
    // eslint-disable-next-line no-undef
    const input = $(el).find('.el-input__inner')
    if (input) {
      input[0].addEventListener('blur', (event) => {
        const val = event.target.value
        if (!val || !/^[0-9]+.?[0-9]*/.test(val)) return
        assignment(event, handlerIllegalStr(val.replace(/,/g, '')))
      })
    }
  }
})
