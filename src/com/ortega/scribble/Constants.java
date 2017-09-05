package com.ortega.scribble;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class Constants {
	public static final int DEFAULT_PORT = 34567;
	public static final String CHARSET = "UTF-8";

	public static final int PEN_STROKE_WIDTH = 3;
	public static final int ERASER_STROKE_WIDTH = 9;
	
	public static final Stroke PEN_STROKE = new BasicStroke(PEN_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final Stroke ERASER_STROKE = new BasicStroke(ERASER_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	public static final Color DEFAULT_PEN_COLOR = Color.BLACK;
	public static final Color BACK_COLOR = Color.WHITE;
	
	public static final long HEARTBEAT_TIMEOUT = 20000;
	public static final long HEARTBEAT_DELAY = 5000;
	
	private Constants() {}
}
