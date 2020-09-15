/* eslint-disable no-useless-escape */
import Vue from 'vue'
Vue.directive('noMoreClick', {
  inserted (el, binding) {
    el.addEventListener('click', e => {
      el.classList.add('is-disabled')
      el.disabled = true
      setTimeout(() => {
        el.disabled = false
        el.classList.remove('is-disabled')
      }, 2000)
    })
  }
})
/*

*  ʹ�÷���

*  �����´��븴�Ƶ�һ��js�ļ��У�Ȼ��������ļ�main.js��import���뼴�ɣ�

*  ��elementUI��dialog�ϼ��� v-dialogDrag ָ��Ϳ���ʵ�ֵ�����ȫ���������ˡ�

*  ��dialog���� :close-on-click-modal="false" , ��ֹ������ֲ�رյ�����

*  �����form������Ҫ���ύ�Ȱ�ť����el-form-item����������������ʱ������

*/

// v-dialogDrag: ������ק+ˮƽ��������

// v-dialogDrag: ������ק
Vue.directive('dialogDrag', {
  bind (el, binding, vnode, oldVnode) {
    const dialogHeaderEl = el.querySelector('.el-dialog__header')
    const dragDom = el.querySelector('.el-dialog')
    dialogHeaderEl.style.cursor = 'move'

      // ��ȡԭ������ ie domԪ��.currentStyle ����ȸ� window.getComputedStyle(domԪ��, null);
    const sty = dragDom.currentStyle || window.getComputedStyle(dragDom, null)

    dialogHeaderEl.onmousedown = (e) => {
          // ��갴�£����㵱ǰԪ�ؾ���������ľ���
      const disX = e.clientX - dialogHeaderEl.offsetLeft
      const disY = e.clientY - dialogHeaderEl.offsetTop

          // ��ȡ����ֵ��px ����ƥ���滻
      let styL, styT

          // ע����ie�� ��һ�λ�ȡ����ֵΪ����Դ�50% �ƶ�֮��ֵΪpx
      if (sty.left.includes('%')) {
        styL = +document.body.clientWidth * (+sty.left.replace(/\%/g, '') / 100)
        styT = +document.body.clientHeight * (+sty.top.replace(/\%/g, '') / 100)
      } else {
        styL = +sty.left.replace(/\px/g, '')
        styT = +sty.top.replace(/\px/g, '')
      };

      document.onmousemove = function (e) {
              // ͨ���¼�ί�У������ƶ��ľ���
        const l = e.clientX - disX
        const t = e.clientY - disY

              // �ƶ���ǰԪ��
        dragDom.style.left = `${l + styL}px`
        dragDom.style.top = `${t + styT}px`

              // ����ʱ��λ�ô���ȥ
              // binding.value({x:e.pageX,y:e.pageY})
      }

      document.onmouseup = function (e) {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
})

// v-dialogDragWidth: ��������ϴ� ��С
Vue.directive('dialogDragWidth', {
  bind (el, binding, vnode, oldVnode) {
    const dragDom = binding.value.$el.querySelector('.el-dialog')

    el.onmousedown = (e) => {
          // ��갴�£����㵱ǰԪ�ؾ���������ľ���
      const disX = e.clientX - el.offsetLeft

      document.onmousemove = function (e) {
        e.preventDefault() // �ƶ�ʱ����Ĭ���¼�

              // ͨ���¼�ί�У������ƶ��ľ���
        const l = e.clientX - disX
        dragDom.style.width = `${l}px`
      }

      document.onmouseup = function (e) {
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
})
