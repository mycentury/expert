/**
 * 
 */
package com.expert.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2017年5月15日
 * @ClassName VideoFramesConverter
 */
public class VideoFramesConverter {
	public static void convertToImages(String videofile, String framefilepath) {
		long start = System.currentTimeMillis();
		try {
			// OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(new File(videofile));
			// FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(videofile);
			// FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new FileInputStream(new File(videofile)));
			File file = new File(videofile);
			FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file);
			grabber.start();
			Java2DFrameConverter converter = new Java2DFrameConverter();
			List<Frame> frames = new ArrayList<Frame>();
			for (int i = 0; i < grabber.getLengthInFrames(); i++) {
				// 取图片
				Frame frame = grabber.grab();
				System.out.println(grabber.getDelayedTime() + "," + frame.timestamp + "," + frame.audioChannels);
				if (frame == null || frame.image == null) {
					continue;
				}
				frames.add(frame);
				BufferedImage bufferedImage = converter.getBufferedImage(frame);
				ImageIO.write(bufferedImage, "jpg", new File(framefilepath + "/" + i + ".jpg"));
			}
			grabber.stop();
			FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(framefilepath + "/" + file.getName(), 0);
			recorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - start);
	}

	public static void main(String[] args) {
		String videofile = "/D:/项目读写文件/image/1494398898905.mp4";
		String framefilepath = "/D:/项目读写文件/image/1494398898905";
		convertToImages(videofile, framefilepath);
	}
}
