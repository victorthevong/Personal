#ifndef POLYGON_H
#define POLYGON_H

#include "glm/glm.hpp"
#include <vector>

struct Polygon {
	int p0; // List of indices into vertex array.
	int p1;
	int p2;
};

typedef std::vector<glm::dvec3> PointVec;
typedef std::vector<Polygon> PolyVec;

#endif
