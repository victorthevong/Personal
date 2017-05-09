#ifndef PARSER_H
#define PARSER_H

#include "Polygon.h"
#include "glm/glm.hpp"
#include <vector>
#include <string>
#include <sstream>
#include <iostream>
#if defined(__APPLE_CC__)
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#include <math.h>
#endif
typedef glm::dvec3 dvec3;
typedef std::string string;
class Parser {
	public:
		Parser();
		void parse(string fp); // Parse given obj file into vectors
		void render();
		PolyVec polys;
		PointVec vertices;
		
	private: 
		int num_vertices;
		int num_polygons;
};

#endif