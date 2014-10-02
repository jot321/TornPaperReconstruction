package Santoshkumar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

class image_fragment{
	ArrayList<Edge_Envelope> feature = new ArrayList<Edge_Envelope>();
	Mat mat,contour;
	String s;
}

public class Coordinator {

	public static double getRa(Point CP1_1, Point CP1_2, Point CP3_2, Point CP3_3)
	{
		double RA, angleA, angleB;
		if(CP1_2.x!=CP1_1.x)angleA=Math.atan((CP1_2.y-CP1_1.y)/(CP1_2.x-CP1_1.x));
		else angleA=Math.PI/2;
		if(CP3_3.x!=CP3_2.x)angleB=Math.atan((CP3_3.y-CP3_2.y)/(CP3_3.x-CP3_2.x));
		else angleB=Math.PI/2;

		RA=angleA-angleB;
		return RA;
	}

	public static double getTx(Point CP1_1, Point CP1_2, Point RCP3_2, Point RCP3_3)
	{
		double Tx;
		//Tx=0.5*((CP1_1.x+CP1_2.x)-(RCP3_3.x-RCP3_2.x));
		Tx= 0.5*((CP1_1.x+CP1_2.x)-(RCP3_3.x+RCP3_2.x));
		//Tx = CP1_1.x - RCP3_2.x ;
		return Tx;
	}
	public static double getTy(Point CP1_1, Point CP1_2, Point RCP3_2, Point RCP3_3)
	{
		double Ty;
		//Ty=0.5*((CP1_1.y+CP1_2.y)-(RCP3_3.y-RCP3_2.y));
		Ty= 0.5*((CP1_1.y+CP1_2.y)-(RCP3_3.y+RCP3_2.y));
		//Ty = CP1_1.y - RCP3_2.y ;
		return Ty;
	}

     public static ArrayList<Point> getlist(int to,int from,Point[] boundary){
    	 ArrayList<Point> p = new ArrayList<Point>();
    	 for(int i=to ; i< boundary.length ; i++)
    		 p.add(boundary[i]);
    	 for(int i=0 ; i<= to ; i++)
    		 p.add(boundary[i]);    	 
    	 return p;
     }
     
	public static void main(String [] args)throws IOException{

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		ArrayList<image_fragment> fragments = new ArrayList<image_fragment>();

		String my_path = "C:/Users/dell/Desktop/OpenSoft_2014/Java_Opencv/Santosh/bin/Santoshkumar/resources",temp ;
		int i,j,a,b,numf_a,numf_b,index1,index2,size ;
		double dd[] = {255,0,0};
		contour c;
		File folder = new File(my_path);
		File[] listOfFiles = folder.listFiles();
		ArrayList<Edge> Edges ;
		Edge_Envelope envelope;
		double max_div ;
		
		// Initial Set of Images 
		for (i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				temp = my_path + "/" + listOfFiles[i].getName() ;

				/*Create a new Fragment for each image*/
				fragments.add(new image_fragment());
				fragments.get(i).mat = Highgui.imread(temp,Highgui.CV_LOAD_IMAGE_COLOR);
				
				/*Contour and Boundary Extraction*/
				System.out.println(temp);
				c = Contour_Extraction.bPoints(temp);
				
				// Contour Matrix + Corner Points 
				fragments.get(i).contour = c.mat ;
				Edges = Polygon_corner.corner_points(c.boundary,Arrays.asList(c.boundary),150.0 , c.image,listOfFiles[i].getName());
				//Edges = Corner_Extraction.corner_detection(c.boundary,c.image,150.0,listOfFiles[i].getName());
				
				size = Edges.size();
				fragments.get(i).s = listOfFiles[i].getName() ;
				System.out.println("No. of points :" + c.boundary.length);
				
				// PARAMETER
				max_div = 2000;
				
				for(j = 0 ; j < size ; j++){
					index1 = Edges.get(j).i1;
					index2 = Edges.get(j).i2;
					System.out.println(" 1: " + index1 + " 2: " + index2 +  "11::" + c.boundary[index1] + "2::" +c.boundary[index2]);
					if(index2+1 > index1)
						envelope = Feature_vector.fb_envelope(Arrays.asList(c.boundary).subList(index1, index2+1));
					else
						envelope = Feature_vector.fb_envelope(getlist(index1,index2+1,c.boundary));
					if(envelope.deviation() > max_div)
						fragments.get(i).feature.add(envelope);
				}
			}
			else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

		System.out.print("End");
		//System.exit(1);
		
		/* NEED (Normalized Edge Envelope Detection) */
		/* Image i , j chosen and fragment a,b chosen */
		
		ArrayList<edge_set> s1,s2;
		int size_s1,size_s2,m,k,min ;
		int min_a, min_b, min_i, min_j ;
		int row,col ;
		double tot,min_tot,alpha,beta ;
		Mat rotated_img,source_img,source_img_2 ;
		Mat map_matrix = new Mat(2,3,CvType.CV_32FC1 );
		double t_x , t_y,theta ;
		Point center , CP1_1 , CP1_2 , CP3_2 , CP3_3 , RCP3_2  , RCP3_3 ;
		center = new Point();
		RCP3_2  = new Point() ; 
		RCP3_3  = new Point() ;
		//rotated_img = new Mat();
		while(fragments.size() > 1){
			min_a = -1;min_b = -1;min_i = -1;min_j = -1;
			min_tot = -2;
			
			// i -> fragment number i
			// j -> fragment number j
			// a -> edge number a
			// b -> edge number b
			// Value of matching  
			// min_i // min_j
			// min_a // min_b 
			
			for(i=0;i<fragments.size();i++){ 
				numf_a = fragments.get(i).feature.size();
				
				for(j=i+1;j<fragments.size();j++){
					numf_b = fragments.get(j).feature.size();
					
					for(a = 0;a < numf_a;a++){
						
						for(b = 0 ; b < numf_b; b++){
							s1 = fragments.get(i).feature.get(a).forward;
							s2 = fragments.get(j).feature.get(b).backward;
							Collections.reverse(s2);
							size_s1 = s1.size();
							size_s2 = s2.size();
							
							//Sample
							m = Math.max(size_s1,size_s2);
							min = Math.min(size_s1, size_s2);
							
							// InterPolation
							tot = 0 ;
							for(k=0;k<m;k++){
								if(k < min) tot += Math.abs(s1.get(k).distance - s2.get(k).distance);
								else {
									if(size_s1 == min) 
										tot += Math.abs(s1.get(min-1).distance - s2.get(k).distance) + Math.abs(s1.get(min-1).angle - s2.get(k).angle);
									else 
										tot += Math.abs(s1.get(k).distance - s2.get(min-1).distance) + Math.abs(s1.get(k).angle - s2.get(min-1).angle);
								}
							}
							tot/=m;
							System.out.println("I " + i + " J " + j + " A " +a  + " B " + b + " tot " + tot );
							if(((min_tot < -1) || (tot < min_tot) )){
								min_tot = tot; min_a = a ; min_b = b; min_i = i; min_j = j;
							}
						}
					}
				}
			}
			
			System.out.println("Images Combined Image 1: " + fragments.get(min_i).s + "Image 2 :" + fragments.get(min_j).s);
			System.out.println(" min I " + min_i + " min J " + min_j + " min A " + min_a  + " min B " + min_b  );
			
			// Rotation And Translation Phase
			CP1_1 = fragments.get(min_i).feature.get(min_a).fStart;
			CP1_2 = fragments.get(min_i).feature.get(min_a).fEnd ;
			
			CP3_2 = fragments.get(min_j).feature.get(min_b).fEnd;
			CP3_3 = fragments.get(min_j).feature.get(min_b).fStart ;
			
			// Draw the portions to be combined
			source_img = fragments.get(min_j).mat;
			source_img_2 = fragments.get(min_i).mat;
			double tempp[] = {255,0,0};
			for(i = (int) CP3_2.x ; i <= CP3_2.x + 300 ; i++) // Y direction 
			for(j = (int) CP3_2.y ; j <= CP3_2.y  + 300 ; j++) // X direction 
				source_img.put(i, j, tempp);
			
			for(i = (int) CP3_3.x ; i <= CP3_3.x + 100 ; i++) // Y direction 
				for(j = (int) CP3_3.y ; j <= CP3_3.y  + 100 ; j++) // X direction 
					source_img.put(i, j, tempp);
			
			for(i = (int) CP1_1.x ; i <= CP1_1.x + 300 ; i++) // Y direction 
				for(j = (int) CP1_1.y ; j <= CP1_1.y  + 300 ; j++) // X direction 
					source_img_2.put(i, j, tempp);
			
			for(i = (int) CP1_2.x ; i <= CP1_2.x + 100 ; i++) // Y direction 
				for(j = (int) CP1_2.y ; j <= CP1_2.y  + 100 ; j++) // X direction 
					source_img_2.put(i, j, tempp);
			
			Highgui.imwrite( "./src/test_edge_1.jpg", source_img );
			Highgui.imwrite( "./src/test_edge_2.jpg", source_img_2 );
			//System.exit(1);
			
			
			rotated_img = Mat.zeros( 3*source_img.rows(), 3*source_img.cols(), source_img.type() );
			theta = getRa(CP1_1,CP1_2,CP3_2,CP3_3); //theta = 0.1 ;
			System.out.println("Theta = "+ theta +  " rows " + source_img.rows() + " cols " + source_img.cols());
			
			
			// Rotation Angle
			alpha = Math.cos(theta);
			beta  = Math.sin(theta);
			center = new Point(source_img.rows()/2,source_img.cols()/2) ;
			//center = new Point((CP3_2.x + CP3_3.x)/2, (CP3_2.y + CP3_3.y)/2) ;
			System.out.println("Center  :" + center);
			map_matrix = Imgproc.getRotationMatrix2D( center, theta*180/Math.PI, 1.0 );
			//Imgproc.warpAffine(source_img, rotated_img,map_matrix,rotated_img.size());
			//Highgui.imwrite( "./src/test1.jpg", rotated_img );
			//break;
			//System.exit(1);
			
			// Rotated Point of image 2
			RCP3_2.x = map_matrix.get(0, 0)[0]*CP3_2.x + map_matrix.get(0, 1)[0]*CP3_2.y +  map_matrix.get(0, 2)[0] ;
			RCP3_3.x = alpha*CP3_3.x + beta*CP3_3.y +  (1 - alpha)*center.x - beta*center.y ;
			RCP3_2.y = -beta*CP3_2.x + alpha*CP3_2.y + beta*center.x + (1- alpha)*center.y ;
			RCP3_3.y = -beta*CP3_3.x + alpha*CP3_3.y + beta*center.x + (1- alpha)*center.y ;
			System.out.println( CP1_1 + " " + CP1_2 + " " + CP3_2 + " " + CP3_3);
			System.out.println( RCP3_2 +  " " +RCP3_3);
			t_x = t_y = 0;
			
			// get t_x , t_y 
			t_x = getTx(CP1_1,CP1_2,RCP3_2,RCP3_3);
			t_y = getTy(CP1_1,CP1_2,RCP3_2,RCP3_3);
			System.out.println("Shift By t_x = "+ t_x + "  t_y=  "  + t_y);
		
			
			map_matrix.put(0,0,alpha,beta, (1 - alpha)*center.x - beta*center.y + t_y + source_img.cols());
			map_matrix.put(1,0,-beta,alpha,(1 - alpha)*center.y + beta*center.x + t_x + source_img.rows());
			Imgproc.warpAffine(source_img, rotated_img,map_matrix,rotated_img.size());
			Highgui.imwrite( "./src/out1.jpg", rotated_img );
			//System.exit(1);
			
			/*Mat mat = Mat.ones( 1500,1500, CvType.CV_8UC3 );
			Rect roi = new Rect(RCP3_2,new Size(1500.0,1500.0));
			Mat destinationROI = new Mat(rotated_img,roi);
			mat.copyTo(destinationROI);
			*/
			
			// Copy image 1 on image 2
			row = source_img_2.rows() ;
			col = source_img_2.cols() ;
			for(i = 0 ; i < row ; i++)
				for(j = 0 ; j < col; j++)
					if(fragments.get(min_i).contour.get(i, j)[0] == 255)
						rotated_img.put(i+source_img.rows(), j+source_img.cols(), source_img_2.get(i, j));
			Highgui.imwrite( "./src/out3.jpg", rotated_img );
			break;
			
			//System.out.println("Alas");
			/* Joining Fragments min_i , min_j */
			
		}
		return ;
	}
}
