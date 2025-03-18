<template>
    <div class="background">
        <h1>
            Welcome to landing page
        </h1>
        <button @click="this.$router.push('/EditingPage')">
            Less EDIT
        </button>
    </div>

</template>

<script>
</script>

<style scoped>
.background {
    height: 100%;
    width: 100%;
    background-color: purple; /* Ensure background fills */
}
</style>