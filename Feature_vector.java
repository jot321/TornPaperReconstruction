package Santoshkumar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Point;

class edge_set{
	double angle;
	double distance;
	edge_set(double a, double d){
		angle = a;
		distance = d;
	}
}

class Edge_Envelope{
	ArrayList<edge_set> forward = new ArrayList<edge_set>();
	ArrayList<edge_set> backward = new ArrayList<edge_set>();
	Point fStart,fEnd ;
	public Edge_Envelope(ArrayList<edge_set> f ,ArrayList<edge_set> b , Point S , Point E ){
		forward = f ; backward = b ; fStart = S ; fEnd = E;
	}
	
	//Deviation corresponding to an edge 
	public double deviation(){
		double sum = 0 ;
		for(edge_set e : forward){
			sum += (e.distance*e.distance*e.distance);
		}
		sum /= forward.size();
		return sum ;
	}
}


public class Feature_vector{

	public static List<Point> reverse(List<Point> l){
		ArrayList<Point> rev = new ArrayList<Point>();
		System.out.println(l.size());
		for(int i = l.size() -1 ;i >= 0 ; i-- ){
			rev.add(l.get(i));
		}
		return rev;
	}
	
	public static Edge_Envelope fb_envelope(List<Point> list){
		Edge_Envelope envelope ;
		ArrayList<edge_set> f  = calculate(list);
		ArrayList<edge_set> b  = calculate(reverse(list));
		envelope = new Edge_Envelope(f,b,list.get(0),list.get(list.size()-1));
		System.out.println(  "Start " + list.get(0) + " End " + list.get(list.size()-1) + " Deviation " + envelope.deviation());
		return envelope;
	}
	
	public static ArrayList<edge_set> calculate(List<Point> list ){
		double m_initial,ang_initial,m_temp,ang_temp;
		int size = list.size();
		double d;
		
		
		ArrayList<edge_set> edges = new ArrayList<edge_set>();
		if( list.get(0).x != list.get(size - 1).x ){
			m_initial = (1)*(list.get(0).y - list.get(size - 1).y)/(list.get(0).x - list.get(size - 1).x);
			ang_initial = Math.atan(m_initial);
			if(ang_initial < 0) ang_initial += Math.PI;
		}
		else ang_initial = Math.PI/2 ;
		
		
		for(int i = 1; i< list.size() - 1 ; i++){	 
			if( list.get(0).x !=  list.get(i).x){
				m_temp = (1)*(list.get(0).y - list.get(i).y)/(list.get(0).x - list.get(i).x);
				ang_temp = Math.atan(m_temp);
				if(ang_temp < 0) ang_temp += Math.PI;
			}
			else ang_temp =  Math.PI/2 ;
			
			d = Math.sqrt(Math.pow((list.get(i).y - list.get(0).y),2) + Math.pow((list.get(i).x - list.get(0).x),2));
			d = Math.abs(d * Math.sin(ang_initial - ang_temp));
			//if(list.get(0).x == 2148.0){ 
				//System.out.println("Initial " + ang_initial + " Angle Temp " + ang_temp + " dist " + (int)d +  " init d " + (int)t);
				//System.out.print();
			//}
			edges.add(new edge_set(ang_initial - ang_temp, d));
		}
		
		return edges;
	}

	public static void main(String args[]){

		ArrayList<Point> input_points = new ArrayList<Point>(10);
		for(int i=0; i<10 ;i++){
			input_points.add(new Point(Math.floor(Math.random()*100), Math.floor(Math.random()*100)));
		}
		calculate(input_points);
		
	}
}
