package Santoshkumar;

import java.util.*;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;


class Point_Index{
	Point p;
	int index;
	public Point_Index(Point P,int i){
		p = P;
		index = i; 
	}
}





public class Corner_Extraction {

	
	public static ArrayList<Edge> corner_detection(Point[] points,Mat image1,double lthreshold,String f_name){
		int transform[][] = {{3,2,1},{4,-1,0},{5,6,7}};
		double param_del,param_tau,angle, bend_value[] ;
		int numpoints,i,param_k;
		double p1_x, p1_y, p2_x, p2_y,fst_transform,prev_transform,cur_transform;
		ArrayList<Integer> bend_point = new ArrayList<>();
		ArrayList<Point_Index> corners = new ArrayList<Point_Index>();
		ArrayList<Point_Index> nonspuriouscorners = new ArrayList<Point_Index>();
		
		/*Set param_k , param_del , angle threshold*/
		param_k = 4 ; param_del =  0.25; 
		angle = 1.60 ;
		//angle = 1.7 ;
		
		/*First Two points Taken*/
		numpoints = points.length;
		if( numpoints < 2){
			System.out.println("Insufficient Points");
			return null;
		}
		p1_x = points[0].x ; p1_y = points[0].y;
		p2_x = points[1].x ; p2_y = points[1].y;
		fst_transform = prev_transform = transform[(int) (p2_x-p1_x+1)][(int) (p2_y-p1_y+1)];

		/*For Any General Point*/
		for( i = 2; i < numpoints ; i++){
			p1_x = p2_x ; p1_y = p2_y ;
			p2_x = points[i].x ; p2_y = points[i].y ;
			cur_transform = transform[(int) (p2_x-p1_x+1)][(int) (p2_y-p1_y+1)];
			if(cur_transform != prev_transform){
				bend_point.add(i-1);
			}
			prev_transform = cur_transform ;
		}
		cur_transform = transform[(int) (points[0].x-points[numpoints-1].x+1)][(int) (points[0].y-points[numpoints-1].y+1)];
		if(cur_transform != prev_transform)
			bend_point.add(numpoints-1);
		if(cur_transform != fst_transform)
			bend_point.add(0);


		param_tau = 2*param_k*param_del ;
		double xf,xb,yf,yb,xc,yc ;
		int temp,j,k = param_k,n=numpoints ;
		bend_value = new double[n];

		// Find Bind Value for each pixel
		i = 0 ;
		for(Point p: points){
			if(i+k >= n) 
				temp = i+k-n;
			else 
				temp = i+k ; 
			xf = points[temp].x - p.x;
			yf = points[temp].y - p.y;
			if(i-k <  0)
				temp = i-k + n ;
			else
				temp = i-k ;
			xb = points[temp].x - p.x;
			yb = points[temp].y - p.y;
			xc = Math.abs(xf+xb);
			yc = Math.abs(yf+yb);
			bend_value[i] =  Math.max(xc, yc);
			i++;
		}

		//Find the corners
		int num_probcorners = bend_point.size() , indx1 , indx2 ;
		for(i = 0 ; i < num_probcorners ; i++){
			indx1 = bend_point.get(i);
			j = -k ;
			if(bend_value[indx1] >= param_tau)
				for(;j<=k;j++){
					if(indx1 - k < 0)
						indx2 = indx1 - k + n;
					else
						indx2 = indx1 - k ;
					if(bend_value[indx1] < bend_value[indx2])
						break;
				}
			if(j == k+1)
				corners.add(new Point_Index(points[indx1],indx1));
		}
		
		ArrayList<Point> temp1 = new ArrayList<Point>();
		for(Point_Index p: corners){
			temp1.add(p.p);
		}
		
		ArrayList<Edge> Edges = Polygon_corner.corner_points(points,temp1,lthreshold,image1,f_name);
		return Edges;
		
		/*End points Before Spurious Removal*/
		/*System.out.println( "Probable Corner Size = " + num_probcorners );
		System.out.println( "Corner Size = " + corners.size() );
		int max_x = -1 , max_y = -1;
		double dd[] = {255,0,0};
		for(Point_Index p: corners){
			System.out.println( "X = " + p.p.x +  "Y = " + p.p.y );
			max_x = (int) p.p.x ; max_y = (int) p.p.y ;
			for(i = max_x ; i <= max_x + 5 ; i++) //-> Y direction 
				for(j = max_y; j <= max_y + 5 ; j++)// -> X direction 
					image1.put(i, j, dd);
		}
		/*max_x = (int) 1984.0 ; max_y = (int) 2384.0 ; 
		for(i = max_x ; i <= max_x + 150 ; i++) //-> Y direction 
			for(j = max_y; j <= max_y + 150 ; j++)// -> X direction 
				image1.put(i, j, dd);
		Highgui.imwrite( "./src/test2_3.bmp", image1 );
	*/
		
		/*Spurious Point Removal*/
		/*int corner_size =  corners.size(),first_indx = -1 , prev_indx = -1;
		Point u,v,point_cur,point_bef,point_after; 
		double val;
		for(i =0;i<corner_size;i++){
			point_cur = corners.get(i).p;
			point_bef = corners.get((i-1 + corner_size)%corner_size).p;
			point_after = corners.get((i+1)%corner_size).p;
			u = new Point(point_bef.x - point_cur.x,point_bef.y - point_cur.y);
			v = new Point(point_after.x - point_cur.x,point_after.y - point_cur.y);
			val = Math.acos( (u.x*v.x + u.y*v.y)/(Math.sqrt(u.x*u.x + u.y*u.y) * Math.sqrt(v.x*v.x + v.y*v.y))) ;
			//System.out.println(val);
			if( point_cur.x == 1983 && point_cur.y == 2384){
				System.out.println("Point u " + u + " v " + v);
				System.out.println( (u.x*v.x + u.y*v.y) +"\n Angle " + val);
				System.out.println(  " u val " + Math.sqrt(u.x*u.x + u.y*u.y)  +  " v val " + Math.sqrt(v.x*v.x + v.y*v.y) );
			} 
			if(val < angle){
				if(prev_indx == -1)
					first_indx = prev_indx = i; // index in the corner array
				else if(i != (prev_indx+1)%corner_size)
					Edges.add(new Edge(corners.get(prev_indx).index,corners.get(i).index));
				prev_indx = i;
				nonspuriouscorners.add(new Point_Index(point_cur,corners.get(i).index) );
			}
		}
		 
		
		/*One final Edge*/
		/*if(first_indx != (prev_indx+1)%corner_size)
			Edges.add(new Edge(corners.get(prev_indx).index,corners.get(first_indx).index));
		
		
		/*End points After Spurious Removal*/
		/*System.out.println( "After Spurious Removal" );
		System.out.println( "Actual Corner Size = " + nonspuriouscorners.size() );
		for(Point_Index p: nonspuriouscorners){
			System.out.println( "X = " + p.p.x +  "Y = " + p.p.y );
			max_x = (int) p.p.x ; max_y = (int) p.p.y ;
			for(i = max_x ; i <= max_x + 75 ; i++) //-> Y direction 
				for(j = max_y; j <= max_y + 75 ; j++)// -> X direction 
					image1.put(i, j, dd);
		}*/
		
		/*Highgui.imwrite( "./src/test2_3.bmp", image1 );
		
		//showResult(image); Only few number of end points of boundary highlighted
		/*int ok = 0 , rows = image.rows() , cols = image.cols();
		for(i = 0; i < rows; i++)
			for( j = 0; j < cols; j++){
				ok = 0;
				for(Point_Index p: nonspuriouscorners)
					if(p.p.x == i && p.p.y == j){
						ok = 1;
						break;
					}
				if(ok == 1)
					image.put(i, j, 255);
				else
					image.put(i, j, 0);
			}
		Highgui.imwrite( "./src/testPP.jpg", image );
		*/
		
		
		//return nonspuriouscorners;
		//return Edges;
		//showResult(image);
		
	}

}
