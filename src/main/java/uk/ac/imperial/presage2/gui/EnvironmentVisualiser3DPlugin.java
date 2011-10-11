package uk.ac.imperial.presage2.gui;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.gui.opengl.SceneGrip;

public class EnvironmentVisualiser3DPlugin extends SWTPlayerPlugin {

	GLCanvas drawArea;
	private SceneGrip grip;

	int maxTime;

	int xSize;
	int ySize;
	int zSize;

	double scaleFactor = 1.0;

	public EnvironmentVisualiser3DPlugin(CTabFolder parent,
			PersistentSimulation sim) {
		super(parent, sim, "3D Environment Visualisation");
	}

	@Override
	protected void initPlayerControls(final PlayerControls controls) {
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
	protected void createPlayerArea(Composite parent) {

		maxTime = sim.getCurrentTime();

		Map<String, Object> params = sim.getParameters();
		xSize = Integer.parseInt(params.get("xSize").toString());
		ySize = Integer.parseInt(params.get("ySize").toString());
		zSize = Integer.parseInt(params.get("zSize").toString());
		// zSize = 10;

		GLData data = new GLData();
		drawArea = new GLCanvas(parent, SWT.NONE, data);

		final GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		drawArea.setLayoutData(gridData);

		drawArea.setCurrent();
		try {
			GLContext.useContext(drawArea);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		drawArea.addListener(SWT.RESIZE, new Listener() {
			public void handleEvent(Event arg0) {
				resize();
			}
		});
		this.grip = new SceneGrip();
		drawArea.addMouseListener(this.grip);
		drawArea.addMouseMoveListener(this.grip);
		drawArea.addListener(SWT.MouseWheel, this.grip);
		drawArea.addKeyListener(this.grip);

		resize();

		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0f);
		// GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glLineWidth(2);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		// GL11.glShadeModel(GL11.GL_SMOOTH);

		drawArea.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!drawArea.isDisposed()) {
					drawArea.setCurrent();
					try {
						GLContext.useContext(drawArea);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT
							| GL11.GL_DEPTH_BUFFER_BIT);
					GL11.glClearColor(.3f, .5f, .8f, 1.0f);
					GL11.glLoadIdentity();
					grip.adjust();
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					GL11.glColor3f(0.9f, 0.9f, 0.9f);

					drawCuboid(xSize, ySize, zSize);

					for (PersistentAgent a : sim.getAgents()) {
						TransientAgentState state = a.getState(time);
						renderAgent(state);
					}

					// drawTorus(1, 1.0f, 10, 10);
					drawArea.swapBuffers();
					drawArea.getDisplay().timerExec(100, this);
				}
			}
		});
	}

	protected void renderAgent(TransientAgentState state) {
		try {
			double x = Double.parseDouble(state.getProperty("x").toString())
					* scaleFactor;
			double y = Double.parseDouble(state.getProperty("y").toString())
					* scaleFactor;
			double z = Double.parseDouble(state.getProperty("z").toString())
					* scaleFactor;
			float[] loc = { (float) x, (float) y, (float) z };
			drawSphere(loc, 1, 0.5f);
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
	}

	@Override
	protected void update() {
		controls.setProgress(time);
		drawArea.setCurrent();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void resize() {
		Rectangle area = drawArea.getClientArea();
		// double windowRatio = (double) area.width / (double) area.height;
		double simRatio = ySize / xSize;
		Rectangle visArea = new Rectangle(0, 0, area.width, area.height);

		GL11.glViewport(0, 0, visArea.width, visArea.height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45.0f, (float) simRatio, 0.5f, 400.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

	}

	static void drawTorus(float r, float R, int nsides, int rings) {
		float ringDelta = 2.0f * (float) Math.PI / rings;
		float sideDelta = 2.0f * (float) Math.PI / nsides;
		float theta = 0.0f, cosTheta = 1.0f, sinTheta = 0.0f;
		for (int i = rings - 1; i >= 0; i--) {
			float theta1 = theta + ringDelta;
			float cosTheta1 = (float) Math.cos(theta1);
			float sinTheta1 = (float) Math.sin(theta1);
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			float phi = 0.0f;
			for (int j = nsides; j >= 0; j--) {
				phi += sideDelta;
				float cosPhi = (float) Math.cos(phi);
				float sinPhi = (float) Math.sin(phi);
				float dist = R + r * cosPhi;
				GL11.glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
				GL11.glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
				GL11.glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
				GL11.glVertex3f(cosTheta * dist, -sinTheta * dist, r * sinPhi);
			}
			GL11.glEnd();
			theta = theta1;
			cosTheta = cosTheta1;
			sinTheta = sinTheta1;
		}
	}

	static void drawCuboid(float x, float y, float z) {
		// sim area rect
		GL11.glBegin(GL11.GL_LINES);

		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(x, 0, 0);

		GL11.glVertex3f(x, 0, 0);
		GL11.glVertex3f(x, y, 0);

		GL11.glVertex3f(x, y, 0);
		GL11.glVertex3f(0, y, 0);

		GL11.glVertex3f(0, y, 0);
		GL11.glVertex3f(0, 0, 0);

		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, z);

		GL11.glVertex3f(x, 0, 0);
		GL11.glVertex3f(x, 0, z);

		GL11.glVertex3f(0, y, 0);
		GL11.glVertex3f(0, y, z);

		GL11.glVertex3f(x, y, 0);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(0, 0, z);
		GL11.glVertex3f(x, 0, z);

		GL11.glVertex3f(x, 0, z);
		GL11.glVertex3f(x, y, z);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(0, y, z);

		GL11.glVertex3f(0, y, z);
		GL11.glVertex3f(0, 0, z);

		GL11.glEnd();
	}

	private static void drawSphere(float[] position, int ndiv, float radius) {
		final float x = 0.525731112119133606f;
		final float z = 0.850650808352039932f;
		float vdata[][] = { { -x, 0.0f, z }, { x, 0.0f, z }, { -x, 0.0f, -z },
				{ x, 0.0f, -z }, { 0.0f, z, x }, { 0.0f, z, -x },
				{ 0.0f, -z, x }, { 0.0f, -z, -x }, { z, x, 0.0f },
				{ -z, x, 0.0f }, { z, -x, 0.0f }, { -z, -x, 0.0f } };
		int tindices[][] = { { 0, 4, 1 }, { 0, 9, 4 }, { 9, 5, 4 },
				{ 4, 5, 8 }, { 4, 8, 1 }, { 8, 10, 1 }, { 8, 3, 10 },
				{ 5, 3, 8 }, { 5, 2, 3 }, { 2, 7, 3 }, { 7, 10, 3 },
				{ 7, 6, 10 }, { 7, 11, 6 }, { 11, 0, 6 }, { 0, 1, 6 },
				{ 6, 1, 10 }, { 9, 0, 11 }, { 9, 11, 2 }, { 9, 2, 5 },
				{ 7, 2, 11 } };
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (int i = 0; i < 20; i++) {
			drawTri(position, vdata[tindices[i][0]], vdata[tindices[i][1]],
					vdata[tindices[i][2]], ndiv, radius);
		}
		GL11.glEnd();
	}

	private static void drawTri(float[] p, float[] a, float[] b, float[] c,
			int div, float r) {
		if (div <= 0) {
			GL11.glNormal3f(p[0] + a[0], p[1] + a[1], p[2] + a[2]);
			GL11.glVertex3f(p[0] + (a[0] * r), p[1] + (a[1] * r), p[2]
					+ (a[2] * r));
			GL11.glNormal3f(p[0] + b[0], p[1] + b[1], p[2] + b[2]);
			GL11.glVertex3f(p[0] + (b[0] * r), p[1] + (b[1] * r), p[2]
					+ (b[2] * r));
			GL11.glNormal3f(p[0] + c[0], p[1] + c[1], p[2] + c[2]);
			GL11.glVertex3f(p[0] + (c[0] * r), p[1] + (c[1] * r), p[2]
					+ (c[2] * r));
		} else {
			float[] ab, ac, bc;
			ab = new float[3];
			ac = new float[3];
			bc = new float[3];
			for (int i = 0; i < 3; i++) {
				ab[i] = (a[i] + b[i]) / 2;
				ac[i] = (a[i] + c[i]) / 2;
				bc[i] = (b[i] + c[i]) / 2;
			}
			normalize(ab);
			normalize(ac);
			normalize(bc);
			drawTri(p, a, ab, ac, div - 1, r);
			drawTri(p, b, bc, ab, div - 1, r);
			drawTri(p, c, ac, bc, div - 1, r);
			drawTri(p, ab, bc, ac, div - 1, r);
		}
	}

	private static void normalize(float[] a) {
		double d = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
		a[0] /= d;
		a[1] /= d;
		a[2] /= d;
	}
}
