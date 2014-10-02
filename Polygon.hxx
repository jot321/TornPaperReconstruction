#ifndef __POLYGON_HXX	// Control inclusion of header files
#define __POLYGON_HXX

/************ C++ Headers ************************************/
#include <iostream>	// Defines istream & ostream for IO
#include <vector>
using namespace std;

/************ CLASS Declaration ******************************/
class Polygon {

public :

  int n_sides;
  vector<double> sides;
  vector<double> angles;
  friend int matching(Polygon&,Polygon&,int &,int &);


};

#include "Polygon.inl" // Methods are implemented here

#endif // __POLYGON_HXX
