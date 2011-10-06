package uk.ac.imperial.presage2.gui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

public class EnvironmentVisualiser2DPlugin extends SWTPlayerPlugin {

	Canvas drawArea;

	int maxTime;

	int xSize;
	int ySize;

	public EnvironmentVisualiser2DPlugin(CTabFolder parent,
			PersistentSimulation sim) {
		super(parent, sim, "2D Environment Visualisation");
	}

	@Override
	protected void createPlayerArea(final Composite parent) {

		finishTime = sim.getFinishTime() - 1;
		maxTime = sim.getCurrentTime();

		drawArea = new Canvas(parent, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		drawArea.setLayoutData(gridData);

		Map<String, Object> params = sim.getParameters();
		xSize = Integer.parseInt(params.get("xSize").toString());
		ySize = Integer.parseInt(params.get("ySize").toString());

		drawArea.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle area = drawArea.getClientArea();
				e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

				// normalise display area
				e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				double windowRatio = (double) area.width / (double) area.height;
				double simRatio = ySize / xSize;
				Rectangle visArea = new Rectangle(0, 0, area.width, area.height);
				double scaleFactor = 1.0;
				if (windowRatio > simRatio) {
					scaleFactor = area.height / ySize;
					visArea.width = (int) Math.ceil(scaleFactor * xSize);
				} else if (windowRatio < simRatio) {
					scaleFactor = area.width / xSize;
					visArea.height = (int) Math.ceil(scaleFactor * ySize);
				} else {
					scaleFactor = area.width / xSize;
				}

				e.gc.fillRectangle(visArea);

				for (PersistentAgent a : sim.getAgents()) {
					TransientAgentState state = a.getState(time);
					renderAgent(state, scaleFactor, e.gc);
				}

			}
		});

	}

	protected void renderAgent(TransientAgentState state, double scaleFactor,
			GC gc) {
		int radius = 5;
		gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		try {
			double x = Double.parseDouble(state.getProperty("x").toString())
					* scaleFactor;
			double y = Double.parseDouble(state.getProperty("y").toString())
					* scaleFactor;
			gc.fillOval(Math.max(0, (int) Math.floor(x - radius)),
					Math.max(0, (int) Math.floor(y - radius)), radius * 2,
					radius * 2);
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}

	}

	@Override
	protected void initPlayerControls(final PlayerControls controls) {

		// run control
		controls.getBtnPlaypause().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				drawArea.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!drawArea.isDisposed() && controls.isPlaying()) {

							if (time >= maxTime) {
								controls.setPlaying(false);
								return;
							}

							time++;
							update();

							if (time == finishTime) {
								controls.setPlaying(false);
								return;
							}

							drawArea.getDisplay().timerExec(
									1000 / controls.getPlaybackRate(), this);
						}

					}

				});
			}
		});

	}

	@Override
	protected void update() {
		drawArea.redraw();
		controls.setProgress(time);
	}

}
