/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eaglepraise.fdo.wsq;

/**
 *
 * @author fernando
 */
public class FilterBank {

	/**
	 * @param args
	 */
	
	final double lpa0 = 0.85269867900940;
	final double lpa1 = 0.37740285561265;
	final double lpa2 = -0.11062440441842;
	final double lpa3 = -0.023849465019380;
	final double lpa4 = 0.037828455506995;
	final double [] h0 = {lpa4,lpa3,lpa2,lpa1,lpa0,lpa1,lpa2,lpa3,lpa4};
	//final int []h0 = {4,3,2,1,5,1,2,3,4};
	final double hpac = 0.78848561640566;
	final double hpa0 = -0.41809227322221;
	final double hpa1 = -0.040689417609558;
	final double hpa2 = 0.064538882628938;
	final double []h1 = {hpa2,hpa1,hpa0,hpac,hpa0,hpa1,hpa2};
	//final int [] h1 = {3,2,1,4,1,2,3};
	final double lpsc = hpac;
	final double lps0 = -hpa0;
	final double lps1 = hpa1;
	final double lps2 = -hpa2;
	final double []f0 = {lps2,lps1,lps0,lpsc,lps0,lps1,lps2};
        final double hps0 = lpa0;
	final double hps1 = -lpa1;
	final double hps2 = lpa2;
	final double hps3 = -lpa3;
	final double hps4 = lpa4;
	final double []f1 = {hps4,hps3,hps2,hps1,hps0,hps1,hps2,hps3,hps4};
	double [][] H0;
	double [][] H1;
	double [][] F0;
	double [][] F1;
	double [][] H0T;
	

//	
	
	public double[][] processColumns(double[][] filter, double[][]extendImage,int h, int w){
	
		double [][]product = new double[h][w];
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++){
				for(int m=0;m<w;m++){
				product[i][j] += filter[i][m]*extendImage[m][j];
				}
			}	
		}		
		return product;
	}
	
	public double[][] processRows(double[][] filter, double[][]extendImage,int h, int w){
		
		double [][]product = new double[h][w];
		int s=h*2;
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++){
				for(int m=0;m<s;m++){
				product[i][j] += extendImage[i][m]*filter[m][j];
				}
			}	
		}		
		return product;
	}
	
	public double[][] symmetricExtension(double[][] img,int h,int w){
		int height =2*h-2;
		int width = 2*w-2;
		double[][] extend = new double [height][width];
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++){
				extend[i][j]=img[i][j];
			}
		}
					
		int l=h-2;
		for(int j=0;j<width;j++){
			for(int i=h;i<height;i++){
				extend[i][j]=extend[l][j];
				l=l-1;
			}
			l=h-2;
		}
		
		int k=w-2;
		for(int i=0;i<height;i++){
			for(int j=w;j<width;j++){
					extend[i][j]=extend[i][k];
					k=k-1;
			}
			k=w-2;
		}
		
		return extend;
	}
	
	public double[][] createH0(int height, int width){
		H0 = new double [height/2][width];
		final int zeros = width-9;
		final int zerosright;
		final int zerosleft;
		zerosright = zeros;
		zerosleft = 0;
		int zr = zerosright;
		int zl = zerosleft;
		
		for(int i=0; i<(height/2);i++){
			//if(i%2==0){	
				if (i<=(int)zeros/2){
					for(int j=0;j<width;j++){
						if(j<zl){
							H0 [i][j] = 0;
						}
						else if (j<(zl+9)){
							H0 [i][j] = h0 [j-zl];
						}
						else {
							H0 [i][j] = 0;
						}
						}
						zl = zl+2;
						zr = zr-2;
					}
				else {
					int x = i*-2;
					int r = x + width;
					int ll = 9-r;
					int rl = width-r;
						for(int j = 0;j<width;j++){
							if(j<ll){
								H0[i][j] = h0[j+r];
							}
							else if(j<rl){
								H0[i][j] = 0;
							}
							else {
								H0[i][j] = h0[j-rl];
							}
						}
				}
			//}
		}
		return H0;
	}
		

        public double[][] createH1(int height, int width){
		H1 = new double [height/2][width];
		final int zeros = width-7;
		final int zerosright;
		final int zerosleft;
		zerosright = zeros;
		zerosleft = 0;
		int zr = zerosright;
		int zl = zerosleft;
		
		for(int i=0; i<(height/2);i++){
			//if(i%2==0){	
				if (i<=(int)zeros/2){
					for(int j=0;j<width;j++){
						if(j<zl){
							H1 [i][j] = 0;
						}
						else if (j<(zl+7)){
							H1 [i][j] = h1 [j-zl];
						}
						else {
							H1 [i][j] = 0;
						}
					}
					zl = zl+2;
					zr = zr-2;
					
					}
				else {
					int x = i*-2;
					int r = x + width;
					int ll = 7-r;
					int rl = width-r;
						for(int j = 0;j<width;j++){
							if(j<ll){
								H1[i][j] = h1[j+r];
							}
							else if(j<rl){
								H1[i][j] = 0;
							}
							else {
								H1[i][j] = h1[j-rl];
							}
						}
				}
			//}
		}
		
		return H1;		
	}
        
        public double[][] createF0(int height, int width){
		F0 = new double [height][width];
		final int zeros = width-7;
		final int zerosright;
		final int zerosleft;
		zerosright = zeros;
		zerosleft = 0;
		int zr = zerosright;
		int zl = zerosleft;
		
		for(int i=0; i<(height);i++){
			//if(i%2==0){	
				if (i<=zeros){
					for(int j=0;j<width;j++){
						if(j<zl){
							F0 [i][j] = 0;
						}
						else if (j<(zl+7)){
							F0 [i][j] = f0 [j-zl];
						}
						else {
							F0 [i][j] = 0;
						}
					}
					zl = zl+1;
					zr = zr-1;
					
					}
				else {
					int x = i*-1;
					int r = x + width;
					int ll = 7-r;
					int rl = width-r;
						for(int j = 0;j<width;j++){
							if(j<ll){
								F0[i][j] = f0[j+r];
							}
							else if(j<rl){
								F0[i][j] = 0;
							}
							else {
								F0[i][j] = f0[j-rl];
							}
						}
				}
			//}
		}
		
		return F0;		
	}
        
        public double[][] createF1(int height, int width){
		F1 = new double [height][width];
		final int zeros = width-9;
		final int zerosright;
		final int zerosleft;
		zerosright = zeros;
		zerosleft = 0;
		int zr = zerosright;
		int zl = zerosleft;
		
		for(int i=0; i<(height);i++){
			//if(i%2==0){	
				if (i<=zeros){
					for(int j=0;j<width;j++){
						if(j<zl){
							F1 [i][j] = 0;
						}
						else if (j<(zl+9)){
							F1 [i][j] = f1 [j-zl];
						}
						else {
							F1 [i][j] = 0;
						}
						}
						zl = zl+1;
						zr = zr-1;
					}
				else {
					int x = i*-1;
					int r = x + width;
					int ll = 9-r;
					int rl = width-r;
						for(int j = 0;j<width;j++){
							if(j<ll){
								F1[i][j] = f1[j+r];
							}
							else if(j<rl){
								F1[i][j] = 0;
							}
							else {
								F1[i][j] = f1[j-rl];
							}
						}
				}
			//}
		}
		return F1;
	}
	
	
	public double[][] createHXT(double[][] lpfilter, int h, int w ){
		H0T = new double[w][h];
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				H0T[i][j]=lpfilter[j][i];
			}			
		}
		return H0T;
	}

}
