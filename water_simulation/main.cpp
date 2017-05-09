#define OUTPUT_ANIMATION 1

#include <stdlib.h>
#include <stdio.h>
#include "Parser.h"
#include "Particles.h"

#if OUTPUT_ANIMATION
#include <opencv2/opencv.hpp>
#endif

using namespace cv;
using namespace std;

// ./sim scenes/scene1.obj
// delete/rename video after each run

int start = 0; // victor added

VideoWriter oVideoWriter; // victor added

inline float clip(const float& n, const float& lower, const float& upper) 
{
    return glm::max(lower, glm::min(n, upper));
}

float theta = M_PI/8;
float phi = -M_PI/8+M_PI_2;
float dist = 2.5;
int width = 800;
int height = 800;
int frame = 0;
const int render_step = 3;
int mx, my;

Particles particles;
Parser parser;
void display(void);

void reshape(int width, int height);

void idle(void)
{

    if (start) {
        particles.step(parser.polys, parser.vertices);
        glutPostRedisplay();
        if(frame/render_step >= 300){
            return;
        }
        if(frame%render_step == 0)
        {

            // window detection has to be based on glm library 

            #if OUTPUT_ANIMATION
            cv::Mat3b image(height, width);
            glReadPixels(0, 0, width, height, GL_BGR, GL_UNSIGNED_BYTE, image.data);
            cv::flip(image, image, 0);
            char fn[512];
            // sprintf(fn, "result/%04d.jpeg", frame/render_step);
            sprintf(fn, "result/%04d.png", frame/render_step);
            cv::imwrite(fn, image);
            // fprintf(stderr,"Finishes writing image\n");

            if (!oVideoWriter.isOpened()) {
                Mat image;
                image = imread("result/0000.png", CV_LOAD_IMAGE_COLOR);  // first png created
                oVideoWriter.open("path/MyVideo.mp4", CV_FOURCC('M','P','4','2'), 3, image.size(), true); // mpeg-4 is the fourcc code
            }

            oVideoWriter.write(image);

            #endif
        }
        frame++;
    }

    // particles.step();
    // glutPostRedisplay();
    // if(frame/render_step >= 300)
    //     return;
    // if(frame%render_step == 0)
    // {
    //     #if OUTPUT_ANIMATION
    //     cv::Mat3b image(height, width);
    //     glReadPixels(0, 0, width, height, GL_BGR, GL_UNSIGNED_BYTE, image.data);
    //     cv::flip(image, image, 0);
    //     char fn[512];
    //     sprintf(fn, "result/%04d.png", frame/render_step);
    //     cv::imwrite(fn, image);
    //     #endif
    // }
    // frame++;
}

void mouse(int button, int state, int x, int y);

void motion(int x, int y);

void keyboard(unsigned char c, int x, int y)
{
    switch(c)
    {
    case 'o' :
        break;
    case 's' : // start/stop
        if (start == 0) {
            start = 1;
        } else {
            start = 0;
        }
        break;
    case 'e' : // exit
        exit(0);
        break;
    }
}

int main(int argc, char** argv)
{
    // Parse the scene into parser 
    parser = Parser();
    if (argc ==  2) {
        parser.parse(argv[1]);
    }

    glutInit(&argc, argv);

    glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
    glutInitWindowSize(width, height);

     // had to switch order to allow keyboard use

    (void)glutCreateWindow("GLUT Program");
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutKeyboardFunc(keyboard);
    glutMouseFunc(mouse);
    glutMotionFunc(motion);
    glutIdleFunc(idle);
    glutMainLoop();

    oVideoWriter.release();

    return EXIT_SUCCESS;

    // glutInit(&argc, argv);

    // glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
    // glutInitWindowSize(width, height);

    // (void)glutCreateWindow("GLUT Program");
    // glutDisplayFunc(display);
    // glutReshapeFunc(reshape);
    // glutIdleFunc(idle);
    // glutMouseFunc(mouse);
    // glutMotionFunc(motion);
    // glutMainLoop();
    // glutKeyboardFunc(keyboard);

    // return EXIT_SUCCESS;
}

void reshape(int w, int h)
{
    width = w;
    height = h;
    glViewport(0, 0, w, h);
}

void display(void)
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    

    // your drawing code goes here
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(90, 1, 0.01, 100);
    gluLookAt(dist*sin(phi)*cos(theta), dist*cos(phi), dist*sin(phi)*sin(theta),
            0, 0, 0, 
            0, 1, 0);
    
    particles.render();
    parser.render();
    glutSwapBuffers();
}

void mouse(int button, int state, int x, int y)
{
    if(button == GLUT_LEFT_BUTTON && state == GLUT_DOWN)
    {
        mx = x;
        my = y;
    }
}

void motion(int x, int y)
{
    int dx = x - mx; 
    int dy = y - my;
    mx = x;
    my = y;
    if(abs(dx) > abs(dy))
        theta += dx*0.005;
    else
        phi -= dy*0.005;
    if(theta > 2*M_PI)
        theta -= 2*M_PI;
    if(theta < -2*M_PI)
        theta += 2*M_PI;
    phi = clip(phi, M_PI/12, M_PI*11/12);
    glutPostRedisplay();
}
