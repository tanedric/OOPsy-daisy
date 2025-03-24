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

    // Updated undo method with actual API call
    async undoAction() {
      try {
        const response = await fetch("http://localhost:8080/api/undo");

        if (response.ok) {
          // Get the updated image
          const imageResponse = await fetch(
            "http://localhost:8080/api/image/get"
          );
          const blob = await imageResponse.blob();
          this.currentPhoto = URL.createObjectURL(blob);

          // Check if we still have history available
          const historyResponse = await fetch(
            "http://localhost:8080/api/history/status"
          );
          const historyStatus = await historyResponse.json();
          this.canUndo = historyStatus.hasHistory;

          // We can always redo after an undo
          this.canRedo = true;
        } else {
          console.error("Failed to undo:", await response.text());
        }
      } catch (error) {
        console.error("Error during undo:", error);
      }
    },

    // Updated redo method with actual API call
    async redoAction() {
      try {
        const response = await fetch("http://localhost:8080/api/redo");

        if (response.ok) {
          // Get the updated image
          const imageResponse = await fetch(
            "http://localhost:8080/api/image/get"
          );
          const blob = await imageResponse.blob();
          this.currentPhoto = URL.createObjectURL(blob);

          // Per requirement, redo returns to original image, so we can no longer redo after that
          this.canRedo = false;

          // But we can undo again since we've saved the current state to history
          this.canUndo = true;
        } else {
          console.error("Failed to redo:", await response.text());
        }
      } catch (error) {
        console.error("Error during redo:", error);
      }
    },

    async uploadNewImage(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      try {
        const uploadResponse = await fetch("http://localhost:8080/api/upload", {
          method: "POST",
          body: formData,
        });

        if (uploadResponse.ok) {
          const response = await fetch("http://localhost:8080/api/image/get");
          const blob = await response.blob();
          this.currentPhoto = URL.createObjectURL(blob);
          console.log("Image uploaded and displayed from backend.");

          // Reset undo/redo state
          this.canUndo = false; // No history for a new image
          this.canRedo = false; // No future for a new image
        } else {
          console.error("Failed to upload image:", await uploadResponse.text());
        }
      } catch (error) {
        console.error("Error uploading image:", error);
      }
    },

    async applyChanges(changes) {
      console.log("Applying changes:", changes);

      if (changes.type === "crop" && this.cropArea) {
        try {
          const cropResponse = await fetch("http://localhost:8080/api/crop", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(this.cropArea),
          });

          if (cropResponse.ok) {
            const response = await fetch("http://localhost:8080/api/image/get");
            const blob = await response.blob();
            this.currentPhoto = URL.createObjectURL(blob);
            this.cropArea = null;
            this.$refs.imageComponent.clearCropBox();

            // We now have history (the pre-crop image)
            this.canUndo = true;
            // We can redo to get back to original
            this.canRedo = true;
          } else {
            console.error("Failed to crop image:", await cropResponse.text());
          }
        } catch (error) {
          console.error("Error cropping image:", error);
        }
      }
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
