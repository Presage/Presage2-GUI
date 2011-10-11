package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class Presage2GUI {

	protected Shell shlPresage;

	private final DatabaseService db;
	private final StorageService sto;

	Menu pluginMenu;
	CTabFolder tabFolder;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Presage2GUI window = new Presage2GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

		tabFolder = new CTabFolder(shlPresage, SWT.NONE);
		tabFolder.setBounds(10, 10, 749, 505);

		new SimulationsTable(sto, tabFolder);

		Menu menu = new Menu(shlPresage, SWT.BAR);
		shlPresage.setMenuBar(menu);

		MenuItem mntmPlugins_1 = new MenuItem(menu, SWT.CASCADE);
		mntmPlugins_1.setText("Plugins");

		pluginMenu = new Menu(mntmPlugins_1);
		mntmPlugins_1.setMenu(pluginMenu);

		addPlugin("2D Environment Visualisation",
				EnvironmentVisualiser2DPlugin.class);
		addPlugin("3D Environment Visualisation",
				EnvironmentVisualiser3DPlugin.class);

		shlPresage.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent arg0) {
				tabFolder.setBounds(10, 10,
						shlPresage.getClientArea().width - 20,
						shlPresage.getClientArea().height - 20);
			}
		});
	}

	void addPlugin(String name, final Class<?> clazz) {
		MenuItem item = new MenuItem(pluginMenu, SWT.NONE);
		item.setText(name);
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				PluginDialog dialog = new PluginDialog(shlPresage, sto,
						tabFolder, clazz);
				dialog.open();
			}
		});
	}
}
