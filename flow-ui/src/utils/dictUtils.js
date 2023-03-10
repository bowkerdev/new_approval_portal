import $http from './httpRequest'

export function getDictLabel (type, value, defaultLabel) {
  if ((!value && value !== 0) || (!type && type !== 0)) {
    if (defaultLabel !== undefined) {
      return defaultLabel
    } else {
      return '--'
    }
  }
  let dictList = JSON.parse(sessionStorage.getItem('dictList') || '[]')
  let dicts = dictList[type]
  if (dicts) {
    for (let i = 0; i < dicts.length; i++) {
      if (dicts[i].value && dicts[i].value.toString() === value.toString()) {
        return dicts[i].label
      }
    }
  }
  if (defaultLabel !== undefined) {
    return defaultLabel
  } else {
    return '--'
  }
}

export function getDictValue (type, label, defaultValue) {
  if ((!label && label !== 0) || (!type && type !== 0)) {
    if (defaultValue !== undefined) {
      return defaultValue
    } else {
      return '--'
    }
  }
  let dictList = JSON.parse(sessionStorage.getItem('dictList') || '[]')
  let dicts = dictList[type]
  if (dicts) {
    for (let i = 0; i < dicts.length; i++) {
      if (dicts[i].label && dicts[i].label.toString() === label.toString()) {
        return dicts[i].value
      }
    }
  }
  if (defaultValue !== undefined) {
    return defaultValue
  } else {
    return '--'
  }
}

export function getDictList (type) {
  if (!type && type !== 0) {
    return []
  }
  let dictList = JSON.parse(sessionStorage.getItem('dictList') || '[]')
  let dicts = dictList[type]
  return dicts || []
}

export function getDictListWithKey (type) {
  let dicts = getDictList(type)
  for (var i=0; i<dicts.length; i++) {
    dicts[i].label = dicts[i].value + ' - ' + dicts[i].label;
  }
  return dicts || []
}

export function getDsDictList (sqlName,params) {
  $http({
    url: "/database/datamodel/dataSet/getDataByName/"+sqlName+"/json",
    method: 'get',
    params: params
  }).then(({data}) => {
    return data.result
  })
}

export function getSqlDictList (sqlName,params,callBack) {
  $http({
    url: "/database/datamodel/dataSet/getDataByName/"+sqlName+"/json",
    method: 'get',
    params: params
  }).then(({data}) => {
    if (data && data.success) {
      callBack(data.result)
    }
    else{
      callBack([])
    }
  })
}

export function refreshDictList () {
  $http.get('/sys/dict/getDictMap').then(({data}) => {
    sessionStorage.setItem('dictList', JSON.stringify(data.dictList || '[]'))
  })
}

export default {getDictLabel, getDictValue, getDictList, getDictListWithKey, getDsDictList, getSqlDictList, refreshDictList}
