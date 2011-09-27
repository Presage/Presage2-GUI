package uk.ac.imperial.presage2.gui;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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
		db.stop();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPresage = new Shell();
		shlPresage.setSize(800, 600);
		shlPresage.setText("Presage2");

		final CTabFolder tabFolder = new CTabFolder(shlPresage, SWT.NONE);
		tabFolder.setBounds(10, 10, 749, 505);

		simTable = new SimulationsTable(sto, tabFolder);

		shlPresage.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				tabFolder.setBounds(10, 10,
						shlPresage.getClientArea().width - 20,
						shlPresage.getClientArea().height - 20);
			}
		});
	}

}
