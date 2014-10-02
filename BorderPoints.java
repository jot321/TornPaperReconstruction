import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

public class test1
{
   public static void main( String[] args )
   {
	   
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      Point[] boundary = bPoints();
      /*Writing border points to a file*/
      try {
      write("./src/boundary.txt",boundary);
      }
      catch (IOException e) {
    	    System.err.println("Caught IOException: " + e.getMessage());
      }
   }
   
   public static Point[] bPoints() {
	  
	  Mat image;
      image = Highgui.imread("./src/Image001.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
      int rows = image.rows();
      int cols = image.cols();
      Mat mat = Mat.zeros( rows,cols, CvType.CV_8UC1 );
      Imgproc.cvtColor(image, mat,Imgproc.COLOR_RGB2GRAY);      
      
      /* Histogram Computation*/
      int[] hist = new int[256];
      for(int i =0;i<256;i++) {
    	  hist[i] = 0;
      }
      for(int i=0;i<rows;i++) {
    	  for(int j=0;j<cols;j++) {
    		  hist[(int)(mat.get(i,j)[0])] += 1;	   
    	  }
      }
      int max = hist[0];
      int max_p=0;
      for(int i =0;i<256;i++){
    	  if(max < hist[i]) {
    		  max = hist[i];
    		  max_p = i;
    	  }
      }
      
      /*Thresholding*/
      int threshold = max_p;
      double lowerb,upperb;
      upperb = (1.01*threshold > 255)?255:1.01*threshold;
      lowerb = (0.99*threshold < 0)?0:0.99*threshold;
      Mat mat1 = Mat.zeros( rows,cols, CvType.CV_8UC1 );
      Mat mat2 = Mat.zeros( rows,cols, CvType.CV_8UC1 );
      Imgproc.threshold(mat, mat1,lowerb,255,Imgproc.THRESH_BINARY_INV);
      Imgproc.threshold(mat, mat2,upperb,255,Imgproc.THRESH_BINARY);
      Core.max(mat1, mat2, mat);
      
      /* Segmentation and Morphological Processing*/
      Imgproc.medianBlur(mat, mat, 9);
      Mat kernel = Mat.ones(10, 5, CvType.CV_8UC1) ;
      Imgproc.morphologyEx(mat, mat, Imgproc.MORPH_OPEN, kernel);
   
      /*Edge Detection*/
      Imgproc.Canny(mat, mat, 0.2, 0.1);
      
      /*Finding the maximum Connected Component*/
      ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
      Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
      double maxArea = -1;
      int maxAreaIdx = -1;
      for (int idx = 0; idx < contours.size(); idx++) {
          Mat contour = contours.get(idx);
          double contourarea = Imgproc.contourArea(contour);
          if (contourarea > maxArea) {
              maxArea = contourarea;
              maxAreaIdx = idx;
          }		
      }
      
      /*Conversion the Contour Points to an array*/
      MatOfPoint maxC = contours.get(maxAreaIdx);
      Point[] boundary = maxC.toArray();
      
      return boundary;
   }
   
   public static void write (String filename, Point[] x) throws IOException{
	   BufferedWriter outputWriter = null;
	   outputWriter = new BufferedWriter(new FileWriter(filename));
	   for (int i = 0; i < x.length; i++) {
	     outputWriter.write(x[i]+"");
	     outputWriter.newLine();
	   }
	   outputWriter.flush();  
	   outputWriter.close();  
	 }
}
