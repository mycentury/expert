package com.expert.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.google.zxing.LuminanceSource;

public class BufferedImageLuminanceSource extends LuminanceSource {
	private final BufferedImage qrcodeImage;
	private final int marginLeft;
	private final int marginTop;

	public BufferedImageLuminanceSource(BufferedImage qrcodeImage) {
		this(qrcodeImage, 0, 0, qrcodeImage.getWidth(), qrcodeImage.getHeight());
	}

	public BufferedImageLuminanceSource(BufferedImage qrcodeImage, int marginLeft, int marginTop, int logoWidth, int logoHeight) {
		super(logoWidth, logoHeight);
		int qrcodeWidth = qrcodeImage.getWidth();
		int qrcodeHeight = qrcodeImage.getHeight();
		if (marginLeft + logoWidth > qrcodeWidth || marginTop + logoHeight > qrcodeHeight) {
			throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
		}

		for (int y = marginTop; y < marginTop + logoHeight; y++) {
			for (int x = marginLeft; x < marginLeft + logoWidth; x++) {
				if ((qrcodeImage.getRGB(x, y) & 0xFF000000) == 0) {
					qrcodeImage.setRGB(x, y, 0xFFFFFFFF); // = white
				}
			}
		}

		this.qrcodeImage = new BufferedImage(qrcodeWidth, qrcodeHeight, BufferedImage.TYPE_BYTE_GRAY);
		this.qrcodeImage.getGraphics().drawImage(qrcodeImage, 0, 0, null);
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("Requested row is outside the image: " + y);
		}
		int width = getWidth();
		if (row == null || row.length < width) {
			row = new byte[width];
		}
		qrcodeImage.getRaster().getDataElements(marginLeft, marginTop + y, width, 1, row);
		return row;
	}

	@Override
	public byte[] getMatrix() {
		int width = getWidth();
		int height = getHeight();
		int area = width * height;
		byte[] matrix = new byte[area];
		qrcodeImage.getRaster().getDataElements(marginLeft, marginTop, width, height, matrix);
		return matrix;
	}

	@Override
	public boolean isCropSupported() {
		return true;
	}

	@Override
	public LuminanceSource crop(int left, int top, int width, int height) {
		return new BufferedImageLuminanceSource(qrcodeImage, this.marginLeft + left, this.marginTop + top, width, height);
	}

	@Override
	public boolean isRotateSupported() {
		return true;
	}

	@Override
	public LuminanceSource rotateCounterClockwise() {
		int qrcodeWidth = qrcodeImage.getWidth();
		int qrcodeHeight = qrcodeImage.getHeight();
		AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, qrcodeWidth);
		BufferedImage rotatedImage = new BufferedImage(qrcodeHeight, qrcodeWidth, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = rotatedImage.createGraphics();
		g.drawImage(qrcodeImage, transform, null);
		g.dispose();
		int logoWidth = this.getWidth();
		int logoHeight = this.getHeight();
		int marginRight = qrcodeWidth - (marginLeft + logoWidth);
		return new BufferedImageLuminanceSource(rotatedImage, marginTop, marginRight, logoHeight, logoWidth);
	}
}