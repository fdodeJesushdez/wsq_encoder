
package eaglepraise.fdo.wsq;

/**
 *
 * @author fernando
 */
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import org.eclipse.swt.graphics.ImageData;


@SuppressWarnings("serial")
   public class ShowImage extends JFrame implements ActionListener {

    private static BufferedImage firstImage;

   private File myLocalImage;
   private JPanel Subimages;
   private static JLabel imageLabel,blurLabel,verticalLabel,horizontalLabel,diagonalLabel;
   private static Icon iconImage,iconBlur,iconVertical,iconHorizontal,iconDiagonal;
   private JButton button;

   private static ImageData imageData;
   
   public ShowImage()
      {

       

       myLocalImage = new File( "UdeA-SIU.jpg");//eagle vision 306 * 452 pixels

   
       try{firstImage = ImageIO.read(myLocalImage);}
       catch(Exception e){System.err.println("Exception is: " + e);
       System.exit(1);}
       
       imageData = new ImageData("UdeA-SIU.jpg");

       setDefaultCloseOperation(EXIT_ON_CLOSE);

   
       iconImage =new ImageIcon(firstImage);
       imageLabel = new JLabel(iconImage);

       button = new JButton("PROCESS");
       button.addActionListener(this);

   

       getContentPane().add(imageLabel, BorderLayout.CENTER);
       getContentPane().add(button, BorderLayout.SOUTH);

   
       pack();
       setVisible(true);
   }
   
   public void actionPerformed(ActionEvent e){
	   
	   Subimages = new JPanel(new GridLayout(2,2));
	   Subimages.add(blurLabel);
	   Subimages.add(verticalLabel);
	   Subimages.add(horizontalLabel);
	   Subimages.add(diagonalLabel);
	   getContentPane().add(Subimages);
	   validate();
	   
   }

  
  
   public double[] getImagePixels() {
       double [] dummy = null;
       int wid, hgt;

       // compute size of the array
       wid = firstImage.getWidth();
       hgt = firstImage.getHeight();

       // start getting the pixels
       Raster pixelData;
       pixelData = firstImage.getData();
       return pixelData.getSamples(0, 0, wid, hgt,2,dummy);
   }

  
   public BufferedImage getImageFromArray(double[] pixels, int width, int height) {

	   BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
       WritableRaster raster = image.getRaster();
       raster.setPixels(0,0,width,height,pixels);
       image.setData(raster);
       return image;
	} 

   /**
    * Main method;
    */
   public static void main(String [] args) {
     

       ShowImage siGUI = null;
       siGUI = new ShowImage();
       double [] readpixels; 
       readpixels = siGUI.getImagePixels();

       int red_component [] = new int [imageData.width*imageData.height];
         int redMask = imageData.palette.redMask;
        int cnt = 0;

        int[] lineData = new int[imageData.width];
        for (int y = 0; y < imageData.height; y++) {
         imageData.getPixels(0,y,imageData.width,lineData,0);
            // Analyze each pixel value in the line
            for (int x=0; x<lineData.length; x++){
               // Extract the red, green and blue component
             int pixelValue = lineData[x];
             red_component[cnt]= pixelValue & redMask;/*
             green_component[cnt] = (pixelValue & greenMask) >> 8;
             blue_component[cnt] = (pixelValue & blueMask) >> 16;*/
             cnt++;
            }
        }
        
      double [] blurIcon = new double[153*153*3];
      double [] verticalIcon = new double[153*153*3];
      double [] horizontalIcon = new double[153*153*3];
      double [] diagonalIcon = new double[153*153*3];
      double [] inverseIcon = new double[306*306*3];
      
      double [][] image = new double [306][306];
     
      int k = 0;
      
      for(int i=0;i<306;i++){
    	  for(int j=0;j<306;j++){
    		  image[i][j] = red_component[k];
    		  k++;
    	  }
      }
      
      
                  
       FilterBank fb = new FilterBank();
      
       
       double [][] extendImage = fb.symmetricExtension(image,306,306);
       
       double [][]H0 = fb.createH0(610,610);
       double [][]H1 = fb.createH1(610, 610);
       double[][]H0T = fb.createHXT(H0, 305, 610);
       double [][]H1T = fb.createHXT(H1, 305, 610);
       double [][]F0 = fb.createF0(610,610);
       double [][]F1 = fb.createF1(610,610);
       double [][]F0T = fb.createHXT(F0, 610, 610);
       double [][]F1T = fb.createHXT(F1, 610, 610);
       
       double [][]halfBlurImage = fb.processColumns(H0, extendImage, 305, 610);       
       double [][]wholeBlurImage = fb.processRows(H0T, halfBlurImage, 305, 305);
       
       double [][]halfVerticalImage = fb.processColumns(H0, extendImage, 305, 610);
       double [][]wholeVerticalImage = fb.processRows(H1T, halfVerticalImage, 305,305);
       
       double [][]halfHorizontalImage = fb.processColumns(H1, extendImage, 305, 610);
       double [][]wholeHorizontalImage = fb.processRows(H0T, halfHorizontalImage, 305, 305);
       
       double [][]halfDiagonalImage = fb.processColumns(H1, extendImage, 305, 610);
       double [][]wholeDiagonalImage = fb.processRows(H1T, halfDiagonalImage, 305, 305);
       
       double [][]blurImage = new double [153][153];
       double [][]verticalImage = new double[153][153];
       double [][]horizontalImage = new double[153][153];
       double [][]diagonalImage = new double[153][153];
       
       for(int i=0;i<153;i++){
    	   for(int j=0;j<153;j++){
    		   blurImage[i][j] = wholeBlurImage[i][j];
    		   verticalImage[i][j] = wholeVerticalImage[i][j];
    		   horizontalImage[i][j] = wholeHorizontalImage[i][j];
    		   diagonalImage[i][j] = wholeDiagonalImage[i][j];
    	   }
    	   
       }
       /*
       double [][] extendBlur = fb.symmetricExtension(blurImage, 153, 153);
       double [][] extendVertical = fb.symmetricExtension(verticalImage, 153, 153);
       double [][] extendHorizontal = fb.symmetricExtension(horizontalImage, 153, 153);
       double [][] extendDiagonal = fb.symmetricExtension(diagonalImage, 153, 153);
       */
       double [][] upSampledBlur = new double[610][610];
       double [][] upSampledVertical = new double[610][610];
       double [][] upSampledHorizontal = new double[610][610];
       double [][] upSampledDiagonal = new double[610][610];
       
      
       
       for(int i=0;i<610;i++){
        for(int j=0;j<610;j++){
            if(((i%2)==0)&&((j%2)==0)){
            upSampledBlur[i][j]=wholeBlurImage[i/2][j/2];
            upSampledVertical[i][j]=wholeVerticalImage[i/2][j/2];
            upSampledHorizontal[i][j]=wholeHorizontalImage[i/2][j/2];
            upSampledDiagonal[i][j]=wholeDiagonalImage[i/2][j/2];
            }
            else{
            upSampledBlur[i][j]= 0;
            upSampledVertical[i][j]= 0;
            upSampledHorizontal[i][j]= 0;
            upSampledDiagonal[i][j]= 0;
            }
        }
       }
       
       double [][] halfInverseBlur = fb.processColumns(F0, upSampledBlur, 610, 610);
       double [][] wholeInverseBlur = fb.processColumns(halfInverseBlur, F0T, 610, 610);
       
       double [][] halfInverseVertical = fb.processColumns(F0, upSampledVertical, 610,610);
       double [][] wholeInverseVertical = fb.processColumns(halfInverseVertical, F1T, 610,610);
       
       double [][] halfInverseHorizontal = fb.processColumns(F1, upSampledHorizontal, 610,610);
       double [][] wholeInverseHorizontal = fb.processColumns(halfInverseHorizontal, F0T, 610,610);
       
       double [][] halfInverseDiagonal = fb.processColumns(F1, upSampledDiagonal, 610,610);
       double [][] wholeInverseDiagonal = fb.processColumns(halfInverseDiagonal, F1T, 610,610);
       
       double [][] wholeInverse = new double [610][610];
       
       for(int i=0;i<610;i++){
        for(int j=0;j<610;j++){
            wholeInverse[i][j] = wholeInverseBlur[i][j]+wholeInverseVertical[i][j]
                +wholeInverseHorizontal[i][j]+wholeInverseDiagonal[i][j];
        }
       }
       
       double [][] inverseImage = new double[306][306];
       
        for(int i=0;i<306;i++){
            for(int j=0;j<306;j++){
            inverseImage[i][j]=wholeInverse[i][j];
                
        }
       }
       
       
       k=0;
       double [] linearBlur = new double[153*153] ;
       double [] linearVertical = new double[153*153] ;
       double [] linearHorizontal = new double[153*153] ;
       double [] linearDiagonal = new double[153*153] ;
       for(int i=0;i<153;i++){
     	  for(int j=0;j<153;j++){
     		  linearBlur[k] = blurImage[i][j];
     		  linearVertical[k] = verticalImage[i][j];
     		  linearHorizontal[k] = horizontalImage[i][j];
     		  linearDiagonal[k] = diagonalImage[i][j];
     		  k++;
     	  }
       }
       
       k=0;
       double [] linearInverse = new double[306*306];
        for(int i=0;i<306;i++){
     	  for(int j=0;j<306;j++){
     		  linearInverse[k] = inverseImage[i][j];
     		  k++;
     	  }
       }
       
                    
       int y = 0;
       for(int i = 0; i < linearBlur.length;i++){
    	   blurIcon[y] = linearBlur[i];
    	   blurIcon[y+1] = linearBlur[i];
    	   blurIcon[y+2] = linearBlur[i];
    	   
    	   verticalIcon[y] = linearVertical[i];
    	   verticalIcon[y+1] = linearVertical[i];
    	   verticalIcon[y+2] = linearVertical[i];       
    	   
    	   horizontalIcon[y] = linearHorizontal[i];
    	   horizontalIcon[y+1] = linearHorizontal[i];
    	   horizontalIcon[y+2] = linearHorizontal[i];
    	   
    	   diagonalIcon[y] = linearDiagonal[i];
    	   diagonalIcon[y+1] = linearDiagonal[i];
    	   diagonalIcon[y+2] = linearDiagonal[i];
    	   y = y+3;
    	   }

       y = 0;
       for(int i = 0; i < linearBlur.length;i++){
    	   inverseIcon[y] = linearInverse[i];
    	   inverseIcon[y+1] = linearInverse[i];
    	   inverseIcon[y+2] = linearInverse[i];
    	   y = y+3;
    	   }

       
       
           
       BufferedImage Blur = siGUI.getImageFromArray(blurIcon,153,153);
       BufferedImage Vertical = siGUI.getImageFromArray(verticalIcon,153,153);
       BufferedImage Horizontal = siGUI.getImageFromArray(horizontalIcon,153,153);
       BufferedImage Diagonal = siGUI.getImageFromArray(diagonalIcon,153,153);
       BufferedImage Inverse = siGUI.getImageFromArray(inverseIcon, 306, 306);
       /*
       File saveBlur = new File("/home/fernando/WSQ/images/blur.jpg");
       File saveVertical = new File("/home/fernando/WSQ/images/vertical.jpg");
       File saveHorizontal = new File("/home/fernando/WSQ/images/horizontal.jpg");
       File saveDiagonal = new File("/home/fernando/WSQ/images/diagonal.jpg");
       try{
    	   ImageIO.write(Blur,"jpg",saveBlur);
    	   ImageIO.write(Vertical,"jpg",saveVertical);
    	   ImageIO.write(Horizontal,"jpg",saveHorizontal);
    	   ImageIO.write(Diagonal,"jpg",saveDiagonal);
       }
       catch(Exception e){
    	   System.err.println("Exception is: " + e);
       }
       */
      File saveInverse = new File("inverse2.jpg");
       try{
    	   ImageIO.write(Inverse,"jpg",saveInverse);
    	  }
       catch(Exception e){
    	   System.err.println("Exception is: " + e);
       } 
      iconBlur = new ImageIcon(Blur);
      blurLabel = new JLabel(iconBlur);
      iconVertical = new ImageIcon(Vertical);
      verticalLabel = new JLabel(iconVertical);
      iconHorizontal = new ImageIcon(Horizontal);
      horizontalLabel = new JLabel(iconHorizontal);
      iconDiagonal = new ImageIcon(Diagonal);
      diagonalLabel = new JLabel(iconDiagonal);
      
            
             
   }
}

