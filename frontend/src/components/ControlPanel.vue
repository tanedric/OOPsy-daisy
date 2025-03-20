<template>
    <div class="control-panel">
      <div class="panel-header">
        <button class="close-btn" @click="$emit('close')">
          <!-- <i-lucide-x class="icon" /> -->

           <div>
            <img src="/assets/app-icons/cancel.png" style="width:100%">
           </div>
           
        </button>
        <h3>{{ getFeatureTitle(feature) }}</h3>
      </div>
      
      <div class="panel-content">
        <!-- Crop controls -->
        <div v-if="feature === 'crop'" class="control-group">
          <div class="control-item">
            <label>Aspect Ratio</label>
            <div class="ratio-options">
              <button 
                v-for="ratio in aspectRatios" 
                :key="ratio.value"
                :class="['control-btn', { active: selectedRatio === ratio.value }]"
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
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
        
        <!-- Background removal controls -->
        <div v-if="feature === 'background-remove'" class="control-group">
          <div class="control-item">
            <label>Removal Method</label>
            <div class="method-options">
              <button 
                :class="['control-btn', { active: removalMethod === 'auto' }]"
                @click="setRemovalMethod('auto')"
              >
                Automatic
              </button>
              <button 
                :class="['control-btn', { active: removalMethod === 'manual' }]"
                @click="setRemovalMethod('manual')"
              >
                Manual Selection
              </button>
            </div>
          </div>
          <div class="control-actions">
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
        
        <!-- Background replacement controls -->
        <div v-if="feature === 'background-replace'" class="control-group">
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
          <div class="control-item">
            <label>Or Upload Image</label>
            <button class="upload-bg-btn">
              <i-lucide-upload class="icon" /> Upload Background
            </button>
          </div>
          <div class="control-actions">
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
        
        <!-- Clothes replacement controls -->
        <div v-if="feature === 'clothes'" class="control-group">
          <div class="control-item">
            <label>Select Clothing Template</label>
            <div class="template-options">
              <div class="template-option">
                <!-- <img src="/placeholder.svg?height=60&width=60" alt="Formal Suit" /> -->
                <span>Formal Suit</span>
              </div>
              <div class="template-option">
                <!-- <img src="/placeholder.svg?height=60&width=60" alt="Business Casual" /> -->
                <span>Business Casual</span>
              </div>
              <div class="template-option">
                <!-- <img src="/placeholder.svg?height=60&width=60" alt="Formal Dress" /> -->
                <span>Formal Dress</span>
              </div>
            </div>
          </div>
          <div class="control-actions">
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
        
        <!-- Face centering controls -->
        <div v-if="feature === 'face'" class="control-group">
          <div class="control-item">
            <label>Face Detection</label>
            <button class="action-btn" @click="detectFace">Detect Face</button>
          </div>
          <div class="control-item">
            <label>Auto Center</label>
            <button class="action-btn" @click="centerFace">Center Face</button>
          </div>
          <div class="control-actions">
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
        
        <!-- Enhancement controls -->
        <div v-if="feature === 'enhance'" class="control-group">
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
          <div class="control-item">
            <label>Skin Smoothing</label>
            <input type="range" min="0" max="100" v-model="skinSmoothing" class="slider-input" />
            <span class="slider-value">{{ skinSmoothing }}</span>
          </div>
          <div class="control-actions">
            <button class="action-btn" @click="applyChanges">Apply</button>
            <button class="action-btn secondary" @click="resetControls">Reset</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      feature: {
        type: String,
        required: true
      }
    },
    data() {
      return {
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
        
        // Background replacement state
        selectedBgColor: '#FFFFFF',
        customBgColor: '#FFFFFF',
        backgroundColors: ['#FFFFFF', '#0000FF', '#FF0000', '#00FF00', '#DDDDDD'],
  
        // Enhancement state
        brightness: 0,
        contrast: 0,
        saturation: 0,
        skinSmoothing: 0
      };
    },
    methods: {
      getFeatureTitle(feature) {
        const titles = {
          'crop': 'Crop & Resize',
          'background-remove': 'Background Removal',
          'background-replace': 'Background Replacement',
          'clothes': 'Clothes Replacement',
          'face': 'Face Centering',
          'enhance': 'Photo Enhancement'
        };
        return titles[feature] || 'Edit Photo';
      },
      setAspectRatio(ratio) {
        this.selectedRatio = ratio;
        if (ratio !== 'custom') {
          const [width, height] = ratio.split(':');
          this.cropWidth = parseInt(width);
          this.cropHeight = parseInt(height);
        }
      },
      setRemovalMethod(method) {
        this.removalMethod = method;
      },
      setBackgroundColor(color) {
        this.selectedBgColor = color;
      },
      detectFace() {
        console.log('Detecting face');
      },
      centerFace() {
        console.log('Centering face');
      },
      resetControls() {
        // Reset controls based on feature type
        if (this.feature === 'crop') {
          this.selectedRatio = '35:45';
          this.cropWidth = 35;
          this.cropHeight = 45;
        } else if (this.feature === 'background-remove') {
          this.removalMethod = 'auto';
        } else if (this.feature === 'background-replace') {
          this.selectedBgColor = '#FFFFFF';
          this.customBgColor = '#FFFFFF';
        } else if (this.feature === 'enhance') {
          this.brightness = 0;
          this.contrast = 0;
          this.saturation = 0;
          this.skinSmoothing = 0;
        }
      },
      applyChanges() {
        // Collect changes based on feature type
        let changes = {};
        
        if (this.feature === 'crop') {
          changes = {
            type: 'crop',
            width: this.cropWidth,
            height: this.cropHeight,
            ratio: this.selectedRatio
          };
        } else if (this.feature === 'background-remove') {
          changes = {
            type: 'background-remove',
            method: this.removalMethod
          };
        } else if (this.feature === 'background-replace') {
          changes = {
            type: 'background-replace',
            color: this.selectedBgColor
          };
        } else if (this.feature === 'clothes') {
          changes = {
            type: 'clothes'
          };
        } else if (this.feature === 'face') {
          changes = {
            type: 'face'
          };
        } else if (this.feature === 'enhance') {
          changes = {
            type: 'enhance',
            brightness: this.brightness,
            contrast: this.contrast,
            saturation: this.saturation,
            skinSmoothing: this.skinSmoothing
          };
        }
        
        this.$emit('apply-changes', changes);
      }
    }
  }
  </script>
  
  <style scoped>
  .control-panel {
    width: 280px;
    background-color: #2a2e32;
    border-left: 1px solid #3a3f45;
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  
  .panel-header {
    padding: 15px;
    border-bottom: 1px solid #3a3f45;
    display: flex;
    align-items: center;
  }
  
  .close-btn {
    background: transparent;
    border: none;
    color: #cccccc;
    width: 24px;
    height: 24px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-right: 10px;
  }
  
  .close-btn:hover {
    background-color: #3a3f45;
    color: #ffffff;
  }
  
  .panel-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 500;
  }
  
  .panel-content {
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
  
  .control-btn {
    background-color: #3a3f45;
    border: none;
    color: white;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 13px;
    cursor: pointer;
  }
  
  .control-btn:hover {
    background-color: #4a4f55;
  }
  
  .control-btn.active {
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
  
  .template-options {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .template-option {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    padding: 5px;
    border-radius: 4px;
    border: 2px solid transparent;
  }
  
  .template-option:hover {
    background-color: #3a3f45;
  }
  
  .template-option img {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .template-option span {
    font-size: 12px;
    text-align: center;
  }
  
  .upload-bg-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background-color: #3a3f45;
    border: none;
    color: white;
    padding: 8px;
    border-radius: 4px;
    cursor: pointer;
    width: 100%;
  }
  
  .upload-bg-btn:hover {
    background-color: #4a4f55;
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