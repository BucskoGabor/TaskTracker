import axios from 'axios'

const API_BASE_URL = '/tasktracker'

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
})

export const taskApi = {
    getAllTasks: () => api.get('/tasks'),
    getTaskById: (id) => api.get(`/tasks/${id}`),
    createTask: (task) => api.post('/tasks', task),
    updateTask: (id, task) => api.put(`/tasks/${id}`, task),
    deleteTask: (id) => api.delete(`/tasks/${id}`),
    completeTask: (id) => api.post(`/tasks/completeTask/${id}`)
}

export const subTaskApi = {
    getAllSubTasks: () => api.get('/subtasks'),
    getSubTaskById: (id) => api.get(`/subtasks/${id}`),
    createSubTask: (subTask) => api.post('/subtasks', subTask),
    updateSubTask: (id, subTask) => api.put(`/subtasks/${id}`, subTask),
    deleteSubTask: (id) => api.delete(`/subtasks/${id}`),
    completeSubTask: (id) => api.post(`/subtasks/completeSubTask/${id}`)
}
