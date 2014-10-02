package Santoshkumar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;
import org.opencv.core.Point;

class contour{
	Point boundary[]; 
	Mat mat,image;
	public contour(Point []pp,Mat mm,Mat image2){
		boundary = pp;
		mat = mm;
		image = image2;
	}
}

public class Contour_Extraction
{

	public static contour bPoints(String s) {

		Mat image;
		
		image = Highgui.imread(s,Highgui.CV_LOAD_IMAGE_COLOR);
		int rows = image.rows();
		int cols = image.cols();
		System.out.println("Initially rows  " + rows + " Columns " + cols);
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
		upperb = (1.015 * threshold > 255)?255:1.015 * threshold;		
		lowerb = (0.985 * threshold <0)?0:0.985 * threshold;
		
		Mat mat1 = Mat.zeros( rows,cols, CvType.CV_8UC1 );
		Mat mat2 = Mat.zeros( rows,cols, CvType.CV_8UC1 );
		Imgproc.threshold(mat, mat1,lowerb,255,Imgproc.THRESH_BINARY_INV);
		Imgproc.threshold(mat, mat2,upperb,255,Imgproc.THRESH_BINARY);
		Core.max(mat1, mat2, mat);

		/* Segmentation and Morphological Processing*/
		Imgproc.medianBlur(mat, mat, 9);
		Mat kernel = Mat.ones(10, 5, CvType.CV_8UC1) ;
		Imgproc.morphologyEx(mat, mat, Imgproc.MORPH_OPEN, kernel);

		/*Finding the maximum Connected Component*/
		Mat m = Mat.zeros(mat.size(),CvType.CV_32F);
		mat.copyTo(m);
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		// Gets all the contours Retr_List , 
		Imgproc.findContours(m, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
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
		m = Mat.zeros(mat.size(),CvType.CV_32F);
		Scalar color = new Scalar(255);
		Imgproc.drawContours(m, contours, maxAreaIdx, color,Core.FILLED);
		Highgui.imwrite("./src/result.jpg", m);
		
		/*Conversion the Contour Points to an array*/
		MatOfPoint maxC = contours.get(maxAreaIdx);
		Point[] boundary = maxC.toArray();
		
		/*Reverse x and Y of boundary*/
		for(Point p: boundary){
			double t1 = p.x, t2 = p.y;
			p.x = t2 ; 
			p.y = t1;
		}
		
		try {
			write("./src/boundary.txt",boundary);
		}
		catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
		
		/*The Segmented Image Printing*/
		System.out.println("Max Area "+maxArea+" Maxidx "+maxAreaIdx+" Size "+contours.size());
		Highgui.imwrite( "./src/test.bmp", mat );
		
		return (new contour(boundary,m,image));
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

	public static void main( String[] args )
	{

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		//Point[] boundary = bPoints("./src/Image001.jpg");
		contour c = bPoints("C:/Users/dell/Desktop/OpenSoft_2014/Java_Opencv/Santosh/bin/Santoshkumar/resources/Image006.jpg");
		Corner_Extraction.corner_detection(c.boundary,c.image,150.0,"xyz1.jpg");
		/*ArrayList <Point> x;
		x = Polygon_corner.RamerDouglasPeuckerAlgo(Arrays.asList(c.boundary), 60.0);
		int max_x, max_y,i,j;
		double dd[] = {255,0,0};
		
		for(Point p: x){
			System.out.println( "X = " + p.x +  "Y = " + p.y );
			max_x = (int) p.x ; max_y = (int) p.y ;
			for(i = max_x ; i <= max_x + 25 ; i++) //-> Y direction 
				for(j = max_y; j <= max_y + 25 ; j++)// -> X direction 
					c.image.put(i, j, dd);
		}
		
		Highgui.imwrite( "./src/test2_3.bmp", c.image );
		*/
		 //ArrayList<Integer> ans = Polygon_corner.corner_points(Arrays.asList(c.boundary), 60.0, c.image, "Image006.jpg");
	}
	
}
