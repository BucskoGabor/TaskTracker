<template>
    <div id="app">
        <TaskList />
    </div>
</template>

<script>
import TaskList from './components/TaskList.vue'

export default {
    name: 'App',
    components: {
        TaskList
    }
}
</script>

<style>
#app {
    min-height: 100vh;
    background-color: #f5f5f5;
}
</style>
