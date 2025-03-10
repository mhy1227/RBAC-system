// axios全局配置
axios.defaults.baseURL = '/';
axios.defaults.timeout = 10000;
axios.defaults.headers.post['Content-Type'] = 'application/json';

// 统一的错误提示函数
function showError(message) {
    if (window.Vue && window.ElementUI) {
        ElementUI.Message.error(message);
    } else {
        console.error('错误:', message);
    }
}

// 请求拦截器
axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        console.log('发送请求前的token:', token);
        if (token) {
            // 确保token格式正确
            if (!token.startsWith('Bearer ')) {
                config.headers['Authorization'] = 'Bearer ' + token;
            } else {
                config.headers['Authorization'] = token;
            }
            console.log('设置请求头Authorization:', config.headers['Authorization']);
        }
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
axios.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.code === 200) {
            return res;
        }
        
        // 处理token过期
        if (res.code === 401) {
            console.log('Token过期，清理登录信息');
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
            localStorage.removeItem('permissions');
            
            // 如果不是登录页面，则跳转到登录页
            if (window.location.pathname !== '/login.html') {
                window.location.href = '/login.html';
            }
        }
        
        showError(res.message || '操作失败');
        return Promise.reject(res);
    },
    error => {
        console.error('响应错误:', error);
        // 如果是网络错误，显示更友好的提示
        const message = error.message || '网络错误';
        showError(message);
        return Promise.reject(error);
    }
); 