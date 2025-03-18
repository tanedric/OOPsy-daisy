<template>
    <div class="feature-panel">
      <div class="logo">
        <h2>ID Photo Generator</h2>
      </div>
      
      <div class="feature-list">
        <button 
          v-for="feature in features" 
          :key="feature.id"
          :class="['feature-btn', { active: activeFeature === feature.id }]"
          @click="selectFeature(feature.id)"
          :title="feature.name"
        >
          <component :is="feature.icon" class="feature-icon" />
          <span class="feature-name">{{ feature.name }}</span>
        </button>
      </div>
      
      <div class="bottom-actions">
        <button class="help-btn" @click="showHelp">
          <i-lucide-help-circle class="icon" />
          <span>Help</span>
        </button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      activeFeature: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        features: [
          {
            id: 'upload',
            name: 'Upload',
            icon: 'i-lucide-upload'
          },
          {
            id: 'crop',
            name: 'Crop & Resize',
            icon: 'i-lucide-crop'
          },
          {
            id: 'background',
            name: 'Background',
            icon: 'i-lucide-image'
          },
          {
            id: 'adjust',
            name: 'Adjustments',
            icon: 'i-lucide-sliders'
          },
          {
            id: 'size',
            name: 'Output Size',
            icon: 'i-lucide-ruler'
          },
          {
            id: 'export',
            name: 'Export',
            icon: 'i-lucide-download'
          }
        ]
      };
    },
    methods: {
      selectFeature(featureId) {
        this.$emit('feature-selected', featureId);
      },
      showHelp() {
        // Implement help functionality
        console.log('Show help');
      }
    }
  }
  </script>
  
  <style scoped>
  .feature-panel {
    width: 80px;
    background-color: #252a2e;
    display: flex;
    flex-direction: column;
    border-right: 1px solid #3a3f45;
    transition: width 0.3s ease;
    overflow: hidden;
  }
  
  .feature-panel:hover {
    width: 180px;
  }
  
  .logo {
    padding: 15px 0;
    text-align: center;
    border-bottom: 1px solid #3a3f45;
  }
  
  .logo h2 {
    font-size: 14px;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .feature-list {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 15px 0;
    overflow-y: auto;
  }
  
  .feature-btn {
    display: flex;
    align-items: center;
    padding: 12px;
    background: transparent;
    border: none;
    color: #cccccc;
    cursor: pointer;
    text-align: left;
    transition: background-color 0.2s;
    border-left: 3px solid transparent;
  }
  
  .feature-btn:hover {
    background-color: #2a2e32;
    color: #ffffff;
  }
  
  .feature-btn.active {
    background-color: #2a2e32;
    color: #4a90e2;
    border-left-color: #4a90e2;
  }
  
  .feature-icon {
    width: 22px;
    height: 22px;
    margin-right: 12px;
    flex-shrink: 0;
  }
  
  .feature-name {
    white-space: nowrap;
    overflow: hidden;
  }
  
  .bottom-actions {
    padding: 15px 0;
    border-top: 1px solid #3a3f45;
  }
  
  .help-btn {
    display: flex;
    align-items: center;
    width: 100%;
    padding: 12px;
    background: transparent;
    border: none;
    color: #cccccc;
    cursor: pointer;
    text-align: left;
  }
  
  .help-btn:hover {
    background-color: #2a2e32;
    color: #ffffff;
  }
  
  .help-btn .icon {
    width: 22px;
    height: 22px;
    margin-right: 12px;
    flex-shrink: 0;
  }
  </style>