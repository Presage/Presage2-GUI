package uk.ac.imperial.presage2.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class SimulationsTable {

	private final TabItem tab;

	private final TableViewer tableViewer;
	private Table table;

	private final StorageService sto;

	private final TabFolder tabFolder;

	SimulationsTable(StorageService sto, TabFolder parent) {
		this.sto = sto;
		this.tabFolder = parent;
		tab = new TabItem(parent, SWT.NONE);
		tab.setText("Simulations");

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);

		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setSorter(new SimulationsSorter());

		// add columns
		TableViewerColumn col = createTableViewerColumn("ID", 50, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return Long.toString(sim.getID());
			}
		});
		col.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				((SimulationsSorter) tableViewer.getSorter()).doSort(1);
				tableViewer.refresh();
			}
		});

		col = createTableViewerColumn("Name", 150, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return sim.getName();
			}
		});

		col = createTableViewerColumn("Class name", 300, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return sim.getClassName();
			}
		});

		col = createTableViewerColumn("State", 100, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return sim.getState();
			}
		});
		col.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				((SimulationsSorter) tableViewer.getSorter()).doSort(2);
				tableViewer.refresh();
			}
		});

		col = createTableViewerColumn("Progress", 100, 0);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return sim.getCurrentTime() + "/" + sim.getFinishTime();
			}
		});
		col.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				((SimulationsSorter) tableViewer.getSorter()).doSort(3);
				tableViewer.refresh();
			}
		});

		tab.setControl(table);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(this.getSimulations());

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public List<PersistentSimulation> getSimulations() {
		List<PersistentSimulation> sims = new ArrayList<PersistentSimulation>();
		for (Long simId : this.sto.getSimulations()) {
			sims.add(this.sto.getSimulationById(simId));
		}
		return sims;
	}

	class SimulationsSorter extends ViewerSorter {
		private int column;
		private int direction;

		void doSort(int column) {
			if (column == this.column) {
				direction = 1 - direction;
			} else {
				this.column = column;
				this.direction = 0;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			PersistentSimulation s1 = (PersistentSimulation) e1;
			PersistentSimulation s2 = (PersistentSimulation) e2;

			int rc = 0;

			switch (column) {
			case 1: // ID
				rc = s1.getID() > s2.getID() ? 1 : -1;
				break;
			case 2: // state
				rc = getComparator().compare(s1.getState(), s2.getState());
				break;
			case 3: // progress
				rc = s1.getCurrentTime() > s2.getCurrentTime() ? 1 : -1;
				break;
			}

			if (direction == 1)
				rc = -rc;
			return rc;
		}

	}

}
