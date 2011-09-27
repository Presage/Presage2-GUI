package uk.ac.imperial.presage2.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Scale;

public class PlayerControls extends Composite {

	boolean playing = false;
	int playbackRate = 0;
	int progress = 0;

	ProgressBar progressBar;
	Button btnPlaypause;
	Button btnStep;
	Scale playRateScale;
	Label lblPlayRate;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public PlayerControls(Composite parent) {
		super(parent, SWT.NONE);

		progressBar = new ProgressBar(this, SWT.NONE);
		progressBar.setSelection(0);
		progressBar.setBounds(89, 10, 501, 27);

		btnPlaypause = new Button(this, SWT.NONE);
		btnPlaypause.setText("Play");
		btnPlaypause.setBounds(10, 10, 73, 27);
		btnPlaypause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaying(!isPlaying());
			}
		});

		btnStep = new Button(this, SWT.NONE);
		btnStep.setText("Step");
		btnStep.setBounds(10, 43, 73, 27);
		btnStep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaying(false);
			}
		});

		playRateScale = new Scale(this, SWT.NONE);
		playRateScale.setSelection(0);
		playRateScale.setBounds(190, 43, 140, 18);
		playRateScale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				setPlaybackRate(playRateScale.getSelection());
			}
		});

		Label lblPlaybackRate = new Label(this, SWT.NONE);
		lblPlaybackRate.setBounds(89, 53, 94, 17);
		lblPlaybackRate.setText("playback rate:");

		lblPlayRate = new Label(this, SWT.NONE);
		lblPlayRate.setAlignment(SWT.RIGHT);
		lblPlayRate.setBounds(218, 57, 48, 17);
		lblPlayRate.setText(String.valueOf(playbackRate));

		Label lblStepss = new Label(this, SWT.NONE);
		lblStepss.setText("steps/s");
		lblStepss.setBounds(272, 57, 58, 17);

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

	public void setProgress(int progress) {
		this.progress = progress;
		progressBar.setSelection(progress);
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
		if (this.playing) {
			btnPlaypause.setText("Pause");
		} else {
			btnPlaypause.setText("Play");
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
