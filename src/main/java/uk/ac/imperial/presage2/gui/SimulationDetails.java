package uk.ac.imperial.presage2.gui;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class SimulationDetails extends CTabItem {

	private final PersistentSimulation sim;

	private Text text_ID;
	private Text text_name;
	private Text text_classname;
	private Text text_state;
	private Text text_progress;
	private Text text_created;
	private Text text_started;
	private Text text_finished;

	/**
	 * Create the details page.
	 */
	public SimulationDetails(PersistentSimulation sim, CTabFolder parent) {
		super(parent, SWT.NONE);
		this.sim = sim;
		this.setShowClose(true);
		this.setText(sim.getID() + ": " + sim.getName());
		createContents();
	}

	/**
	 * Create contents of the details page.
	 * 
	 * @param tab
	 */
	public void createContents() {
		FormToolkit toolkit = new FormToolkit(this.getDisplay());
		this.getParent().setLayout(new FillLayout());
		Composite wrapper = toolkit.createComposite(this.getParent(), SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.fill = true;
		wrapper.setLayout(layout);
		//
		Section section = toolkit.createSection(wrapper,
				ExpandableComposite.TITLE_BAR);
		section.setText("Simulation Info");

		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		Label lblId = new Label(composite, SWT.NONE);
		lblId.setBounds(10, 15, 90, 27);
		toolkit.adapt(lblId, true, true);
		lblId.setText("ID:");

		Label lblName = new Label(composite, SWT.NONE);
		lblName.setBounds(10, 52, 90, 27);
		toolkit.adapt(lblName, true, true);
		lblName.setText("Name:");

		Label lblClassName = new Label(composite, SWT.NONE);
		lblClassName.setBounds(10, 89, 90, 27);
		toolkit.adapt(lblClassName, true, true);
		lblClassName.setText("Class name:");

		Label lblState = new Label(composite, SWT.NONE);
		lblState.setBounds(10, 126, 90, 27);
		toolkit.adapt(lblState, true, true);
		lblState.setText("State:");

		Label lblProgress = new Label(composite, SWT.NONE);
		lblProgress.setBounds(10, 163, 90, 27);
		toolkit.adapt(lblProgress, true, true);
		lblProgress.setText("Progress:");

		Label lblCreated = new Label(composite, SWT.NONE);
		lblCreated.setText("Created:");
		lblCreated.setBounds(10, 200, 90, 27);
		toolkit.adapt(lblCreated, true, true);

		Label lblStarted = new Label(composite, SWT.NONE);
		lblStarted.setText("Started:");
		lblStarted.setBounds(10, 237, 90, 27);
		toolkit.adapt(lblStarted, true, true);

		Label lblFinished = new Label(composite, SWT.NONE);
		lblFinished.setText("Finished:");
		lblFinished.setBounds(10, 274, 90, 27);
		toolkit.adapt(lblFinished, true, true);

		text_ID = new Text(composite, SWT.NONE);
		text_ID.setBounds(106, 10, 300, 27);
		toolkit.adapt(text_ID, true, true);
		text_ID.setEditable(false);
		text_ID.setText(Long.toString(sim.getID()));

		text_name = new Text(composite, SWT.NONE);
		text_name.setBounds(106, 47, 300, 27);
		toolkit.adapt(text_name, true, true);
		text_name.setEditable(false);
		text_name.setText(sim.getName());

		text_classname = new Text(composite, SWT.NONE);
		text_classname.setBounds(106, 84, 300, 27);
		toolkit.adapt(text_classname, true, true);
		text_classname.setEditable(false);
		text_classname.setText(sim.getClassName());

		text_state = new Text(composite, SWT.NONE);
		text_state.setBounds(106, 121, 300, 27);
		toolkit.adapt(text_state, true, true);
		text_state.setEditable(false);
		text_state.setText(sim.getState());

		text_progress = new Text(composite, SWT.NONE);
		text_progress.setBounds(106, 158, 300, 27);
		toolkit.adapt(text_progress, true, true);
		text_progress.setEditable(false);
		text_progress.setText(sim.getCurrentTime() + "/" + sim.getFinishTime());

		text_created = new Text(composite, SWT.NONE);
		text_created.setBounds(106, 195, 300, 27);
		toolkit.adapt(text_created, true, true);
		text_created.setEditable(false);
		text_created.setText(new Date(sim.getCreatedAt()).toString());

		text_started = new Text(composite, SWT.NONE);
		text_started.setBounds(106, 232, 300, 27);
		toolkit.adapt(text_started, true, true);
		text_started.setEditable(false);
		text_started.setText(sim.getStartedAt() > 0 ? new Date(sim
				.getCreatedAt()).toString() : "no");

		text_finished = new Text(composite, SWT.NONE);
		text_finished.setBounds(106, 269, 300, 27);
		toolkit.adapt(text_finished, true, true);
		text_finished.setEditable(false);
		text_finished.setText(sim.getFinishedAt() > 0 ? new Date(sim
				.getFinishedAt()).toString() : "no");

		// parameter table.
		TableViewer tableViewer = new TableViewer(wrapper, SWT.BORDER);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText("Parameter");
		column.setWidth(200);
		column.setResizable(true);
		column.setMoveable(true);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] params = (String[]) element;
				return params[0];
			}
		});
		viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		column = viewerColumn.getColumn();
		column.setText("Value");
		column.setWidth(200);
		column.setResizable(true);
		column.setMoveable(false);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String[] params = (String[]) element;
				return params[1];
			}
		});

		tableViewer.setContentProvider(new ArrayContentProvider());
		List<String[]> params = new LinkedList<String[]>();
		for (String p : sim.getParameters().keySet()) {
			params.add(new String[] { p, (String) sim.getParameters().get(p) });
		}
		tableViewer.setInput(params);

		this.setControl(wrapper);

	}

	public void dispose() {
		super.dispose();
	}

	public void setFocus() {
		// Set focus
	}

	private void update() {
		// Update
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void commit(boolean onSave) {
		// Commit
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
		update();
	}
}
