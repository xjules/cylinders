package glshader;
import com.jogamp.opengl.GL2.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame; 

/**
 * COMMENT: A white triangle on black background
 *
 * @author angf
 */
public class Triangle implements GLEventListener {

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
       
    @Override
    public void init(GLAutoDrawable drawable) {
    	
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
