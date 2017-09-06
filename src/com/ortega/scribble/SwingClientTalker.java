package com.ortega.scribble;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ortega.scribble.context.GraphicContext;
import com.ortega.scribble.context.UsersContext;

public class SwingClientTalker extends ClientTalker {

	private JFrame parentFrame;
	
	public SwingClientTalker(ScribbleProcessor proc, GraphicContext graphicContext, UsersContext usersContext,
			JFrame parentFrame) {
		super(proc, graphicContext, usersContext);
		this.parentFrame = parentFrame;
	}

	@Override
	public void onHeartbeatTimeout() {
		JOptionPane.showMessageDialog(parentFrame, "Connection lost");
		parentFrame.dispose();
	}
}
