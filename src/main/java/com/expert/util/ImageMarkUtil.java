/**
 * 
 */
package com.expert.util;

import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvGet2D;
import static org.bytedeco.javacpp.opencv_core.cvSet2D;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LANCZOS4;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_NN;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.util.CollectionUtils;

import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月4日
 * @ClassName ImageMarkUtil
 */
@SuppressWarnings("restriction")
public class ImageMarkUtil {
	public static File extractMarkFromImg(BufferedImage markedImg, String markPath, int P, int SIW, int SMW, int SMH) {
		try {
			int width = markedImg.getWidth();
			int height = markedImg.getHeight();
			int markWidth = SMW * width / SIW;
			int markHeight = SMH * width / SIW;
			int startX = 0, startY = 0;
			if (P == 1) {
			} else if (P == 2) {
				startX = width - markWidth;
			} else if (P == 3) {
				startY = height - markHeight;
			} else if (P == 4) {
				startX = width - markWidth;
				startY = height - markHeight;
			} else {
				return null;
			}
			BufferedImage markImg = new BufferedImage(markWidth, markHeight, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < markWidth; i++) {
				for (int j = 0; j < markHeight; j++) {
					int rgb = markedImg.getRGB(i + startX, j + startY);
					markImg.setRGB(i, j, rgb);
				}
			}
			File output = new File(markPath);
			String[] split = markPath.split("\\.");
			String format = split.length == 2 ? split[1] : "jpg";
			ImageIO.write(markImg, format, output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 给图片增加文字水印
	public static BufferedImage markImgWithText(BufferedImage sourceImg, String text, Font font, Color color, int x, int y) {
		try {
			// 加水印
			Graphics2D g = sourceImg.createGraphics();
			g.setColor(color);
			g.setFont(font);
			g.drawString(text, x, y);
			g.dispose();
			return sourceImg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File markMp4WithImg(File sourceFile, String destFilepath, BufferedImage markImg, int x, int y) {
		try {
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourceFile);
			grabber.start();
			Java2DFrameConverter converter = new Java2DFrameConverter();
			File destFile = new File(destFilepath);
			FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(destFile, grabber.getImageWidth(), grabber.getImageHeight(),
					grabber.getAudioChannels());
			recorder.setFrameRate(grabber.getFrameRate());
			recorder.setAspectRatio(grabber.getAspectRatio());
			recorder.setAudioCodec(grabber.getAudioCodec());
			recorder.setAudioBitrate(grabber.getAudioBitrate());
			recorder.setVideoCodec(grabber.getVideoCodec());
			recorder.setVideoBitrate(grabber.getVideoBitrate());

			recorder.start();
			boolean hasEnd = false;
			int count = grabber.getLengthInFrames();// 并非帧数
			for (int i = 0; i < count || !hasEnd; i++) {
				// 取图片和声音
				Frame frame = grabber.grabFrame();
				if (frame == null) {
					hasEnd = true;
					continue;
				}
				if (frame.image == null && frame.samples == null) {
					continue;
				}
				if (frame.image != null) {
					BufferedImage bufferedImage = converter.getBufferedImage(frame);
					bufferedImage = markImgWithImg(bufferedImage, markImg, markImg.getWidth(), markImg.getHeight(), x, y);
					ImageIO.write(bufferedImage, "jpg", new File("/D:/项目读写文件/mark/marks/" + i + ".jpg"));
					frame.image = converter.convert(bufferedImage).image;
				}
				recorder.record(frame);
			}
			grabber.stop();
			recorder.stop();
			return destFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File markGifWithImg(File sourceFile, String destFilepath, BufferedImage markImg, int x, int y) {
		ImageReaderSpi readerSpi = new GIFImageReaderSpi();
		try {
			GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
			gifReader.setInput(new FileImageInputStream(sourceFile));
			int num = gifReader.getNumImages(true);

			AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
			gifEncoder.setRepeat(0);
			gifEncoder.start(destFilepath);
			for (int i = 0; i < num; i++) {
				BufferedImage img = gifReader.read(i);
				GIFImageMetadata imageMetadata = (GIFImageMetadata) gifReader.getImageMetadata(i);
				img = markImgWithImg(img, markImg, markImg.getWidth(), markImg.getHeight(), x, y);
				gifEncoder.setDelay(imageMetadata.delayTime);
				gifEncoder.addFrame(img);
			}
			gifEncoder.finish();
			return new File(destFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 加图片水印
	public static BufferedImage markImgWithImg(BufferedImage sourceImg, BufferedImage markImg, int markWidth, int markHeight, int x, int y) {
		Graphics2D g = sourceImg.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
		g.drawImage(markImg, x, y, markWidth, markHeight, null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		g.dispose();
		return sourceImg;
	}

	private static Color getStandardColor(Color color, int colorDiff) {
		if (color == null) {
			return null;
		}
		// int alpha = Math.round((float) color.getAlpha() / colorDifference);
		int red = Math.round((float) color.getRed() / colorDiff);
		int green = Math.round((float) color.getGreen() / colorDiff);
		int blue = Math.round((float) color.getBlue() / colorDiff);
		red *= colorDiff;
		green *= colorDiff;
		blue *= colorDiff;
		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		blue = blue > 255 ? 255 : blue;
		return new Color(red, green, blue);
	}

	public static boolean isBlackOrWhite(Color color) {
		return isLightColor(color, 16, 0);
	}

	public static boolean isWhite(Color color) {
		return isLightColor(color, 32, 48);
	}

	public static boolean isNotWhite(Color color) {
		return isDarkColor(color, 32, 255);
	}

	/*
	 * public static boolean isBlack(Color color, int colorDiff, int max) { int red = color.getRed(); int green =
	 * color.getGreen(); int blue = color.getBlue(); return red <= max && green <= max && blue <= max && Math.abs(red -
	 * green) <= colorDiff && Math.abs(red - blue) <= colorDiff && Math.abs(blue - green) <= colorDiff; }
	 */

	private static boolean isDarkColor(Color color, int colorDiff, int max) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		return red < max && green < max && blue < max || Math.abs(red - green) > colorDiff || Math.abs(red - blue) > colorDiff
				|| Math.abs(blue - green) > colorDiff;
	}

	private static boolean isLightColor(Color color, int colorDiff, int min) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		return red >= min && green >= min && blue >= min && Math.abs(red - green) <= colorDiff && Math.abs(red - blue) <= colorDiff
				&& Math.abs(blue - green) <= colorDiff;
	}

	private static int getColorDistance(Color color1, Color color2, int colorDiff) {
		if (color1 == null || color2 == null) {
			throw new IllegalArgumentException("color1,color2不能为空！");
		}
		int distance = 0;
		distance += Math.abs(color1.getAlpha() - color2.getAlpha()) / colorDiff;
		distance += Math.abs(color1.getRed() - color2.getRed()) / colorDiff;
		distance += Math.abs(color1.getGreen() - color2.getGreen()) / colorDiff;
		distance += Math.abs(color1.getBlue() - color2.getBlue()) / colorDiff;
		return distance;
	}

	/**
	 * @param sourceImg
	 * @param markImg
	 * @param standardWidth=640
	 */
	public static BufferedImage removeMarkSimple(BufferedImage sourceImg, int SMW, int SMH, int SIW) {
		try {
			int imgWidth = sourceImg.getWidth();
			int imgHeight = sourceImg.getHeight();
			int markWidth = SMW * imgWidth / SIW;
			int markHeight = SMH * imgWidth / SIW;
			// 找水印-颜色处理
			Map<Color, List<Point>> map = getColorPointsMap(sourceImg, imgWidth - markWidth, imgHeight - markHeight, imgWidth, imgHeight);
			map = searchLinesPoints2(map, 6);

			BufferedImage temp = new BufferedImage(markWidth, markHeight, BufferedImage.TYPE_INT_RGB);
			int count = 0;
			for (Entry<Color, List<Point>> entry : map.entrySet()) {
				List<Point> linePoints = entry.getValue();
				for (int k = 0; k < linePoints.size(); k++) {
					Point point = linePoints.get(k);
					int x = (int) point.getX();
					int y = (int) point.getY();
					temp.setRGB(x - (imgWidth - markWidth), y - (imgHeight - markHeight), entry.getKey().getRGB());
					int avgRgb = getLeftUpAvgRgb(sourceImg, x, y, 4, 4, linePoints);
					sourceImg.setRGB(x, y, avgRgb);
				}
				ImageIO.write(temp, "jpg", new File("/D:/项目读写文件/mark/temp_" + count + ".jpg"));
				count++;
			}
			return sourceImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param sourceImg
	 * @param markImg
	 * @param standardWidth=640
	 */
	public static BufferedImage removeMarkFromImg(BufferedImage sourceImg, BufferedImage markImg, int standardWidth, boolean isOnBlack) {
		try {
			int imgWidth = sourceImg.getWidth();
			int imgHeight = sourceImg.getHeight();
			int markWidth = markImg.getWidth() * imgWidth / standardWidth;
			int markHeight = markImg.getHeight() * imgWidth / standardWidth;

			String markFingerprint = ImageAnalyseUtil.generateFingerprint(markImg);
			BufferedImage destImg = new BufferedImage(markWidth, markHeight, BufferedImage.TYPE_INT_RGB);

			// 取4个水印角
			// 找水印-分区
			for (int i = 0; i <= imgWidth - markWidth; i += imgWidth - markWidth) {
				for (int j = 0; j <= imgHeight - markHeight; j += imgHeight - markHeight) {
					// 找水印-颜色处理

					for (int i1 = 0; i1 < markWidth; i1++) {
						for (int j1 = 0; j1 < markHeight; j1++) {
							int rgb = sourceImg.getRGB(i + i1, j + j1);
							Color color = new Color(rgb);
							if (isOnBlack) {
								if (isWhite(color)) {
									destImg.setRGB(i1, j1, Color.WHITE.getRGB());
								} else {
									destImg.setRGB(i1, j1, Color.BLACK.getRGB());
								}
							} else {
								destImg.setRGB(i1, j1, rgb);
							}
						}
					}
					String splitFingerprint = ImageAnalyseUtil.generateFingerprint(destImg);
					int hammingDistance = ImageAnalyseUtil.hammingDistance(markFingerprint, splitFingerprint);
					if (hammingDistance <= 5) {
						ImageIO.write(destImg, "jpg", new File("/D:/项目读写文件/mark/dest_" + i + "X" + j + "=" + hammingDistance + ".jpg"));
						Map<Color, List<Point>> map = getColorPointsMap(sourceImg, isOnBlack, i, j, i + markWidth, j + markHeight);
						List<Point> linePoints = searchLinesPoints(map, 10);
						// BufferedImage temp = new BufferedImage(destImg.getWidth(), destImg.getHeight(),
						// BufferedImage.TYPE_INT_RGB);
						for (int k = 0; k < linePoints.size(); k++) {
							Point point = linePoints.get(k);
							int x = (int) point.getX();
							int y = (int) point.getY();
							// System.out.println(x + "," + y);
							// temp.setRGB(x - i, y - j, Color.WHITE.getRGB());
							// int[] xs = { x - 12, x - 11, x - 10, x - 9, x - 8, x - 7, x - 6, x - 5, x - 4, x - 3, x -
							// 2, x - 1, x };
							// int[] ys = { y - 12, y - 11, y - 10, y - 9, y - 8, y - 7, y - 6, y - 5, y - 4, y - 3, y -
							// 2, y - 1, y };
							// for (int m = 0; m < xs.length; m++) {
							// for (int n = 0; n < ys.length; n++) {
							// int avgRgb = getLeftUpAvgRgb(sourceImg, xs[m], ys[n], 5, 5, linePoints);
							// sourceImg.setRGB(xs[m], ys[n], avgRgb);
							// }
							// }

							int avgRgb = getLeftUpAvgRgb(sourceImg, x, y, 5, 5, linePoints);
							sourceImg.setRGB(x, y, avgRgb);
							// ImageIO.write(sourceImg, "jpg", new File("/D:/项目读写文件/mark/temp/temp_" + k + ".jpg"));
						}
						// String splitFingerprint = ImageAnalyseUtil.generateFingerprint(temp);
						// int hammingDistance = ImageAnalyseUtil.hammingDistance(markFingerprint, splitFingerprint);
						// ImageIO.write(temp, "jpg", new File("/D:/项目读写文件/mark/temp_" + i + "_" + j + "=" +
						// hammingDistance
						// + ".jpg"));
						// for (int i1 = i; i1 < i + markWidth; i1++) {
						// for (int j1 = j; j1 < j + markHeight; j1++) {
						// int rgb = sourceImg.getRGB(i1, j1);
						// Color color = new Color(rgb);
						// if (isWhite(color)) {
						// int avgRgb = getRoundAvgRgb(sourceImg, i1, j1, 5, 5);
						// sourceImg.setRGB(i1, j1, avgRgb);
						// } else {
						// int avgRgb = getLeftUpAvgRgb(sourceImg, i1, j1, 5, 5);
						// sourceImg.setRGB(i1, j1, avgRgb);
						// }
						// int avgRgb = getLeftUpAvgRgb(sourceImg, i1, j1, 2, 2);
						// int avgRgb = getLeftUpAvgRgb(sourceImg, i1, j1, 2, 2);
						// sourceImg.setRGB(i1, j1, avgRgb);
						// }
						// }
					}
				}
			}
			return sourceImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<Color, List<Point>> searchLinesPoints2(Map<Color, List<Point>> map, int lineWidth) {
		for (Entry<Color, List<Point>> entry : map.entrySet()) {
			List<Point> points = entry.getValue();
			List<Point> linePoints = new ArrayList<Point>();
			for (int i = 0; i < points.size(); i++) {
				Point point1 = points.get(i);
				int countX = 1;
				int countY = 1;
				int x1 = (int) point1.getX(), y1 = (int) point1.getY();
				int maxX = (int) point1.getX(), maxY = (int) point1.getY();
				for (int j = 0; j < points.size(); j++) {
					Point point2 = points.get(j);
					int x2 = (int) point2.getX(), y2 = (int) point2.getY();
					// 找相邻点
					if (x1 == x2 && y2 - maxY == 1) {
						countY++;
						maxY++;
					}
					if (y1 == y2 && x2 - maxX == 1) {
						countX++;
						maxX++;
					}
				}
				if (countX <= lineWidth || countY <= lineWidth) {
					linePoints.add(point1);
				}
			}
			entry.setValue(linePoints);
		}
		return map;
	}

	public static List<Point> searchLinesPoints(Map<Color, List<Point>> map, int lineWidth) {
		List<Point> linePoints = new ArrayList<Point>();
		for (Entry<Color, List<Point>> entry : map.entrySet()) {
			List<Point> points = entry.getValue();
			for (int i = 0; i < points.size(); i++) {
				Point point1 = points.get(i);
				int countX = 1;
				int countY = 1;
				int x1 = (int) point1.getX(), y1 = (int) point1.getY();
				int maxX = (int) point1.getX(), maxY = (int) point1.getY();
				for (int j = 0; j < points.size(); j++) {
					Point point2 = points.get(j);
					int x2 = (int) point2.getX(), y2 = (int) point2.getY();
					// 找相邻点
					if (x1 == x2 && y2 - maxY == 1) {
						countY++;
						maxY++;
					}
					if (y1 == y2 && x2 - maxX == 1) {
						countX++;
						maxX++;
					}
				}
				if (countX <= lineWidth || countY <= lineWidth) {
					linePoints.add(point1);
				}
			}
		}
		return linePoints;
	}

	public static int getRoundAvgRgb(BufferedImage sourceImg, int x, int y, int scopeX, int scopeY) {
		int currentRGB = sourceImg.getRGB(x, y);
		Color cuerrentColor = new Color(currentRGB);
		int startX = x - scopeX;
		int startY = y - scopeY;
		int endX = x + scopeX;
		int endY = y + scopeY;
		if (startX < 0) {
			startX = 0;
		}
		if (startY < 0) {
			startY = 0;
		}
		if (endX > sourceImg.getWidth() - 1) {
			endX = sourceImg.getWidth() - 1;
		}
		if (endY > sourceImg.getHeight() - 1) {
			endY = sourceImg.getHeight() - 1;
		}
		// int sumRGB = 0;
		int count = 0;
		int R = 0;
		int G = 0;
		int B = 0;
		List<Color> list = new ArrayList<Color>();
		for (int i = startX; i <= endX; i++) {
			for (int j = startY; j <= endY; j++) {
				if (i == x && j == y) {
					continue;
				}
				count++;
				int rgb = sourceImg.getRGB(i, j);
				Color color = new Color(rgb);
				list.add(color);
				R += color.getRed();
				G += color.getGreen();
				B += color.getBlue();
			}
		}
		if (x == 0 && y == 0) {
			return sourceImg.getRGB(x, y);
		}
		Color color = new Color(R / count, G / count, B / count);
		R = 0;
		G = 0;
		B = 0;
		count = 0;
		for (Color tempColor : list) {
			if (getColorDistance(cuerrentColor, tempColor, 32) > 3) {
				continue;
			}
			R += color.getRed();
			G += color.getGreen();
			B += color.getBlue();
			count++;
		}
		if (count > 0) {
			color = new Color(R / count, G / count, B / count);
		}

		return color.getRGB();
	}

	public static int getLeftUpAvgRgb(BufferedImage sourceImg, int x, int y, int scopeX, int scopeY, List<Point> linePoints) {
		int currentRGB = sourceImg.getRGB(x, y);
		int startX = x - scopeX;
		int startY = y - scopeY;
		if (startX < 0) {
			startX = 0;
		}
		if (startY < 0) {
			startY = 0;
		}
		int count = 0;
		int R = 0;
		int G = 0;
		int B = 0;
		for (int i = startX; i <= x; i++) {
			for (int j = startY; j <= y; j++) {
				boolean found = false;
				for (Point point : linePoints) {
					int px = (int) point.getX();
					int py = (int) point.getY();
					if (i == px && j == py) {
						found = true;
					}
				}
				if (found) {
					continue;
				}
				count++;
				int roundRGB = sourceImg.getRGB(i, j);
				Color color = new Color(roundRGB);
				R += color.getRed();
				G += color.getGreen();
				B += color.getBlue();
			}
		}
		if (x == 0 && y == 0 || count == 0) {
			return currentRGB;
		}
		Color color = new Color(R / count, G / count, B / count);
		return color.getRGB();
	}

	public static void parseGifToImages(File sourceFile, String destFilepath) {
		try {
			ImageReaderSpi readerSpi = new GIFImageReaderSpi();
			GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
			gifReader.setInput(new FileImageInputStream(sourceFile));
			File output = new File(destFilepath);
			output.mkdirs();
			for (int i = 0; i < gifReader.getNumImages(true); i++) {
				BufferedImage img = gifReader.read(i);
				ImageIO.write(img, "jpg", new File(destFilepath + "/" + i + ".jpg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		IplImage watermarkedImage = cvLoadImage("/D:/项目读写文件/mark/white_mark2.jpg");
		IplImage thumnbailImage = cvLoadImage("/D:/项目读写文件/mark/mark_on_black.jpg");
		IplImage maskedImage = cvLoadImage("/D:/项目读写文件/mark/img_with_mark_removed.jpg");

		// Resize thumbnail
		IplImage thumbnail_resized = new IplImage();
		thumbnail_resized = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvResize(thumnbailImage, thumbnail_resized, CV_INTER_LANCZOS4);

		// Resize mask
		IplImage masked_resized = new IplImage();
		masked_resized = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvResize(maskedImage, masked_resized, CV_INTER_NN);

		// Our cleaned image
		IplImage cleanedImage = new IplImage();
		cleanedImage = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvCopy(watermarkedImage, cleanedImage);

		// Copy thumbnail resized over to mask
		for (int x = 0; x < watermarkedImage.cvSize().width(); x++) {
			for (int y = 0; y < watermarkedImage.cvSize().height(); y++) {
				CvScalar g = cvGet2D(masked_resized, y, x);

				if (g.red() == 0 && g.green() == 0 && g.blue() == 0) {
					CvScalar s = cvGet2D(thumbnail_resized, y, x);
					cvSet2D(cleanedImage, y, x, s);
				}
			}
		}
	}

	public static Map<Color, List<Point>> getColorPointsMap(BufferedImage sourceImg, int startX, int startY, int endX, int endY) {
		Map<Color, List<Point>> map = new HashMap<Color, List<Point>>();
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				int rgb = sourceImg.getRGB(i, j);
				Color color = new Color(rgb);
				color = getStandardColor(color, 16);
				List<Point> list = map.get(color);
				if (CollectionUtils.isEmpty(list)) {
					list = new ArrayList<Point>();
				}
				if (isBlackOrWhite(color)) {
					list.add(new Point(i, j));
					map.put(color, list);
				}
			}
		}
		return map;
	}

	public static Map<Color, List<Point>> getColorPointsMap(BufferedImage sourceImg, boolean isOnBlack, int startX, int startY, int endX, int endY) {
		Map<Color, List<Point>> map = new HashMap<Color, List<Point>>();
		for (int i = startX; i < endX; i++) {
			for (int j = startY; j < endY; j++) {
				int rgb = sourceImg.getRGB(i, j);
				Color color = new Color(rgb);
				color = getStandardColor(color, 16);
				List<Point> list = map.get(color);
				if (CollectionUtils.isEmpty(list)) {
					list = new ArrayList<Point>();
				}
				if (isOnBlack && isWhite(color) || !isOnBlack && !Color.WHITE.equals(color)) {
					list.add(new Point(i, j));
					map.put(color, list);
				}
			}
		}
		return map;
	}
}
