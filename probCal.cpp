#include <iostream>
#include <cv.h>
#include <highgui.h>
#include <vector>
#include <string>
#include <sys/types.h>
#include <dirent.h>
#include "declarations.h"

using namespace std;
using namespace cv;

class imgFiles{
public:
	Mat mat;
};

class Edge{
public:
	Point2f start,end;
	float angle;
	vector<int> pixels;
	bool valid;
};

class Shred{
public:
	vector<Edge> edges;
};

class Set{
public:
	vector<Shred> shreds;
	Set Union(Set A,Set B);
};

int hashVal(int a,int b,int c, int d);


class probCal{
public:
	float white[16],black[16];
	probCal(string dataset)
	{
		 DIR *d = NULL;
		 struct dirent *dir;
		 d = opendir(dataset.c_str());
		 vector<imgFiles> orgImg ;
		 Mat image;
		 int i=0,j,k;
		 if (d)
		 {
		   while ((dir = readdir(d)) != NULL)
		   {
			   string img = dataset + dir->d_name;
			      image = imread(img.c_str(), 0);   // Read the file

			      if(! image.data )                              // Check for invalid input
			      {
			          continue;
			      }
			      	/*
			      for(j=0;j<image.rows;j++)
			      {
			        for (k=0;k<image.cols;k++)
			        {
			          if( image.at<uchar>(j,k) > 127)
			             image.at<uchar>(j,k) = 255; //white
			          else image.at<uchar>(j,k) = 0;
			        }
			      }
	*/






			      int rows = image.rows;
			      		int cols = image.cols;
			      //		cout << "Initially rows" << rows << " Columns " <<  cols;

			      		/* Histogram Computation*/
			      		int hist[256];
			      		for(j =0;j<256;j++) {
			      			hist[j] = 0;
			      		}
			      		for(j=0;j<rows;j++) {
			      			for(k=0;k<cols;k++) {
			      				hist[(int)(image.at<uchar>(j,k))] += 1;
			      			}
			      		}
			      		int max = hist[0];
			      		int max_p=0;
			      		for(j =0;j<256;j++){
			      			if(max < hist[j]) {
			      				max = hist[j];
			      				max_p = j;
			      			}
			      		}

			      		/*Image Resize*/
			      		int param = 0;
			      	      Mat nmat = Mat::zeros( rows+2*param,cols+2*param, CV_8UC1 );
			      	      for(j = 0;j<rows+2*param;j++){
			      	    	  for(k =0;k<cols+2*param;k++){
			      	    		  nmat.at<uchar>(j,k) = max_p;
			      	    	  }
			      	      }
			      	      for(j = 0;j<rows;j++){
			      	    	  for(k =0;k<cols;k++){
			      	    		  nmat.at<uchar>(j+param, k+param) = image.at<uchar>(j,k);
			      	    	  }
			      	      }

			      		/*Thresholding*/
			      		int threshold_ = max_p;
			      		double lowerb,upperb;
			      		upperb = (1.015 * threshold_ > 255)?255:1.015 * threshold_;
			      		lowerb = (0.985 * threshold_ <0)?0:0.985 * threshold_;

			      		Mat mat1 = Mat::zeros( rows,cols, CV_8UC1 );
			      		Mat mat2 = Mat::zeros( rows,cols, CV_8UC1 );
			      		threshold(nmat, mat1,lowerb,255,THRESH_BINARY_INV);
			      		threshold(nmat, mat2,upperb,255,THRESH_BINARY);
			      		//Core::max(mat1, mat2, nmat);

			      		for (j = 0; j < rows;j++){
			      			for(k = 0 ; k< cols ; k++){
			      				nmat.at<uchar>(j,k) =  mat1.at<uchar>(j,k) > mat2.at<uchar>(j,k)?mat1.at<uchar>(j,k):mat2.at<uchar>(j,k);
			      			}
			      		}


			      		/* Segmentation and Morphological Processing*/
			      		medianBlur(nmat, nmat, 9);
			      		Mat kernel = Mat::ones(10, 5, CV_8UC1) ;
			      		morphologyEx(nmat, nmat, MORPH_OPEN, kernel);

			      		/*Edge Detection*/
			      		//Imgproc.Canny(nmat, nmat, 0.2, 0.1);

			      		/*Finding the maximum Connected Component*/
			      		Mat m = Mat::zeros(nmat.size(),CV_32F);
			      		nmat.copyTo(m);
			      		vector<vector<Point> > contours ;
			      		vector<Point> contour;

			      		// Gets all the contours Retr_List ,
			      		findContours(m, contours, RETR_LIST, CHAIN_APPROX_NONE);
			      		double maxArea = -1;
			      		int maxAreaIdx = -1;
			      		unsigned int idx;
			      		for (idx = 0; idx < contours.size(); idx++) {
			      			contour = contours.at(idx);
			      			double contourarea = contourArea(contour);
			      			if (contourarea > maxArea) {
			      				maxArea = contourarea;
			      				maxAreaIdx = idx;
			      			}
			      		}
			      		m = Mat::zeros(nmat.size(),CV_32F);
			      		Scalar color(255);
			      		drawContours(m, contours, maxAreaIdx, color,CV_FILLED);
			      		img = dataset +"result/"+ dir->d_name;
			      		imwrite(img.c_str(), m);







			      imgFiles *imgF = new imgFiles();
			      orgImg.push_back(*imgF);
			      orgImg.at(i).mat = image;

			      i++;
			     //namedWindow( "Display window", WINDOW_NORMAL );// Create a window for display.
			      //    imshow( "Display window",imread(img.c_str(),0) );                   // Show our image inside it.

			       //   waitKey(0);
		   }
		    closedir(d);
		  }
		 for(i=0;i<16;i++)
			 white[i] = black[i] = 0;
	}

	void countAll(string dataset);


};

void probCal::countAll(string dataset){
	 DIR *d = NULL;
	 struct dirent *dir;
	 d = opendir(dataset.c_str());
	 vector<imgFiles> org ;
	 Mat orgImg,bwImg;
	 int j,k,total=0;
	 if (d)
	 {
	   while ((dir = readdir(d)) != NULL)
	   {
		   string img = dataset + dir->d_name;
		   orgImg = imread(img.c_str(),0);   // Read the file

			if(! orgImg.data )                              // Check for invalid input
			{
			  continue;
			}
		   img = dataset + "result/" + dir->d_name;
		   bwImg = imread(img.c_str(),0);
		   if(! bwImg.data )                              // Check for invalid input
			{
			  continue;
			}

		   for(j=0;j<orgImg.rows;j++)
			  {
				for (k=0;k<orgImg.cols;k++)
				{
				  if( orgImg.at<uchar>(j,k) > 127)
					 orgImg.at<uchar>(j,k) = 255; //white
				  else orgImg.at<uchar>(j,k) = 0;
				}
			  }

		  // imwrite(dataset + "result/Binary" + dir->d_name,orgImg);

		   for (j = 1; j < orgImg.rows-1;j++){
				for(k = 1 ; k< orgImg.cols ; k++){
					if(bwImg.at<uchar>(j,k) == 255)
					{
						if(bwImg.at<uchar>(j-1,k-1) == 0 || bwImg.at<uchar>(j-1,k) == 0 || bwImg.at<uchar>(j,k-1) == 0 || bwImg.at<uchar>(j+1,k-1) == 0)
							continue;
						int hash = hashVal(orgImg.at<uchar>(j-1,k-1),orgImg.at<uchar>(j-1,k),orgImg.at<uchar>(j,k-1),orgImg.at<uchar>(j+1,k-1));
						if( orgImg.at<uchar>(j,k) == 255)
							white[hash]++;
						else black[hash]++;
						total++;
					}
				}
			}


	   }
	   for(j=0;j<16;j++)
	   {
		   white[j] /= (float)total;
		   black[j] /= (float)total;
		   cout << white[j] << "\t" << black[j] <<endl;
	   }

	 }
}

int hashVal(int a,int b,int c, int d)
{
	int count = 0;
	if(a==255)
		count = count + 1;
	if(b==255)
		count = count + 2;
	if(c==255)
		count = count + 4;
	if(d==255)
		count = count + 8;
	return count;

}

int main()
{
	probCal *probs = new probCal("images/");
	cout<<"done init"<<endl;
	probs->countAll("images/");

	return 0;
}
