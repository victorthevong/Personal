#include "Parser.h"
#include <vector>
#include <string>
#include <sstream>
#include <iostream>
#include <fstream>
Parser::Parser() {
	this->num_vertices = 0;
	this->num_polygons = 0;
	this->polys = std::vector<Polygon>();
	this->vertices = std::vector<dvec3>();
	}


void Parser::parse(string fp) {
	std::ifstream infile(fp);
	string line;
	while (std::getline(infile, line)) { 
		if (line.c_str()[0] == 'v' and line.c_str()[1] == ' ') {
			double d1, d2, d3;
			string data = line.substr(1, line.length());
			std::istringstream iss(data);
			iss >> d1;
			iss >> d2;
			iss >> d3;
			vertices.push_back(dvec3(d1, d2, d3));
			num_vertices += 1;

		} else if (line.c_str()[0] == 'f') { 
			int f1, f2, f3;
			string delimiter1 = "//";
			string delimiter2 = " ";
  			string data = line.substr(2, line.length());

  			string token;
  			int pos;

  			pos = data.find(delimiter1);
  			token = data.substr(0, pos);
  			f1 = std::stoi(token) - 1;
  			data.erase(0, pos + delimiter1.length());

  			pos = data.find(delimiter2);
  			data.erase(0, pos + delimiter2.length());

  			pos = data.find(delimiter1);
  			token = data.substr(0, pos);
  			f2 = std::stoi(token) - 1;
  			data.erase(0, pos + delimiter1.length());

  			pos = data.find(delimiter2);
  			data.erase(0, pos + delimiter2.length());

  			pos = data.find(delimiter1);
  			token = data.substr(0, pos);
  			f3 = std::stoi(token) - 1;
  			data.erase(0, pos + delimiter1.length());
  			Polygon face = {f1, f2, f3};
  			polys.push_back(face);
  			num_polygons += 1;
		}
	}
}
void Parser::render() {
	glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
	glBegin(GL_TRIANGLES);
	for (int i = 0; i < num_polygons; i++) {
		Polygon p = polys[i];
		dvec3 v0 = vertices[p.p0];
		dvec3 v1 = vertices[p.p1];
		dvec3 v2 = vertices[p.p2];
		glVertex3d(v0[0], v0[1], v0[2]);
		glVertex3d(v1[0], v1[1], v1[2]);
		glVertex3d(v2[0], v2[1], v2[2]);	
	}
	glEnd();
	glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
}
