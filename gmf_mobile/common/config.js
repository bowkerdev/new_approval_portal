let APP_SERVER_URL = ""

if(process.env.NODE_ENV === 'development'){
    // 开发环境
    APP_SERVER_URL = '/approval'
}else{
    // 生产环境
    APP_SERVER_URL = '/approval'
}

export default APP_SERVER_URL