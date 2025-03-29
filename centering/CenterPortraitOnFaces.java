import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CenterPortraitOnFaces {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        try {
            // Replace with your input and output file paths
            String inputPath = "./portrait.jpg";
            String outputPath = "centered_portrait.jpg";
            
            // Read input image to get its dimensions
            BufferedImage inputImage = ImageIO.read(new File(inputPath));
            int targetWidth = inputImage.getWidth();
            int targetHeight = inputImage.getHeight();
            
            centerPortraitOnFaces(inputPath, outputPath, targetWidth, targetHeight);
            System.out.println("Portrait centered on faces and saved to " + outputPath);
            
        } catch (Exception e) {
            System.err.println("Error processing the image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void centerPortraitOnFaces(String inputPath, String outputPath, int targetWidth, int targetHeight) throws IOException {
        // Read the input image using OpenCV
        Mat image = Imgcodecs.imread(inputPath);
        if (image.empty()) {
            throw new IOException("Could not read the image: " + inputPath);
        }
        
        // Convert to grayscale for face detection
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        
        // Load the face detector (make sure to have the XML file in your project)
        String cascadePath = "haarcascade_frontalface_default.xml"; // Default OpenCV face cascade
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            throw new IOException("Could not load face cascade classifier");
        }
        
        // Detect faces
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faceDetections);
        Rect[] faces = faceDetections.toArray();
        
        if (faces.length == 0) {
            System.out.println("No faces detected. Centering the entire image instead.");
            // Fall back to centering the entire image if no faces are detected
            centerEntireImage(inputPath, outputPath, targetWidth, targetHeight);
            return;
        }
        
        // Calculate the center of all faces
        int faceCenterX = 0;
        int faceCenterY = 0;
        for (Rect face : faces) {
            faceCenterX += face.x + face.width / 2;
            faceCenterY += face.y + face.height / 2;
        }
        faceCenterX /= faces.length;
        faceCenterY /= faces.length;
        
        // Calculate the image center
        int imageCenterX = image.width() / 2;
        int imageCenterY = image.height() / 2;
        
        // Calculate the offset to move the face center to the image center
        int offsetX = imageCenterX - faceCenterX;
        int offsetY = imageCenterY - faceCenterY;
        
        // Create a transformation matrix for translation
        Mat translationMatrix = new Mat(2, 3, CvType.CV_32F);
        translationMatrix.put(0, 0, 1, 0, offsetX, 0, 1, offsetY);
        
        // Apply the translation
        Mat centeredImage = new Mat();
        Imgproc.warpAffine(image, centeredImage, translationMatrix, image.size(), Imgproc.INTER_LINEAR, Core.BORDER_CONSTANT, new Scalar(255, 255, 255));

        
        // Convert the OpenCV Mat to a BufferedImage for further processing
        BufferedImage bufferedImage = matToBufferedImage(centeredImage);
        
        // Create a new blank image with the target dimensions
        BufferedImage outputImage = new BufferedImage(
            targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        // Create graphics context and fill with white background
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, targetWidth, targetHeight);
        
        // Calculate position to center the image
        int x = (targetWidth - bufferedImage.getWidth()) / 2;
        int y = (targetHeight - bufferedImage.getHeight()) / 2;
        
        // Draw the centered image
        g2d.drawImage(bufferedImage, x, y, null);
        g2d.dispose();
        
        // Save the result
        ImageIO.write(outputImage, "jpg", new File(outputPath));
    }
    
    // Fall back method to center the entire image if no faces are detected
    public static void centerEntireImage(String inputPath, String outputPath, 
                                       int targetWidth, int targetHeight) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(inputPath));
        
        BufferedImage outputImage = new BufferedImage(
            targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, targetWidth, targetHeight);
        
        int x = (targetWidth - inputImage.getWidth()) / 2;
        int y = (targetHeight - inputImage.getHeight()) / 2;
        
        g2d.drawImage(inputImage, x, y, null);
        g2d.dispose();
        
        ImageIO.write(outputImage, "jpg", new File(outputPath));
        System.out.println("Error, pls include face in the photo");
    }
    
    // Helper method to convert OpenCV Mat to BufferedImage
    private static BufferedImage matToBufferedImage(Mat mat) throws IOException {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        return ImageIO.read(byteArrayInputStream);
    }
}
