import request from '@/utils/httpRequest'
export const getObj = (id) => request({
  url: '/datav/dataScreen/queryById',
  method: 'get',
  params: {
    id
  }
});
export const uploadImg = (file) => request({
  url: '/sys/file/webupload/upload?uploadPath=/datascreen',
  method: 'post',
  data: 'file=' + file,
  headers: { "Content-Type": "multipart/form-data" }
});

export const addObj = (data) => request({
  url:  '/datav/dataScreen/save',
  method: 'post',
  data: {
    visual: {
      password: data.password,
      category: data.category,
      status: data.status,
      title: data.title,
    },
    config: {
      detail: JSON.stringify({
        name: data.title,
        width: data.width,
        height: data.height,
        scale: 1,
        backgroundImage: '/img/bg/bg1.png',
        url: '',
        mark: {},
        gradeShow: false,
        gradeLen: 30,
      }),
      component: '[]'
    },
  }
});

export const updateComponent = (data) => request({
  url:  '/datav/dataScreen/save',
  method: 'post',
  data: data
});
