/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Particles.cpp
 * Author: swl
 * 
 * Created on April 15, 2016, 12:16 PM
 */

#include "Particles.h"
#include "math.h"
#include <vector>
#include "Parser.h"
#include <iostream>
#include <map>
#include "String.h"
#include "glm/ext.hpp"

typedef glm::ivec3 ivec3;

const double GRAV_CONST = .01; // Gravitational constant
const double PARTICLE_RAD = 0.05;
const double VOLUME_DENSITY = .001; //In g / mm^3
const double render_step = 3;

// min and max corners of box used
dvec3 max = dvec3(1.0,1.0,1.0);
dvec3 min = dvec3(-1.0,-1.0,-1.0);



Particles::Particles() 
{

    int nx = 10;
    int ny = 15;
    int nz = 10;
    float d = .1;
    
    // Mass = dW*dH*dD*rho /(W*H*D)
    double mass = (4.0 / 3.0) * M_PI * pow(PARTICLE_RAD, 3.0) * VOLUME_DENSITY / (nx*nz*ny);
    for(int x=0; x<nx; x++)
    {
        for(int y=0; y<ny; y++)
        {
            for(int z=0; z<nz; z++)
            {  
                Particle par;
                par.x = glm::dvec3((x+0.5-nx*0.5)*d, (y+4.0)*d-1.0, (z+0.5-nz*0.5)*d);
                par.forces = glm::dvec3(0, 0, 0);
                par.mass = mass;
                par.radius = PARTICLE_RAD;
                par.neighbors = std::vector<Particle>();
                particles.push_back(par);
            }
        }
    }
}

void Particles::render() const
{
    GLfloat mat_specular[] = { 1.0, 1.0, 1.0, 1.0};
    GLfloat mat_shininess[] = { 50.0 };
    GLfloat light_position[] = { 10.0, 10.0, 10.0, 0.0};
    glShadeModel (GL_SMOOTH);
    glPushAttrib(GL_ALL_ATTRIB_BITS);
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
    glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);
    glLightfv(GL_LIGHT0, GL_POSITION, light_position);
    
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_COLOR_MATERIAL);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glColorMaterial(GL_FRONT, GL_DIFFUSE);
    glColor4f(0.2, 0.5, 0.8, 1);
    glColorMaterial(GL_FRONT, GL_SPECULAR);
    glColor4f(0.9, 0.9, 0.9, 1);
    glColorMaterial(GL_FRONT, GL_AMBIENT);
    glColor4f(0.2, 0.5, 0.8, 1);
    
    for(const Particle &par : particles)
    {    
        
        glPushMatrix();
        glTranslatef(par.x.x, par.x.y, par.x.z);
        glutSolidSphere(PARTICLE_RAD, 10, 10);
        glPopMatrix();
    }
    
    glPopAttrib();
}

double poly6Kern(double r, double h) {
    double cofactor = 315.0 / (64.0 * M_PI * pow(h, 9.0));
    if (r <= h && r >= 0.0) {
        return cofactor * pow((h*h - r*r),3.0);
    }
    return 0.0;
}

double spikyKern(double r, double h) {
    double cofactor = 15.0 / (M_PI * pow(h, 6.0));
    if (r <= h && r >= 0.0) {
        return cofactor * pow((h - r),3.0);
    }
    return 0.0;
}

void Particles::step(std::vector<Polygon> polys, std::vector<glm::dvec3> verts) {
    for (Particle &par : particles) {
        par.forces[1] = -GRAV_CONST;
        par.v += par.forces * render_step; 
        par.x_approx = par.x + (par.v * render_step);
        par.neighbors.clear();
    }

    // Neighbor Find
    double h = .15; // 2 x average particle distance
    int i = 0;
    std::map<std::string, std::vector<Particle>> neighbor_map;
    for (Particle &par : particles) {
        ivec3 k = ivec3((int) floor(par.x_approx / h)[0], (int) floor(par.x_approx / h)[1], (int) floor(par.x_approx / h)[2]);
        std::string key = glm::to_string(k);
        if (neighbor_map.find(key) == neighbor_map.end()) {
            std::vector<Particle> bin = std::vector<Particle>();
            neighbor_map[key] = bin;
        }
        neighbor_map[key].push_back(par);

    }

    for (Particle& par : particles) {
        dvec3 flooredvec = floor(par.x_approx / h);
        std::string key = glm::to_string(flooredvec);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    std::string key_prime = glm::to_string(ivec3((int) flooredvec[0] + i, (int) flooredvec[1] + j, (int) flooredvec[2] + k));
                    if (neighbor_map.find(key_prime) != neighbor_map.end()) {
                        std::vector<Particle> bin = neighbor_map[key_prime];
                        for (Particle n : bin) {
                            if (length(n.x_approx - par.x_approx) <= h) {
                                par.neighbors.push_back(n);
                            }
                        }
                           
                    } 
                }
            }
        }
        //printf("%ld neighbors \n", par.neighbors.size());
    }


    // Forces on particle;
    int numIters = 10;
    // double rho_0 = VOLUME_DENSITY;
    double rho_0 = 1000.0;
    dvec3 r;
    double rlen;
    for (int i = 0; i < numIters; i++) {

        for (Particle &par : particles) {
            double rho_i = 0.0;
            double denom = 0.0;

            for (Particle n : par.neighbors) {

                rho_i += poly6Kern(length(par.x_approx - n.x_approx), h);
                r = par.x_approx - n.x_approx;
                rlen = length(r);

                if (rlen > 0.0) {
                    denom += pow(length((45.0 / (M_PI * pow(h,6.0))) * pow((h - rlen), 2.0) * (r / rlen)), 2.0);
                    // fprintf(stderr, "denom: %f\n",denom);
                    // fprintf(stderr, "rlen %f h %f i %u\n",rlen, h, i);
                    // fprintf(stderr, "grad %f\n", 45.0 / (M_PI * pow(h,6.0)));
                }

            }


            //come back to later algo may be different

            denom /= rho_0;

            par.rho_i = rho_i;
            
            //fprintf((stderr), "%f \n", par.rho_i);
            //fprintf(stderr, "rho_i %f rho_0 %f\n", rho_i, rho_0);

            // fprintf(stderr, "rho_i %f rho_0 %f\n", rho_i, rho_0);

            par.lambda_i = -1 * ((par.rho_i / rho_0) - 1.0);

            par.lambda_i /= denom;

        }

        double s_corr = 0;
        for (Particle& par : particles) {
            dvec3 correction(0.0,0.0,0.0);

            // fprintf(stderr, "par neighbor size %lu \n", par.neighbors.size());

            for (Particle& n : par.neighbors) {

            if ((par.x_approx[0] != n.x_approx[0] && par.x_approx[1] != n.x_approx[1] && par.x_approx[2] != n.x_approx[2])) {
                r = par.x_approx - n.x_approx;
                rlen = length(r);
                s_corr = .005 * pow(spikyKern(rlen, h) / spikyKern(.01*h, h), 4.0);
                dvec3 grad = (45.0 / (M_PI * pow(h,6.0))) * pow((h - rlen), 2.0) * (r / rlen);
                correction += (par.lambda_i + n.lambda_i + s_corr) * grad;
              }
                
            }

            correction /= rho_0;
            par.correction_vec = correction;

             // fprintf(stderr, "correction_vec %s\n", glm::to_string(par.correction_vec).c_str());
             // collisions
            //  double count = 0;
            //  dvec3 avg(0.0,0.0,0.0);
            //  for (Particle& p2 : particles) {
            //      if (length(par.x_approx - p2.x_approx) < (2.2*PARTICLE_RAD) && (par.x_approx[0] != p2.x_approx[0] && par.x_approx[1] != p2.x_approx[1] && par.x_approx[2] != p2.x_approx[2])) {
            //          dvec3 unitvec = (par.x_approx - p2.x_approx) / length(par.x_approx - p2.x_approx);
            //          dvec3 temp = p2.x_approx + ((2.2*PARTICLE_RAD) * unitvec);
            //          avg += temp - par.x_approx;
            //          count += 1;
            //      }

            // }
             
            //  if (count != 0) {
            //      par.x_approx += (avg / count);
            //  }

            for (Polygon poly : polys) {

                dvec3 origin = par.x;
                dvec3 dir = par.x_approx - par.x;

                dvec3 a, b, c;

                a = verts[poly.p0];
                b = verts[poly.p1];
                c = verts[poly.p2];

                dvec3 e1 = b - a;
                dvec3 e2 = c - a;
                dvec3 s0 = origin - a;
                dvec3 s1 = cross(dir, e2);
                dvec3 s2 = cross(s0, e1);

                dvec3 solution = dvec3(dot(s2, e2), dot(s1, s0), dot(s2, dir));

                solution /= dot(s1, e1);

                double t = solution[0];
                double b1 = solution[1];
                double b2 = solution[2];
                bool isvalid = ((b1 >= 0) && (b2 >= 0) && (b1 + b2 < 1));

                dvec3 intersect_pt = origin + (dir*t);
                double delta = length(par.x_approx - origin) - length(intersect_pt - origin);

                if (isvalid && delta > 0) {
                    par.x_approx = intersect_pt;
                } 

            }


            double count = 0;
             dvec3 avg(0.0,0.0,0.0);
             for (Particle& p2 : particles) {
                 if (length(par.x_approx - p2.x_approx) < (2.5*PARTICLE_RAD) && (par.x_approx[0] != p2.x_approx[0] && par.x_approx[1] != p2.x_approx[1] && par.x_approx[2] != p2.x_approx[2])) {
                     dvec3 unitvec = (par.x_approx - p2.x_approx) / length(par.x_approx - p2.x_approx);
                     dvec3 temp = p2.x_approx + ((2.5*PARTICLE_RAD) * unitvec);
                     avg += temp - par.x_approx;
                     count += 1;
                 }

            }
             
             if (count != 0) {
                 par.x_approx += (avg / count);
             }
            
        }


        for (Particle &par : particles) {
            par.x_approx += par.correction_vec;
        }

    }


    // update velocity and position

    for (Particle &par : particles) {
        par.v = (1/render_step) * (par.x_approx - par.x);
        //vorticity and viscosity   
        par.x = par.x_approx;

        float friction = .5;

        //reflect x position
        if (par.x.x > max.x){
            par.x.x = max.x-(par.x.x-max.x);
            par.v.x = -par.v.x*friction;
        }else if(par.x.x < min.x){
            par.x.x = min.x+(min.x-par.x.x);
            par.v.x = -par.v.x*friction;
        }

        //reflect y position
        if (par.x.y > max.y){
            par.x.y = max.y-(par.x.y-max.y);
            par.v.y = -par.v.y*friction;
        }else if(par.x.y<min.y){
            par.x.y = min.y+(min.y-par.x.y);
            par.v.y  = -par.v.y *friction;
        }

        //reflect z position
        if (par.x.z>max.z){
            par.x.z = max.z-(par.x.z-max.z);
            par.v.z = -par.v.z*friction;
        }else if(par.x.z<min.z){
            par.x.z = min.z+(min.z-par.x.z);
            par.v.z= -par.v.z*friction;
        }

    }

}
        






