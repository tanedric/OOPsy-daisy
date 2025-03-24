<template>
  <div class="header">
    <div class="left-menu">
      <button class="header-btn" @click="$emit('new-project')">
        <i-lucide-file-plus class="icon" />
        <span>New</span>
      </button>
      <button class="header-btn" @click="$emit('save-project')">
        <i-lucide-save class="icon" />
        <span>Save</span>
      </button>
      <button class="header-btn" @click="$emit('export-project')">
        <i-lucide-download class="icon" />
        <span>Export</span>
      </button>
    </div>

    <div class="right-menu">
      <button
        class="header-btn undo-redo-btn"
        @click="$emit('undo')"
        :disabled="!canUndo"
        title="Undo (Ctrl+Z)"
      >
        <i-lucide-undo-2 class="icon" />
        <span>Undo</span>
      </button>
      <button
        class="header-btn undo-redo-btn"
        @click="$emit('redo')"
        :disabled="!canRedo"
        title="Redo (Ctrl+Y)"
      >
        <i-lucide-redo-2 class="icon" />
        <span>Redo</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Header",
  props: {
    canUndo: {
      type: Boolean,
      default: false,
    },
    canRedo: {
      type: Boolean,
      default: false,
    },
  },
  emits: ["new-project", "save-project", "export-project", "undo", "redo"],
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background-color: #252a2e;
  border-bottom: 1px solid #3a3f45;
  height: 50px;
  width: 100%;
}

.left-menu {
  display: flex;
  gap: 8px;
}

.right-menu {
  display: flex;
  gap: 8px;
  position: relative;
  left: -200px;
}

.header-btn {
  background: transparent;
  border: none;
  color: #ffffff;
  padding: 6px 10px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.header-btn:hover {
  background-color: #3a3f45;
}

.header-btn:disabled {
  color: #5a5f65;
  cursor: not-allowed;
}

.header-btn .icon {
  width: 16px;
  height: 16px;
}

.undo-redo-btn {
  color: #ffffff;
}

.undo-redo-btn:disabled {
  color: #5a5f65;
}

@media (max-width: 600px) {
  .header-btn span {
    display: none;
  }
}
</style>
