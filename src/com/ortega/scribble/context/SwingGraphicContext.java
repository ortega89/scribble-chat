package com.ortega.scribble.context;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class SwingGraphicContext extends GraphicContext {

	private JComponent paintBox;
	
	public SwingGraphicContext(BufferedImage canvas) {
		super(canvas);
	}
	
	public SwingGraphicContext(BufferedImage canvas, JComponent paintBox) {
		this(canvas);
		setPaintBox(paintBox);
	}

	public void setPaintBox(JComponent paintBox) {
		this.paintBox = paintBox;
	}
	
	@Override
	public void doRepaint() {
		if (paintBox != null)
			paintBox.repaint();
	}

}
