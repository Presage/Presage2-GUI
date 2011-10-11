package uk.ac.imperial.presage2.gui;

import java.lang.reflect.Constructor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class PluginDialog extends Dialog {

	private final StorageService sto;
	private Combo simChoice;
	private final CTabFolder tabFolder;
	private final Class<?> plugin;
	private long selected = -1L;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public PluginDialog(Shell parentShell, StorageService sto,
			CTabFolder tabFolder, Class<?> plugin) {
		super(parentShell);
		this.sto = sto;
		this.plugin = plugin;
		this.tabFolder = tabFolder;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 10;
		Label l = new Label(container, SWT.NONE);
		l.setText("Choose simulationID for datasource: ");

		simChoice = new Combo(container, SWT.NONE);
		simChoice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		simChoice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selected = Long.parseLong(simChoice.getItem(simChoice
						.getSelectionIndex()));
			}
		});

		for (Long id : this.sto.getSimulations()) {
			simChoice.add(id.toString());
		}

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button ok = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					Constructor<?> ctor = plugin.getConstructor(
							CTabFolder.class, PersistentSimulation.class);
					ctor.newInstance(tabFolder, sto.getSimulationById(selected));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
