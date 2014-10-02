  public static  double calculate(List<Double> a,List<Double> b)
    {
	int i;
	double sum=0.0;
        for(i=0;i<a.size();i++)
	    {
		double dif=(a.get(i)>b.get(i))?(a.get(i)-b.get(i)):(b.get(i)-a.get(i));
		sum+=dif;
	    }
       	return sum;
    }
