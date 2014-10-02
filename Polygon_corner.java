package Santoshkumar;

import java.io.*;
import java.lang.*;
import java.util.*;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

class Edge{
	int i1 ,i2 ;
	public Edge(int i,int j){
		i1 = i; i2 = j;
	}
}

public class Polygon_corner {
	
	public static double get_val(int ind1 , int ind2,Point point_bef, Point point_after,List<Point> points){
		Point point_cur,u,v ;
		int val = 0,i ;
		for (i= ind1; i <= ind2 ; i++){
			point_cur = points.get(i);			
			u = new Point(point_bef.x - point_cur.x,point_bef.y - point_cur.y);
			v = new Point(point_after.x - point_cur.x,point_after.y - point_cur.y);
			val += Math.toDegrees(Math.acos( (u.x*v.x + u.y*v.y)/(Math.sqrt(u.x*u.x + u.y*u.y) * Math.sqrt(v.x*v.x + v.y*v.y)))) ;
			//System.out.println(val);
		}
		return val ;
	}
	
	public static void main( String [] args ){
	
		Scanner stdin = new Scanner(System.in);
		int i;
		int noOfPoints = stdin.nextInt();
		ArrayList<Point> points = new ArrayList<Point>();
		for( i = 0; i < noOfPoints; i++ ){
			double tempX = stdin.nextDouble();
			double tempY = stdin.nextDouble();
                        points.add(new Point(tempX,tempY)); 
		}
		double epsilon = stdin.nextDouble();

		ArrayList<Point> reducedPoints = RamerDouglasPeuckerAlgo(points,epsilon);
		System.out.println("No of Points : " + reducedPoints.size() );
		for( i = 0; i < reducedPoints.size(); i++ ){
			System.out.println(reducedPoints.get(i).x + " " + reducedPoints.get(i).y); 
		} 
	}

    
	public static ArrayList<Edge> corner_points(Point[] boundary,List<Point> points , double epsilon, Mat image, String f_name){
		
		ArrayList<Point> reducedPoints = RamerDouglasPeuckerAlgo(points,epsilon);
		
		int max_x, max_y,i,j;
		double dd[] = {255,0,0};
		
		/*All the Probable Points from Corner_Extrction are plotted*/
		for(Point p: points){
			//System.out.println("X = " + p.x +  "Y = " + p.y );
			max_x = (int) p.x ; max_y = (int) p.y;
					image.put(max_x, max_y, dd);
		}
		
		
		/*Corners detected by RamerDouglasPeuckerAlgo*/
		for(Point p: reducedPoints){
			//System.out.println( "X = " + p.x +  "Y = " + p.y );
			max_x = (int) p.x ; max_y = (int) p.y ;
			for(i = max_x ; i <= max_x + 25 ; i++) //-> Y direction 
				for(j = max_y; j <= max_y + 25 ; j++)// -> X direction 
					image.put(i, j, dd);
		}
		Highgui.imwrite( "./src/"+f_name+".jpeg", image );
		
		
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int temp1 = 0;
		/*Find index of the final corner points in the true boundary*/
		for(Point p1 : reducedPoints){
			for( ; temp1  < boundary.length; temp1++){
				if(p1.x == boundary[temp1].x && p1.y == boundary[temp1].y){
					temp.add(temp1);
					break;
				}
			}
			System.out.println(temp1);
			temp1++;
		}
		
		
		ArrayList<Edge> Edges = new ArrayList<Edge>();
		int ind1, ind2;
		temp1 = 0 ;
		
		// Form edge out of the set of boundary points
		for(Integer p : temp){
			ind1 = temp.get(temp1);
			ind2 =  temp.get((temp1 + 1)%temp.size());
			temp1++ ;
			if(ind2 != 0){
				Edges.add(new Edge(ind1,ind2));
				System.out.println(" Ind 1 " + ind1  +  "Ind2 " + ind2);
			}
		}
		return Edges;
		
	}
	
    public static ArrayList<Point> RamerDouglasPeuckerAlgo( List<Point> points , double epsilon ){
        ArrayList<Point> resultPoints = new ArrayList<Point>();
        double dmax = 0;
        int storeIndex = -1;

        for( int i = 1; i < points.size()-1; i++ ){
            double d = shortestDistanceToSegment(points.get(i), points.get(0), points.get(points.size()-1) ); 
            if( d > dmax ){
                dmax = d;
                storeIndex = i;
            }
        }
        
        if( dmax > epsilon ){
            
            ArrayList<Point> rec1Result = RamerDouglasPeuckerAlgo( new ArrayList<Point>( points.subList(0, storeIndex)) , epsilon);
            ArrayList<Point> rec2Result = RamerDouglasPeuckerAlgo( new ArrayList<Point>( points.subList(storeIndex, points.size()-1)), epsilon); 
            
            resultPoints.addAll(rec1Result);  resultPoints.remove(rec1Result.size()-1);
            resultPoints.addAll(rec2Result); 
        }
        else {
            resultPoints.add(points.get(0));
            resultPoints.add(points.get(points.size()-1));
        }

        return resultPoints;
    }

    private static double shortestDistanceToSegment(Point C, Point A, Point B) {
        
        double dist = Cross(A,B,C)/Dist(A,B);
        double dot1 = Dot(A,B,C);
        if( dot1 > 0 ) return Dist(B,C);
        double dot2 = Dot(B,A,C);
        if( dot2 > 0 ) return Dist(A,C);
        return Math.abs(dist);
    }
    
    private static double Dist( Point A, Point B){
        return Math.sqrt( (A.x-B.x)*(A.x-B.x) + (A.y-B.y)*(A.y-B.y) );
    }
    
    private static double Dot( Point A, Point B, Point C){
        double dot = ((B.x-A.x) * (C.x-B.x))  + ( (B.y-A.y) * (C.y-B.y) ) ;
        return dot;
    }
    
    private static double Cross( Point A, Point B, Point C){
        double cross = ( (B.x-A.x) * (C.y-A.y) )  - ( (B.y-A.y) * (C.x-A.x) ) ;
        return cross;
    }
    
}
