#ifndef PLANE_H
#define PLANE_H

#include "glm/glm.hpp"
#include <vector>
#if defined(__APPLE_CC__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#include <math.h>
#endif

class Plane {
	public:
		Plane();
    	Plane(const std::vector<glm::dvec3> &v);
    	bool intersect(glm::dvec3 pos, double rad);
};

#endif /* PLANE_H */