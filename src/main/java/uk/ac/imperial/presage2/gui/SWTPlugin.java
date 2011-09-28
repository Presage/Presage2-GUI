package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

public abstract class SWTPlugin extends CTabItem {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SWTPlugin(CTabFolder parent, String name) {
		super(parent, SWT.NONE);
		this.setShowClose(true);
		this.setText(name);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
