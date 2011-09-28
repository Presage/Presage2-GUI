package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public abstract class SWTPlayerPlugin extends SWTPlugin {

	int time = 0;
	int finishTime;

	protected final PersistentSimulation sim;

	PlayerControls controls;

	public SWTPlayerPlugin(CTabFolder parent, PersistentSimulation sim,
			String name) {
		super(parent, name);
		this.sim = sim;

		// Composite fillWrap = new Composite(parent, SWT.NONE);
		// fillWrap.setLayout(new FillLayout());

		Composite wrapper = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		wrapper.setLayout(layout);
		createPlayerArea(wrapper);
		controls = new PlayerControls(wrapper);
		GridData controlGridData = new GridData();
		controlGridData.minimumHeight = 85;
		controlGridData.minimumWidth = 600;
		controls.setLayoutData(controlGridData);
		initPlayerControls(controls);

		wrapper.setBounds(this.getBounds());
		this.setControl(wrapper);

		controls.getBtnStep().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (time < finishTime)
					time++;
				update();
			}
		});
		controls.getBtnStepBack().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (time > 0)
					time--;
				update();
			}
		});
		controls.getBtnStepEnd().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				time = finishTime;
				update();
			}
		});
		controls.getBtnStepStart().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				time = 0;
				update();
			}
		});
	}

	protected abstract void initPlayerControls(PlayerControls controls);

	protected abstract void createPlayerArea(Composite parent);

	protected abstract void update();

	public void createContents() {

	}

}
