package uk.ac.imperial.presage2.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
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

		// add columns
		TableViewerColumn col = createTableViewerColumn("ID", 50, 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return Long.toString(sim.getID());
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

		col = createTableViewerColumn("Progress", 100, 0);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PersistentSimulation sim = (PersistentSimulation) element;
				return sim.getCurrentTime() + "/" + sim.getFinishTime();
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

}
