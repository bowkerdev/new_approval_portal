let APP_SERVER_URL = ""

if(process.env.NODE_ENV === 'development'){
    // 开发环境
    APP_SERVER_URL = 'http://47.242.28.61:8082/approval'
}else{
    // 生产环境
    VUE_APP_SERVER_URL = 'http://47.242.28.61:8082/approval'
}

export default APP_SERVER_URL