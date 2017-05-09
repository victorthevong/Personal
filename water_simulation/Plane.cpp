#include "Plane.h"
#include "math.h"
#include "Particles.h"
Plane::Plane() {
	vertices = std::vector<glm::dvec3>();
}

Plane::Plane(const std::vector<glm::dvec3> &v) {
	vertices = std::vector<glm::dvec3>();
	for(int i = 0; i < 4; i++){
		vertices.push_back(vec);
	}
}

bool Plane::intersect(glm::dvec3 pos, double rad) {
	dvec3 n = cross(vertices[2], vertices[1]).normalize();
	double dist = dot(pos - vertices[0], n);
	if (dist <= rad) {
		return true;
	}
	return false;
}