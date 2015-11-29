package glshader;
import com.jogamp.opengl.DebugGL4bc;
import com.jogamp.opengl.GL2.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GL;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL3.GL_MAX_GEOMETRY_OUTPUT_COMPONENTS;
import static com.jogamp.opengl.GL3.GL_PROGRAM_POINT_SIZE;
import static com.jogamp.opengl.GL3ES3.GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS;
import static com.jogamp.opengl.GL3ES3.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS;
import static com.jogamp.opengl.GL3ES3.GL_MAX_SHADER_STORAGE_BLOCK_SIZE;
import static com.jogamp.opengl.GL3ES3.GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLJPanel;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import javax.swing.JFrame; 
import javax.vecmath.Vector4f;

/**
 * COMMENT: A white triangle on black background
 *
 * @author angf
 */
public class Triangle implements GLEventListener {
    
        private Vector4f[] mCurvePoints=null;

	public static void main(String[] args) {
        
        // Initialise OpenGL
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a panel to draw on
        GLJPanel panel = new GLJPanel(caps);

        final JFrame jframe = new JFrame("Triangle");        
        jframe.setSize(300, 300);
        jframe.add(panel);
        jframe.setVisible(true);

        // Catch window closing events and quit             
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add a GL Event listener to handle rendering
        Triangle triangle = new Triangle();
        panel.addGLEventListener(triangle);

    }
        
    private int getInteger(GL4 gl, int param) {
        int[] data = new int[1];
        gl.glGetIntegerv(param, data, 0);
        return data[0];
    }
       
    @Override
    public void init(GLAutoDrawable drawable) {
    	GL4bc gl = new DebugGL4bc(drawable.getGL().getGL4bc());
        
        // set OpenGL global state
	gl.glEnable(GL_DEPTH_TEST);
        //gl.glEnable(GL_CULL_FACE);
        //gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        //gl.glEnable(GL_POINT_SMOOTH);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
        // enable gl_PointSize in shaders
        gl.glEnable(GL_PROGRAM_POINT_SIZE);
        
        // get compute shader resource info
        int maxBindingsCount = getInteger(gl, GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS);
        int maxBlockSize = getInteger(gl, GL_MAX_SHADER_STORAGE_BLOCK_SIZE);
        int maxComputeBlocksCount = getInteger(gl, GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS);
        int maxComputeWorkGroupInvocations = getInteger(gl, GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
        int maxGeometryOutputComponents = getInteger(gl, GL_MAX_GEOMETRY_OUTPUT_COMPONENTS);
        System.out.println("Max bindings count: " + maxBindingsCount);
        System.out.println("Max SSBO size: " + maxBlockSize);
        System.out.println("Max compute shader SSBO count: " + maxComputeBlocksCount);
        System.out.println("Max compute shader Work Group invocations: " + maxComputeWorkGroupInvocations);
        System.out.println("Max geometry output components: " + maxGeometryOutputComponents);
        
        // get extension info
        boolean available = gl.isExtensionAvailable("GL_ARB_compute_variable_group_size");
        System.out.println("Extension GL_ARB_compute_variable_group_size: " + available);
        available = gl.isExtensionAvailable("GL_INTEL_fragment_shader_ordering");
        System.out.println("Extension GL_INTEL_fragment_shader_ordering: " + available);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();
        
        // clear the window
        // By default color is black
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        //By default vertex colors are white
        gl.glBegin(GL2.GL_TRIANGLES);
        {
            gl.glVertex2d(-1, -1);
            gl.glVertex2d(1, -1);
            gl.glVertex2d(0, 1);
           
        }
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
           
    }

}
