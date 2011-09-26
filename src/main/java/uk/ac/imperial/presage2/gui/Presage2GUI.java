package uk.ac.imperial.presage2.gui;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Presage2GUI {

	protected Shell shlPresage;

	private final DatabaseService db;
	private final StorageService sto;

	private SimulationsTable simTable;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Presage2GUI window = new Presage2GUI();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	Presage2GUI() throws Exception {
		super();
		DatabaseModule dbModule = DatabaseModule.load();
		Injector injector = Guice.createInjector(dbModule);
		this.db = injector.getInstance(DatabaseService.class);
		this.sto = injector.getInstance(StorageService.class);
		db.start();

	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPresage.open();
		shlPresage.layout();
		while (!shlPresage.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shlPresage.addListener(SWT.CLOSE, new Listener() {
			public void handleEvent(Event arg0) {
				db.stop();
			}
		});
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPresage = new Shell();
		shlPresage.setSize(771, 543);
		shlPresage.setText("Presage2");

		TabFolder tabFolder = new TabFolder(shlPresage, SWT.NONE);
		tabFolder.setBounds(10, 10, 749, 505);

		simTable = new SimulationsTable(sto, tabFolder);
	}

}
