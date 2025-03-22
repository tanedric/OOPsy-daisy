<template>
  <div class="image-container">
    <div class="canvas">
      <div
        @wheel="handleZoom"
        @mousedown="startDrag"
        @mousemove="dragging"
        @mouseup="endDrag"
        class="zoom-container"
      >
        <div ref="imageWrapper" class="image-wrapper">
          <img :src="image" alt="ID Photo" class="photo" draggable="false" />
          <div v-if="cropBox" class="crop-box" :style="cropBoxStyle"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    image: {
      type: String,
      required: true,
    },
    activeFeature: {
      type: String,
      required: false,
    },
  },
  mounted() {
    this.$nextTick(() => {
      const imageWrapper = this.$refs.imageWrapper;
      imageWrapper.style.transform = `scale(${this.scale})`;
    });
  },
  data() {
    return {
      scale: 1, // Initial zoom level (scale)
      isDragging: false,
      cropStart: null,
      cropEnd: null,
    };
  },
  computed: {
    cropBox() {
      return this.cropStart && this.cropEnd;
    },
    cropBoxStyle() {
      if (!this.cropBox) return {};
      const x = Math.min(this.cropStart.x, this.cropEnd.x);
      const y = Math.min(this.cropStart.y, this.cropEnd.y);
      const width = Math.abs(this.cropEnd.x - this.cropStart.x);
      const height = Math.abs(this.cropEnd.y - this.cropStart.y);
      return {
        position: "absolute",
        left: `${x}px`,
        top: `${y}px`,
        width: `${width}px`,
        height: `${height}px`,
        border: "2px dashed red",
        backgroundColor: "rgba(255, 0, 0, 0.1)",
      };
    },
  },
  methods: {
    clearCropBox() {
      this.cropStart = null;
      this.cropEnd = null;
    },
    // Handle the zoom event (Ctrl + Scroll)
    handleZoom(event) {
      // Only zoom when the Ctrl key is pressed
      if (event.ctrlKey) {
        event.preventDefault(); // Prevent the default browser zoom

        // Calculate the zoom factor (zoom in or out)
        const delta = event.deltaY || event.detail || event.wheelDelta;
        const zoomFactor = delta < 0 ? 1.1 : 0.9; // Zoom in when scrolling up, zoom out when scrolling down

        // Update the scale (zoom level)
        this.scale *= zoomFactor;

        // Limit zoom level to avoid extreme zoom
        this.scale = Math.min(Math.max(this.scale, 0.5), 3);

        // Call zoomImage function to zoom into the mouse position
        this.zoomImage(event);
      }
      this.$refs.imageWrapper.style.transform = `scale(${this.scale})`;
    },
    // Zoom the image based on the mouse position
    zoomImage(event) {
      const imageWrapper = this.$refs.imageWrapper;
      const rect = imageWrapper.getBoundingClientRect(); // Get image wrapper dimensions
      const offsetX = event.clientX - rect.left; // X offset of mouse
      const offsetY = event.clientY - rect.top; // Y offset of mouse

      // Set the zoom origin based on mouse position
      imageWrapper.style.transformOrigin = `${(offsetX / rect.width) * 100}% ${
        (offsetY / rect.height) * 100
      }%`;
      imageWrapper.style.transform = `scale(${this.scale})`; // Apply zoom
    },
    startDrag(event) {
      event.preventDefault();
      if (this.activeFeature !== "crop") return; // Block drag if not cropping

      const rect = this.$refs.imageWrapper.getBoundingClientRect();
      this.cropStart = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top,
      };
      this.isDragging = true;
    },
    dragging(event) {
      if (!this.isDragging) return;
      const rect = this.$refs.imageWrapper.getBoundingClientRect();
      this.cropEnd = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top,
      };
    },

    endDrag() {
      if (!this.isDragging) return;
      this.isDragging = false;

      if (!this.cropStart || !this.cropEnd) return;

      const rect = this.$refs.imageWrapper.getBoundingClientRect();
      const startX = Math.min(this.cropStart.x, this.cropEnd.x);
      const startY = Math.min(this.cropStart.y, this.cropEnd.y);
      const width = Math.abs(this.cropEnd.x - this.cropStart.x);
      const height = Math.abs(this.cropEnd.y - this.cropStart.y);

      const img = this.$refs.imageWrapper.querySelector("img");
      const scaleX = img.naturalWidth / rect.width;
      const scaleY = img.naturalHeight / rect.height;

      this.$emit("crop-area", {
        x: Math.floor(startX * scaleX),
        y: Math.floor(startY * scaleY),
        width: Math.floor(width * scaleX),
        height: Math.floor(height * scaleY),
      });

      // Clear crop box after emitting
    },
  },
};
</script>

<style scoped>
.image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #121416;
  overflow: auto;
}

.canvas {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.photo {
  display: block;
  max-width: 90%;
  max-height: 80vh;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);

  /* width: 100%;
    height: 100%; */
  object-fit: contain;
  transition: transform 0.2s ease-in-out;
}

.zoom-container {
  position: relative;
  width: 100%;
  height: 100%;
  /* overflow: hidden; */
}

.crop-box {
  position: absolute;
  z-index: 10;
  pointer-events: none;
}
.image-wrapper {
  position: relative; /* ADD THIS */
  transition: transform 0.1s ease;
  width: 100%;
  height: 100%;
}
</style>
