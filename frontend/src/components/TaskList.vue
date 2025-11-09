<template>
    <div class="task-list">
        <div class="task-list-header">
            <h1>TaskTracker</h1>
            <button class="btn btn-primary" @click="openCreateTaskDialog">
                + Új Task
            </button>
        </div>

        <div v-if="loading" class="loading">
            Betöltés...
        </div>

        <div v-else-if="tasks.length === 0" class="empty-state">
            <p>Nincsenek taskok</p>
            <button class="btn btn-primary" @click="openCreateTaskDialog">
                Első Task létrehozása
            </button>
        </div>

        <div v-else class="tasks">
            <div 
                v-for="task in tasks" 
                :key="task.id" 
                class="task-card"
                :class="{ 'completed': task.status === 'COMPLETED' }"
            >
                <div class="task-header">
                    <div class="task-title">
                        <h3>{{ task.name }}</h3>
                        <span class="status-badge" :class="statusClass(task.status)">
                            {{ task.status }}
                        </span>
                    </div>
                    <div class="task-actions">
                        <button class="btn btn-sm btn-secondary" @click="editTask(task)">
                            Szerkesztés
                        </button>
                        <button 
                            v-if="task.status !== 'COMPLETED'"
                            class="btn btn-sm btn-success" 
                            @click="completeTask(task.id)"
                        >
                            Teljesítés
                        </button>
                        <button class="btn btn-sm btn-danger" @click="deleteTask(task.id)">
                            Törlés
                        </button>
                    </div>
                </div>

                <div v-if="task.description" class="task-description">
                    {{ task.description }}
                </div>

                <div class="task-meta">
                    <div class="meta-item">
                        <strong>Létrehozva:</strong> {{ formatDate(task.startDate) }}
                    </div>
                    <div v-if="task.deadline" class="meta-item">
                        <strong>Határidő:</strong> {{ formatDate(task.deadline) }}
                        <span v-if="isOverdue(task.deadline, task.status)" class="overdue">
                            (LEJÁRT)
                        </span>
                    </div>
                    <div v-if="task.completedAt" class="meta-item">
                        <strong>Befejezve:</strong> {{ formatDate(task.completedAt) }}
                    </div>
                </div>

                <div class="subtasks-section">
                    <div class="subtasks-header">
                        <h4>SubTaskok ({{ task.subTasks ? task.subTasks.length : 0 }})</h4>
                        <div class="subtasks-header-actions">
                            <button 
                                class="btn btn-sm btn-primary" 
                                @click="openCreateSubTaskDialog(task.id)"
                                :disabled="task.status === 'COMPLETED'"
                            >
                                + SubTask
                            </button>
                            <button 
                                v-if="task.subTasks && task.subTasks.length > 0"
                                class="btn btn-sm btn-secondary" 
                                @click="toggleSubtasks(task.id)"
                            >
                                {{ task.subTasksExpanded ? '▲ Összecsukás' : '▼ Kinyitás' }}
                            </button>
                        </div>
                    </div>

                    <div v-if="task.subTasks && task.subTasks.length > 0 && task.subTasksExpanded" class="subtasks">
                        <div 
                            v-for="subTask in task.subTasks" 
                            :key="subTask.id" 
                            class="subtask-item"
                            :class="{ 'completed': subTask.status === 'COMPLETED' }"
                        >
                            <div class="subtask-header">
                                <div class="subtask-title">
                                    <span>{{ subTask.name }}</span>
                                    <span class="status-badge small" :class="statusClass(subTask.status)">
                                        {{ subTask.status }}
                                    </span>
                                </div>
                                <div class="subtask-actions">
                                    <button 
                                        class="btn btn-xs btn-secondary" 
                                        @click="editSubTask(subTask, task.id)"
                                        :disabled="task.status === 'COMPLETED'"
                                    >
                                        Szerkesztés
                                    </button>
                                    <button 
                                        v-if="subTask.status !== 'COMPLETED'"
                                        class="btn btn-xs btn-success" 
                                        @click="completeSubTask(subTask.id)"
                                        :disabled="task.status === 'COMPLETED'"
                                    >
                                        Teljesítés
                                    </button>
                                    <button 
                                        class="btn btn-xs btn-danger" 
                                        @click="deleteSubTask(subTask.id)"
                                        :disabled="task.status === 'COMPLETED'"
                                    >
                                        Törlés
                                    </button>
                                </div>
                            </div>

                            <div v-if="subTask.description" class="subtask-description">
                                {{ subTask.description }}
                            </div>

                            <div v-if="subTask.completedAt" class="subtask-meta">
                                <strong>Befejezve:</strong> {{ formatDate(subTask.completedAt) }}
                            </div>
                        </div>
                    </div>

                    <div v-else-if="!task.subTasksExpanded && (!task.subTasks || task.subTasks.length === 0)" class="no-subtasks">
                        <p>Nincsenek subtaskok</p>
                    </div>
                </div>
            </div>
        </div>

        <TaskDialog
            :is-visible="taskDialogVisible"
            :task="selectedTask"
            @close="closeTaskDialog"
            @save="handleTaskSave"
        />

        <SubTaskDialog
            :is-visible="subTaskDialogVisible"
            :sub-task="selectedSubTask"
            :parent-task-id="selectedParentTaskId"
            @close="closeSubTaskDialog"
            @save="handleSubTaskSave"
        />
    </div>
</template>

<script>
import { taskApi, subTaskApi } from '../services/api'
import TaskDialog from './TaskDialog.vue'
import SubTaskDialog from './SubTaskDialog.vue'

export default {
    name: 'TaskList',
    components: {
        TaskDialog,
        SubTaskDialog
    },
    data() {
        return {
            tasks: [],
            loading: false,
            taskDialogVisible: false,
            subTaskDialogVisible: false,
            selectedTask: null,
            selectedSubTask: null,
            selectedParentTaskId: null
        }
    },
    async mounted() {
        await this.loadTasks()
    },
    methods: {
        async loadTasks() {
            this.loading = true
            try {
                const response = await taskApi.getAllTasks()
                this.tasks = response.data.map(task => ({
                    ...task,
                    subTasksExpanded: false
                }))
            } catch (error) {
                console.error('Hiba a taskok betöltése közben:', error)
                alert('Hiba a taskok betöltése közben')
            } finally {
                this.loading = false
            }
        },

        openCreateTaskDialog() {
            this.selectedTask = null
            this.taskDialogVisible = true
        },

        editTask(task) {
            this.selectedTask = task
            this.taskDialogVisible = true
        },

        closeTaskDialog() {
            this.taskDialogVisible = false
            this.selectedTask = null
        },

        async handleTaskSave(taskData) {
            try {
                if (this.selectedTask) {
                    await taskApi.updateTask(this.selectedTask.id, taskData)
                } else {
                    await taskApi.createTask(taskData)
                }
                await this.loadTasks()
                this.closeTaskDialog()
            } catch (error) {
                console.error('Hiba a task mentése közben:', error)
                alert('Hiba a task mentése közben')
            }
        },

        async deleteTask(taskId) {
            if (confirm('Biztosan törli ezt a taskot?')) {
                try {
                    await taskApi.deleteTask(taskId)
                    await this.loadTasks()
                } catch (error) {
                    console.error('Hiba a task törlése közben:', error)
                    alert('Hiba a task törlése közben')
                }
            }
        },

        async completeTask(taskId) {
            const task = this.tasks.find(t => t.id === taskId)
            if (task && task.subTasks && task.subTasks.length > 0) {
                const incompleteSubTasks = task.subTasks.filter(subTask => subTask.status !== 'COMPLETED')
                if (incompleteSubTasks.length > 0) {
                    alert(`Nem lehet completelni a taskot, mert még ${incompleteSubTasks.length} subtask nincs befejezve:\n${incompleteSubTasks.map(st => `- ${st.name}`).join('\n')}`)
                    return
                }
            }
            
            try {
                await taskApi.completeTask(taskId)
                await this.loadTasks()
            } catch (error) {
                console.error('Hiba a task befejezése közben:', error)
                alert('Hiba a task befejezése közben')
            }
        },

        openCreateSubTaskDialog(parentTaskId) {
            this.selectedSubTask = null
            this.selectedParentTaskId = parentTaskId
            this.subTaskDialogVisible = true
        },

        editSubTask(subTask, parentTaskId) {
            this.selectedSubTask = subTask
            this.selectedParentTaskId = parentTaskId
            this.subTaskDialogVisible = true
        },

        closeSubTaskDialog() {
            this.subTaskDialogVisible = false
            this.selectedSubTask = null
            this.selectedParentTaskId = null
        },

        async handleSubTaskSave(subTaskData) {
            try {
                if (this.selectedSubTask) {
                    await subTaskApi.updateSubTask(this.selectedSubTask.id, subTaskData)
                } else {
                    await subTaskApi.createSubTask(subTaskData)
                }
                await this.loadTasks()
                this.closeSubTaskDialog()
            } catch (error) {
                console.error('Hiba a subtask mentése közben:', error)
                alert('Hiba a subtask mentése közben')
            }
        },

        async deleteSubTask(subTaskId) {
            console.log('deleteSubTask called with ID:', subTaskId)
            if (confirm('Biztosan törli ezt a subtaskot?')) {
                console.log('User confirmed deletion')
                try {
                    console.log('Calling API to delete subtask:', subTaskId)
                    const response = await subTaskApi.deleteSubTask(subTaskId)
                    console.log('Delete response:', response)
                    console.log('Reloading tasks...')
                    await this.loadTasks()
                    console.log('Tasks reloaded')
                } catch (error) {
                    console.error('Hiba a subtask törlése közben:', error)
                    console.error('Error details:', error.response || error.message || error)
                    alert(`Hiba a subtask törlése közben: ${error.response?.data?.message || error.message || error}`)
                }
            } else {
                console.log('User cancelled deletion')
            }
        },

        async completeSubTask(subTaskId) {
            try {
                await subTaskApi.completeSubTask(subTaskId)
                await this.loadTasks()
            } catch (error) {
                console.error('Hiba a subtask befejezése közben:', error)
                alert('Hiba a subtask befejezése közben')
            }
        },

        toggleSubtasks(taskId) {
            const taskIndex = this.tasks.findIndex(t => t.id === taskId)
            if (taskIndex !== -1) {
                // Create new array to trigger reactivity
                const updatedTasks = [...this.tasks]
                updatedTasks[taskIndex] = {
                    ...updatedTasks[taskIndex],
                    subTasksExpanded: !updatedTasks[taskIndex].subTasksExpanded
                }
                this.tasks = updatedTasks
            }
        },

        statusClass(status) {
            const statusClasses = {
                'TODO': 'todo',
                'IN_PROGRESS': 'in-progress',
                'ON_HOLD': 'on-hold',
                'COMPLETED': 'completed',
                'CANCELLED': 'cancelled'
            }
            return statusClasses[status] || 'todo'
        },

        formatDate(dateString) {
            if (!dateString) return ''
            const date = new Date(dateString)
            return date.toLocaleString('hu-HU')
        },

        isOverdue(deadline, status) {
            if (!deadline || status === 'COMPLETED') return false
            return new Date(deadline) < new Date()
        }
    }
}
</script>

<style scoped>
.task-list {
    max-width: 1200px;
    margin: 0 auto;
}

.task-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #e0e0e0;
}

.task-list-header h1 {
    color: #333;
    margin: 0;
}

.loading {
    text-align: center;
    padding: 40px;
    font-size: 18px;
    color: #666;
}

.empty-state {
    text-align: center;
    padding: 60px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.empty-state p {
    margin-bottom: 20px;
    color: #666;
    font-size: 16px;
}

.tasks {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.task-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    padding: 20px;
    transition: box-shadow 0.2s;
}

.task-card:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.task-card.completed {
    opacity: 0.7;
    background-color: #f8f9fa;
}

.task-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;
}

.task-title {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;
}

.task-title h3 {
    margin: 0;
    color: #333;
    font-size: 18px;
}

.task-actions {
    display: flex;
    gap: 8px;
}

.task-description {
    margin-bottom: 15px;
    color: #666;
    line-height: 1.5;
}

.task-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #e0e0e0;
}

.meta-item {
    font-size: 14px;
    color: #666;
}

.overdue {
    color: #dc3545;
    font-weight: bold;
    margin-left: 5px;
}

.subtasks-section {
    margin-top: 15px;
}

.subtasks-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.subtasks-header h4 {
    margin: 0;
    color: #333;
    font-size: 16px;
}

.subtasks-header-actions {
    display: flex;
    gap: 8px;
}

.subtasks {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.subtask-item {
    background: #f8f9fa;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    padding: 15px;
    transition: background-color 0.2s;
}

.subtask-item:hover {
    background: #e9ecef;
}

.subtask-item.completed {
    opacity: 0.7;
    background-color: #e8f5e8;
}

.subtask-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.subtask-title {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    font-weight: 500;
}

.subtask-actions {
    display: flex;
    gap: 6px;
}

.subtask-description {
    margin-bottom: 10px;
    color: #666;
    font-size: 14px;
    line-height: 1.4;
}

.subtask-meta {
    font-size: 13px;
    color: #666;
}

.no-subtasks {
    text-align: center;
    padding: 20px;
    color: #666;
    font-style: italic;
}

.status-badge {
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    text-transform: uppercase;
}

.status-badge.small {
    padding: 2px 6px;
    font-size: 11px;
}

.status-badge.todo {
    background-color: #ffc107;
    color: #212529;
}

.status-badge.in-progress {
    background-color: #17a2b8;
    color: white;
}

.status-badge.on-hold {
    background-color: #6c757d;
    color: white;
}

.status-badge.completed {
    background-color: #28a745;
    color: white;
}

.status-badge.cancelled {
    background-color: #dc3545;
    color: white;
}

.btn {
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: background-color 0.2s;
    text-decoration: none;
    display: inline-block;
}

.btn-sm {
    padding: 6px 12px;
    font-size: 12px;
}

.btn-xs {
    padding: 4px 8px;
    font-size: 11px;
}

.btn-primary {
    background-color: #007bff;
    color: white;
}

.btn-primary:hover {
    background-color: #0056b3;
}

.btn-primary:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.btn-secondary {
    background-color: #6c757d;
    color: white;
}

.btn-secondary:hover {
    background-color: #545b62;
}

.btn-secondary:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.btn-success {
    background-color: #28a745;
    color: white;
}

.btn-success:hover {
    background-color: #1e7e34;
}

.btn-success:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.btn-danger {
    background-color: #dc3545;
    color: white;
}

.btn-danger:hover {
    background-color: #c82333;
}

.btn-danger:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

@media (max-width: 768px) {
    .task-list-header {
        flex-direction: column;
        gap: 15px;
        align-items: stretch;
    }

    .task-header {
        flex-direction: column;
        gap: 15px;
    }

    .task-actions {
        flex-wrap: wrap;
    }

    .subtasks-header {
        flex-direction: column;
        gap: 10px;
        align-items: stretch;
    }

    .subtask-header {
        flex-direction: column;
        gap: 10px;
    }

    .task-meta {
        flex-direction: column;
        gap: 8px;
    }
}
</style>
