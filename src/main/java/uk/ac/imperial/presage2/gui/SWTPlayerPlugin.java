package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class SWTPlayerPlugin extends SWTPlugin {

	protected final PersistentSimulation sim;
	
	private int time = 0;
	
	PlayerControls controls;
	
	public SWTPlayerPlugin(CTabFolder parent, PersistentSimulation sim, String name) {
		super(parent, name);
		this.sim = sim;
		
		Composite wrapper = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.fill = true;
		wrapper.setLayout(layout);
		createPlayerArea(wrapper);
		controls = new PlayerControls(wrapper);
		
		this.setControl(wrapper);
	}

	private void createPlayerArea(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public void createContents() {
		
	}

}
