package sk.henrichg.phoneprofiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapResampler {
	
	public static Bitmap resample(String bitmapFile, int width, int height)
	{
		// first decode with inJustDecodeDpunds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile, options);
		// calaculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, height);
		// decode bitmap with inSampleSize
		options.inJustDecodeBounds = false;
		Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(bitmapFile, options);

		
		return decodedSampleBitmap;
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth)
		{
			// calculate ratios of height and width to requested height an width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			// choose the smalest ratio as InSamleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width
			inSampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	

}
