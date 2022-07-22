// 时间戳转换成格式化时间显示，提交时再转回原时间戳提交
const ymdhisKey = ["expectedDate"]
const ymdKey = ["applicantDate"]

export const original_key = '__value'

export default {
	methods: {
		// 时间戳转格式化时间
		mix_transfterVal(key, val) {
			if (isTimeString(key, val)) {
				if (ymdKey.includes(key)) {
					return parseTime(val, '{y}-{m}-{d}')
				}
				if (ymdhisKey.includes(key)) { 
					return parseTime(val)
				}
			}
			return val
		}
	}
}

function isTimeString(key, val) {
	return (typeof val === 'string') && (/^[0-9]+$/.test(val))
}

function parseTime(time, cFormat) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string')) {
      if ((/^[0-9]+$/.test(time))) {
        // support "1548221490638"
        time = parseInt(time)
      } else {
        // support safari
        // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
        time = time.replace(new RegExp(/-/gm), '/')
      }
    }

    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['SUN', 'MON', 'TUE', 'WEN', 'THU', 'FRI', 'SAT'][value] }
    return value.toString().padStart(2, '0')
  })
  return time_str
}
