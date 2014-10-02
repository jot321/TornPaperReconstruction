#include<iostream>
#include<cstdlib>
#include<vector>
#include<cstdio>
#include<cmath>
#define pi 3.14159
#define err 1e-9

using namespace std;

int matching(Polygon& A,Polygon& B,int &vert1,int &vert2)
{
 int W=0;
 int i=0,j=0,x,y;
 int side1=A.n_sides,side2=B.n_sides;
 
 for(x=0;x<side1;x++)
 {
 for(y=0;y<side2;y++)
 {
 int count1=0,count2=0;
 int weight=0;
 i=x;
 j=y;
 while(count1<side1 && count2<side2)
 {
  int dummy=weight;
  if(abs(A.angles[i]+B.angles[j],2*pi)<err)
  {
   if(abs(A.sides[i],B.sides[j])<err)
    {
     weight=weight+1;
     count1++;
     count2++;
     i=(i+1)%side1;
     j=(j+side2-1)%side2;
     if(abs(A.sides[(i+side1-1)%side1],B.sides[(j+1)%side2])<err)
     {
      weight=weight+4;
     }
    }
  }
 if(weight==dummy)
 break;
 }
 if(W<weight)
 {
  W=weight;
  vert1=i;
  vert2=j;
 }
 }
 }
 return W;
}

      

 
    
