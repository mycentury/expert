/**
 * 
 */
package com.expert.util;

import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月17日
 * @ClassName ImageMarkUtilTest
 */
public class ImageMarkUtilTest {

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#extractMarkFromImg(BufferedImage, String, int, int, int, int)} .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testExtractMarkFromImg() throws IOException {
		File markedImgFile = new File("/D:/项目读写文件/mark/white_mark2.jpg");
		String markImagePath = "/D:/项目读写文件/mark/mark_on_black.jpg";
		BufferedImage markedImg = ImageIO.read(markedImgFile);
		ImageMarkUtil.extractMarkFromImg(markedImg, markImagePath, 4, 640, 225, 70);

		markedImgFile = new File("/D:/项目读写文件/mark/white_mark9.jpg");
		markImagePath = "/D:/项目读写文件/mark/mark_on_white.jpg";
		markedImg = ImageIO.read(markedImgFile);
		ImageMarkUtil.extractMarkFromImg(markedImg, markImagePath, 4, 640, 225, 70);
	}

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#markImgWithText(BufferedImage, String, Font, Color, int, int)} .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMarkImgWithText() throws IOException {
		File sourceImgFile = new File("/D:/项目读写文件/mark/white_mark2.jpg");
		String text = "新手试驾";
		String destImgPath = "/D:/项目读写文件/mark/img_marked_with_text.jpg";
		Font font = new Font("方正舒体", Font.PLAIN, 30);
		Color color = new Color(255, 255, 255, 128);
		int x = 8;
		int y = 8;
		BufferedImage sourceImg = ImageIO.read(sourceImgFile);
		sourceImg = ImageMarkUtil.markImgWithText(sourceImg, text, font, color, x, y + font.getSize());
		ImageIO.write(sourceImg, "jpg", new File(destImgPath));
	}

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#markMp4WithImg(java.io.File, java.lang.String, java.io.File, int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMarkMp4WithImg() throws IOException {
		File sourceFile = new File("/D:/项目读写文件/mark/source.mp4");
		String destFilepath = "/D:/项目读写文件/mark/dest.mp4";
		int x = 8;
		int y = 8;
		File markImgFile = new File("/D:/项目读写文件/mark/mark.png");
		BufferedImage markImg = ImageIO.read(markImgFile);
		ImageMarkUtil.markMp4WithImg(sourceFile, destFilepath, markImg, x, y);
	}

	/**
	 * Test method for {@link com.expert.util.ImageMarkUtil#markGifWithImg(File, String, BufferedImage, int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMarkGifWithImg() throws IOException {
		File sourceFile = new File("/D:/项目读写文件/mark/source.gif");
		String destFilepath = "/D:/项目读写文件/mark/dest.gif";
		int x = 8;
		int y = 8;
		File markImgFile = new File("/D:/项目读写文件/mark/mark.png");
		BufferedImage markImg = ImageIO.read(markImgFile);
		ImageMarkUtil.markGifWithImg(sourceFile, destFilepath, markImg, x, y);
	}

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#markImgWithImg(BufferedImage, BufferedImage, int, int, int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMarkImgWithImg() throws IOException {
		File sourceImgFile = new File("/D:/项目读写文件/mark/white_mark2.jpg");
		String destImgPath = "/D:/项目读写文件/mark/img_marked_with_img.jpg";
		File markImgFile = new File("/D:/项目读写文件/mark/mark.png");
		int x = 8;
		int y = 8;
		BufferedImage sourceImg = ImageIO.read(sourceImgFile);
		BufferedImage markImg = ImageIO.read(markImgFile);
		sourceImg = ImageMarkUtil.markImgWithImg(sourceImg, markImg, markImg.getWidth(), markImg.getHeight(), x, y);
		ImageIO.write(sourceImg, "jpg", new File(destImgPath));
	}

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#removeMarkFromImg(java.awt.image.BufferedImage, java.awt.image.BufferedImage, int)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRemoveMarkFromImg() throws IOException {
		File sourceImgFile = new File("/D:/项目读写文件/mark/white_mark3.jpg");
		File markOnBlackFile = new File("/D:/项目读写文件/mark/mark_on_black.jpg");
		File markOnWhiteFile = new File("/D:/项目读写文件/mark/mark_on_white.jpg");
		File removeMarkFile = new File("/D:/项目读写文件/mark/img_with_mark_removed.jpg");
		BufferedImage sourceImg = ImageIO.read(sourceImgFile);
		BufferedImage markImg = ImageIO.read(markOnBlackFile);
		BufferedImage removeMark = ImageMarkUtil.removeMarkFromImg(sourceImg, markImg, 640, true);
		ImageIO.write(removeMark, "jpg", removeMarkFile);

		// sourceImg = ImageIO.read(sourceImgFile);
		markImg = ImageIO.read(markOnWhiteFile);
		removeMark = ImageMarkUtil.removeMarkFromImg(removeMark, markImg, 640, false);
		ImageIO.write(removeMark, "jpg", removeMarkFile);
	}

	@Test
	public void testRemoveMarkSimple() throws IOException {
		File sourceImgFile = new File("/D:/项目读写文件/mark/white_mark5.jpg");
		File removeMarkFile = new File("/D:/项目读写文件/mark/img_with_mark_removed.jpg");
		BufferedImage sourceImg = ImageIO.read(sourceImgFile);
		BufferedImage removeMark = ImageMarkUtil.removeMarkSimple(sourceImg, 225, 70, 640);
		ImageIO.write(removeMark, "jpg", removeMarkFile);
	}

	@Test
	public void test() throws IOException {
		File sourceFile1 = new File("/D:/项目读写文件/mark/extract_mark.jpg");
		File sourceFile2 = new File("/D:/项目读写文件/mark/dest_291X433=13.jpg");
		BufferedImage source1 = ImageIO.read(sourceFile1);
		BufferedImage source2 = ImageIO.read(sourceFile2);
		String hashCode1 = ImageAnalyseUtil.generateFingerprint(source1);
		String hashCode2 = ImageAnalyseUtil.generateFingerprint(source2);
		int hammingDistance = ImageAnalyseUtil.hammingDistance(hashCode1, hashCode2);
		System.out.println(hashCode1 + "与" + hashCode2 + "相差：" + hammingDistance);
	}

	/**
	 * Test method for
	 * {@link com.expert.util.ImageMarkUtil#getLeftUpAvgRgb(java.awt.image.BufferedImage, int, int, int)}.
	 */
	@Test
	public void testGetLeftUpAvgRgb() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.expert.util.ImageMarkUtil#parseGifToImages(File, String)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testParseGifToImages() throws IOException {
		File sourceFile = new File("/D:/项目读写文件/mark/source2.gif");
		String destFilepath = "/D:/项目读写文件/mark/dest2";
		ImageMarkUtil.parseGifToImages(sourceFile, destFilepath);
	}

}
