package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;

public class PlayerControls extends Composite {

	boolean playing = false;
	int playbackRate = 1;
	int progress = 0;
	int maxTime = 100;

	ProgressBar progressBar;
	Button btnPlaypause;
	Button btnStep;
	Scale playRateScale;
	Label lblPlayRate;
	Label lblCycle;
	Button btnStepEnd;
	Button btnStepStart;
	Button btnStepBack;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PlayerControls(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(null);

		progressBar = new ProgressBar(this, SWT.NONE);
		progressBar.setBounds(96, 10, 494, 27);
		progressBar.setSelection(0);

		btnPlaypause = new Button(this, SWT.NONE);
		btnPlaypause.setBounds(10, 10, 80, 27);
		btnPlaypause.setText("Play");
		btnPlaypause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaying(!isPlaying());
			}
		});

		btnStep = new Button(this, SWT.NONE);
		btnStep.setBounds(50, 43, 20, 27);
		btnStep.setText(">");
		SelectionListener stopOnSelect = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaying(false);
			}
		};
		btnStep.addSelectionListener(stopOnSelect);

		playRateScale = new Scale(this, SWT.NONE);
		playRateScale.setBounds(218, 43, 140, 18);
		playRateScale.setMinimum(1);
		playRateScale.setSelection(1);
		playRateScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaybackRate(playRateScale.getSelection());
			}
		});

		Label lblPlaybackRate = new Label(this, SWT.NONE);
		lblPlaybackRate.setBounds(118, 53, 94, 17);
		lblPlaybackRate.setText("playback rate:");

		lblPlayRate = new Label(this, SWT.NONE);
		lblPlayRate.setBounds(218, 57, 48, 17);
		lblPlayRate.setAlignment(SWT.RIGHT);
		lblPlayRate.setText(String.valueOf(playbackRate));

		Label lblStepss = new Label(this, SWT.NONE);
		lblStepss.setBounds(272, 57, 58, 17);
		lblStepss.setText("steps/s");

		lblCycle = new Label(this, SWT.NONE);
		lblCycle.setBounds(520, 43, 70, 17);

		btnStepEnd = new Button(this, SWT.NONE);
		btnStepEnd.setText(">|");
		btnStepEnd.setBounds(70, 43, 20, 27);
		btnStepEnd.addSelectionListener(stopOnSelect);

		btnStepStart = new Button(this, SWT.NONE);
		btnStepStart.setText("|<");
		btnStepStart.setBounds(10, 43, 20, 27);
		btnStepStart.addSelectionListener(stopOnSelect);

		btnStepBack = new Button(this, SWT.NONE);
		btnStepBack.setText("<");
		btnStepBack.setBounds(30, 43, 20, 27);
		btnStepBack.addSelectionListener(stopOnSelect);

	}

	public int getPlaybackRate() {
		return playbackRate;
	}

	public void setPlaybackRate(int playbackRate) {
		this.playbackRate = playbackRate;
		lblPlayRate.setText(String.valueOf(playbackRate));
		playRateScale.setSelection(playbackRate);
	}

	public Button getBtnPlaypause() {
		return btnPlaypause;
	}

	public Button getBtnStep() {
		return btnStep;
	}

	public Button getBtnStepEnd() {
		return btnStepEnd;
	}

	public Button getBtnStepStart() {
		return btnStepStart;
	}

	public Button getBtnStepBack() {
		return btnStepBack;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		progressBar.setSelection(progress);
		lblCycle.setText(this.progress + "/" + this.maxTime);
	}

	public void setMaxTime(int time) {
		this.maxTime = time;
		progressBar.setMaximum(this.maxTime);
		lblCycle.setText(this.progress + "/" + this.maxTime);
	}

	public boolean isPlaying() {
		return playing;
	}

	public synchronized void waitForPlay() {
		while (!isPlaying()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void setPlaying(boolean playing) {
		this.playing = playing;
		if (this.playing) {
			btnPlaypause.setText("Pause");
		} else {
			btnPlaypause.setText("Play");
		}
		notifyAll();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
