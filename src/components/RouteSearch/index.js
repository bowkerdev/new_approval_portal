import { isURL } from '@/utils/validate'

const pinyin = require('pinyin')

export function generateRoutesInfo(routes, prefixTitle = []) {
  let res = []
  if (Array.isArray(routes)) {
    for(const routeItem of routes) {
      // 跳过不显示菜单, 外链，iframe打开页面
      if (routeItem.isShow !== "1" || isURL(routeItem.href) || routeItem.target === 'iframe') { continue }
      const item = {
        path: routeItem.href,
        title: [...prefixTitle, routeItem.name || 'Unnamed Entry']
      }
      if (routeItem.href) {
        res.push(item)
      }
      if(routeItem.children) {
        const tempRoutes = generateRoutesInfo(routeItem.children, item.title)
        if (tempRoutes.length >= 1) {
          res = [...res, ...tempRoutes]
        }
      }
    }
  }
  return res
}

export const fuse_option = {
  shouldSort: true,
  threshold: 0.4,
  location: 0,
  distance: 100,
  maxPatternLength: 32,
  minMatchCharLength: 1,
  keys: [{
    name: 'title',
    weight: 0.7
  }, {
    name: 'path',
    weight: 0.3
  }, {
    name: 'pinyinTitle',
    weight: 0.3
  }]
}

export function initPinyinTitle(list) {
  const lang = localStorage.getItem('lang')
  if (['zh-CN', 'zh-TW'].includes(lang) && Array.isArray(list)) {
    list.forEach(element => {
      const title = element.title
      if (Array.isArray(title)) {
        let tmp = ''
        title.forEach(v => {
          tmp += pinyin(v, {
            style: pinyin.STYLE_NORMAL
          }).join('')
        })
        element.pinyinTitle = tmp
      }
    })
  }
  return list
}
