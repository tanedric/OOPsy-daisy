<template>
  <div class="editing-page">
    <!-- Header Component -->
    <Header 
      @new-project="createNewProject"
      @save-project="saveProject"
      @export-project="exportProject"
      @undo="undoAction"
      @redo="redoAction"
      :canUndo="canUndo"
      :canRedo="canRedo"
    />
    
    <div class="main-container">
      <!-- Feature Panel Component -->
      <FeaturePanel 
        :activeFeature="activeFeature" 
        @feature-selected="setActiveFeature" 
      />
      
      <!-- Image Component -->
      <ImageComponent 
        :image="currentPhoto || '/assets/zhiyuan.jpg'"
      />
      
      <!-- Control Panel Component (only shown when a feature is selected) -->
      <ControlPanel 
        v-if="activeFeature && activeFeature !== 'upload'"
        :feature="activeFeature"
        @close="closeControlPanel"
        @apply-changes="applyChanges"
      />
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue';
import FeaturePanel from '../components/FeaturePanel.vue';
import ImageComponent from '../components/ImageComponent.vue';
import ControlPanel from '../components/ControlPanel.vue';

export default {
  components: {
    Header,
    FeaturePanel,
    ImageComponent,
    ControlPanel
  },
  data() {
    return {
      activeFeature: null,
      currentPhoto: null,
      canUndo: false,
      canRedo: false
    };
  },
  methods: {
    setActiveFeature(feature) {
      this.activeFeature = feature;
    },
    closeControlPanel() {
      this.activeFeature = null;
    },
    createNewProject() {
      console.log('Creating new project');
    },
    saveProject() {
      console.log('Saving project');
    },
    exportProject() {
      console.log('Exporting project');
    },
    undoAction() {
      console.log('Undo action');
      this.canRedo = true;
    },
    redoAction() {
      console.log('Redo action');
    },
    applyChanges(changes) {
      console.log('Applying changes:', changes);
      this.canUndo = true;
    }
  }
}
</script>

<style scoped>
.editing-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background-color: #1e2124;
  color: #ffffff;
  font-family: 'Inter', sans-serif;
  overflow: hidden;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}
</style>