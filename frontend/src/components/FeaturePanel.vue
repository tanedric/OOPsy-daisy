<template>
  <div class="feature-panel">
    <div class="feature-list">
      <button 
        v-for="feature in features" 
        :key="feature.id"
        :class="['feature-btn', { active: activeFeature === feature.id }]"
        @click="selectFeature(feature.id)"
        :title="feature.name"
      >
        <div class="feature-icon">
          <!-- Simple custom icons instead of Lucide -->
          <svg v-if="feature.id === 'crop'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 2v14a2 2 0 0 0 2 2h14"></path>
            <path d="M18 22V8a2 2 0 0 0-2-2H2"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'background-remove'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 3h18v18H3z"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'background-replace'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="9" cy="9" r="2"></circle>
            <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'clothes'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
            <path d="M3 6h18"></path>
            <path d="M10 6V2"></path>
            <path d="M14 6V2"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'face'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <circle cx="8" cy="9" r="1"></circle>
            <circle cx="16" cy="9" r="1"></circle>
            <path d="M8 14h.01"></path>
            <path d="M16 14h.01"></path>
            <path d="M12 16v2"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'enhance'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z"></path>
          </svg>
          
          <svg v-else-if="feature.id === 'google-drive'" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z"></path>
          </svg>
          
        </div>
        <span class="feature-name">{{ feature.name }}</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    activeFeature: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      features: [
        {
          id: 'crop',
          name: 'Crop & Resize'
        },
        {
          id: 'background-remove',
          name: 'Background Removal'
        },
        {
          id: 'background-replace',
          name: 'Background Replace'
        },
        {
          id: 'clothes',
          name: 'Clothes Replace'
        },
        {
          id: 'face',
          name: 'Face Centering'
        },
        {
          id: 'enhance',
          name: 'Photo Enhancement'
        },
        {
          id: 'google-drive',
          name: 'Google Drive'
        }
      ]
    };
  },
  methods: {
    selectFeature(featureId) {
      this.$emit('feature-selected', featureId);
    }
  }
}
</script>

<style scoped>
.feature-panel {
  width: 100px;
  background-color: #252a2e;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #3a3f45;
  overflow: hidden;
}

.feature-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px 0;
  overflow-y: auto;
}

.feature-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  background: transparent;
  border: none;
  color: #cccccc;
  cursor: pointer;
  text-align: center;
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
  width: 24px;
  height: 24px;
  margin-bottom: 6px;
}

.feature-name {
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>