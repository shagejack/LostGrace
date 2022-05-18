import org.lwjgl.opengl.*;
import shagejack.lostgrace.foundation.render.SphereBuilder;
import shagejack.lostgrace.foundation.render.TriangleFace;
import shagejack.lostgrace.foundation.utility.Vector3;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.List;
import java.util.Random;

public class SphereDemo {

    private final List<TriangleFace> sphere = new SphereBuilder().build(Vector3.Z_POS_AXIS.multiply(2), 16, true);

    private long seed = 1145141919810L;
    private Random random;

    private long window;

    private int width  = 1080;
    private int height = 1080;

    private long lastTick = 0;

    private Color color = Color.BLUE;

    public void run() {
        init();

        while (!glfwWindowShouldClose(window)) {
            render();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        createWindow();
        initGL();
    }

    private void createWindow() {
        if (!glfwInit()) {
            System.out.println("GLFW initialization failed.");
            System.exit(1);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Demo", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwShowWindow(window);
    }


    private void initGL() {
        glViewport(0, 0, width, height);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-5, 5, -5, 5, -5, 5);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_MODELVIEW);

        glDisable(GL_CULL_FACE);

        glClearDepth(1.0f);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        glTranslated(0, 0, 0);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glRotated(1, 0, 1, 0);

        glPushMatrix();

        glBegin(GL_TRIANGLES);

        random = new Random(seed);

        sphere.forEach(this::renderTriangleFace);

        glEnd();

        glPopMatrix();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void renderTriangleFace(TriangleFace face) {
        glColor3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        glVertex3d(face.getV1().x(), face.getV1().y(), face.getV1().z());
        glColor3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        glVertex3d(face.getV2().x(), face.getV2().y(), face.getV2().z());
        glColor3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        glVertex3d(face.getV3().x(), face.getV3().y(), face.getV3().z());
    }
}
