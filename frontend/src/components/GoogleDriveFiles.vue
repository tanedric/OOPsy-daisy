<template>
  <div class="google-drive-files">
    <div class="files-header">
      <h3>Google Drive Files</h3>
      <button class="refresh-btn" @click="fetchFiles">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
          <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"></path>
          <path d="M21 3v5h-5"></path>
          <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"></path>
          <path d="M8 16H3v5"></path>
        </svg>
      </button>
    </div>
    
    <div class="files-list" v-if="!loading">
      <div v-for="file in files" :key="file.id" class="file-item">
        <div class="file-icon" :class="getFileTypeClass(file.mimeType)">
          <svg v-if="isImageFile(file.mimeType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <circle cx="9" cy="9" r="2"></circle>
            <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"></path>
          </svg>
          
          <svg v-else-if="isPdfFile(file.mimeType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
          </svg>
          
          <svg v-else-if="isDocFile(file.mimeType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <line x1="10" y1="9" x2="8" y2="9"></line>
          </svg>
          
          <svg v-else-if="isExcelFile(file.mimeType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="3" y1="9" x2="21" y2="9"></line>
            <line x1="3" y1="15" x2="21" y2="15"></line>
            <line x1="9" y1="3" x2="9" y2="21"></line>
            <line x1="15" y1="3" x2="15" y2="21"></line>
          </svg>
          
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
          </svg>
        </div>
        <div class="file-info">
          <div class="file-name">{{ file.name }}</div>
          <div class="file-meta">
            <span class="file-type">{{ getFileType(file.mimeType) }}</span>
            <span class="file-date">{{ formatDate(file.modifiedTime) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="loading">
      Loading files...
    </div>
    
    <div v-if="error" class="error">
      {{ error }}
    </div>
  </div>
</template>

<script>
import axios from '../axiosConfig';

export default {
  name: 'GoogleDriveFiles',
  data() {
    return {
      files: [],
      loading: false,
      error: null
    };
  },
  methods: {
    async fetchFiles() {
      this.loading = true;
      this.error = null;
      try {
        const response = await axios.get('http://localhost:8080/api/drive/files', { 
          timeout: 30000,  // Increased timeout from 10000ms to 30000ms
          headers: { 'Cache-Control': 'no-cache' }
        });
        this.files = response.data;
      } catch (error) {
        console.error('Error fetching files:', error);
        this.error = 'Using sample data instead.';
        // Fallback to mock data if the API is unavailable
        this.files = this.getMockFiles();
      } finally {
        this.loading = false;
      }
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
    getFileType(mimeType) {
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
    getFileTypeClass(mimeType) {
      if (mimeType.startsWith('image/')) {
        return 'image-file';
      } else if (mimeType.includes('pdf')) {
        return 'pdf-file';
      } else if (mimeType.includes('word') || mimeType.includes('document')) {
        return 'doc-file';
      } else if (mimeType.includes('excel') || mimeType.includes('spreadsheet')) {
        return 'excel-file';
      } else if (mimeType.includes('text')) {
        return 'text-file';
      }
      return '';
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString();
    },
    isImageFile(mimeType) {
      return mimeType.startsWith('image/');
    },
    isPdfFile(mimeType) {
      return mimeType.includes('pdf');
    },
    isDocFile(mimeType) {
      return mimeType.includes('word') || mimeType.includes('document');
    },
    isExcelFile(mimeType) {
      return mimeType.includes('excel') || mimeType.includes('spreadsheet');
    }
  },
  mounted() {
    this.fetchFiles();
  }
};
</script>

<style scoped>
.google-drive-files {
  padding: 16px;
  background-color: #252a2e;
  border-radius: 8px;
  color: #ffffff;
}

.files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.files-header h3 {
  margin: 0;
  color: #ffffff;
}

.refresh-btn {
  background: transparent;
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.refresh-btn:hover {
  background-color: #3a3f45;
}

.files-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background-color: #2a2e32;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.file-item:hover {
  background-color: #3a3f45;
}

.file-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #4a90e2;
  border-radius: 4px;
}

.file-icon .icon {
  width: 20px;
  height: 20px;
  color: #ffffff;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.file-meta {
  display: flex;
  gap: 12px;
  font-size: 0.875rem;
  color: #a0a0a0;
}

.loading {
  text-align: center;
  padding: 24px;
  color: #a0a0a0;
}

.error {
  color: #ff6b6b;
  text-align: center;
  padding: 16px;
}

.file-icon.image-file {
  background-color: #4caf50;
}

.file-icon.pdf-file {
  background-color: #f44336;
}

.file-icon.doc-file {
  background-color: #2196f3;
}

.file-icon.excel-file {
  background-color: #4caf50;
}

.file-icon.text-file {
  background-color: #9e9e9e;
}
</style> 