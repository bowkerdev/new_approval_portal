// 非h5端不能使用

function isSupportCookie() {
	return !!(document)
}

export function getAllCookieObject () {
	if (!isSupportCookie()) { return {} }
	const cookies = document.cookie
	const cookieKeyValArr = cookies.split(/;\s*/)
	const res = {}
	cookieKeyValArr.forEach((item) => {
		const itemArr = item.split('=')
		res[itemArr[0]] = itemArr[1]
	})
	return res
}

// 获取cookie
export function getCookieBy(key) {
	const cookieObj = getAllCookieObject()
	return cookieObj[key]
}

// 设置cookie通过name, value
// attributes.expires参数从今天开始，过期的天数；删除则传入负数即可
export function setCookieBy(name, val, attributes = {}) {
	if (!isSupportCookie()) { return false }
	// path
	attributes.path = '/'
	// expires
	const expiresDate = attributes.expires
	if (Object.prototype.toString.call(expiresDate) == '[object Number]') {
		var expires = new Date()
		expires.setMilliseconds(expires.getMilliseconds() + expiresDate * 864e+5)
		attributes.expires = expires
	}
	// name，value如果含有特殊字符，应该要进行编码处理
	// https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Set-Cookie
	// 拼接成串
	const attributesArr = [`${name}=${val}`];
	Object.keys(attributes).forEach(name => {
		const value = attributes[name]
		if (!value) { return; }
		let attrStr = name
		if (value !== true) {
			attrStr += '=' + value
		}
		attributesArr.push(attrStr)
	})
	document.cookie = attributesArr.join('; ')
	return true
}


