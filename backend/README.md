# OOPsy-daisy Backend

## Google Drive Integration

To enable Google Drive integration, follow these steps:

1. Place your `credential.json` file in the `src/main/resources` directory
   - This file contains your OAuth 2.0 client credentials from Google Cloud Console
   - The file should be in the format provided by Google Cloud Console when you create OAuth credentials

2. When you first run the application, it will prompt you to authorize access to your Google Drive:
   - A browser window will open
   - Log in with your Google account
   - Grant permission to the application to access your Google Drive files
   - After authorization, the application will store tokens in a directory named `tokens` for future use

3. If the credential file is not found, the application will fall back to displaying sample files

## Getting Google API Credentials

If you don't have a `credential.json` file:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable the Google Drive API
4. Create OAuth 2.0 Client ID credentials
   - Set application type to "Desktop application"
5. Download the credentials as JSON file
6. Rename the downloaded file to `credential.json`
7. Place it in the `src/main/resources` directory

## Running the Application

```
./mvnw spring-boot:run
```

The backend will start on port 8080. 