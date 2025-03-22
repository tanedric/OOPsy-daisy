<template>
  <div class="editing-page">
    <input
      type="file"
      ref="fileInput"
      @change="uploadNewImage"
      style="display: none"
      accept="image/*"
    />
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
      ref="imageComponent"
      :image="currentPhoto || '/assets/zhiyuan.jpg'"
      :activeFeature="activeFeature"
      @crop-area="cropArea = $event"
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
import Header from "../components/Header.vue";
import FeaturePanel from "../components/FeaturePanel.vue";
import ImageComponent from "../components/ImageComponent.vue";
import ControlPanel from "../components/ControlPanel.vue";

export default {
  components: {
    Header,
    FeaturePanel,
    ImageComponent,
    ControlPanel,
  },
  data() {
    return {
      activeFeature: null,
      currentPhoto: null,
      canUndo: false,
      canRedo: false,
      cropArea: null,
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
      this.$refs.fileInput.click(); // trigger file selection
    },
    saveProject() {
      console.log("Saving project");
    },
    exportProject() {
      console.log("Exporting project");
    },
    undoAction() {
      console.log("Undo action");
      this.canRedo = true;
    },
    redoAction() {
      console.log("Redo action");
    },
    async uploadNewImage(event) {
      const file = event.target.files[0];
      const formData = new FormData();
      formData.append("file", file);

      await fetch("http://localhost:8080/api/upload", {
        method: "POST",
        body: formData,
      });

      const response = await fetch("http://localhost:8080/api/image/get");
      const blob = await response.blob();
      this.currentPhoto = URL.createObjectURL(blob);
      console.log("Image uploaded and displayed from backend.");
    },
    async applyChanges(changes) {
      console.log("Applying changes:", changes);

      if (changes.type === "crop" && this.cropArea) {
        await fetch("http://localhost:8080/api/crop", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(this.cropArea),
        });

        const response = await fetch("http://localhost:8080/api/image/get");
        const blob = await response.blob();
        this.currentPhoto = URL.createObjectURL(blob);
        this.cropArea = null;
        this.$refs.imageComponent.clearCropBox();
      }

      this.canUndo = true;
    },
  },
};
</script>

<style scoped>
.editing-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background-color: #1e2124;
  color: #ffffff;
  font-family: "Inter", sans-serif;
  overflow: hidden;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}
</style>
