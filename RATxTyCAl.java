double getRa(Point CP1_1, Point CP1_2, Point CP3_2, Point CP3_3)
{
    double RA, angleA, angleB;
    if(CP1_2.x!=CP1_1.x)angleA=Math.atan((CP1_2.y-CP1_1.y)/(CP1_2.x-CP1_1.x));
    else angleA=Math.PI/2;
    if(CP3_3.x!=CP3_2.x)angleB=Math.atan((CP3_3.y-CP3_2.y)/(CP3_3.x-CP3_2.x));
    else angleB=Math.PI/2;

    RA=angleA-angleB;
    return RA;
}
double getTx(Point CP1_1, Point CP1_2, Point RCP3_2, Point RCP3_3)
{
    double Tx;
    Tx=0.5*((CP1_1.x+CP1_2.x)-(RCP3_3.x-RCP3_2.x));
    return Tx;
}
double getTy(Point CP1_1, Point CP1_2, Point RCP3_2, Point RCP3_3)
{
    double Ty;
    Ty=0.5*((CP1_1.y+CP1_2.y)-(RCP3_3.y-RCP3_2.y));
    return Ty;
}
