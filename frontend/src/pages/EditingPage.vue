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
      
      <!-- Google Drive Button (now more visible) -->
      <button class="google-drive-btn" @click="openGoogleDrive">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="button-icon">
          <path d="M4 20h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.93a2 2 0 0 1-1.66-.9l-.82-1.2A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13c0 1.1.9 2 2 2Z"/>
        </svg>
        Google Drive Files
      </button>
      
      <!-- Drive Status Message (if any) -->
      <div v-if="driveStatus" class="drive-status" :class="{ connected: driveConnected }">
        {{ driveStatus }}
      </div>
      
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
import GoogleDriveFiles from '../components/GoogleDriveFiles.vue';
import axios from '../axiosConfig';

export default {
  components: {
    Header,
    FeaturePanel,
    ImageComponent,
    ControlPanel,
    GoogleDriveFiles
  },
  data() {
    return {
      activeFeature: null,
      currentPhoto: null,
      canUndo: false,
      canRedo: false,
      driveStatus: null,
      driveConnected: false
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
    },
    getGoogleDriveFiles() {
      axios.get('http://localhost:8080/api/drive/files', { 
        timeout: 30000,  // Increased timeout from 5000ms to 30000ms
        headers: { 'Cache-Control': 'no-cache' }
      })
        .then(response => {
          const files = response.data;
          console.log('Google Drive Files:');
          console.table(files.map(file => ({
            Name: file.name,
            Type: this.getFileTypeDisplay(file.mimeType),
            Modified: new Date(file.modifiedTime).toLocaleString()
          })));
          
          this.driveStatus = `Found ${files.length} files in Google Drive`;
          setTimeout(() => {
            this.driveStatus = null;
          }, 3000);
        })
        .catch(error => {
          console.error('Error fetching Google Drive files:', error);
          this.driveStatus = 'Using sample data instead of real Google Drive files';
          
          // Mock data for demonstration when backend is unavailable
          const mockFiles = this.getMockFiles();
          console.log('Using mock Google Drive Files:');
          console.table(mockFiles.map(file => ({
            Name: file.name,
            Type: this.getFileTypeDisplay(file.mimeType),
            Modified: new Date(file.modifiedTime).toLocaleString()
          })));
          
          setTimeout(() => {
            this.driveStatus = null;
          }, 3000);
        });
    },
    
    // Generate mock files for testing when backend is unavailable
    getMockFiles() {
      return [
        {
          id: 'sample-1',
          name: 'Resume.pdf',
          mimeType: 'application/pdf',
          modifiedTime: new Date().toISOString()
        },
        {
          id: 'sample-2',
          name: 'Profile Picture.jpg',
          mimeType: 'image/jpeg',
          modifiedTime: new Date(Date.now() - 86400000).toISOString()
        },
        {
          id: 'sample-3',
          name: 'Project Plan.docx',
          mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          modifiedTime: new Date(Date.now() - 172800000).toISOString()
        },
        {
          id: 'sample-4',
          name: 'Budget.xlsx',
          mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          modifiedTime: new Date(Date.now() - 259200000).toISOString()
        },
        {
          id: 'sample-5',
          name: 'Notes.txt',
          mimeType: 'text/plain',
          modifiedTime: new Date(Date.now() - 345600000).toISOString()
        }
      ];
    },
    getFileTypeDisplay(mimeType) {
      const types = {
        'image/jpeg': 'Image',
        'image/png': 'Image',
        'application/pdf': 'PDF',
        'application/msword': 'Word',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'Word',
        'application/vnd.ms-excel': 'Excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'Excel',
        'text/plain': 'Text'
      };
      return types[mimeType] || 'File';
    },
    checkDriveStatus() {
      axios.get('http://localhost:8080/api/drive/status', { 
        timeout: 30000,  // Increased timeout from 5000ms to 30000ms
        headers: { 'Cache-Control': 'no-cache' } 
      })
        .then(response => {
          this.driveStatus = response.data.message;
          this.driveConnected = response.data.connected;
        })
        .catch(error => {
          console.error('Error checking drive status:', error);
          this.driveStatus = 'Using mock data - backend not available';
          this.driveConnected = false;
          
          setTimeout(() => {
            this.driveStatus = null;
          }, 3000);
        });
    },
    openGoogleDrive() {
      this.setActiveFeature('google-drive');
      this.getGoogleDriveFiles();
    }
  },
  mounted() {
    this.checkDriveStatus();
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
  position: relative;
}

.google-drive-btn {
  position: absolute;
  top: 70px;
  right: 20px;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.google-drive-btn:hover {
  background-color: #3a80d2;
}

.button-icon {
  width: 16px;
  height: 16px;
}

.drive-status {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background-color: #333;
  color: #fff;
  padding: 8px 16px;
  border-radius: 4px;
  z-index: 10;
}

.drive-status.connected {
  background-color: #4caf50;
}
</style>