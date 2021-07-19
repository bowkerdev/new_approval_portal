const path = require('path')

function resolve(dir) {
  return path.join(__dirname, dir)
}

module.exports = {
  publicPath: "./",
  productionSourceMap: false,
  chainWebpack: (config) => {
    config.resolve.alias.set('@/', resolve('src'))
    // config.plugins.delete('prefetch')
  },

  css: {
    loaderOptions: {
      less: {
        modifyVars: {
        },
        javascriptEnabled: true,
      }
    }
  },
  pages: {
    datav: {
      entry: 'src/pages/datav/main.js',
      template: 'src/pages/datav/index.html',
      title: 'datav',
      filename: 'datav.html',
      chunks: ['chunk-vendors', 'chunk-common',  'datav']
    },
    index: {
      entry: 'src/main.js',
      template: 'public/index.html',
      title: 'index.html',
      filename: 'index.html'
    }
  },
  devServer: {
    port: 3000,
    open: true,
    proxy: {
      '/approval': {
        target: process.env.VUE_APP_SERVER_URL,
        changeOrigin: true,
        pathRewrite: {
          '^/approval': ''
        }
      },
      '/userfiles': {
        target: process.env.VUE_APP_SERVER_DOMAIN_URL,
        changeOrigin: true,
        pathRewrite: {
          '^/userfiles': 'approval/userfiles'
        }
      }
    }
  },

  lintOnSave: false
}
