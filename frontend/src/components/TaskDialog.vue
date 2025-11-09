<template>
    <div class="dialog-overlay" v-if="isVisible" @click="closeDialog">
        <div class="dialog" @click.stop>
            <div class="dialog-header">
                <h2>{{ isEdit ? 'Task szerkesztése' : 'Új Task létrehozása' }}</h2>
                <button class="close-btn" @click="closeDialog">&times;</button>
            </div>
            <div class="dialog-body">
                <div class="form-group">
                    <label for="name">Név *</label>
                    <input 
                        type="text" 
                        id="name" 
                        v-model="formData.name" 
                        required
                        placeholder="Task neve"
                    >
                </div>
                <div class="form-group">
                    <label for="description">Leírás</label>
                    <textarea 
                        id="description" 
                        v-model="formData.description" 
                        rows="3"
                        placeholder="Task leírása"
                    ></textarea>
                </div>
                <div class="form-group">
                    <label for="deadline">Határidő</label>
                    <input 
                        type="datetime-local" 
                        id="deadline" 
                        v-model="formData.deadline"
                    >
                </div>
                <div class="form-group" v-if="isEdit">
                    <label for="status">Státusz</label>
                    <select id="status" v-model="formData.status">
                        <option value="TODO">TODO</option>
                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                        <option value="ON_HOLD">ON_HOLD</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                </div>
            </div>
            <div class="dialog-footer">
                <button class="btn btn-secondary" @click="closeDialog">Mégse</button>
                <button class="btn btn-primary" @click="saveTask" :disabled="!formData.name">
                    {{ isEdit ? 'Mentés' : 'Létrehozás' }}
                </button>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TaskDialog',
    props: {
        isVisible: {
            type: Boolean,
            default: false
        },
        task: {
            type: Object,
            default: null
        }
    },
    emits: ['close', 'save'],
    data() {
        return {
            formData: {
                name: '',
                description: '',
                deadline: '',
                status: 'TODO'
            }
        }
    },
    computed: {
        isEdit() {
            return this.task !== null
        }
    },
    watch: {
        task(newTask) {
            if (newTask) {
                this.formData = {
                    name: newTask.name || '',
                    description: newTask.description || '',
                    deadline: newTask.deadline ? this.formatDateTimeLocal(newTask.deadline) : '',
                    status: newTask.status || 'TODO'
                }
            } else {
                this.resetForm()
            }
        },
        isVisible(visible) {
            if (visible && !this.isEdit) {
                this.resetForm()
            }
        }
    },
    methods: {
        closeDialog() {
            this.$emit('close')
        },
        saveTask() {
            const taskData = {
                ...this.formData,
                deadline: this.formData.deadline ? new Date(this.formData.deadline).toISOString() : null
            }
            
            if (!this.isEdit) {
                delete taskData.status
            }
            
            this.$emit('save', taskData)
        },
        resetForm() {
            this.formData = {
                name: '',
                description: '',
                deadline: '',
                status: 'TODO'
            }
        },
        formatDateTimeLocal(dateString) {
            const date = new Date(dateString)
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, '0')
            const day = String(date.getDate()).padStart(2, '0')
            const hours = String(date.getHours()).padStart(2, '0')
            const minutes = String(date.getMinutes()).padStart(2, '0')
            return `${year}-${month}-${day}T${hours}:${minutes}`
        }
    }
}
</script>

<style scoped>
.dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.dialog {
    background: white;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #e0e0e0;
}

.dialog-header h2 {
    margin: 0;
    color: #333;
}

.close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #666;
    padding: 0;
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.close-btn:hover {
    color: #333;
}

.dialog-body {
    padding: 20px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
    color: #555;
}

.form-group input,
.form-group textarea,
.form-group select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding: 20px;
    border-top: 1px solid #e0e0e0;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: background-color 0.2s;
}

.btn-primary {
    background-color: #007bff;
    color: white;
}

.btn-primary:hover:not(:disabled) {
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
</style>
