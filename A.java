import java.io.*;
import java.lang.*;
import java.util.*;


public class A {

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
		RamaerDouglasPeucker ramerDouglasPeucker = new RamaerDouglasPeucker();
		ArrayList<Point> reducedPoints = ramerDouglasPeucker.RamerDouglasPeuckerAlgo(points,epsilon);
		System.out.println("No of Points : " + reducedPoints.size() );
		for( i = 0; i < reducedPoints.size(); i++ ){
			System.out.println(reducedPoints.get(i).x + " " + reducedPoints.get(i).y); 
		} 
	}

    

}

class Point{
	public double x;
	public double y;

    Point(double tempX, double tempY) {
        this.x = tempX;
        this.y = tempY;
    }
}

class RamaerDouglasPeucker{
	
    public ArrayList<Point> RamerDouglasPeuckerAlgo( ArrayList<Point> points , double epsilon ){
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

    private double shortestDistanceToSegment(Point C, Point A, Point B) {
        
        double dist = Cross(A,B,C)/Dist(A,B);
        double dot1 = Dot(A,B,C);
        if( dot1 > 0 ) return Dist(B,C);
        double dot2 = Dot(B,A,C);
        if( dot2 > 0 ) return Dist(A,C);
        return Math.abs(dist);
    }
    
    private double Dist( Point A, Point B){
        return Math.sqrt( (A.x-B.x)*(A.x-B.x) + (A.y-B.y)*(A.y-B.y) );
    }
    
    private double Dot( Point A, Point B, Point C){
        double dot = ((B.x-A.x) * (C.x-B.x))  + ( (B.y-A.y) * (C.y-B.y) ) ;
        return dot;
    }
    
    private double Cross( Point A, Point B, Point C){
        double cross = ( (B.x-A.x) * (C.y-A.y) )  - ( (B.y-A.y) * (C.x-A.x) ) ;
        return cross;
    }
    
}
