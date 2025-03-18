<template>
    <div class="editing-page">
      <!-- Left sidebar with feature panel -->
      <FeaturePanel 
        :activeFeature="activeFeature" 
        @feature-selected="setActiveFeature" 
      />
      
      <!-- Main editing area -->
      <div class="main-content">
        <!-- Top toolbar -->
        <div class="toolbar">
          <button class="toolbar-btn" @click="undoAction" :disabled="!canUndo">
            <i-lucide-undo-2 class="icon" />
          </button>
          <button class="toolbar-btn" @click="redoAction" :disabled="!canRedo">
            <i-lucide-redo-2 class="icon" />
          </button>
          <div class="divider"></div>
          <button class="toolbar-btn" @click="zoomIn">
            <i-lucide-zoom-in class="icon" />
          </button>
          <button class="toolbar-btn" @click="zoomOut">
            <i-lucide-zoom-out class="icon" />
          </button>
          <div class="zoom-level">{{ Math.round(zoomLevel * 100) }}%</div>
          <div class="divider"></div>
          <button class="primary-btn" @click="savePhoto">
            <i-lucide-save class="icon" /> Save
          </button>
          <button class="primary-btn" @click="exportPhoto">
            <i-lucide-download class="icon" /> Export
          </button>
        </div>
        
        <!-- Canvas area -->
        <div class="canvas-container" ref="canvasContainer">
          <div 
            class="canvas" 
            :style="{ transform: `scale(${zoomLevel})` }"
            @mousedown="startDrag"
            @mousemove="onDrag"
            @mouseup="endDrag"
            @mouseleave="endDrag"
          >
            <img 
              v-if="currentPhoto" 
              :src="currentPhoto" 
              alt="ID Photo" 
              class="photo"
              :class="{ 'dragging': isDragging }"
            />
            <div v-else class="placeholder">
              <i-lucide-image class="placeholder-icon" />
              <p>Upload a photo to get started</p>
              <button class="upload-btn" @click="triggerUpload">
                Upload Photo
              </button>
              <input 
                type="file" 
                ref="fileInput" 
                @change="handleFileUpload" 
                accept="image/*" 
                class="hidden-input"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- Right sidebar with editing controls -->
      <div class="editing-controls">
        <div class="controls-header">
          <h3>{{ getFeatureTitle(activeFeature) }}</h3>
        </div>
        
        <!-- Dynamic controls based on active feature -->
        <div class="controls-content">
          <!-- Crop controls -->
          <div v-if="activeFeature === 'crop'" class="control-group">
            <div class="control-item">
              <label>Aspect Ratio</label>
              <div class="ratio-options">
                <button 
                  v-for="ratio in aspectRatios" 
                  :key="ratio.value"
                  :class="['ratio-btn', { active: selectedRatio === ratio.value }]"
                  @click="setAspectRatio(ratio.value)"
                >
                  {{ ratio.label }}
                </button>
              </div>
            </div>
            <div class="control-item">
              <label>Size (mm)</label>
              <div class="size-inputs">
                <input type="number" v-model="cropWidth" class="size-input" /> × 
                <input type="number" v-model="cropHeight" class="size-input" />
              </div>
            </div>
            <div class="control-actions">
              <button class="action-btn" @click="applyCrop">Apply</button>
              <button class="action-btn secondary" @click="resetCrop">Reset</button>
            </div>
          </div>
          
          <!-- Background removal controls -->
          <div v-if="activeFeature === 'background'" class="control-group">
            <div class="control-item">
              <label>Removal Method</label>
              <div class="method-options">
                <button 
                  :class="['method-btn', { active: removalMethod === 'auto' }]"
                  @click="setRemovalMethod('auto')"
                >
                  Automatic
                </button>
                <button 
                  :class="['method-btn', { active: removalMethod === 'manual' }]"
                  @click="setRemovalMethod('manual')"
                >
                  Manual Selection
                </button>
              </div>
            </div>
            <div class="control-item">
              <label>Background Color</label>
              <div class="color-picker">
                <div 
                  v-for="color in backgroundColors" 
                  :key="color"
                  :class="['color-option', { active: selectedBgColor === color }]"
                  :style="{ backgroundColor: color }"
                  @click="setBackgroundColor(color)"
                ></div>
                <div class="color-option custom">
                  <input type="color" v-model="customBgColor" @change="setBackgroundColor(customBgColor)" />
                </div>
              </div>
            </div>
            <div class="control-actions">
              <button class="action-btn" @click="applyBackground">Apply</button>
              <button class="action-btn secondary" @click="resetBackground">Reset</button>
            </div>
          </div>
          
          <!-- Adjust controls -->
          <div v-if="activeFeature === 'adjust'" class="control-group">
            <div class="control-item slider">
              <label>Brightness</label>
              <input type="range" min="-100" max="100" v-model="brightness" class="slider-input" />
              <span class="slider-value">{{ brightness }}</span>
            </div>
            <div class="control-item slider">
              <label>Contrast</label>
              <input type="range" min="-100" max="100" v-model="contrast" class="slider-input" />
              <span class="slider-value">{{ contrast }}</span>
            </div>
            <div class="control-item slider">
              <label>Saturation</label>
              <input type="range" min="-100" max="100" v-model="saturation" class="slider-input" />
              <span class="slider-value">{{ saturation }}</span>
            </div>
            <div class="control-actions">
              <button class="action-btn" @click="applyAdjustments">Apply</button>
              <button class="action-btn secondary" @click="resetAdjustments">Reset</button>
            </div>
          </div>
          
          <!-- Size controls -->
          <div v-if="activeFeature === 'size'" class="control-group">
            <div class="control-item">
              <label>Preset Sizes</label>
              <select v-model="selectedPreset" class="preset-select" @change="applyPreset">
                <option value="passport">Passport (35×45mm)</option>
                <option value="visa">Visa (50×50mm)</option>
                <option value="id">ID Card (25×35mm)</option>
                <option value="custom">Custom Size</option>
              </select>
            </div>
            <div class="control-item">
              <label>Custom Size (mm)</label>
              <div class="size-inputs">
                <input type="number" v-model="outputWidth" class="size-input" /> × 
                <input type="number" v-model="outputHeight" class="size-input" />
              </div>
            </div>
            <div class="control-item">
              <label>DPI</label>
              <select v-model="outputDpi" class="dpi-select">
                <option value="300">300 DPI (Standard)</option>
                <option value="600">600 DPI (High Quality)</option>
              </select>
            </div>
            <div class="control-actions">
              <button class="action-btn" @click="applySize">Apply</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import FeaturePanel from '/src/components/FeaturePanel.vue';
  
  export default {
    components: {
      FeaturePanel
    },
    data() {
      return {
        // State
        activeFeature: 'upload',
        currentPhoto: null,
        zoomLevel: 1,
        isDragging: false,
        dragStartPos: { x: 0, y: 0 },
        canUndo: false,
        canRedo: false,
  
        // Crop state
        selectedRatio: '35:45',
        cropWidth: 35,
        cropHeight: 45,
        aspectRatios: [
          { label: '35×45', value: '35:45' },
          { label: '50×50', value: '50:50' },
          { label: '25×35', value: '25:35' },
          { label: 'Custom', value: 'custom' }
        ],
  
        // Background removal state
        removalMethod: 'auto',
        selectedBgColor: '#FFFFFF',
        customBgColor: '#FFFFFF',
        backgroundColors: ['#FFFFFF', '#0000FF', '#FF0000', '#00FF00', '#DDDDDD'],
  
        // Adjustment state
        brightness: 0,
        contrast: 0,
        saturation: 0,
  
        // Size state
        selectedPreset: 'passport',
        outputWidth: 35,
        outputHeight: 45,
        outputDpi: '300'
      };
    },
    methods: {
      setActiveFeature(feature) {
        this.activeFeature = feature;
      },
      getFeatureTitle(feature) {
        const titles = {
          upload: 'Upload Photo',
          crop: 'Crop & Resize',
          background: 'Background Removal',
          adjust: 'Adjustments',
          size: 'Output Size',
          export: 'Export Options'
        };
        return titles[feature] || 'Edit Photo';
      },
      triggerUpload() {
        this.$refs.fileInput.click();
      },
      handleFileUpload(event) {
        const file = event.target.files[0];
        if (file && file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = (e) => {
            this.currentPhoto = e.target.result;
            this.activeFeature = 'crop'; // Move to crop after upload
          };
          reader.readAsDataURL(file);
        }
      },
      zoomIn() {
        this.zoomLevel = Math.min(this.zoomLevel + 0.1, 3);
      },
      zoomOut() {
        this.zoomLevel = Math.max(this.zoomLevel - 0.1, 0.5);
      },
      startDrag(event) {
        if (this.currentPhoto) {
          this.isDragging = true;
          this.dragStartPos = { x: event.clientX, y: event.clientY };
        }
      },
      onDrag(event) {
        if (this.isDragging) {
          // Implement dragging logic here
        }
      },
      endDrag() {
        this.isDragging = false;
      },
      // Crop methods
      setAspectRatio(ratio) {
        this.selectedRatio = ratio;
        if (ratio !== 'custom') {
          const [width, height] = ratio.split(':');
          this.cropWidth = parseInt(width);
          this.cropHeight = parseInt(height);
        }
      },
      applyCrop() {
        // Implement crop logic
        console.log('Applying crop with dimensions:', this.cropWidth, 'x', this.cropHeight);
        this.canUndo = true;
      },
      resetCrop() {
        // Reset crop
      },
      // Background methods
      setRemovalMethod(method) {
        this.removalMethod = method;
      },
      setBackgroundColor(color) {
        this.selectedBgColor = color;
      },
      applyBackground() {
        // Implement background removal and replacement
        console.log('Applying background removal with color:', this.selectedBgColor);
        this.canUndo = true;
      },
      resetBackground() {
        // Reset background
      },
      // Adjustment methods
      applyAdjustments() {
        // Implement adjustments
        console.log('Applying adjustments:', {
          brightness: this.brightness,
          contrast: this.contrast,
          saturation: this.saturation
        });
        this.canUndo = true;
      },
      resetAdjustments() {
        this.brightness = 0;
        this.contrast = 0;
        this.saturation = 0;
      },
      // Size methods
      applyPreset() {
        if (this.selectedPreset === 'passport') {
          this.outputWidth = 35;
          this.outputHeight = 45;
        } else if (this.selectedPreset === 'visa') {
          this.outputWidth = 50;
          this.outputHeight = 50;
        } else if (this.selectedPreset === 'id') {
          this.outputWidth = 25;
          this.outputHeight = 35;
        }
      },
      applySize() {
        // Implement size change
        console.log('Applying output size:', this.outputWidth, 'x', this.outputHeight, 'at', this.outputDpi, 'DPI');
      },
      // History methods
      undoAction() {
        // Implement undo
        console.log('Undo action');
        this.canRedo = true;
      },
      redoAction() {
        // Implement redo
        console.log('Redo action');
      },
      // Export methods
      savePhoto() {
        // Implement save
        console.log('Saving photo');
      },
      exportPhoto() {
        // Implement export
        console.log('Exporting photo');
      }
    },
    mounted() {
      // Any initialization code
    }
  }
  </script>
  
  <style scoped>
  .editing-page {
    margin: 0;
  padding: 0;
    display: flex;
    height: 100vh;
    width: 100%;
    background-color: #1e2124;
    color: #ffffff;
    font-family: 'Inter', sans-serif;
  }
  
  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  
  .toolbar {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    background-color: #2a2e32;
    border-bottom: 1px solid #3a3f45;
  }
  
  .toolbar-btn {
    background: transparent;
    border: none;
    color: #ffffff;
    width: 36px;
    height: 36px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-right: 5px;
  }
  
  .toolbar-btn:hover {
    background-color: #3a3f45;
  }
  
  .toolbar-btn:disabled {
    color: #5a5f65;
    cursor: not-allowed;
  }
  
  .primary-btn {
    background-color: #4a90e2;
    border: none;
    color: white;
    padding: 8px 16px;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    margin-left: auto;
    margin-right: 8px;
  }
  
  .primary-btn:hover {
    background-color: #3a80d2;
  }
  
  .primary-btn .icon {
    margin-right: 6px;
  }
  
  .divider {
    width: 1px;
    height: 24px;
    background-color: #3a3f45;
    margin: 0 10px;
  }
  
  .zoom-level {
    font-size: 14px;
    margin: 0 10px;
    min-width: 40px;
    text-align: center;
  }
  
  .canvas-container {
    flex: 1;
    overflow: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #121416;
    position: relative;
  }
  
  .canvas {
    position: relative;
    transition: transform 0.2s ease;
    cursor: grab;
  }
  
  .canvas.dragging {
    cursor: grabbing;
  }
  
  .photo {
    display: block;
    max-width: 100%;
    max-height: 80vh;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
  }
  
  .placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 400px;
    height: 300px;
    background-color: #2a2e32;
    border-radius: 8px;
    padding: 30px;
    text-align: center;
  }
  
  .placeholder-icon {
    font-size: 48px;
    margin-bottom: 16px;
    color: #4a90e2;
    width: 48px;
    height: 48px;
  }
  
  .placeholder p {
    margin-bottom: 20px;
    color: #aaaaaa;
  }
  
  .upload-btn {
    background-color: #4a90e2;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
  }
  
  .upload-btn:hover {
    background-color: #3a80d2;
  }
  
  .hidden-input {
    display: none;
  }
  
  .editing-controls {
    width: 280px;
    background-color: #2a2e32;
    border-left: 1px solid #3a3f45;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  
  .controls-header {
    padding: 15px;
    border-bottom: 1px solid #3a3f45;
  }
  
  .controls-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 500;
  }
  
  .controls-content {
    flex: 1;
    overflow-y: auto;
    padding: 15px;
  }
  
  .control-group {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .control-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .control-item label {
    font-size: 14px;
    color: #cccccc;
  }
  
  .ratio-options, .method-options {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .ratio-btn, .method-btn {
    background-color: #3a3f45;
    border: none;
    color: white;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 13px;
    cursor: pointer;
  }
  
  .ratio-btn:hover, .method-btn:hover {
    background-color: #4a4f55;
  }
  
  .ratio-btn.active, .method-btn.active {
    background-color: #4a90e2;
  }
  
  .size-inputs {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .size-input {
    width: 60px;
    padding: 6px;
    background-color: #3a3f45;
    border: 1px solid #4a4f55;
    border-radius: 4px;
    color: white;
    font-size: 14px;
  }
  
  .color-picker {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .color-option {
    width: 30px;
    height: 30px;
    border-radius: 4px;
    cursor: pointer;
    border: 2px solid transparent;
  }
  
  .color-option.active {
    border-color: #4a90e2;
  }
  
  .color-option.custom {
    background: linear-gradient(45deg, #ff0000, #00ff00, #0000ff);
    overflow: hidden;
    position: relative;
  }
  
  .color-option.custom input {
    position: absolute;
    width: 150%;
    height: 150%;
    top: -25%;
    left: -25%;
    cursor: pointer;
    opacity: 0;
  }
  
  .control-item.slider {
    display: grid;
    grid-template-columns: 1fr auto;
    grid-template-rows: auto auto;
    gap: 8px;
  }
  
  .control-item.slider label {
    grid-column: 1;
    grid-row: 1;
  }
  
  .control-item.slider .slider-value {
    grid-column: 2;
    grid-row: 1;
    text-align: right;
    font-size: 14px;
  }
  
  .slider-input {
    grid-column: 1 / span 2;
    grid-row: 2;
    -webkit-appearance: none;
    width: 100%;
    height: 6px;
    background: #3a3f45;
    border-radius: 3px;
    outline: none;
  }
  
  .slider-input::-webkit-slider-thumb {
    -webkit-appearance: none;
    appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #4a90e2;
    cursor: pointer;
  }
  
  .preset-select, .dpi-select {
    padding: 8px;
    background-color: #3a3f45;
    border: 1px solid #4a4f55;
    border-radius: 4px;
    color: white;
    font-size: 14px;
    width: 100%;
  }
  
  .control-actions {
    display: flex;
    gap: 8px;
    margin-top: 10px;
  }
  
  .action-btn {
    flex: 1;
    background-color: #4a90e2;
    border: none;
    color: white;
    padding: 8px 0;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
  }
  
  .action-btn:hover {
    background-color: #3a80d2;
  }
  
  .action-btn.secondary {
    background-color: #3a3f45;
  }
  
  .action-btn.secondary:hover {
    background-color: #4a4f55;
  }
  
  .icon {
    width: 18px;
    height: 18px;
  }
  </style>